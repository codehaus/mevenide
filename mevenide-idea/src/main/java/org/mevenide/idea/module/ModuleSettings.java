/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizableStringList;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.HashSet;
import org.jdom.Element;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.IQueryErrorCallback;
import org.mevenide.idea.support.AbstractModuleComponent;
import org.mevenide.idea.util.ui.UIUtils;

import java.io.File;
import java.util.Set;

/**
 * @author Arik
 */
public class ModuleSettings extends AbstractModuleComponent implements JDOMExternalizable {
    /**
     * The module's query context. If <code>null</code>, it means that the module
     * has no POM, or that an error has occured while reading it.
     */
    private IQueryContext queryContext = null;

    /**
     * The module's favorite goals.
     */
    private Set<String> favoriteGoals = new HashSet<String>(10);

    /**
     * This is a file system listener that synchronizes the module's query
     * context if maven files change in the module directory.
     */
    private final FSListener fileSystemListener = new FSListener();

    /**
     * Creates a module settings manager for the specified module.
     *
     * @param pModule
     */
    public ModuleSettings(final Module pModule) {
        super(pModule);
    }

    public File getPomFile() {
        final VirtualFile moduleDir = getModuleDir();
        if(moduleDir == null)
            return null;

        final File moduleDirFile = new File(moduleDir.getPath());
        if(!moduleDirFile.isDirectory())
            return null;

        final File pomFile = new File(moduleDirFile, "project.xml");
        if(!pomFile.isFile())
            return null;

        return pomFile;
    }

    /**
     * Initializes the manager.
     *
     * <p>Registers a file system listener to be notified if maven files change
     * in the module directory.</p>
     */
    public void initComponent() {
        module.getModuleFile().getFileSystem().addVirtualFileListener(fileSystemListener);
        refreshQueryContext();
    }

    /**
     * Disposes of the manager.
     *
     * <p>Unregisters the file system listener.</p>
     */
    @Override public void disposeComponent() {
        module.getModuleFile().getFileSystem().removeVirtualFileListener(fileSystemListener);
    }

    private VirtualFile getModuleDir() {
        final VirtualFile moduleFile = module.getModuleFile();
        if (moduleFile == null)
            return null;

        return moduleFile.getParent();
    }

    /**
     * Refreshes the query context by recreating it.
     *
     * <p>This method is called both by the {@link #initComponent()} method and
     * when a pom file (e.g. a file named <i>project.xml</i>) is created, deleted,
     * changed or moved in the module directory.</p>
     */
    protected void refreshQueryContext() {
        synchronized (LOCK) {
            final VirtualFile moduleDir = getModuleDir();
            if(moduleDir == null) {
                queryContext = null;
                return;
            }

            final File moduleDirFile = new File(moduleDir.getPath());
            queryContext = new DefaultQueryContext(moduleDirFile,
                                                   new UIQueryErrorCallback());
        }
    }

    /**
     * Returns the module's Maven query context.
     *
     * <p>This method might return <code>null</code>, which means that the module
     * is not associated with a Maven context.</p>
     *
     * @return a query context, or <code>null</code>
     */
    public IQueryContext getQueryContext() {
        synchronized (LOCK) {
            return queryContext;
        }
    }

    /**
     * Returns the module's favorite goals.
     *
     * <p>If no favorite goals have been selected, this method will return an empty
     * array, but never <code>null</code>.</p>
     *
     * @return array of fully-qualified goal names.
     */
    public String[] getFavoriteGoals() {
        synchronized (LOCK) {
            return favoriteGoals.toArray(new String[favoriteGoals.size()]);
        }
    }

    /**
     * Sets the module's favorite goals.
     *
     * <p>This method will fire a property change event for "favoriteGoals" property.</p>
     *
     * @param pGoals the favorite goals - fully qualified goal names (may be <code>null</code> or an empty array)
     */
    public void setFavoriteGoals(final String[] pGoals) {
        synchronized(LOCK) {
            final String[] oldGoals = getFavoriteGoals();

            favoriteGoals.clear();
            if(pGoals != null)
                for(String goal : pGoals)
                    favoriteGoals.add(goal);

            changeSupport.firePropertyChange("favoriteGoals", oldGoals, getFavoriteGoals());
        }
    }

    public void readExternal(final Element pElement) throws InvalidDataException {
        synchronized(LOCK) {
            //
            //load favorite goals
            //
            final Element favGoalsElt = pElement.getChild("favoriteGoals");
            if(favGoalsElt != null) {
                final JDOMExternalizableStringList goals = new JDOMExternalizableStringList();
                goals.readExternal(favGoalsElt);
                setFavoriteGoals(goals.toArray(new String[goals.size()]));
            }
        }
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        synchronized (LOCK) {
            //
            //save favorite goals
            //
            final JDOMExternalizableStringList goals = new JDOMExternalizableStringList();
            goals.addAll(favoriteGoals);
            final Element favoriteGoalsElt = new Element("favoriteGoals");
            goals.writeExternal(favoriteGoalsElt);
            pElement.addContent(favoriteGoalsElt);
        }
    }

    /**
     * Returns the module settings instance for the specified module.
     *
     * @param pModule the module to retrieve the settings manager for
     * @return a ModuleSettings instance
     */
    public static ModuleSettings getInstance(final Module pModule) {
        return pModule.getComponent(ModuleSettings.class);
    }

    /**
     * A {@link IQueryErrorCallback} implementation which displays the error
     * to the user in a message box.
     */
    private class UIQueryErrorCallback implements IQueryErrorCallback {
        public void handleError(int errorNumber, Exception exception) {
            UIUtils.showError(module, exception.getMessage(), exception);
        }

        public void discardError(int errorNumber) {
        }
    }

    /**
     * A virtual file system listener that refreshes the module's query
     * context if a Maven file (e.g. <i>project.xml</i>, <i>maven.xml</i>,
     * <i>project.properties</i> or <i>build.properties</i> change in the
     * module's directory.
     */
    private class FSListener extends VirtualFileAdapter {
        protected boolean shouldRefresh(final String pFileName) {
            return pFileName.equalsIgnoreCase("project.xml") ||
                    pFileName.equalsIgnoreCase("maven.xml") ||
                    pFileName.equalsIgnoreCase("project.properties") ||
                    pFileName.equalsIgnoreCase("build.properties");
        }

        @Override public void fileCreated(VirtualFileEvent event) {
            if(shouldRefresh(event.getFileName()))
                refreshQueryContext();
        }

        @Override public void fileDeleted(VirtualFileEvent event) {
            if (shouldRefresh(event.getFileName()))
                refreshQueryContext();
        }

        @Override public void fileMoved(VirtualFileMoveEvent event) {
            if (shouldRefresh(event.getFileName()))
                refreshQueryContext();
        }

        @Override public void contentsChanged(VirtualFileEvent event) {
            if (shouldRefresh(event.getFileName()))
                refreshQueryContext();
        }
    }
}
