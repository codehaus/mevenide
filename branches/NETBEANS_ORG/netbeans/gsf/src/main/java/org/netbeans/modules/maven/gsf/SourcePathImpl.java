/*
 *  Copyright 2008 mkleint.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.netbeans.modules.maven.gsf;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathImplementation;
import org.netbeans.modules.gsfpath.spi.classpath.PathResourceImplementation;
import org.netbeans.modules.gsfpath.spi.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
class SourcePathImpl implements ClassPathImplementation {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private CPProvider provider;
    private NbMavenProject project;
    private List resources;
    private boolean test;

    public SourcePathImpl(NbMavenProject project, CPProvider prvd, boolean b) {
        this.project = project;
        provider = prvd;
        test = b;
        //TODO add listening on project change..
    }

    public synchronized List /*<PathResourceImplementation>*/ getResources() {
        if (resources == null) {
            resources = this.getPath();
        }
        return resources;
    }



    private List getPath() {
        List<PathResourceImplementation> result = new ArrayList<PathResourceImplementation>();
        URI[] pieces = provider.getSourceRoots(test);
        for (int i = 0; i < pieces.length; i++) {
            try {
                URL entry;

                // if file does not exist (e.g. build/classes folder
                // was not created yet) then corresponding File will
                // not be ended with slash. Fix that.

                //HACK the url is considered archive if the name contains a dot.
                // should be safe since the only folder classpath items are sources and target/classes
                // if this causes problems we need to move the url finetuning in the place which
                //creates the URIs (createPath())
                String lowecasePath = pieces[i].toString().toLowerCase();
                int lastdot = lowecasePath.lastIndexOf('.');
                int lastslash = lowecasePath.lastIndexOf('/');
                boolean isFile = (lastdot > 0 && lastdot > lastslash);

                if (isFile) {//NOI18N
                    entry = FileUtil.getArchiveRoot(pieces[i].toURL());
                } else {
                    entry = pieces[i].toURL();
                    if  (!entry.toExternalForm().endsWith("/")) { //NOI18N
                        entry = new URL(entry.toExternalForm() + "/"); //NOI18N
                    }
                }
                if (entry != null) {
                    result.add(ClassPathSupport.createResource(entry));
                }
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        synchronized (support) {
            support.addPropertyChangeListener(arg0);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        synchronized (support) {
            support.removePropertyChangeListener(arg0);
        }
    }

}
