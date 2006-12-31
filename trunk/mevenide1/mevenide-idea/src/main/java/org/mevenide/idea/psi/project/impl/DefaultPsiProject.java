package org.mevenide.idea.psi.project.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.psi.project.*;
import org.mevenide.idea.psi.support.AbstractPsiNamedPropertyObservable;
import org.mevenide.idea.psi.util.PsiUtils;

/**
 * @author Arik
 */
public class DefaultPsiProject extends AbstractPsiNamedPropertyObservable implements PsiProject {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DefaultPsiProject.class);

    /**
     * A flag indicating whether the parent POM has been searched or not.
     */
    private boolean searchedForParent = false;

    /**
     * The cached reference to the parent POM.
     */
    private PsiProject parent;

    /**
     * The project organization PSI handler.
     */
    private PsiOrganization organization = null;

    /**
     * Project mailing lists.
     */
    private PsiMailingLists mailingLists = null;

    /**
     * Project dependencies.
     */
    private PsiDependencies dependencies = null;

    /**
     * Project developers team.
     */
    private PsiDevelopers developers = null;

    /**
     * Project contributors.
     */
    private PsiContributors contributors = null;

    /**
     * The SCM repository.
     */
    private PsiScmRepository scmRepository = null;

    /**
     * The SCM branches.
     */
    private PsiScmBranches scmBranches = null;

    /**
     * The versions.
     */
    private PsiVersions versions = null;

    /**
     * The resources.
     */
    private PsiResources mainResources = null;

    /**
     * The test resources.
     */
    private PsiResources testResources = null;

    /**
     * POM reports.
     */
    private PsiReports reports = null;

    /**
     * Creates a new instance for the given XML (POM) file.
     *
     * @param pXmlFile the POM file
     */
    public DefaultPsiProject(final XmlFile pXmlFile) {
        super(pXmlFile, "project");

        registerTag("pomVersion", "pomVersion");
        registerTag("extend", "extend");
        registerTag("name", "name");
        registerTag("currentVersion", "currentVersion");
        registerTag("artifactId", "artifactId");
        registerTag("groupId", "groupId");
        registerTag("url", "url");
        registerTag("logoUri", "logo");
        registerTag("inceptionYear", "inceptionYear");
        registerTag("packageName", "package");
        registerTag("shortDescription", "shortDescription");
        registerTag("description", "description");
        registerTag("issueTrackingUrl", "issueTrackingUrl");
        registerTag("siteAddress", "siteAddress");
        registerTag("siteDirectory", "siteDirectory");
        registerTag("distributionAddress", "distributionAddress");
        registerTag("distributionDirectory", "distributionDirectory");
        registerTag("sourceDirectory", "build/sourceDirectory");
        registerTag("aspectSourceDirectory", "build/aspectSourceDirectory");
        registerTag("unitTestSourceDirectory", "build/unitTestSourceDirectory");

        //
        //make sure we reset our parent reference if the "extend" tag changes
        //
        addPropertyChangeListener("extend", new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent pEvent) {
                if ("extend".equals(pEvent.getPropertyName()))
                    resetParent();
            }
        });
    }

    /**
     * Returns the PSI parent project. <p/> <p>This method will parse the "extend" tag value, and
     * use it to find the parent POM file (relative path for this POM file is supported). If the
     * parent POM project is found, a PSI project is created for it and returned. </p> <p/>
     * <p><b>NOTE</b>: this is not a bound property. To track parent changes - track the "extend"
     * property.</p>
     *
     * @return a PSI project for the parent POM, or {@code null} if the extend tag has an empty
     *         value, or if the parent POM cannot be found/parsed.
     */
    public PsiProject getParent() {
        if (!searchedForParent) {
            parent = createParent();
            searchedForParent = true;
        }
        return parent;
    }

    public String getPomVersion() {
        return getValue("pomVersion");
    }

    public void setPomVersion(final String pValue) throws IncorrectOperationException {
        setValue("pomVersion", pValue);
    }

    public String getExtend() {
        return getValue("extend");
    }

    public void setExtend(final String pValue) throws IncorrectOperationException {
        setValue("extend", pValue);
    }

    public String getName() {
        return getValue("name");
    }

    public void setName(final String pValue) throws IncorrectOperationException {
        setValue("name", pValue);
    }

    public String getCurrentVersion() {
        return getValue("currentVersion");
    }

    public void setCurrentVersion(final String pValue)
            throws IncorrectOperationException {
        setValue("currentVersion", pValue);
    }

    public String getArtifactId() {
        return getValue("artifactId");
    }

    public void setArtifactId(final String pValue) throws IncorrectOperationException {
        setValue("artifactId", pValue);
    }

    public String getGroupId() {
        return getValue("groupId");
    }

    public void setGroupId(final String pValue) throws IncorrectOperationException {
        setValue("groupId", pValue);
    }

    public String getUrl() {
        return getValue("url");
    }

    public void setUrl(final String pValue) throws IncorrectOperationException {
        setValue("url", pValue);
    }

    public String getLogoUri() {
        return getValue("logoUri");
    }

    public void setLogoUri(final String pValue) throws IncorrectOperationException {
        setValue("logoUri", pValue);
    }

    public String getInceptionYear() {
        return getValue("inceptionYear");
    }

    public void setInceptionYear(final String pValue) throws IncorrectOperationException {
        setValue("inceptionYear", pValue);
    }

    public String getPackageName() {
        return getValue("packageName");
    }

    public void setPackageName(final String pValue) throws IncorrectOperationException {
        setValue("packageName", pValue);
    }

    public String getShortDescription() {
        return getValue("shortDescription");
    }

    public void setShortDescription(final String pValue)
            throws IncorrectOperationException {
        setValue("shortDescription", pValue);
    }

    public String getDescription() {
        return getValue("description");
    }

    public void setDescription(final String pValue) throws IncorrectOperationException {
        setValue("description", pValue);
    }

    public String getIssueTrackingUrl() {
        return getValue("issueTrackingUrl");
    }

    public void setIssueTrackingUrl(final String pValue)
            throws IncorrectOperationException {
        setValue("issueTrackingUrl", pValue);
    }

    public String getDistributionDirectory() {
        return getValue("distributionDirectory");
    }

    public void setDistributionDirectory(final String pDistributionDirectory) {
        setValue("distributionDirectory", pDistributionDirectory);
    }

    public String getDistributionAddress() {
        return getValue("distributionAddress");
    }

    public void setDistributionAddress(final String pDistributionAddress) {
        setValue("distributionAddress", pDistributionAddress);
    }

    public String getSiteAddress() {
        return getValue("siteAddress");
    }

    public void setSiteAddress(final String pSiteAddress) {
        setValue("siteAddress", pSiteAddress);
    }

    public String getSiteDirectory() {
        return getValue("siteDirectory");
    }

    public void setSiteDirectory(final String pSiteDirectory) {
        setValue("siteDirectory", pSiteDirectory);
    }

    public String getSourceDirectory() {
        return getValue("sourceDirectory");
    }

    public void setSourceDirectory(final String pSourceDirectory) {
        setValue("sourceDirectory", pSourceDirectory);
    }

    public String getAspectSourceDirectory() {
        return getValue("aspectSourceDirectory");
    }

    public void setAspectSourceDirectory(final String pAspectSourceDirectory) {
        setValue("aspectSourceDirectory", pAspectSourceDirectory);
    }

    public String getUnitTestSourceDirectory() {
        return getValue("unitTestSourceDirectory");
    }

    public void setUnitTestSourceDirectory(final String pUnitTestSourceDirectory) {
        setValue("unitTestSourceDirectory", pUnitTestSourceDirectory);
    }

    public final PsiOrganization getOrganization() {
        if (organization == null)
            organization = new DefaultPsiOrganization(this);
        return organization;
    }

    public final PsiMailingLists getMailingLists() {
        if (mailingLists == null)
            mailingLists = new DefaultPsiMailingLists(this);
        return mailingLists;
    }

    public final PsiDependencies getDependencies() {
        if (dependencies == null)
            dependencies = new DefaultPsiDependencies(this);
        return dependencies;
    }

    public PsiDevelopers getDevelopers() {
        if (developers == null)
            developers = new DefaultPsiDevelopers(this);
        return developers;
    }

    public PsiContributors getContributors() {
        if (contributors == null)
            contributors = new DefaultPsiContributors(this);
        return contributors;
    }

    public PsiScmRepository getScmRepository() {
        if (scmRepository == null)
            scmRepository = new DefaultPsiScmRepository(this);
        return scmRepository;
    }

    public PsiScmBranches getScmBranches() {
        if (scmBranches == null)
            scmBranches = new DefaultPsiScmBranches(this);
        return scmBranches;
    }

    public PsiVersions getVersions() {
        if (versions == null)
            versions = new DefaultPsiVersions(this);
        return versions;
    }

    public PsiResources getResources() {
        if (mainResources == null)
            mainResources = new DefaultPsiMainResources(this);
        return mainResources;
    }

    public PsiResources getTestResources() {
        if (testResources == null)
            testResources = new DefaultPsiTestResources(this);
        return testResources;
    }

    public PsiReports getReports() {
        if (reports == null)
            reports = new DefaultPsiReports(this);
        return reports;
    }

    /**
     * Returns the file associated with the value of the "extend" tag. <p/> <p>If the value is
     * empty, or does not evaluate to an existing file, this method will return {@code null}.</p>
     *
     * @return a virtual file reference, or {@code null}
     * @todo parse and resolve the 'getExtend' result for "${xxx}" properties
     */
    private VirtualFile getParentFile() {
        final String extendPath = getExtend();
        if (extendPath == null)
            return null;

        VirtualFile file = xmlFile.getVirtualFile();
        if (file != null)
            file = file.getParent();

        if (file == null)
            return null;

        return file.findFileByRelativePath(extendPath);
    }

    /**
     * Creates a PSI project based on the result of the {@link #getParentFile()} method. <p/> <p>If
     * the {@link #getParentFile()} method returns {@code null}, this method will also return {@code
     * null}.</p>
     *
     * @return a PSI project or {@code null}
     */
    private PsiProject createParent() {
        final VirtualFile parentFile = getParentFile();
        if (parentFile == null)
            return null;

        final Project project = xmlFile.getProject();
        return new DefaultPsiProject(PsiUtils.findXmlFile(project, parentFile));
    }

    private void resetParent() {
        parent = null;
        searchedForParent = false;

        if (LOG.isTraceEnabled()) {
            final VirtualFile virtualFile = xmlFile.getVirtualFile();
            LOG.trace("Reseting parent for " + (virtualFile == null ? null : virtualFile.getPath()));
        }
    }

    public void dispose() {
    }
}
