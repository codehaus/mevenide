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
package org.mevenide.ui.eclipse.editors.jelly;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.ui.eclipse.editors.jelly.contentassist.JellyContentAssistProcessor;
import org.mevenide.ui.eclipse.goals.outline.MavenXmlOutlinePage;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenXmlEditor extends AbstractJellyEditor {
    
    private static Log log = LogFactory.getLog(MavenXmlEditor.class);
    
    private MavenXmlOutlinePage outlinePage;
    
    public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage= new MavenXmlOutlinePage((IFileEditorInput) getEditorInput());
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}
    
    protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(overwrite, progressMonitor);
		try {
            outlinePage.forceRefresh();
            JellyContentAssistProcessor contentAssist = ((XMLConfiguration) this.getSourceViewerConfiguration()).getTagContentAssist();
            String basedir = new File(((FileEditorInput) this.getEditorInput()).getFile().getLocation().toOSString()).getParent();
            contentAssist.setBasedir(basedir);
        }
        catch (Throwable e) {
            String message = "Prblem occured while refreshing outline"; 
            log.error(message, e);
        }
	}
}

