/*
 * MavenRepoLibraryProvider.java
 *
 * Created on April 7, 2004, 6:15 PM
 */

package org.mevenide.netbeans.project.libraries;

import java.beans.Customizer;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.project.dependency.DependencyResolverFactory;
import org.mevenide.project.dependency.IDependencyResolver;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenRepoLibraryProvider implements LibraryProvider {
    private static final Log logger = LogFactory.getLog(MavenRepoLibraryProvider.class);
    
    public static final String TYPE = "MavenRepository"; //NOI18N
    public static final String VOLUME_TYPE_CLASSPATH = "classpath";       //NOI18N

    private PropertyChangeSupport support;
    private ILocationFinder finder;
    private File locRepoFile;
    /** Creates a new instance of MavenRepoLibraryProvider */
    public MavenRepoLibraryProvider() {
        support = new PropertyChangeSupport(this);
        locRepoFile = findRepo();
        logger.debug("created instance");
        logger.debug("repo=" + locRepoFile);
    }
    
//    public static MavenRepoLibraryProvider createInstance() {
//        return new MavenRepoLibraryProvider();
//    }
    
    public void setMavenContext(ILocationFinder locFinder) {
        finder = locFinder;
        File newRepo = findRepo();
        if (newRepo != null && (!newRepo.equals(locRepoFile))) {
            locRepoFile = newRepo;
            fireLibrariesChanged();
        }
    }
    
    private File findRepo() {
        if (finder == null) {
            finder = new LocationFinderAggregator();
        }
        File toReturn = new File(finder.getMavenLocalRepository());
        return toReturn;
    }
    
    private void fireLibrariesChanged() {
        support.firePropertyChange(PROP_LIBRARIES, null, null);
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
    }
    
    public LibraryImplementation[] getLibraries() {
        logger.debug("getLibraries");
        File obj = locRepoFile;
        if (obj != null) {
            Set toReturn = new HashSet();
            FileObject[] fos = FileUtil.fromFile(obj);
            if (fos != null && fos.length > 0) {
                FileObject root = fos[0];
                FileObject[] groups = root.getChildren();
                if (groups != null && groups.length > 0) {
                    for (int i = 0; i < groups.length; i++) {
                        processGroup(groups[i], toReturn);
                    }
                }
            }
            LibraryImplementation[] impls = new LibraryImplementation[toReturn.size()];
            impls = (LibraryImplementation[])toReturn.toArray(impls);
            return impls;
        }
        return new LibraryImplementation[0];
    }
    
    private void processGroup(FileObject fo, Set libraries) {
        logger.debug("processGroup:" + fo.getName());
        FileObject[] types = fo.getChildren();
        if (types != null && types.length > 0) {
            for (int i = 0; i < types.length; i++) {
                if (types[i].isFolder()) {
                    String type = types[i].getName();
                    if (type.length() > 1 && type.endsWith("s")) {
                        type = type.substring(0, type.length() - 1);
                    }
                    processType(types[i], type, libraries);
                }
            }
        }
    }
    
    private void processType(FileObject type, String typStr, Set libraries) {
        logger.debug("processType" + type.getName() + " typ=" + typStr);
        FileObject[] artifacts = type.getChildren();
        if (artifacts != null && artifacts.length > 0) {
            for (int i = 0; i < artifacts.length; i++) {
                logger.debug("Artifact=" + artifacts[i].getNameExt() + " isdata=" + artifacts[i].isData());
                if (artifacts[i].isData() && artifacts[i].getExt().equals(typStr)) {
                    try {
                        IDependencyResolver res = DependencyResolverFactory.getFactory().newInstance(
                                                    FileUtil.toFile(artifacts[i]).getAbsolutePath());
                    MavenLibraryImpl library = new MavenLibraryImpl();
                    library.setName(artifacts[i].getNameExt() + " (Maven Repo)");
                    StringBuffer desc = new StringBuffer();
                    desc.append("GroupID:").append(res.guessGroupId());
                    desc.append("\nArtifactID:").append(res.guessArtifactId());
                    desc.append("\nVersion:").append(res.guessVersion());
                    library.setDescription( desc.toString());
                    library.setLocalizingBundle(null);
                    List urls = new ArrayList();
                    URL url = FileUtil.toFile(artifacts[i]).toURI().toURL();
                    urls.add(url);
                    logger.debug("url=" + url);
                    library.setContent("classpath", urls);
                    libraries.add(library);
                    } catch (Exception exc) {
                        logger.error("Error while creating library", exc);
                    }
                }
            }
        }
    }
}
