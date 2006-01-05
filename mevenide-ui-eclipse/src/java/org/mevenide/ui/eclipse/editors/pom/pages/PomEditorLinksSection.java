/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.editors.pom.pages;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.util.StringUtils;

/**
 * Section for links to pages within the POM Editor
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomEditorLinksSection extends SectionPart {

    private class LinkListener extends HyperlinkAdapter {
        /**
         * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        public void linkActivated(HyperlinkEvent e) {
            String href = (String) e.getHref();
            if (!StringUtils.isNull(href)) {
                page.getPomEditor().setActivePage(href);
            }
        }
        
    }
    
    private final OverviewPage page;
    private final IHyperlinkListener listener;
    
    public PomEditorLinksSection(OverviewPage page, Composite parent, FormToolkit toolkit) {
        super(parent, toolkit, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.FOCUS_TITLE | ExpandableComposite.TITLE_BAR);
        getSection().setText(MevenideResources.LINKS_SECTION_HEADER);
        this.page = page;
        this.listener = new LinkListener();
    }
    
    public void initialize(IManagedForm form) {
        super.initialize(form);
        
        Section section = getSection();
        section.setClient(createSectionContent(section, getManagedForm().getToolkit()));
    }
    
    protected Composite createSectionContent(Composite parent, FormToolkit factory) {
        Composite container = factory.createComposite(parent);
        TableWrapLayout layout = new TableWrapLayout();
        layout.leftMargin = layout.rightMargin = layout.topMargin = layout.bottomMargin = 0;
        container.setLayout(layout);
        
        Image image = Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_POM_OBJ).createImage();
        
        FormText text = factory.createFormText(container, true);
        text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        text.setText(MevenideResources.LINKS_SECTION_CONTENT, true, false);
        text.setImage("pomObject", image);
        text.addHyperlinkListener(this.listener);
        
        return container;
    }

}
