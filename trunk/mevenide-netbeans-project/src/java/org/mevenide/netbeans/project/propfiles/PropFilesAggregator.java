/*
 * PropFilesAggregator.java
 *
 * Created on April 1, 2004, 5:50 PM
 */

package org.mevenide.netbeans.project.propfiles;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.netbeans.project.FileUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropFilesAggregator {
    private static final Log logger = LogFactory.getLog(PropFilesAggregator.class);
    
    private FileObject projectDirFO;
    private FileObject userDirFO;
    private SinglePropFile project;
    private SinglePropFile projectBuild;
    private SinglePropFile userBuild;
    private ILocationFinder locFinder;
    private static Properties defaults;
    
    //TODO lazy initialization
    static {
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Plugins/Maven/Properties/defaults.properties");
            defaults = new Properties();
            defaults.load(fo.getInputStream());
        } catch (Exception exc) {
            logger.error("Cannot load default properties.", exc);
        }
    }
    /** Creates a new instance of PropFilesAggregator */
    public PropFilesAggregator(FileObject projectDir, FileObject userDir, ILocationFinder finder) {
        projectDirFO = projectDir;
        userDirFO = userDir;
        locFinder = finder;
        //TODO - add change listeners to figure out added/remove prop files.
    }
    
    private void initialize() {
        FileObject fo = projectDirFO.getFileObject("project", "properties");
        project = (fo == null ? null : new SinglePropFile(fo));
        fo = projectDirFO.getFileObject("build", "properties");
        projectBuild = (fo == null ? null : new SinglePropFile(fo));
        fo = userDirFO.getFileObject("build", "properties");
        userBuild = (fo == null ? null : new SinglePropFile(fo));
        
    }
    
    public String getValue(String key) {
        return getValue(key, true);
    }
    
    private String getValue(String key, boolean resolve) {
        String toReturn = null;
        if (userBuild != null) {
            toReturn = userBuild.getValue(key);
        }
        if (toReturn == null && projectBuild != null) {
            toReturn = projectBuild.getValue(key);
        }
        if (toReturn == null && project != null) {
            toReturn = project.getValue(key);
        }
        if (toReturn == null) {
            toReturn = getDefault(key);
        }
        if (resolve && toReturn != null) {
            toReturn = resolve(new StringBuffer(toReturn)).toString();
        }
        return toReturn;
    }
    
    private StringBuffer resolve(StringBuffer value) {
        StringBuffer toReturn = value;
        int index = value.indexOf("${");
        if (index > -1) {
            int end = value.indexOf("}", index);
            if (end > index + 2) {
                String key = value.substring(index + 2, end);
                String keyvalue = getValue(key, true);
                if (keyvalue != null) {
                    toReturn.replace(index, end, keyvalue);
                    return resolve(toReturn);
                } else {
                    logger.warn("cannot resolve key? '" + key + "'");
                }
            } else {
                logger.warn("badly formed value? '" + value + "'");
            }
        } 
        return toReturn;
    }
    
    
    private String getDefault(String key) {
        if ("basedir".equals(key)) {
            return FileUtil.toFile(projectDirFO).getAbsolutePath();
        }
        if ("user.home".equals(key)) {
            return FileUtil.toFile(userDirFO).getAbsolutePath();
        }
        if ("maven.home".equals(key)) {
            return locFinder.getMavenHome();
        }
        if ("maven.home.local".equals(key)) {
            return locFinder.getMavenLocalHome();
        }
        return defaults.getProperty(key);
    }
}
