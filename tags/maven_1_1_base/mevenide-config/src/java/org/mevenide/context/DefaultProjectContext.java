/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.DefaultJDOMFactory;
import org.jdom.JDOMFactory;
import org.mevenide.properties.IPropertyResolver;


/**
 * default implementation of IProjectContext
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 *
 */
class DefaultProjectContext implements IProjectContext {
    private static final Log logger = LogFactory.getLog(DefaultProjectContext.class);
    
    private IQueryErrorCallback callback;
    private Project mergedProject;
    private List projects;
    private List projectFiles;
    private List projectTimestamps;
    private Element mergedJDomRoot;
    private List jdomRootElements;
    
    private Object LOCK = new Object();
    private JDomProjectUnmarshaller unmarshaller;
    private IPropertyResolver propResolver;
    private DefaultQueryContext queryContext;
    
    public DefaultProjectContext(DefaultQueryContext context, IPropertyResolver resolver, IQueryErrorCallback errorCallback) {
        callback = errorCallback;
        unmarshaller = new JDomProjectUnmarshaller();
        propResolver = resolver;
        queryContext = context;
        projectTimestamps = new ArrayList();
        projectFiles = new ArrayList();
        projects = new ArrayList();
        jdomRootElements = new ArrayList();
        if (context.getProjectDirectory() == null) {
            throw new IllegalStateException("Cannot initialize ProjectContext without a project-based Querycontext");
        }
        synchronized (LOCK) {
            reloadData();
        }
    }
    
    // shall be run in synchronized lock.
    private void reloadData() {
        projectFiles.clear();
        projectTimestamps.clear();
        projects.clear();
        jdomRootElements.clear();
        mergedProject = new Project();
        mergedJDomRoot = factory.element("project"); // blank initial root

        File rootFile = new File(queryContext.getProjectDirectory(), "project.xml");
        readProject(rootFile, 1);

        for (int i = 0; i < this.jdomRootElements.size(); ++i) {
            Element root = (Element)this.jdomRootElements.get(i);
            File    file = (File)this.projectFiles.get(i);

            this.mergedJDomRoot = mergeProjectDOMs(this.mergedJDomRoot, root);

            Project project = this.unmarshaller.generateProject(root);
            project.setFile(file);
            this.projects.add(project);
        }

        this.mergedProject = this.unmarshaller.generateProject(this.mergedJDomRoot);
        this.mergedProject.setFile(rootFile);
    }
    
    public Project getFinalProject() {
        synchronized (LOCK) {
            checkTimeStamps();
            return mergedProject;
        }
    }
    
    public File[] getProjectFiles() {
        File[] fls;
        synchronized (LOCK) {
            checkTimeStamps();
            fls = new File[projectFiles.size()];
            fls = (File[])projectFiles.toArray(fls);
        }
        return fls;
    }
    
    public int getProjectDepth() {
        File[] fls = getProjectFiles();
        return fls.length;
    }
    
    public Project[] getProjectLayers() {
        Project[] prjs;
        synchronized (LOCK) {
            checkTimeStamps();
            prjs = new Project[projects.size()];
            prjs = (Project[])projects.toArray(prjs);
        }
        return prjs;
    }
    
    public Element[] getRootElementLayers() {
        Element[] prjs;
        synchronized (LOCK) {
            checkTimeStamps();
            prjs = new Element[jdomRootElements.size()];
            prjs = (Element[])jdomRootElements.toArray(prjs);
        }
        return prjs;
    }
    
    public Element getRootProjectElement() {
        synchronized (LOCK) {
            checkTimeStamps();
            return mergedJDomRoot;
        }
    }
    
    
    // shall be run in synchronized lock.
    private void checkTimeStamps() {
        for (int i = 0; i < projectFiles.size(); i++) {
            File file = (File)projectFiles.get(i);
            Long stamp = (Long)projectTimestamps.get(i);
            if (file.lastModified() > stamp.longValue()) {
                //TODO don't delete all, but only current and succeeding..
                reloadData();
            }
        }
    }
    
    // shall be run in synchronized lock.
    private void readProject(File file, int level) {
        logger.debug("readproject=" + file);
        if (file.exists()) {
            queryContext.initLevel(level, file.getParentFile());
            callback.discardError(IQueryErrorCallback.ERROR_CANNOT_FIND_POM);
            Element proj;
            try {
                proj = unmarshaller.parseRootElement(file);
            } catch (Exception exc) {
                JDOMFactory fact = new DefaultJDOMFactory();
                jdomRootElements.add(fact.element("project"));
                projectFiles.add(file);
                projectTimestamps.add(new Long(file.lastModified()));
                callback.handleError(IQueryErrorCallback.ERROR_UNPARSABLE_POM, exc);
                return;
            }
            if (proj != null) {
                jdomRootElements.add(proj);
                projectFiles.add(file);
                projectTimestamps.add(new Long(file.lastModified()));
                String extend = proj.getChildText("extend");
                callback.discardError(IQueryErrorCallback.ERROR_UNPARSABLE_POM);
                if (extend != null) {
                    extend = doReplaceExtend(file.getParentFile(), propResolver, extend);
                    File absolute = new File(extend);
                    absolute = JDomProjectUnmarshaller.normalizeFile(absolute);
                    if (absolute.exists() && (!absolute.equals(file))) {
                        callback.discardError(IQueryErrorCallback.ERROR_CANNOT_FIND_PARENT_POM);
                        readProject(absolute, level + 1);
                    } else {
                        File relative = new File(queryContext.getProjectDirectory(), extend);
                        relative = JDomProjectUnmarshaller.normalizeFile(relative);
                        if (relative.exists() && (!relative.equals(file))) {
                            callback.discardError(IQueryErrorCallback.ERROR_CANNOT_FIND_PARENT_POM);
                            readProject(relative, level + 1);
                        } else {
                                //throw new IllegalStateException("Cannot read parent.(" + extend + ")" );
                            callback.handleError(IQueryErrorCallback.ERROR_CANNOT_FIND_PARENT_POM, 
                                   new FileNotFoundException("Cannot find <extend> file:" + extend));
                        }
                    }
                }
            }
        } else {
            callback.handleError(IQueryErrorCallback.ERROR_CANNOT_FIND_POM, 
                       new FileNotFoundException("Cannot find project.xml file:" + file.getAbsolutePath()));
        }
    }

    private static Pattern pattern = Pattern.compile(".*\\$\\{([a-zA-Z0-9_\\-\\.]*)\\}.*"); //NOI18N
    
    static String doReplaceExtend(File basedir, IPropertyResolver resolver, String extend) {
        if (extend.indexOf("${") > -1) { //NOI18N
            int index = extend.indexOf("${basedir}"); //NOI18N
            if (index > -1) {
                extend = extend.substring(0, index) + basedir.getAbsolutePath() + extend.substring(index + "${basedir}".length());  //NOI18N
            }
            Matcher match = pattern.matcher(extend);
            if (match.matches()) {
                String gr1 = match.group(1);
                String replace = resolver.getValue(gr1);
                if (replace != null) {
                    index = extend.indexOf(gr1);
                    extend = extend.substring(0, index - 2) + replace + extend.substring(index + gr1.length() + 1);
                    extend = doReplaceExtend(basedir, resolver, extend);
                } else {
                    // cannot resolve, just exit..
                    return extend;
                }
            } else {
                // wrong parenthesis now, doens't match pattern, just exit.
                // MEVENIDE-267
                return extend;
            }
        }
        return extend;
    }
    
    //##########################################################################
    // project merge related
    //##########################################################################    
    private static JDOMFactory factory = new DefaultJDOMFactory();
    
    private void mergeSimpleElement(String name, Element primaryParent, Element secondaryParent, Element resultParent) {
        String primary = primaryParent != null ? primaryParent.getChildText(name) : null;
        String secondary = secondaryParent != null ? secondaryParent.getChildText(name) : null;
        String source = (primary != null ? primary : secondary);
        if (source != null) {
            Element toReturn = factory.element(name);
            toReturn.addContent(factory.text(source));
            resultParent.addContent(toReturn);
        }
    }
    
    private void mergeSubtree(String name, Element primaryParent, Element secondaryParent, Element resultParent) {
        Element primary = (primaryParent != null ? primaryParent.getChild(name) : null);
        Element secondary = (secondaryParent != null ? secondaryParent.getChild(name) : null);
        Element source = (primary != null ? primary : secondary);
        if (source != null) {
            Element toReturnRoot = copyTree(source);
            resultParent.addContent(toReturnRoot);
        }
    }
    
    private Element copyTree(Element source) {
        Element toReturn = factory.element(source.getName());
        Iterator it = source.getContent().iterator();
        while (it.hasNext()) {
            Object content = it.next();
            if (content instanceof Element) {
                toReturn.addContent(copyTree((Element)content));
            }
            if (content instanceof Text) {
                Text text = (Text)content;
                toReturn.addContent(factory.text(text.getText()));
            }
        }
        return toReturn;
    }

    
    private Element mergeProjectDOMs(Element primary, Element secondary) {
        Element toReturn = factory.element("project");
        if (primary != null && secondary != null) {
            mergeSimpleElement("extend", primary, secondary, toReturn);
            mergeSimpleElement("pomVersion", primary, secondary, toReturn);
            mergeSimpleElement("id", primary, secondary, toReturn);
            mergeSimpleElement("groupId", primary, secondary, toReturn);
            mergeSimpleElement("artifactId", primary, secondary, toReturn);
            mergeSimpleElement("name", primary, secondary, toReturn);
            mergeSimpleElement("currentVersion", primary, secondary, toReturn);
            mergeSubtree("organization", primary, secondary, toReturn);
            mergeSimpleElement("inceptionYear", primary, secondary, toReturn);
            mergeSimpleElement("package", primary, secondary, toReturn);
            mergeSimpleElement("logo", primary, secondary, toReturn);
            mergeSimpleElement("gumpRepositoryId", primary, secondary, toReturn);
            mergeSimpleElement("description", primary, secondary, toReturn);
            mergeSimpleElement("shortDescription", primary, secondary, toReturn);
            mergeSimpleElement("url", primary, secondary, toReturn);
            mergeSimpleElement("issueTrackingUrl", primary, secondary, toReturn);
            mergeSimpleElement("siteAddress", primary, secondary, toReturn);
            mergeSimpleElement("siteDirectory", primary, secondary, toReturn);
            mergeSimpleElement("distributionSite", primary, secondary, toReturn);
            mergeSimpleElement("distributionDirectory", primary, secondary, toReturn);
            mergeSubtree("repository", primary, secondary, toReturn);
            mergeSubtree("versions", primary, secondary, toReturn);
            mergeSubtree("branches", primary, secondary, toReturn);
            mergeSubtree("mailingLists", primary, secondary, toReturn);
            mergeSubtree("developers", primary, secondary, toReturn);
            mergeSubtree("contributors", primary, secondary, toReturn);
            mergeSubtree("licenses", primary, secondary, toReturn);
            mergeDependencies(primary, secondary, toReturn);
            mergeBuild(primary, secondary, toReturn);
            mergeSubtree("reports", primary, secondary, toReturn);
            mergeProperties(primary, secondary, toReturn);
        } else {
            throw new IllegalStateException("Shall not call method with null params.");
        }
        return toReturn;
    }
    
    /**
     * truly merging dependencies.
     */
    private void mergeDependencies(Element primary, Element secondary, Element toReturn) {
        Element primarySource = (primary != null ? primary.getChild("dependencies") : null);
        Element secondarySource = (secondary != null ? secondary.getChild("dependencies") : null);
        if (primarySource == null && secondarySource == null) {
            return;
        }
        Element root = factory.element("dependencies");
        if (primarySource != null) {
            Iterator it = primarySource.getChildren("dependency").iterator();
            while (it.hasNext()) {
                Element dependency = (Element)it.next();
                root.addContent(copyTree(dependency));
            }
        }
        if (secondarySource != null) {
            Iterator it = secondarySource.getChildren("dependency").iterator();
            while (it.hasNext()) {
                Element dependency = (Element)it.next();
                root.addContent(copyTree(dependency));
            }
        }
        toReturn.addContent(root);
    }
    
    private void mergeBuild(Element primary, Element secondary, Element toReturn) {
        Element primarySource = primary.getChild("build");
        Element secondarySource = secondary.getChild("build");
        if (primarySource == null && secondarySource == null) {
            return;
        }
        Element root = factory.element("build");
        mergeSimpleElement("nagEmailAddress", primarySource, secondarySource, root);
        mergeSimpleElement("sourceDirectory", primarySource, secondarySource, root);
        mergeSimpleElement("aspectSourceDirectory", primarySource, secondarySource, root);
        mergeSimpleElement("unitTestSourceDirectory", primarySource, secondarySource, root);
        mergeSimpleElement("integrationUnitTestSourceDirectory", primarySource, secondarySource, root);
        mergeSourceModifications(primarySource, secondarySource, root);
        // TODO no idea here.. merge or select one?
        mergeSubtree("unitTest", primarySource, secondarySource, root);
        mergeSubtree("resources", primarySource, secondarySource, root);
        toReturn.addContent(root);
    }
    
   /**
     * truly merging dependencies.
     */
    private void mergeSourceModifications(Element primary, Element secondary, Element toReturn) {
        Element primarySource = (primary != null ? primary.getChild("sourceModifications") : null);
        Element secondarySource = (secondary != null ? secondary.getChild("sourceModifications") : null);
        if (primarySource == null && secondarySource == null) {
            return;
        }
        Element root = factory.element("sourceModifications");
        if (primarySource != null) {
            Iterator it = primarySource.getChildren("sourceModification").iterator();
            while (it.hasNext()) {
                Element dependency = (Element)it.next();
                root.addContent(copyTree(dependency));
            }
        }
        if (secondarySource != null) {
            Iterator it = secondarySource.getChildren("sourceModification").iterator();
            while (it.hasNext()) {
                Element dependency = (Element)it.next();
                root.addContent(copyTree(dependency));
            }
        }
    }    

    /**
     * truly merging properties.
     */
    private void mergeProperties(Element primary, Element secondary, Element toReturn) {
        Element primarySource = (primary != null ? primary.getChild("properties") : null);
        Element secondarySource = (secondary != null ? secondary.getChild("properties") : null);
        if (primarySource == null && secondarySource == null) {
            return;
        }
        Element root = factory.element("properties");
        if (primarySource != null) {
            Iterator it = primarySource.getChildren().iterator();
            while (it.hasNext()) {
                Element prop = (Element)it.next();
                root.addContent(copyTree(prop));
            }
        }
        if (secondarySource != null) {
            Iterator it = secondarySource.getChildren("").iterator();
            while (it.hasNext()) {
                Element prop = (Element)it.next();
                root.addContent(copyTree(prop));
            }
        }
    }
    
	/**
	 * copied from mevenide-core.. cannot reference, but I didn't want to move MevenideUtil to mevenide-config
	 * 
     * Convert an absolute path to a relative path if it is under a given base directory.
     * @param basedir the base directory for relative paths
     * @param path the directory to resolve
     * @return the relative path
     * @throws IOException if canonical path fails
     */
    static String makeRelativePath( File basedir, String path ) throws IOException {
        String canonicalBasedir = basedir.getCanonicalPath();
        String canonicalPath = new File( path ).getCanonicalPath();

        if ( canonicalPath.equals(canonicalBasedir) ) {
            return ".";
        }

        if ( canonicalPath.startsWith( canonicalBasedir ) ) {
            if ( canonicalPath.charAt( canonicalBasedir.length() ) == File.separatorChar ) {
                canonicalPath = canonicalPath.substring( canonicalBasedir.length() + 1 );
            }
            else {
                canonicalPath = canonicalPath.substring( canonicalBasedir.length() );
            }
        }
        return canonicalPath;
    }    
 
}