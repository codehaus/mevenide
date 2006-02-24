
package org.codehaus.mevenide.netbeans.customizer;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.AdditionalM2ActionsProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.execute.model.ActionToGoalMapping;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class CustomizerProviderImpl implements CustomizerProvider {
    
    private final NbMavenProject project;
    
    private ProjectCustomizer.Category categories[];
    private ProjectCustomizer.CategoryComponentProvider panelProvider;
    
    // Option indexes
    private static final int OPTION_OK = 0;
    private static final int OPTION_CANCEL = OPTION_OK + 1;
    
    // Option command names
    private static final String COMMAND_OK = "OK";          // NOI18N
    private static final String COMMAND_CANCEL = "CANCEL";  // NOI18N

    private ModelHandle handle;
    
    
    public CustomizerProviderImpl(NbMavenProject project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        showCustomizer( null );
    }
    
    
    public void showCustomizer( String preselectedCategory ) {
        showCustomizer( preselectedCategory, null );
    }
    
    public void showCustomizer( String preselectedCategory, String preselectedSubCategory ) {
        try {
            init();
            OptionListener listener = new OptionListener();
            Dialog dialog = ProjectCustomizer.createCustomizerDialog( categories, panelProvider, preselectedCategory, listener, null );
            dialog.addWindowListener( listener );
            listener.setDialog(dialog);
            dialog.setTitle( MessageFormat.format(
                    "{0}", // NOI18N
                    new Object[] { ProjectUtils.getInformation(project).getDisplayName() } ) );
            
            dialog.setVisible(true);
        } catch (FileNotFoundException ex) {
            //TODO
            ex.printStackTrace();
        } catch (IOException ex) {
            //TODO
            ex.printStackTrace();
        } catch (XmlPullParserException ex) {
            //TODO
            ex.printStackTrace();
        }
    }
    
    private void init() throws XmlPullParserException, FileNotFoundException, IOException {
        ProjectCustomizer.Category basic = ProjectCustomizer.Category.create(
                M2CustomizerPanelProvider.PANEL_BASIC, 
                "Basic", 
                null,
                null);
        ProjectCustomizer.Category run = ProjectCustomizer.Category.create(
                M2CustomizerPanelProvider.PANEL_RUN, 
                "Run", 
                null,
                null);
        
        categories = new ProjectCustomizer.Category[] {
            basic,
            run
        };
        Model model = project.getEmbedder().readModel(project.getPOMFile());
        UserActionGoalProvider usr = (UserActionGoalProvider)project.getLookup().lookup(UserActionGoalProvider.class);
        ActionToGoalMapping mapping = new NetbeansBuildActionXpp3Reader().read(new StringReader(usr.getRawMappingsAsString()));
        handle = new ModelHandle(model, project.getOriginalMavenProject(), mapping);
        panelProvider = new PanelProvider(handle, project);
    }
    
    private static class PanelProvider implements ProjectCustomizer.CategoryComponentProvider {
        
        private JPanel EMPTY_PANEL = new JPanel();
        private NbMavenProject project;
        private ModelHandle handle;
        
        PanelProvider(ModelHandle handle, NbMavenProject project) {
            this.handle = handle;
            this.project = project;
        }
        
        public JComponent create( ProjectCustomizer.Category category ) {
            Lookup.Result res = Lookup.getDefault().lookup(new Lookup.Template(M2CustomizerPanelProvider.class));
            Iterator it = res.allInstances().iterator();
            while (it.hasNext()) {
                M2CustomizerPanelProvider prov = (M2CustomizerPanelProvider) it.next();
                JComponent comp = prov.createPanel(handle, project, category);
                if (comp != null) {
                    return comp;
                }
            }
            return EMPTY_PANEL;
        }
        
    }
    
    /** Listens to the actions on the Customizer's option buttons */
    private class OptionListener extends WindowAdapter implements ActionListener {
        private Dialog dialog;
        
        OptionListener() {
        }
        
        void setDialog(Dialog dlg) {
            dialog = dlg;
        }
        
        // Listening to OK button ----------------------------------------------
        
        public void actionPerformed( ActionEvent e ) {
            if ( dialog != null ) {
                dialog.setVisible(false);
                dialog.dispose();
                try {
                    WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), handle.getPOMModel());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    //TODO
                }
            }
        }
        
        // Listening to window events ------------------------------------------
        
        public void windowClosed( WindowEvent e) {
        }
        
        public void windowClosing(WindowEvent e) {
            if ( dialog != null ) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }
    }
    
    static interface SubCategoryProvider {
        public void showSubCategory(String name);
    }
    
    
}
