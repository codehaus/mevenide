package org.mevenide.idea.project.goals;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;

/**
 * @author Arik
 */
public abstract class AbstractGoalContainer<GoalType extends Goal>
        implements GoalContainer, VirtualFilePointerListener {
    private String id;
    private String artifactId;
    private String groupId;
    private String version;
    private String name;
    private String description;
    private GoalType[] goals;
    private VirtualFilePointer scriptFile;
    private VirtualFilePointer pomFile;

    public String getId() {
        if (id != null && id.trim().length() > 0)
            return id;

        if(groupId == null)
            return artifactId;

        if(artifactId == null)
            return groupId;
        
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

    public GoalType[] getGoals() {
        return goals;
    }

    public void setGoals(final GoalType[] pGoals) {
        goals = pGoals;
    }

    public GoalType getGoal(String pName) {
        for (GoalType goalInfo : goals) {
            if (goalInfo.getName().equals(pName))
                return goalInfo;
        }

        return null;
    }

    public VirtualFilePointer getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(final VirtualFile pScriptFilePointer) {
        final VirtualFilePointerManager mgr = VirtualFilePointerManager.getInstance();
        final VirtualFilePointer pointer = mgr.create(pScriptFilePointer, this);
        setScriptFile(pointer);
    }

    public void setScriptFile(final VirtualFilePointer pScriptFilePointer) {
        scriptFile = pScriptFilePointer;
    }

    public VirtualFilePointer getPomFile() {
        return pomFile;
    }

    public void setPomFile(final VirtualFile pPomFilePointer) {
        final VirtualFilePointerManager mgr = VirtualFilePointerManager.getInstance();
        final VirtualFilePointer pointer = mgr.create(pPomFilePointer, this);
        setPomFile(pointer);
    }

    public void setPomFile(final VirtualFilePointer pPomFilePointer) {
        pomFile = pPomFilePointer;
    }

    public void beforeValidityChanged(VirtualFilePointer[] pointers) {
    }

    public void validityChanged(VirtualFilePointer[] pointers) {
    }

    public boolean equals(final Object pValue) {
        if (this == pValue) return true;
        if (pValue == null || getClass() != pValue.getClass()) return false;

        final AbstractGoalContainer that = (AbstractGoalContainer) pValue;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(version != null ? !version.equals(that.version) : that.version != null);
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 29 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 29 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 29 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
