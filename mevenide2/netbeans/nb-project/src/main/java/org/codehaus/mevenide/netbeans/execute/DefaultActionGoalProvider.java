/*
 * DefaultActionProvider.java
 *
 * Created on January 18, 2006, 6:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.execute;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Action;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * a default implementation of AdditionalM2ActionsProvider, a fallback when nothing is
 * user configured or overriden by a more specialized provider.
 * @author mkleint
 */
public class DefaultActionGoalProvider implements AdditionalM2ActionsProvider {
    /** Creates a new instance of DefaultActionProvider */
    public DefaultActionGoalProvider() {
    }
    
    public Action[] createPopupActions(NbMavenProject project) {
        return new Action[0];
    }
    
    public RunConfig createConfigForDefaultAction(String actionName, NbMavenProject project, Lookup lookup) {
        FileObject[] fos = FileUtilities.extractFileObjectsfromLookup(lookup);
        String relPath = null;
        String group = null;
        HashMap replaceMap = new HashMap();
        if (fos.length > 0) {
            Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
            SourceGroup[] grp = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            for (int i = 0; i < grp.length; i++) {
                relPath = FileUtil.getRelativePath(grp[i].getRootFolder(), fos[0]);
                if (relPath != null) {
                    group = grp[i].getName();
                    replaceMap.put("classNameWithExtension", fos[0].getNameExt());
                    replaceMap.put("className", fos[0].getName());
                    replaceMap.put("packageClassName", (FileUtil.getRelativePath(grp[i].getRootFolder(), 
                                                                          fos[0].getParent()) 
                                                 + fos[0].getName()).replace('/','.'));
                    break;
                }
            }
        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_TESTSOURCE) &&
                ActionProvider.COMMAND_RUN_SINGLE.equals(actionName)) {
            actionName = ActionProvider.COMMAND_TEST_SINGLE;
        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_TESTSOURCE) &&
                ActionProvider.COMMAND_DEBUG_SINGLE.equals(actionName)) {
            actionName = ActionProvider.COMMAND_DEBUG_TEST_SINGLE;
        }
        if (group != null && group.equals(MavenSourcesImpl.NAME_SOURCE) &&
                (ActionProvider.COMMAND_TEST_SINGLE.equals(actionName) ||
                 ActionProvider.COMMAND_DEBUG_TEST_SINGLE.equals(actionName))) {
            //TODO.. get the rel path for test not class..
            
        }
        String path = "/org/codehaus/mevenide/netbeans/execute/" + actionName.replace('.','-') + "Action.xml";
        
        InputStream in = getClass().getResourceAsStream(path);
//TODO --------------------------------        
        if (in == null) {
            return null;
        }
        ModelRunConfig rc;
        try {
            return rc = new ModelRunConfig(project, performDynamicSubstitutions(replaceMap, in), getClass().getClassLoader());
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * takes the input stream and a map, and for each occurence of ${<mapKey>}, replaces it with map entry value..
     */
    public static Reader performDynamicSubstitutions(final HashMap replaceMap, final InputStream in) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtil.copy(in, writer);
        Iterator it = replaceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry elem = (Map.Entry) it.next();
            String replaceItem = "${" + elem.getKey() + "}";
            int index = writer.getBuffer().indexOf(replaceItem);
            while (index > -1) {
                writer.getBuffer().replace(index, index + replaceItem.length(), (String)elem.getValue());
                index = writer.getBuffer().indexOf(replaceItem);
            }
        }
        return new StringReader(writer.toString());
    }
    
}
