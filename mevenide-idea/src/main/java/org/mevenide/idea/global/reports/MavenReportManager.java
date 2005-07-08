package org.mevenide.idea.global.reports;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.global.properties.PropertiesEvent;
import org.mevenide.idea.global.properties.PropertiesListener;
import org.mevenide.idea.global.properties.PropertiesManager;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik
 * @todo should be moved to 'project' package
 */
public class MavenReportManager extends AbstractProjectComponent {
    private Report[] reports = null;

    public MavenReportManager(final Project pProject) {
        super(pProject);
    }

    public Report getReport(final String pId) {
        for (Report report : getReports()) {
            if (report.getId().equals(pId))
                return report;
        }

        return null;
    }

    public Report[] getReports() {
        synchronized (this) {
            if (reports == null)
                reports = loadReports();

            return reports;
        }
    }

    private Report[] loadReports() {
        final MavenPluginsManager plgMgr = MavenPluginsManager.getInstance(project);
        final PluginGoalContainer[] plugins = plgMgr.getPlugins();
        final Set<Report> reports = new HashSet<Report>(plugins.length);

        for (final PluginGoalContainer plugin : plugins) {
            final VirtualFile script = plugin.getScriptFile().getFile();
            if (script == null || !script.isValid())
                continue;

            final String id;
            if (plugin.getArtifactId() != null)
                id = plugin.getArtifactId();
            else
                id = plugin.getId();
            final String regGoalName = id + ":register";

            final XmlFile xmlFile = PsiUtils.findXmlFile(project, script);
            if (xmlFile == null)
                continue;

            final XmlTagPath regGoalPath = new XmlTagPath(xmlFile, "project/goal");
            final XmlTag[] goals = regGoalPath.getAllTags();
            for (XmlTag goalTag : goals) {
                final String goalName = goalTag.getAttributeValue("name");
                if (!regGoalName.equals(goalName))
                    continue;

                PsiTreeUtil.processElements(goalTag, new PsiElementProcessor() {
                    public boolean execute(final PsiElement element) {
                        if (!(element instanceof XmlTag))
                            return true;

                        final XmlTag tag = (XmlTag) element;
                        if ("doc:registerReport".equals(tag.getName())) {
                            final DefaultReport report = new DefaultReport();
                            report.setPlugin(plugin);
                            report.setDescription(tag.getAttributeValue("description"));
                            report.setId(id);
                            report.setName(tag.getAttributeValue("name"));
                            reports.add(report);
                        }

                        return true;
                    }
                });
            }
        }

        final Report[] buffer = new Report[reports.size()];
        final Report[] reportsArray = reports.toArray(buffer);
        Arrays.sort(reportsArray, new Comparator<Report>() {
            public int compare(final Report o1, final Report o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return reportsArray;
    }

    @Override
    public void initComponent() {
        MavenManager.getInstance().addPropertyChangeListener(
                "mavenHome",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        synchronized (this) {
                            reports = null;
                        }
                    }
                });

        PropertiesManager.getInstance(project).addPropertiesListener(
                new PropertiesListener() {
                    public void propertiesChanged(PropertiesEvent pEvent) {
                        synchronized (this) {
                            reports = null;
                        }
                    }
                });

        StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
            public void run() {
                getReports();
            }
        });
    }

    public static MavenReportManager getInstance(final Project pProject) {
        return pProject.getComponent(MavenReportManager.class);
    }
}
