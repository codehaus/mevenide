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

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ISynchronizer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.WorkspaceLock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.mockobjects.MockObject;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com) 
 * @version $Id$
 */
public class MockWorkspace 
	extends MockObject
	implements IWorkspace {

	public IProjectDescription loadProjectDescription(InputStream projectDescriptionFile) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public MockWorkspace() {
		super();
	}

	public void addResourceChangeListener(IResourceChangeListener listener) {
	}

	public void addResourceChangeListener(
		IResourceChangeListener listener,
		int eventMask) {
	}

	public ISavedState addSaveParticipant(
		Plugin plugin,
		ISaveParticipant participant)
		throws CoreException {
		return null;
	}

	public void build(int kind, IProgressMonitor monitor)
		throws CoreException {
	}

	public void checkpoint(boolean build) {
	}

	public IProject[][] computePrerequisiteOrder(IProject[] projects) {
		return null;
	}

	public ProjectOrder computeProjectOrder(IProject[] projects) {
		return null;
	}

	public IStatus copy(
		IResource[] resources,
		IPath destination,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public IStatus copy(
		IResource[] resources,
		IPath destination,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public IStatus delete(
		IResource[] resources,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public IStatus delete(
		IResource[] resources,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public void deleteMarkers(IMarker[] markers) throws CoreException {
	}

	public void forgetSavedTree(String pluginId) {
	}

	public IProjectNatureDescriptor[] getNatureDescriptors() {
		return null;
	}

	public IProjectNatureDescriptor getNatureDescriptor(String natureId) {
		return null;
	}

	public Map getDanglingReferences() {
		return null;
	}

	public IWorkspaceDescription getDescription() {
		return null;
	}

	public IWorkspaceRoot getRoot() {
		return null;
	}

	public ISynchronizer getSynchronizer() {
		return null;
	}

	public boolean isAutoBuilding() {
		return false;
	}

	public boolean isTreeLocked() {
		return false;
	}

	public IProjectDescription loadProjectDescription(IPath projectDescriptionFile)
		throws CoreException {
		return null;
	}

	public IStatus move(
		IResource[] resources,
		IPath destination,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public IStatus move(
		IResource[] resources,
		IPath destination,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public IProjectDescription newProjectDescription(String projectName) {
		return null;
	}

	public void removeResourceChangeListener(IResourceChangeListener listener) {
	}

	public void removeSaveParticipant(Plugin plugin) {
	}

	public void run(
		IWorkspaceRunnable action,
		ISchedulingRule rule,
		int flags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void run(
		IWorkspaceRunnable action,
		ISchedulingRule rule,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void run(IWorkspaceRunnable action, IProgressMonitor monitor)
		throws CoreException {
	}

	public IStatus save(boolean full, IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public void setDescription(IWorkspaceDescription description)
		throws CoreException {
	}

	/**
	 * @deprecated
	 * @see org.eclipse.core.resources.IWorkspace#setWorkspaceLock(org.eclipse.core.resources.WorkspaceLock)
	 */
	public void setWorkspaceLock(WorkspaceLock lock) {
	}

	public String[] sortNatureSet(String[] natureIds) {
		return null;
	}

	public IStatus validateEdit(IFile[] files, Object context) {
		return null;
	}

	public IStatus validateLinkLocation(IResource resource, IPath location) {
		return null;
	}

	public IStatus validateName(String segment, int typeMask) {
		return null;
	}

	public IStatus validateNatureSet(String[] natureIds) {
		return null;
	}

	public IStatus validatePath(String path, int typeMask) {
		return null;
	}

	public IStatus validateProjectLocation(IProject project, IPath location) {
		return null;
	}

	public IPathVariableManager getPathVariableManager() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public IResourceRuleFactory getRuleFactory() {
		return null;
	}
}
