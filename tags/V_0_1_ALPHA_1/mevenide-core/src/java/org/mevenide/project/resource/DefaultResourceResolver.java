/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.project.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultResourceResolver implements IResourceResolver  {
	
	
	/**
	 * iterate ${pom.build.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
	public void mergeSimilarResources(Project project, Resource resource) {
		List resources = project.getBuild().getResources();
		
		prepareResource(resource, resources);
		
		project.getBuild().addResource(resource);
	}
	
	public void mergeSimilarUnitTestResources(Project project, Resource resource) {
		List resources = project.getBuild().getUnitTest().getResources();
	
		prepareResource(resource, resources);
		
		project.getBuild().getUnitTest().addResource(resource);
	}

	private void prepareResource(Resource resource, List resources) {
		List similar = getSimilarResources(resources, resource);
		
		for (int i = 0; i < similar.size(); i++) {
			Resource similarResource = (Resource) similar.get(i);
			mergeIncludes(resource, similarResource);
			mergeExcludes(resource, similarResource);
			resources.remove(similarResource);
		}
	}

	private void mergeExcludes(Resource resource, Resource similarResource) {
		for (int j = 0; j < similarResource.getExcludes().size(); j++) {
			if ( !resource.getExcludes().contains(similarResource.getExcludes().get(j)) ) {
				resource.getExcludes().add(similarResource.getExcludes().get(j));
			}
		}
	}

	private void mergeIncludes(Resource resource, Resource similarResource) {
		for (int j = 0; j < similarResource.getIncludes().size(); j++) {
			if ( !resource.getIncludes().contains(similarResource.getIncludes().get(j)) ) {
				resource.getIncludes().add(similarResource.getIncludes().get(j));
			}
		}
	}

	private List getSimilarResources(List resources, Resource resource) {
		List similar = new ArrayList();
		
		for (int i = 0; i < resources.size(); i++) {
			Resource declaredResource = (Resource) resources.get(i);
			if ( declaredResource.getDirectory().equals(resource.getDirectory()) ) {
				similar.add(declaredResource);
			}
		}
		return similar;
	}
	
}
