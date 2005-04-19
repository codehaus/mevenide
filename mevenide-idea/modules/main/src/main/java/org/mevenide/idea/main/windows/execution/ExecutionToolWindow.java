package org.mevenide.idea.main.windows.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.common.JdkNotDefinedException;
import org.mevenide.idea.common.MavenHomeNotDefinedException;
import org.mevenide.idea.common.PomNotDefinedException;
import org.mevenide.idea.common.ui.Images;
import org.mevenide.idea.main.settings.global.GlobalSettings;
import org.mevenide.idea.main.settings.module.ModuleSettings;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.File;

/**
 * TODO: Use OpenAPI's ExecutionManager.getInstance().execute(...) when it is released.
 *
 * @author Arik
 */
public class ExecutionToolWindow extends JPanel {
    /**
     * The tool window name.
     */
    private static final String TITLE = "Maven Executions";

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

    private void init() {
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
        final ToolWindow toolWindow = getToolWindow(project);

        //if this is the first console we are adding, we need to make the toolwindow available
        if (tabs.getComponentCount() == 1)
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

    public static ToolWindow getToolWindow(final Project pProject) {
        return ToolWindowManager.getInstance(pProject).getToolWindow(TITLE);
    }

    public static ExecutionToolWindow getInstance(final Project pProject) {
        return (ExecutionToolWindow) getToolWindow(pProject).getComponent();
    }

    public static void register(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        final ExecutionToolWindow toolWin = new ExecutionToolWindow(project);
        toolMgr.registerToolWindow(TITLE, toolWin, ToolWindowAnchor.BOTTOM);
        final ToolWindow goalsTw = getToolWindow(project);
        goalsTw.setIcon(new ImageIcon(Images.MAVEN_ICON));
        goalsTw.setAvailable(false, null);
    }

    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(TITLE);
    }
}
