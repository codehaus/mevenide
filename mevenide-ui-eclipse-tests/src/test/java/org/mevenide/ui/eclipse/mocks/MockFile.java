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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.mockobjects.MockObject;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com) 
 * @version $Id$
 */
public class MockFile 
	extends MockObject
	implements IFile {

    public String getCharsetFor(Reader reader) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceAttributes getResourceAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void revertModificationStamp(long value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
       
    }
	private IPath path;
	private IProject project;
	private InputStream in;
	private IWorkspace workspace;

	public MockFile() {
		super();
	}

	public void appendContents(
		InputStream source,
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void appendContents(
		InputStream source,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void create(
		InputStream source,
		boolean force,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void create(
		InputStream source,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void createLink(
		IPath localLocation,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void delete(
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public InputStream getContents() throws CoreException {
		return in;
	}

	public InputStream getContents(boolean force) throws CoreException {
		return in;
	}

	public int getEncoding() throws CoreException {
		return 0;
	}

	public IPath getFullPath() {
		return null;
	}

	public IFileState[] getHistory(IProgressMonitor monitor)
		throws CoreException {
		return null;
	}

	public String getName() {
		return null;
	}

	public boolean isReadOnly() {
		return false;
	}

	public void move(
		IPath destination,
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void setContents(
		InputStream source,
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void setContents(
		IFileState source,
		boolean force,
		boolean keepHistory,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void setContents(
		InputStream source,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public void setContents(
		IFileState source,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
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

	public IContainer getParent() {
		return null;
	}

	public String getPersistentProperty(QualifiedName key)
		throws CoreException {
		return null;
	}

	public IProject getProject() {
		return project;
	}

	public IPath getProjectRelativePath() {
		return path;
	}

	public IPath getRawLocation() {
		return path;
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		return null;
	}

	public int getType() {
		return 0;
	}

	public IWorkspace getWorkspace() {
		return workspace;
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

	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean contains(ISchedulingRule rule) {
		return false;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return false;
	}

	//////////////////////////////////////////////////////////////////////
	// Mock setup methods
	
	public void setupPathAndInputStream(URL resource) {
		this.path = new Path(resource.getFile());
		try {
			this.in = resource.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupProject(IProject theProject) {
		this.project = theProject;
	}
	
	public void setupWorkspace(IWorkspace ws) {
		this.workspace = ws;
	}
	
	
	public String getCharset() throws CoreException {
		return null;
	}
	
	public void setCharset(String arg0) throws CoreException {
		
	}
	/**
	 *
	 */

	public String getCharset(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 *
	 */

	public IContentDescription getContentDescription() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
}
