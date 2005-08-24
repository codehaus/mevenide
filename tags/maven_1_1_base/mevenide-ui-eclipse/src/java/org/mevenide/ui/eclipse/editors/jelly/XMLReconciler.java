/*
 * Created on 16.05.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.editors.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.outline.MavenXmlOutlinePage;

/**
 * @author jll
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLReconciler implements IReconcilingStrategy, IDocumentListener {

    private IDocument document;
    private ArrayList storedPos;
    private ArrayList deleted;
    private ArrayList added;
    private AbstractJellyEditor editor;
    private XMLNode root;
    private MavenXmlOutlinePage op;
    private Map namespaces;
    private boolean sendOnlyAdditions = false;

    public XMLReconciler(AbstractJellyEditor editor, MavenXmlOutlinePage op) {
        this.editor = editor;
        this.op = op;
        this.namespaces = new TreeMap();
    }

    public void setDocument(IDocument document) {
        this.document = document;
    }

    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        System.out.println("Reconcile dirty:" + dirtyRegion);
    }

    public void reconcile(IRegion partition) {
        System.out.println("Reconcile for " + ((TypedRegion) partition).getType());
        documentChanged(null);
    }

    public void documentAboutToBeChanged(DocumentEvent event) {
        // do nothing
    }

    public void createTree(IDocument doc) {
        document = doc;
        try {
            Position[] pos = doc.getPositions("__content_types_category");
            Arrays.sort(pos, new Comparator() {

                public int compare(Object o1, Object o2) {
                    int offset1 = ((XMLNode) o1).getOffset();
                    int offset2 = ((XMLNode) o2).getOffset();
                    return (offset1 > offset2) ? 1 : ((offset1 < offset2) ? -1 : 0);
                }
            });
            root = new XMLNode(0, 0, "/", doc);
            storedPos = new ArrayList();
            root.setParent(null);
            for (int i = 0; i < pos.length; i++) {
                storedPos.add(pos[i]);
                ((XMLNode) pos[i]).setAdded(false);
            }
            added = (ArrayList) storedPos.clone();
            deleted = new ArrayList();
            fix(pos, 0, root);
            editor.setNamespaces(namespaces);
        }
        catch (BadPositionCategoryException e) {
            e.printStackTrace();
        }
    }

    public void documentChanged(DocumentEvent event) {
        IDocument doc = event.getDocument();
        document = doc;
        try {
            Position[] pos = doc.getPositions("__content_types_category");
            Arrays.sort(pos, new Comparator() {

                public int compare(Object o1, Object o2) {
                    int offset1 = ((XMLNode) o1).getOffset();
                    int offset2 = ((XMLNode) o2).getOffset();
                    return (offset1 > offset2) ? 1 : ((offset1 < offset2) ? -1 : 0);
                }
            });
            if (root != null) {
                if (deleted == null) {
                    deleted = new ArrayList();
                }
                else {
                    deleted.clear();
                }
                for (int i = 0; i < storedPos.size(); i++) {
                    if (((XMLNode) storedPos.get(i)).isDeleted()) {
                        XMLNode n = (XMLNode) storedPos.get(i);
                        deleted.add(n);
                    }
                }
                updateTree(deleted);
                if (added == null) {
                    added = new ArrayList();
                }
                else {
                    added.clear();
                }
                boolean fixed = false;
                for (int i = 0; i < pos.length; i++) {
                    XMLNode n = (XMLNode) pos[i];
                    if (n.isAdded() || n.isModified()) {
                        if (n.isAdded()) {
                            storedPos.add(n);
                        }
                        n.setAdded(false);
                        n.setModified(false);
                        if (!fixed) {
                            if (i == 0) {
                                fix(pos, i, root);
                            }
                            else {
                                XMLNode prev = (XMLNode) pos[i - 1];
                                if (prev.getType().equals("TAG")) {
                                    fix(pos, i, prev);
                                }
                                else {
                                    fix(pos, i, prev.getParent());
                                }
                            }
                            if (n.getType().equals("DECL")) {
                            }
                            else {
                                fixed = true;
                            }
                        }
                    }
                }
            }
            if (op != null && op.getControl() != null && !op.getControl().isDisposed() && op.getControl().isVisible()) {
                op.forceRefresh();
            }
            if (sendOnlyAdditions) {
            }
            sendOnlyAdditions = false;
        }
        catch (BadPositionCategoryException e) {
            e.printStackTrace();
        }
    }

    public void prependNewNodeTo(String name, XMLNode to) {
        try {
            sendOnlyAdditions = true;
            document.replace(to.getOffset(), 0, "<" + name + "/>");
        }
        catch (BadLocationException e) {
            Mevenide.displayError(e.getLocalizedMessage(), e);
        }
    }

    public void appendNewNodeTo(String name, XMLNode to) {
        try {
            sendOnlyAdditions = true;
            document.replace(to.getOffset() + to.getLength(), 0, "<" + name + "/>");
        }
        catch (BadLocationException e) {
            Mevenide.displayError(e.getLocalizedMessage(), e);
        }
    }

    private void fix(Position[] pos, int start, XMLNode parent) {
        if (parent == null) {
            parent = root;
        }
        for (int i = start; i < pos.length; i++) {
            XMLNode n = (XMLNode) pos[i];
            if (n.isDeleted) {
                System.out.println("deleted!" + n);
            }
            if (!n.getType().equals("ENDTAG") && !n.getType().equals("END_DECL")) {
                if (n.getParent() != parent) {
                    if (n.getParent() != null) {
                        n.getParent().removeChild(n);
                    }
                    n.setParent(parent);
                }
            }
            if (n.getType().equals("TAG")) {
                List attrs = n.getAttributes();
                n.setCorrespondingNode(n);
                for (Iterator it = attrs.iterator(); it.hasNext();) {
                    XMLNode element = (XMLNode) it.next();
                    String name = element.getName();
                    if (name.indexOf("xmlns") != -1) {
                        String value = element.getValue();
                        int index = name.indexOf(":");
                        String prefix = null;
                        Namespace ns = null;
                        if (index == -1) {
                            prefix = Namespace.DEFAULTNAMESPACE;
                        }
                        else {
                            prefix = name.substring(index + 1);
                        }
                        ns = (Namespace) namespaces.get(prefix);
                        if (ns == null || !ns.getUri().equals(value)) {
                            ns = new Namespace(prefix, value);
                            ns.setGeneric(!MavenXmlEditor.class.equals(editor.getClass()));
                            namespaces.put(prefix, ns);
                        }
                    }
                }
                parent = n;
            }
            else if (n.getType().equals("ENDTAG")) {
                n.setCorrespondingNode(n);
                if (parent != null) {
                    XMLNode newParent = parent;
                    while (!n.getName().equals(newParent.getName()) && newParent != root && newParent != null) {
                        newParent = newParent.getParent();
                    }
                    if (newParent != root && newParent != null) {
                        parent = newParent;
                        n.setCorrespondingNode(parent);
                        parent.setCorrespondingNode(n);
                        if (n.getParent() != parent.getParent()) {
                            if (n.getParent() != null) {
                                n.getParent().removeChild(n);
                            }
                            n.setParent(parent.getParent());
                        }
                        parent = parent.getParent();
                    }
                    else {
                        n.setParent(parent);
                    }
                }
            }
//            else if (n.getType().equals("DECL")) {
//            }
            else if (ITypeConstants.START_DECL.equals(n.getType())) {
                n.setCorrespondingNode(null);
                parent = n;
            }
            else if (ITypeConstants.END_DECL.equals(n.getType())) {
                n.setCorrespondingNode(null);
                if (parent != null) {
                    XMLNode newParent = parent;
                    while (!ITypeConstants.START_DECL.equals(newParent.getType()) && newParent != root && newParent != null) {
                        newParent = newParent.getParent();
                    }
                    if (newParent != root && newParent != null) {
                        parent = newParent;
                        n.setCorrespondingNode(parent);
                        parent.setCorrespondingNode(n);
                        if (n.getParent() != parent.getParent()) {
                            if (n.getParent() != null) {
                                n.getParent().removeChild(n);
                            }
                            n.setParent(parent.getParent());
                        }
                        parent = parent.getParent();
                    }
                    else {
                        n.setParent(parent);
                    }
                }
            }
        }
    }

    public void updateTree(List deleted) {
        for (Iterator it = deleted.iterator(); it.hasNext();) {
            XMLNode node = (XMLNode) it.next();
            if (node.getType().equals(ITypeConstants.DECL)) {
                if (node.getName() != null && node.getName().equals("!DOCTYPE")) {
                    System.out.println("dtd removed");
                }
            }
            if (node.getParent() != null) {
                node.getParent().removeChild(node);
                node.setParent(null);
            }
            else {
                System.out.println("parent not set!" + node.getType() + "|" + node.getName() + "|" + node);
            }
            storedPos.remove(node);
        }
    }

    public void insertTagAfter(String name, XMLNode node) {
        int offset = node.getOffset() + node.getLength() + 1;
        try {
            document.replace(offset, 0, "<" + name + "/>");
            sendOnlyAdditions = true;
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public XMLNode getRoot() {
        return root;
    }

    public void setRoot(XMLNode node) {
        root = node;
    }

    public ArrayList getStoredPos() {
        return storedPos;
    }

    public Map getNamespaces() {
        return namespaces;
    }

    public void addAttributeTo(String name, XMLNode to) {
        try {
            sendOnlyAdditions = true;
            document.replace(to.getOffset() + to.getLength() - 1, 0, " " + name + "=\"\"");
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}