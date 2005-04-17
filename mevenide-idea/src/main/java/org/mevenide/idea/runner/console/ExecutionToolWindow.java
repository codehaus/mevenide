package org.mevenide.idea.runner.console;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.JdkNotDefinedException;
import org.mevenide.idea.MavenHomeNotDefinedException;
import org.mevenide.idea.PomNotDefinedException;
import org.mevenide.idea.settings.global.GlobalSettings;
import org.mevenide.idea.settings.module.ModuleSettings;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.File;

/**
 * @author Arik
 */
public class ExecutionToolWindow extends JPanel {
    /**
     * The tool window name.
     */
    public static final String NAME = "Maven Executions";

    /**
     * The tool window title.
     */
    public static final String TITLE = NAME;

    //
    //command line constants
    //

    private static final String ENDORSED_DIR_NAME = "lib/endorsed";
    private static final String FOREHEAD_MAIN_CLASS = "com.werken.forehead.Forehead";
    private static final String FOREHEAD_CONF_FILE = "bin/forehead.conf";
    private static final String FOREHEAD_JAR_FILE = "lib/forehead-1.0-beta-5.jar";
    private static final String CLASSPATH_ARG_NAME = "-classpath";
    private static final String DEFAULT_SUFFIX = ":(default)";

    /**
     * The tab container that contains all execution consoles.
     */
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);

    /**
     * The project this tool window belongs to.
     */
    private final Project project;

    public ExecutionToolWindow(final Project pProject) {
        project = pProject;
        init();
    }

    public ExecutionToolWindow(final Project pProject,
                               final boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        project = pProject;
        init();
    }

    protected void init() {
        removeAll();
        setLayout(new BorderLayout());

        add(tabs, BorderLayout.CENTER);
    }

    public void runMaven(final Module pModule,
                         final String[] pGoals) throws PomNotDefinedException,
                                                       JdkNotDefinedException,
                                                       ExecutionException,
                                                       MavenHomeNotDefinedException {
        //
        //make sure the user has set the Maven home location
        //
        final File mavenHome = GlobalSettings.getInstance().getMavenHome();
        if (mavenHome == null)
            throw new MavenHomeNotDefinedException();

        //
        //make sure selected module has a POM file
        //
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(pModule);
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile == null || !pomFile.exists())
            throw new PomNotDefinedException();

        //
        //make sure selected module has a valid JDK set
        //
        final ProjectJdk jdk = moduleSettings.getJdk();
        if (jdk == null)
            throw new JdkNotDefinedException();

        //
        //important locations for the command line
        //
        final File foreheadConf = new File(mavenHome, FOREHEAD_CONF_FILE);
        final File javaEndorsed = new File(jdk.getHomeDirectory().getPath(), ENDORSED_DIR_NAME);
        final File mavenEndorsed = new File(mavenHome, ENDORSED_DIR_NAME);
        final String endorsedDirs =
                javaEndorsed.getAbsolutePath() + File.pathSeparatorChar + mavenEndorsed.getAbsolutePath();
        final File foreheadJar = new File(mavenHome, FOREHEAD_JAR_FILE);

        //
        //build the command line to execute
        //
        final GeneralCommandLine cmdLine = new GeneralCommandLine();
        cmdLine.setWorkDirectory(pomFile.getParentFile().getAbsolutePath());
        cmdLine.setExePath(jdk.getVMExecutablePath());
        cmdLine.addParameter("-Dmaven.home=" + mavenHome.getAbsolutePath());
        cmdLine.addParameter("-Dtools.jar=" + jdk.getToolsPath());
        cmdLine.addParameter("-Dforehead.conf.file=" + foreheadConf.getAbsolutePath());
        cmdLine.addParameter("-Djava.endorsed.dirs=" + endorsedDirs);
        cmdLine.addParameter("-Xmx256m");//TODO: add as configuration entry in MavenPlugin!
        cmdLine.addParameter(CLASSPATH_ARG_NAME);
        cmdLine.addParameter(foreheadJar.getAbsolutePath());
        cmdLine.addParameter(FOREHEAD_MAIN_CLASS);
        cmdLine.addParameters(finalizeGoalNames(pGoals));

        //
        //start process
        //
        final ProcessHandler processHandler = new DefaultJavaProcessHandler(cmdLine);

        //
        //create and add a new execution console
        //
        tabs.add(StringUtils.join(pGoals, ' '),
                 new ExecutionConsole(project, processHandler));

        //
        //show the new execution console
        //
        final ToolWindowManager toolWinMgr = ToolWindowManager.getInstance(project);
        final ToolWindow toolWindow = toolWinMgr.getToolWindow(NAME);
        if (tabs.getComponentCount() == 1)  //if we have one, including the one we just added
            toolWindow.setAvailable(true, null);
        toolWindow.show(null);
    }

    protected String[] finalizeGoalNames(final String[] pGoals) {
        final String[] goals = new String[pGoals.length];
        System.arraycopy(pGoals, 0, goals, 0, pGoals.length);
        for (int i = 0; i < goals.length; i++) {
            String goalName = goals[i];
            if (goalName.endsWith(DEFAULT_SUFFIX)) {
                goalName = goalName.substring(0, goalName.length() - DEFAULT_SUFFIX.length());
                goals[i] = goalName;
            }

        }
        return goals;
    }

    public static ExecutionToolWindow getInstance(final Project pProject) {
        final ToolWindow toolWindow = ToolWindowManager.getInstance(pProject).getToolWindow(NAME);
        return (ExecutionToolWindow) toolWindow.getComponent();
    }
}
