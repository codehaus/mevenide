package org.mevenide.idea.project.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;

/**
 * @author Arik
 */
public class DefaultPluginInfo implements PluginInfo {
    private String id;
    private String artifactId;
    private String groupId;
    private String version;
    private String name;
    private String description;
    private GoalInfo[] goals;
    private VirtualFilePointer scriptFilePointer;
    private VirtualFilePointer pomFilePointer;

    public String getId() {
        if (id != null && id.trim().length() > 0)
            return id;

        return groupId + ":" + artifactId;
    }

    public void setId(final String pId) {
        id = pId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String pArtifactId) {
        artifactId = pArtifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String pGroupId) {
        groupId = pGroupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String pVersion) {
        version = pVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(final String pName) {
        name = pName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String pDescription) {
        description = pDescription;
    }

    public GoalInfo[] getGoals() {
        return goals;
    }

    public void setGoals(final GoalInfo[] pGoals) {
        goals = pGoals;
    }

    public GoalInfo getGoal(String pName) {
        for (GoalInfo goalInfo : goals) {
            if (goalInfo.getName().equals(pName))
                return goalInfo;
        }

        return null;
    }

    public VirtualFile getScriptFile() {
        if (scriptFilePointer == null || !scriptFilePointer.isValid())
            return null;
        else
            return scriptFilePointer.getFile();
    }

    public void setScriptFile(final VirtualFile pScriptFile) {
        if (pScriptFile == null)
            setScriptFile((VirtualFilePointer) null);
        else {
            final String url = pScriptFile.getUrl();
            final VirtualFilePointerManager vfpMgr = VirtualFilePointerManager.getInstance();
            setScriptFile(vfpMgr.create(url, null));
        }
    }

    public void setScriptFile(final VirtualFilePointer pScriptFilePointer) {
        scriptFilePointer = pScriptFilePointer;
    }

    public VirtualFile getPomFile() {
        if (pomFilePointer == null || !pomFilePointer.isValid())
            return null;
        else
            return pomFilePointer.getFile();
    }

    public void setPomFile(final VirtualFile pPomFile) {
        if (pPomFile == null)
            setPomFile((VirtualFilePointer) null);
        else {
            final String url = pPomFile.getUrl();
            final VirtualFilePointerManager vfpMgr = VirtualFilePointerManager.getInstance();
            setPomFile(vfpMgr.create(url, null));
        }
    }

    public void setPomFile(final VirtualFilePointer pPomFilePointer) {
        pomFilePointer = pPomFilePointer;
    }
}
