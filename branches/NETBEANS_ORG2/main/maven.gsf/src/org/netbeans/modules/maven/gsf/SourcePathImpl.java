/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
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
