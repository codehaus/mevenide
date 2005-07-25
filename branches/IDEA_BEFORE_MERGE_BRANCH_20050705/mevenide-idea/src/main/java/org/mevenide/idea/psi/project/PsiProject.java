package org.mevenide.idea.psi.project;

import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.PropertyObservable;

/**
 * @author Arik
 */
public interface PsiProject extends PropertyObservable, XmlPsiObject, PsiChild<PsiProject> {
    PsiProject getParent();

    String getPomVersion();

    void setPomVersion(String pValue) throws IncorrectOperationException;

    String getExtend();

    void setExtend(String pValue) throws IncorrectOperationException;

    String getName();

    void setName(String pValue) throws IncorrectOperationException;

    String getCurrentVersion();

    void setCurrentVersion(String pValue)
            throws IncorrectOperationException;

    String getArtifactId();

    void setArtifactId(String pValue) throws IncorrectOperationException;

    String getGroupId();

    void setGroupId(String pValue) throws IncorrectOperationException;

    String getUrl();

    void setUrl(String pValue) throws IncorrectOperationException;

    String getLogoUri();

    void setLogoUri(String pValue) throws IncorrectOperationException;

    String getInceptionYear();

    void setInceptionYear(String pValue) throws IncorrectOperationException;

    String getPackageName();

    void setPackageName(String pValue) throws IncorrectOperationException;

    String getShortDescription();

    void setShortDescription(String pValue)
            throws IncorrectOperationException;

    String getDescription();

    void setDescription(String pValue) throws IncorrectOperationException;

    String getIssueTrackingUrl();

    void setIssueTrackingUrl(String pValue)
            throws IncorrectOperationException;

    String getDistributionDirectory();

    void setDistributionDirectory(String pDistributionDirectory);

    String getDistributionAddress();

    void setDistributionAddress(String pDistributionAddress);

    String getSiteAddress();

    void setSiteAddress(String pSiteAddress);

    String getSiteDirectory();

    void setSiteDirectory(String pSiteDirectory);

    String getSourceDirectory();

    void setSourceDirectory(String pSourceDirectory);

    String getAspectSourceDirectory();

    void setAspectSourceDirectory(String pAspectSourceDirectory);

    String getUnitTestSourceDirectory();

    void setUnitTestSourceDirectory(String pUnitTestSourceDirectory);

    PsiOrganization getOrganization();

    PsiMailingLists getMailingLists();

    PsiDependencies getDependencies();

    PsiDevelopers getDevelopers();

    PsiContributors getContributors();

    PsiScmRepository getScmRepository();

    PsiScmBranches getScmBranches();

    PsiVersions getVersions();

    PsiResources getResources();

    PsiResources getTestResources();

    PsiReports getReports();
}
