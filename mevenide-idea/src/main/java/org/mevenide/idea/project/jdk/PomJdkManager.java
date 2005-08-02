package org.mevenide.idea.project.jdk;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import java.util.List;
import org.jdom.Element;
import org.mevenide.idea.project.AbstractPomSettingsManager;
import org.mevenide.idea.project.PomManager;

/**
 * @author Arik
 */
public class PomJdkManager extends AbstractPomSettingsManager implements JDOMExternalizable {
    private static final Key<ProjectJdk> KEY = Key.create(ProjectJdk.class.getName());

    public PomJdkManager(final Project pProject) {
        super(pProject);
    }

    public ProjectJdk getJdk(final String pPomUrl) {
        if (!isRegistered(pPomUrl))
            return null;

        final ProjectJdk jdk = get(KEY, pPomUrl);
        if (jdk != null)
            return jdk;

        return ProjectRootManager.getInstance(project).getProjectJdk();
    }

    public void setJdk(final String pPomUrl, final ProjectJdk pJdk) {
        if (!isRegistered(pPomUrl))
            return;

        final ProjectRootManager rootMgr = ProjectRootManager.getInstance(project);
        final ProjectJdk defJdk = rootMgr.getProjectJdk();

        final ProjectJdk oldJdk = getJdk(pPomUrl);
        if (pJdk == null || pJdk.equals(defJdk))
            put(KEY, pPomUrl, null);
        else
            put(KEY, pPomUrl, pJdk);

        changeSupport.firePropertyChange(
                new JdkPropertyChangeEvent(this, "jdk", oldJdk, pJdk, pPomUrl));
    }

    public void readExternal(final Element pElt) throws InvalidDataException {
        //noinspection unchecked
        final List<Element> pomElts = pElt.getChildren("pom");
        for (Element pomElt : pomElts) {
            final String url = pomElt.getAttributeValue("url");
            if (url == null || url.trim().length() == 0)
                continue;

            final String jdkName = pomElt.getAttributeValue("jdk");
            if (jdkName != null && jdkName.trim().length() > 0) {
                final ProjectJdk jdk = ProjectJdkTable.getInstance().findJdk(jdkName);
                setJdk(url, jdk);
            }
        }
    }

    public void writeExternal(final Element pElt) throws WriteExternalException {
        final PomManager pomMgr = PomManager.getInstance(project);
        final String[] urls = pomMgr.getFileUrls();
        for (String url : urls) {
            final Element pomElt = new Element("pom");
            pomElt.setAttribute("url", url);
            final ProjectJdk jdk = getJdk(url);
            if (jdk != null)
                pomElt.setAttribute("jdk", jdk.getName());

            pElt.addContent(pomElt);
        }
    }

    public static PomJdkManager getInstance(final Project pProject) {
        return pProject.getComponent(PomJdkManager.class);
    }

    public class JdkPropertyChangeEvent extends PomPropertyChangeEvent<PomJdkManager, ProjectJdk> {
        public JdkPropertyChangeEvent(final PomJdkManager source,
                                      final String propertyName,
                                      final ProjectJdk oldValue,
                                      final ProjectJdk newValue,
                                      final String pPomUrl) {
            super(source, pPomUrl, propertyName, oldValue, newValue);
        }
    }
}
