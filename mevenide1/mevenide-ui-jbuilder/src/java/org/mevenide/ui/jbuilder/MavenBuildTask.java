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
package org.mevenide.ui.jbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.mevenide.environment.ConfigUtils;
import com.borland.jbuilder.node.JBProject;
import com.borland.jbuilder.paths.JDKPathSet;
import com.borland.primetime.PrimeTime;
import com.borland.primetime.build.BuildInformation;
import com.borland.primetime.build.BuildListener;
import com.borland.primetime.build.BuildProcess;
import com.borland.primetime.build.BuildTask;
import com.borland.primetime.build.ExternalTask;
import com.borland.primetime.ide.BrowserIcons;
import com.borland.primetime.vfs.Url;
import com.borland.primetime.node.Project;

/**
 * <p>Title: Maven Build Task</p>
 * <p>Description: This class actually does the dispatching to Maven, executing
 * the requested goals. The dispatching code is based on the contents of the
 * Maven launcher script but still a little hardcoded. </p>
 * @author Serge Huber
 * @version 1.0
 */
public class MavenBuildTask extends BuildTask
    implements ExternalTask {

    private MavenFileNode mavenFileNode;
    private ArrayList goals = new ArrayList();
    private String displayName;
    private javax.swing.Icon smallDisplayIcon;
    private boolean displayOutput;
    private java.io.File runningDirectory;
    private String commandLine;
    private long refreshDelay;

    public MavenBuildTask () {
        setDisplayName("Maven");
        setSmallDisplayIcon(BrowserIcons.ICON_ANT_TARGET);
        setDisplayOutput(true);
        setRefreshDelay(250);
    }

    public void addGoal (String newGoal) {
        goals.add(newGoal);
    }

    public void setMavenFileNode (MavenFileNode mavenFileNode) {
        this.mavenFileNode = mavenFileNode;
    }

    private String findBootstrapJar (String mavenHome) {
        String mavenLibPath = mavenHome + File.separator + "lib";
        File mavenLibDir = new File(mavenLibPath);
        File[] jarFiles = mavenLibDir.listFiles(new FileFilter() {
            public boolean accept (File pathname) { // is file ok?
                return pathname.isFile() &&
                    pathname.getName().toLowerCase().startsWith("forehead") &&
                    pathname.getName().toLowerCase().endsWith(".jar");
            }
        });
        if (jarFiles.length > 0) {
            return jarFiles[0].toString();
        } else {
            return null;
        }
    }

    private String buildToolsJarLocation (String javaHome) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName == null) {
            osName = "";
        }
        System.out.println("os.name=" + osName);
        if (osName.indexOf("mac") != -1) {
            return javaHome + File.separator + ".." + File.separator +
                "Classes" +
                File.separator + "classes.jar";
        } else {
            return javaHome + File.separator + "lib" +
                File.separator + "tools.jar";
        }
    }

    public boolean build (BuildProcess buildProcess) {

        String mavenHome = ConfigUtils.getDefaultLocationFinder().getMavenHome();
        if ( (mavenHome == null) || (mavenHome.equals(""))) {
            buildProcess.fireBuildProblem(null, true, "No MAVEN_HOME variable set in user's environment, please set it and relaunch JBuilder",
                                          BuildProcess.UNKNOWN_LINE,
                                          BuildProcess.UNKNOWN_COLUMN, null);
            return false;
        }
        String mavenBootStrapLib = findBootstrapJar(mavenHome);
        String mavenForeheadConf = delimitArgument(mavenHome + File.separator + "bin" +
            File.separator + "forehead.conf");
        String mavenMainClass = "com.werken.forehead.Forehead";

        // Get the project JDK
        final Project project = buildProcess.getProject();
        final JDKPathSet hostJdk = ((JBProject)project).getPaths().getJDKPathSet();
        String javaHome = delimitArgument(hostJdk.getHomePath().getFileObject().
                                          getAbsolutePath());
        String jvmArgs =
            " -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" +
            " -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl" +
            " -Dmaven.home=" + delimitArgument(mavenHome) +
            " -Dtools.jar=" + buildToolsJarLocation(javaHome) +
            " -Dforehead.conf.file=" + mavenForeheadConf +
            " -Djava.endorsed.dirs=" + javaHome + File.separator + "lib" +
            File.separator + "endorsed" + File.pathSeparator +
            delimitArgument(mavenHome + File.separator + "lib" +
                            File.separator + "endorsed") +
            " -Xmx160m" +
            " -classpath " + delimitArgument(mavenBootStrapLib);
        JBProject jbProject = (JBProject) mavenFileNode.getProject();

        File runDirFile = mavenFileNode.getUrl().getFileObject().getParentFile();
        if (PrimeTime.isVerbose()) {
            System.out.println("Setting runDir=" + runDirFile.toString());
        }
        setRunningDirectory(runDirFile);

        StringBuffer command = new StringBuffer();

        // Build up a command line to run Retroguard, which will look something
        // like this:
        //
        // The actual generated command line will have fully qualified paths
        // to all arguments that are files.

        command.append(delimitArgument(hostJdk.getHomePath().getRelativeUrl(
            "bin/java").getFileObject().getAbsolutePath()));

        command.append(jvmArgs);

        // Specify the main class
        command.append(' ');
        command.append(mavenMainClass);

        if (MavenPropertyGroup.DEBUG_MODE.getBoolean(mavenFileNode)) {
            command.append(" -X ");
        }

        if (MavenPropertyGroup.OFFLINE_MODE.getBoolean(mavenFileNode)) {
            command.append(" -o ");
        }

        if (MavenPropertyGroup.QUIET_MODE.getBoolean(mavenFileNode)) {
            command.append(" -q ");
        }

        if (goals.size() > 0) {
            command.append(' ');
        }

        // Arguments
        Iterator goalIter = goals.iterator();
        while (goalIter.hasNext()) {
            String curGoal = (String) goalIter.next();
            command.append(curGoal);
            if (goalIter.hasNext()) {
                command.append(' ');
            }
        }

        buildProcess.fireBuildStatus("Launching Maven...", false);
        if (PrimeTime.isVerbose()) {
            System.out.println("Maven commandLine=[" + command.toString() + "]");
        }
        setCommandLine(command.toString());

        // If an error occurs running Maven, it will be reported via
        // BuildProcess.fireBuildProblem().  Add ourselves as a BuildListener
        // for the duration of the external process to see if an error occurs:
        MavenBuildListener buildListener = new MavenBuildListener();
        buildProcess.addBuildListener(buildListener);

        // here we actually perform the build.
        boolean ret = performBuild(buildProcess);
        buildProcess.removeBuildListener(buildListener);

        return ret;
    }

    private boolean performBuild (BuildProcess buildProcess) {
        boolean result = false;
        int exitValue = 0;

        try {
            Process process = Runtime.getRuntime().exec(commandLine, null,
                runningDirectory);
            if (process == null) {
                return false;
            }
            BufferedReader processOutput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
            BufferedReader processErrors = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));
            while (true) {
                try {
                    // the exitValue method only generates an exception if
                    // the process has not yet terminated, so we use this
                    // technique to know if the process has finished or not.
                    // Not very elegant from the JDK if you ask me...
                    exitValue = process.exitValue();
                    // we only reach this point if the process has terminated.
                    break;
                } catch (IllegalThreadStateException itse) {
                    // we reach this point if the process has not yet
                    // terminated !
                }
                if (buildProcess.isCancelled()) {
                    process.destroy();
                    return true;
                }
                // now let's process output and error information.
                parseProcessStream(buildProcess, processOutput, false);
                parseProcessStream(buildProcess, processErrors, true);
                Thread.sleep(refreshDelay);
            }
            parseProcessStream(buildProcess, processOutput, false);
            parseProcessStream(buildProcess, processErrors, true);
        } catch (InterruptedException ie) {
            // let's report the problem via the fireBuildProblem call ?
            ie.printStackTrace();
        } catch (IOException ioe) {
            // let's report the problem via the fireBuildProblem call ?
            ioe.printStackTrace();
        }

        if (exitValue > 0) {
            return false;
        }
        return true;
    }

    private void parseProcessStream (BuildProcess buildProcess,
                                     BufferedReader processReader,
                                     boolean fromErrorStream)
        throws IOException {
        ArrayList buildInfos = new ArrayList();
        while (processReader.ready()) {
            String curLine = processReader.readLine();
            BuildInformation buildInfo = new BuildInformation(null, curLine,
                BuildProcess.UNKNOWN_LINE, BuildProcess.UNKNOWN_COLUMN,
                fromErrorStream);
            buildInfos.add(buildInfo);
        }
        BuildInformation[] buildInfoArray = (BuildInformation[]) buildInfos.
            toArray(new BuildInformation[buildInfos.size()]);
        buildProcess.fireBuildInformation(this, buildInfoArray);
    }

    /**
     * Delimits an argument with double quotes if the <code>String</code> has any
     * embedded spaces.
     *
     * @param arg String
     * @return String
     */
    private String delimitArgument (String arg) {
        if (arg.indexOf(' ') != -1) {
            return '\"' + arg + '\"';
        }
        return arg;
    }

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }

    public javax.swing.Icon getSmallDisplayIcon () {
        return smallDisplayIcon;
    }

    public void setSmallDisplayIcon (javax.swing.Icon smallDisplayIcon) {
        this.smallDisplayIcon = smallDisplayIcon;
    }

    public boolean isDisplayOutput () {
        return displayOutput;
    }

    public void setDisplayOutput (boolean displayOutput) {
        this.displayOutput = displayOutput;
    }

    public java.io.File getRunningDirectory () {
        return runningDirectory;
    }

    public void setRunningDirectory (java.io.File runningDirectory) {
        this.runningDirectory = runningDirectory;
    }

    public String getCommandLine () {
        return commandLine;
    }

    public void setCommandLine (String commandLine) {
        this.commandLine = commandLine;
    }

    public long getRefreshDelay () {
        return refreshDelay;
    }

    public void setRefreshDelay (long refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    private class MavenBuildListener extends BuildListener {
        private boolean error = false;
        public void buildProblem (BuildProcess buildProcess, Url url,
                                  boolean error, String string, int line,
                                  int col, String helpTopic) {
            if (error) {
                if (PrimeTime.isVerbose()) {
                    System.out.println(url + ":" + string + "on line" + line +
                                       ", col " + col + " " + helpTopic);
                }
                this.error = true;
            }
        }

        public boolean isError () {
            return error;
        }
    }

}
