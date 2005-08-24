/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.repository.view;

import java.net.URI;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryObjectLabelProvider implements ILabelProvider {

    public void addListener(ILabelProviderListener listener) {
    }

    public Image getImage(Object element) {
        if (element instanceof RepoPathElement) {
            RepoPathElement rpe = (RepoPathElement)element;

            switch (rpe.getLevel()) {
                case RepoPathElement.LEVEL_VERSION:
                case RepoPathElement.LEVEL_ARTIFACT: {
                    String ext = rpe.getExtension();
                    if ( "pom".equals(ext) ) {
                        return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_POM_OBJ).createImage();
                    }
                    if ( "jar".equals(ext) ) {
                        return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.DEPENDENCY_OBJ).createImage();
                    }
                    if ( "plugin".equals(ext)) {
                        return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.PLUGIN_OBJ).createImage();
                    }
                    return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.FILE_OBJ).createImage();
                }
                case RepoPathElement.LEVEL_GROUP: {
                    return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_REPO_GROUP).createImage();
                }
                case RepoPathElement.LEVEL_ROOT: {
                    return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_REPO).createImage();
                }
                case RepoPathElement.LEVEL_TYPE: {
                    return Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_REPO_TYPE).createImage();
                }
            }

        }

        return null;
    }
    
    public String getText(Object element) {
        if (element == null) return null;

        if (element instanceof RepoPathElement) {
            RepoPathElement rpe = (RepoPathElement)element;

            switch (rpe.getLevel()) {
                case RepoPathElement.LEVEL_ARTIFACT: {
                    String artifactId = rpe.getArtifactId();
                    String version = rpe.getVersion();
                    return artifactId + ((!StringUtils.isNull(version) && !version.equals("."))? " : " + version: "");
                }
                case RepoPathElement.LEVEL_GROUP: {
                    String groupId = rpe.getGroupId();
                    return (groupId == null)? "": groupId;
                }
                case RepoPathElement.LEVEL_ROOT: {
                    URI uri = rpe.getURI();
                    return (uri == null)? "": uri.toString();
                }
                case RepoPathElement.LEVEL_TYPE: {
                    String type = rpe.getType();
                    return (type == null)? "": type;
                }
                case RepoPathElement.LEVEL_VERSION: {
                    String version = rpe.getVersion();
                    return (version == null)? "": version;
                }
            }

        }

        return element.toString();
    }
    
    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }
}
