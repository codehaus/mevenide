/*
 * RunContext.java
 *
 * Created on February 12, 2005, 3:26 PM
 */

package org.mevenide.netbeans.project.exec;

import java.io.File;

/**
 *
 * @author cenda
 */
public interface RunContext {
    String getExecutionName();
    File getExecutionDirectory();
    String getMavenHome();
    String[] getAdditionalParams();
}
