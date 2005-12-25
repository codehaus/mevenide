/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.mocks;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
//import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
//import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.mockobjects.MockObject;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com) 
 * @version $Id$
 */
public class MockProject
	extends MockObject
	implements IProject {

//    public ResourceAttributes getResourceAttributes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public void revertModificationStamp(long value) throws CoreException {
		// TODO Auto-generated method stub
		
	}
//	public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}
//	public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public void open(int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
    }
	public IPath getWorkingLocation(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getDefaultCharset(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	private String projectName;
	
	public MockProject(String name) {
		this.projectName = name;
	}

	public void build(
		int kind,
		String builderName,
		Map args,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void build(int kind, IProgressMonitor monitor)
		throws CoreException {
	}

	public void close(IProgressMonitor monitor) throws CoreException {
	}

	public void create(
		IProjectDescription description,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void create(IProgressMonitor monitor) throws CoreException {
	}

	public void delete(
		boolean deleteContent,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public IProjectDescription getDescription() throws CoreException {
		return null;
	}

	public IFile getFile(String name) {
		return null;
	}

	public IFolder getFolder(String name) {
		return null;
	}

	public IProjectNature getNature(String natureId) throws CoreException {
		return null;
	}

	public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
		return null;
	}

	public IProject[] getReferencedProjects() throws CoreException {
		return null;
	}

	public IProject[] getReferencingProjects() {
		return null;
	}

	public boolean hasNature(String natureId) throws CoreException {
		return false;
	}

	public boolean isNatureEnabled(String natureId) throws CoreException {
		return false;
	}

	public boolean isOpen() {
		return false;
	}

	public void move(
		IProjectDescription description,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void open(IProgressMonitor monitor) throws CoreException {
	}

	public void setDescription(
		IProjectDescription description,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void setDescription(
		IProjectDescription description,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public boolean exists(IPath path) {
		return false;
	}

	public IResource findMember(String name) {
		return null;
	}

	public IResource findMember(String name, boolean includePhantoms) {
		return null;
	}

	public IResource findMember(IPath path) {
		return null;
	}

	public IResource findMember(IPath path, boolean includePhantoms) {
		return null;
	}

	public IFile getFile(IPath path) {
		return null;
	}

	public IFolder getFolder(IPath path) {
		return null;
	}

	public IResource[] members() throws CoreException {
		return null;
	}

	public IResource[] members(boolean includePhantoms) throws CoreException {
		return null;
	}

	public IResource[] members(int memberFlags) throws CoreException {
		return null;
	}

	public IFile[] findDeletedMembersWithHistory(
		int depth,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags)
		throws CoreException {
	}

	public void accept(IResourceVisitor visitor) throws CoreException {
	}

	public void accept(
		IResourceVisitor visitor,
		int depth,
		boolean includePhantoms)
		throws CoreException {
	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
		throws CoreException {
	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {
	}

	public void copy(
		IPath destination,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void copy(
		IPath destination,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void copy(
		IProjectDescription description,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void copy(
		IProjectDescription description,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public IMarker createMarker(String type) throws CoreException {
		return null;
	}

	public void delete(boolean force, IProgressMonitor monitor)
		throws CoreException {
	}

	public void delete(int updateFlags, IProgressMonitor monitor)
		throws CoreException {
	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth)
		throws CoreException {
	}

	public boolean exists() {
		return false;
	}

	public IMarker findMarker(long id) throws CoreException {
		return null;
	}

	public IMarker[] findMarkers(
		String type,
		boolean includeSubtypes,
		int depth)
		throws CoreException {
		return null;
	}

	public String getFileExtension() {
		return null;
	}

	public IPath getFullPath() {
		return null;
	}

	public long getLocalTimeStamp() {
		return 0;
	}

	public IPath getLocation() {
		return null;
	}

	public IMarker getMarker(long id) {
		return null;
	}

	public long getModificationStamp() {
		return 0;
	}

	public String getName() {
		return projectName;
	}

	public IContainer getParent() {
		return null;
	}

	public String getPersistentProperty(QualifiedName key)
		throws CoreException {
		return null;
	}

	public IProject getProject() {
		return null;
	}

	public IPath getProjectRelativePath() {
		return null;
	}

	public IPath getRawLocation() {
		return null;
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		return null;
	}

	public int getType() {
		return 0;
	}

	public IWorkspace getWorkspace() {
		return null;
	}

	public boolean isAccessible() {
		return false;
	}

	public boolean isDerived() {
		return false;
	}

	public boolean isLocal(int depth) {
		return false;
	}

	public boolean isLinked() {
		return false;
	}

	public boolean isPhantom() {
		return false;
	}

	public boolean isReadOnly() {
		return false;
	}

	public boolean isSynchronized(int depth) {
		return false;
	}

	public boolean isTeamPrivateMember() {
		return false;
	}

	public void move(
		IPath destination,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void move(
		IPath destination,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void move(
		IProjectDescription description,
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void move(
		IProjectDescription description,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void refreshLocal(int depth, IProgressMonitor monitor)
		throws CoreException {
	}

	public void setDerived(boolean isDerived) throws CoreException {
	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
		throws CoreException {
	}

	public long setLocalTimeStamp(long value) throws CoreException {
		return 0;
	}

	public void setPersistentProperty(QualifiedName key, String value)
		throws CoreException {
	}

	public void setReadOnly(boolean readOnly) {
	}

	public void setSessionProperty(QualifiedName key, Object value)
		throws CoreException {
	}

	public void setTeamPrivateMember(boolean isTeamPrivate)
		throws CoreException {
	}

	public void touch(IProgressMonitor monitor) throws CoreException {
	}

	public boolean contains(ISchedulingRule rule) {
		return false;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return false;
	}
	
	
	public String getDefaultCharset() throws CoreException {
		return null;
	}
	public void setDefaultCharset(String arg0) throws CoreException {
	}
}
