/*
 * J2eeActionsProvider.java
 *
 * Created on January 10, 2006, 9:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.j2ee;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.DefaultActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.ModelRunConfig;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.SimplePluginConfig;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class J2eeActionsProvider implements AdditionalM2ActionsProvider {
    
    /** Creates a new instance of J2eeActionsProvider */
    public J2eeActionsProvider() {
    }
    
    public Action[] createPopupActions(NbMavenProject project) {
        return new Action[0];
    }
    
    public RunConfig createConfigForDefaultAction(String actionName, NbMavenProject project, Lookup lookup) {
        if ((ActionProvider.COMMAND_RUN_SINGLE.equals(actionName) || 
             ActionProvider.COMMAND_DEBUG.equals(actionName) ||
             ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName) ||
             ActionProvider.COMMAND_RUN.equals(actionName)) &&
                "war".equals(project.getOriginalMavenProject().getPackaging())) {
            String relPath = null;
            if (ActionProvider.COMMAND_RUN_SINGLE.equals(actionName) || 
                    ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName)) {
                FileObject[] fos = FileUtilities.extractFileObjectsfromLookup(lookup);
                if (fos.length > 0) {
                    Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
                    SourceGroup[] grp = srcs.getSourceGroups(MavenSourcesImpl.TYPE_DOC_ROOT);
                    for (int i = 0; i < grp.length; i++) {
                        relPath = FileUtil.getRelativePath(grp[i].getRootFolder(), fos[0]);
                        if (relPath != null) break;
                    }
                }
                if (relPath == null) {
                    // in case we don't do and let the default decide if the selected file is a testcase or what..
                    return null;
                }
            } else {
                //for RUN/DEBUG
                relPath = "";
            }
            HashMap replacements = new HashMap();
            replacements.put("webpagePath", relPath);
            String debug = ActionProvider.COMMAND_DEBUG.equals(actionName) || ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName) ?
                            "Debug" : "";
            String single = ActionProvider.COMMAND_RUN_SINGLE.equals(actionName) || ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName) ?
                            "Single" : "";
            
            InputStream str = getClass().getResourceAsStream("/org/codehaus/mevenide/netbeans/j2ee/deploy" + debug + single + "WebAction.xml");
            assert str != null;
            
            try {
                ModelRunConfig rc = new ModelRunConfig(project, DefaultActionGoalProvider.performDynamicSubstitutions(replacements, str),
                        getClass().getClassLoader());
                return rc;
            } catch (XmlPullParserException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return null;
    }
    
}
