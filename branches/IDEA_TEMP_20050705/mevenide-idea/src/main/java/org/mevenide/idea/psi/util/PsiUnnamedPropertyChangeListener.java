package org.mevenide.idea.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import java.util.*;
import javax.swing.event.EventListenerList;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.event.*;

/**
 * @author Arik
 */
public class PsiUnnamedPropertyChangeListener extends PsiPropertyChangeListener
    implements BeanRowsObservable {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PsiUnnamedPropertyChangeListener.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PsiUnnamedPropertyChangeListener.class);

    /**
     * Synchronization lock.
     */
    private final Object LOCK = new Object();

    /**
     * The path to the tag containing all row tags.
     */
    private final XmlTagPath containerPath;

    /**
     * Cache for lazily created tag paths, as property names are not known in advance.
     */
    private final Map<String, XmlTagPath> pathsCache = Collections.synchronizedMap(new HashMap<String, XmlTagPath>(
        10));

    /**
     * Property change listeners container.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The name to use for properties without a name.
     */
    private String unknownPropertyName;

    public PsiUnnamedPropertyChangeListener(final XmlFile pFile,
                                            final String pContainerPath) {
        this(new XmlTagPath(pFile, pContainerPath));
    }

    public PsiUnnamedPropertyChangeListener(final XmlTagPath pContainerPath) {
        if (pContainerPath == null)
            throw new IllegalArgumentException(RES.get("null.arg", "pContainerPath"));
        containerPath = pContainerPath;
    }

    public String getUnknownPropertyName() {
        return unknownPropertyName;
    }

    public void setUnknownPropertyName(final String pUnknownPropertyName) {
        unknownPropertyName = pUnknownPropertyName;
    }

    public final String[] getPropertyNames() {
        final XmlTag[] tags = getPropertyTags();
        final String[] names = new String[tags.length];
        for (int i = 0; i < tags.length; i++)
            names[i] = tags[i].getName();

        return names;
    }

    public final void renameProperty(final String pPropertyName,
                                     final String pNewPropertyName) {
        final XmlTagPath tagPath = getPropertyTagPath(pPropertyName, false);
        XmlTag tag = tagPath == null ? null : tagPath.getTag();
        if (tag == null)
            return;

        //
        //rename the tag
        //
        final TagRenamerRunnable runnable = new TagRenamerRunnable(tag, pNewPropertyName);
        IDEUtils.runCommand(containerPath.getProject(), runnable);
        tag = runnable.getTag();
        if (tag == null)
            return;

        //
        //create new path based on new property name
        //
        final String[] pathTokens = tagPath.getPathTokens(false);
        pathTokens[pathTokens.length - 1] = pNewPropertyName;

        //
        //remove the old path
        //
        pathsCache.remove(pPropertyName);
        notifyRemoval(pPropertyName);

        //
        //put in the cache the new path
        //
        final String path = StringUtils.join(pathTokens, '/');
        final XmlTagPath newTagPath = new XmlTagPath(containerPath, path);
        pathsCache.put(pNewPropertyName, newTagPath);

        notifyAddition(pNewPropertyName);
    }

    public final XmlTag[] getPropertyTags() {
        final XmlTag containerTag = containerPath.getTag();
        if (containerTag == null)
            return new XmlTag[0];

        return containerTag.getSubTags();
    }

    protected String getPropertyForElement(final PsiElement pElement) {
        final XmlTag[] tags = getPropertyTags();
        for (XmlTag tag : tags) {
            if (PsiTreeUtil.isAncestor(tag, pElement, false))
                return tag.getName();
        }

        return null;
    }

    protected String getPropertyForPath(final String[] pPath) {
        final XmlTag[] tags = getPropertyTags();
        for (XmlTag tag : tags) {
            final String[] path = PsiUtils.getPathTokens(tag);

            int i = 0;
            boolean equal = true;
            while (i < path.length && i < pPath.length && equal) {
                equal = path[i].equals(pPath[i]);
                i++;
            }

            if (equal && pPath.length >= path.length)
                return tag.getName();
        }

        return null;
    }

    public final XmlTagPath getPropertyTagPath(final String pProperty) {
        return getPropertyTagPath(pProperty, true);
    }

    public final XmlTagPath getPropertyTagPath(final String pProperty,
                                               final boolean pCreateIfNotFound) {
        XmlTagPath path = pathsCache.get(pProperty);
        if (path == null && pCreateIfNotFound) {
            path = new XmlTagPath(containerPath, pProperty);
            pathsCache.put(pProperty, path);
        }

        return path;
    }

    public void addBeanRowsListener(BeanRowsListener pListener) {
        synchronized (LOCK) {
            listenerList.add(BeanRowsListener.class, pListener);
        }
    }

    public void removeBeanRowsListener(BeanRowsListener pListener) {
        synchronized (LOCK) {
            listenerList.remove(BeanRowsListener.class, pListener);
        }
    }

    public int getRowCount() {
        final XmlTag containerTag = containerPath.getTag();
        if (containerTag == null)
            return 0;

        return containerTag.getSubTags().length;
    }

    public int appendRow() {
        final RowAppenderRunnable addTagCmd = new RowAppenderRunnable();

        IDEUtils.runCommand(containerPath.getProject(), addTagCmd);
        return addTagCmd.getResult();
    }

    public void deleteRows(final int... pRowIndices) {
        final Runnable deleteTagsCmd = new RowDeleterRunnable(pRowIndices);

        IDEUtils.runCommand(containerPath.getProject(), deleteTagsCmd);
    }

    @Override
    protected void notifyAddition(final String pPropertyName) {
        final String[] propertyNames = getPropertyNames();
        final int row = ArrayUtils.indexOf(propertyNames, pPropertyName);
        fireRowAddedEvent(row);
    }

    @Override
    protected void notifyChange(final String pPropertyName) {
        String[] propertyNames = getPropertyNames();
        int row = ArrayUtils.indexOf(propertyNames, pPropertyName);
        fireRowChangedEvent(row, pPropertyName, getValue(pPropertyName));
    }

    @Override
    protected void notifyRemoval(final String pPropertyName) {
        fireRowsChangedEvent();
    }

    private BeanRowsListener[] getListeners() {
        return listenerList.getListeners(BeanRowsListener.class);
    }

    private void fireRowAddedEvent(final int pRow) {
        BeanRowEvent event = null;
        for (BeanRowsListener listener : getListeners()) {
            if (event == null)
                event = new BeanRowAddedEvent(this, pRow);
            listener.rowAdded(event);
        }
    }

    private void fireRowRemovedEvent(final int pRow) {
        BeanRowEvent event = null;
        for (BeanRowsListener listener : getListeners()) {
            if (event == null)
                event = new BeanRowRemovedEvent(this, pRow);
            listener.rowRemoved(event);
        }
    }

    private void fireRowChangedEvent(final int pRow,
                                     final String pProperty,
                                     final Object pValue) {
        BeanRowEvent event = null;
        for (BeanRowsListener listener : getListeners()) {
            if (event == null)
                event = new BeanRowChangedEvent(this, pRow, pProperty, pValue);
            listener.rowChanged(event);
        }
    }

    private void fireRowsChangedEvent() {
        BeanRowEvent event = null;
        for (BeanRowsListener listener : getListeners()) {
            if (event == null)
                event = new BeanRowsChangedEvent(this);
            listener.rowsChanged(event);
        }
    }

    private class RowAppenderRunnable implements Runnable {
        private int result = -1;

        public void run() {
            result = -1;
            try {
                final XmlTag containerTag = containerPath.ensureTag();
                final String namespace = containerTag.getNamespace();
                final XmlTag rowTag = (XmlTag) containerTag.add(
                    containerTag.createChildTag(unknownPropertyName,
                                                namespace,
                                                null,
                                                false));

                final XmlTag[] childTags = containerTag.getSubTags();
                for (int i = 0; i < childTags.length; i++) {
                    XmlTag tag = childTags[i];
                    if (tag.equals(rowTag)) {
                        result = i;
                        break;
                    }
                }
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        public final int getResult() {
            return result;
        }
    }

    private class RowDeleterRunnable implements Runnable {
        private final int[] rowIndices;

        public RowDeleterRunnable(final int... pRowIndices) {
            rowIndices = pRowIndices;
        }

        public void run() {
            try {
                final XmlTag containerTag = containerPath.getTag();
                if (containerTag == null)
                    return;

                final XmlTag[] childTags = containerTag.getSubTags();
                final Set<XmlTag> tags = new HashSet<XmlTag>(rowIndices.length);
                for (int rowIndice : rowIndices)
                    tags.add(childTags[rowIndice]);

                for (XmlTag xmlTag : tags)
                    xmlTag.delete();
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private class TagRenamerRunnable implements Runnable {
        private final String newName;
        private XmlTag tag;

        public TagRenamerRunnable(final XmlTag pTag, final String pNewName) {
            tag = pTag;
            newName = pNewName;
        }

        public void run() {
            try {
                tag = (XmlTag) tag.setName(newName);
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
                tag = null;
            }
        }

        public XmlTag getTag() {
            return tag;
        }
    }
}
