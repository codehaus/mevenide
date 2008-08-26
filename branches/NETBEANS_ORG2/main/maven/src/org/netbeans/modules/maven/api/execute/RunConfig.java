/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.api.execute;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.netbeans.api.project.Project;

/**
 * Context provider for maven executor
 * @author Milos Kleint
 */
public interface RunConfig {
    
    /**
     * directory where the maven build execution happens.
     * @return
     */
    File getExecutionDirectory();

    /**
     * project that is being used for execution, can be null.
     * @return 
     */
    Project getProject();
    /**
     * goals to be executed.
     * @return a list of goals to run
     */
    List<String> getGoals();

    String getExecutionName();
    
    String getTaskDisplayName();

    String getActionName();
    
    /**
     * properties to be used in the execution. Do not modify the returned Properties instance.
     * 
     * @return
     */
    Properties getProperties();

    /**
     * use the properties in the parameter for execution. Will not necessarily use the
     * parameter instance
     * 
     * @param properties property keys+value to be used in execution.
     */
    void setProperties(Properties properties);
    
    String setProperty(String key, String value);
    
    String removeProperty(String key);

    boolean isShowDebug();
    
    boolean isShowError();
    
    Boolean isOffline();
    
    void setOffline(Boolean bool);
    
    boolean isRecursive();
    
    boolean isUpdateSnapshots();

    List<String> getActivatedProfiles();
    
    void setActivatedProfiles(List<String> profiles);
    
    boolean isInteractive();

    
}
