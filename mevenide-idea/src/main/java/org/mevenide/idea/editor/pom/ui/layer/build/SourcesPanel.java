package org.mevenide.idea.editor.pom.ui.layer.build;

import com.intellij.psi.xml.XmlFile;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

/**
 * @author Arik
 */
public class SourcesPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SourcesPanel.class);

    /**
     * The source code directory field.
     */
    private final JTextField sourceDirField = new JTextField();

    /**
     * The aspects source code directory field.
     */
    private final JTextField aspectSourceDirField = new JTextField();

    /**
     * The resources panel.
     */
    private final ResourcesPanel resourcesPanel;

    /**
     * Creates an instance for the given project and document.
     *
     * @param pFile the POM file
     */
    public SourcesPanel(final XmlFile pFile) {
        super(pFile);

        resourcesPanel = new ResourcesPanel(pFile, "project/build/resources");
        initComponents();
        layoutComponents();
        bindComponents();
    }

    private void initComponents() {
        final Field[] fields = this.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                final Object value = field.get(this);
                if (value != null && value instanceof Component) {
                    final Component comp = (Component) value;
                    comp.setName(field.getName());
                }
            }
            catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = new GridBagConstraints();
        c.fill = BOTH;
        add(createDirectoriesPanel(), c);

        c = new GridBagConstraints();
        c.fill = BOTH;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        add(resourcesPanel, c);
        UIUtils.installBorder(resourcesPanel, 10, 0, 0, 0);
    }

    private void bindComponents() {
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(file);

            binder.bind(sourceDirField, "project/build/sourceDirectory");
            binder.bind(aspectSourceDirField, "project/build/aspectSourceDirectory");
        }
    }

    /**
     * Creates the directories panel.
     * @return panel
     */
    protected JPanel createDirectoriesPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        //
        //repository
        //
        builder.appendSeparator(RES.get("src.dirs.title"));
        builder.append(RES.get("java.src.dir"), sourceDirField);
        builder.append(RES.get("aspect.src.dir"), aspectSourceDirField);
        builder.appendSeparator(RES.get("conf.dirs.title"));
        final JPanel panel = builder.getPanel();
        UIUtils.installBorder(panel, 0, 0, 0, 0);

        return panel;
    }
}
