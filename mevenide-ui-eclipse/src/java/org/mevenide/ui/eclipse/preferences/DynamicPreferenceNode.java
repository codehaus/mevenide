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
package org.mevenide.ui.eclipse.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;


/**  
 * 
 * needed to obtain a consistent behaviour (f.i. disposed page cause troubles if not correctly handled) 
 * much has been taken from superclass 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferenceNode extends PreferenceNode {

    private String pageId;
    private String pageTitle;
    private String pluginDescription;
    
    private List pluginProperties;
    
    private IPreferencePage page;
	private List subNodes;
	private ImageDescriptor imageDescriptor;
	private Image image;
	
    public DynamicPreferenceNode(String pageId, String pageTitle, String pluginDescription, List properties) {
        super(pageId, pageTitle, null, DynamicPreferencePage.class.getName());
        this.pageId = pageId;
        this.pageTitle = pageTitle; 
        this.pluginProperties = properties;
        this.pluginDescription = pluginDescription;
    }
    
    public void createPage() {
        if ( page == null ) {
            page = new DynamicPreferencePage();
            page.setTitle(pageTitle);
            ((DynamicPreferencePage) page).setProperties(pluginProperties);
            ((DynamicPreferencePage) page).setPluginDescription(pluginDescription);
            if (getLabelImage() != null) {
    			page.setImageDescriptor(imageDescriptor);
            }
        }
    }
    
    public String getLabelText() {
        return pageTitle;
    }
    
    public IPreferencePage getPage() {
        if ( page == null ) {
            createPage();
        }
        return page;
    }
    
	public void add(IPreferenceNode node) {
		if (subNodes == null)
			subNodes = new ArrayList();
		subNodes.add(node);
	}

	public void disposeResources() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		if (page != null) {
			page.dispose();
			page = null;
		}
	}
	
	public IPreferenceNode findSubNode(String id) {
		Assert.isNotNull(id);
		Assert.isTrue(id.length() > 0);
		if (subNodes == null)
			return null;
		int size = subNodes.size();
		for (int i = 0; i < size; i++) {
			IPreferenceNode node = (IPreferenceNode) subNodes.get(i);
			if (id.equals(node.getId()))
				return node;
		}
		return null;
	}
	
	public String getId() {
		return this.pageId;
	}
	
	protected ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
	public Image getLabelImage() {
		if (image == null && imageDescriptor != null) {
			image = imageDescriptor.createImage();
		}
		return image;
	}
	
	
	public IPreferenceNode[] getSubNodes() {
		if (subNodes == null)
			return new IPreferenceNode[0];
		return (IPreferenceNode[]) subNodes
				.toArray(new IPreferenceNode[subNodes.size()]);
	}
	
	public IPreferenceNode remove(String id) {
		IPreferenceNode node = findSubNode(id);
		if (node != null)
			remove(node);
		return node;
	}
	
	public boolean remove(IPreferenceNode node) {
		if (subNodes == null)
			return false;
		return subNodes.remove(node);
	}
	
	public void setPage(IPreferencePage newPage) {
		page = newPage;
	}
}
