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
package org.netbeans.modules.maven.output;

import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.output.OutputProcessor;
import org.netbeans.modules.maven.api.output.OutputUtils;
import org.netbeans.modules.maven.api.output.OutputVisitor;
import org.netbeans.modules.maven.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.windows.OutputListener;




/**
 * exec plugin output processing, just handle stacktraces.
 * @author  Milos Kleint
 */
public class ExecPluginOutputListenerProvider implements OutputProcessor {
    
    private static final String[] EXECGOALS = new String[] {
        "mojo-execute#exec:exec", //NOI18N
        "mojo-execute#exec:java" //NOI18N
    };
    private NbMavenProjectImpl project;
    
    /** Creates a new instance of ExecPluginOutputListenerProvider */
    public ExecPluginOutputListenerProvider(NbMavenProjectImpl proj) {
        project = proj;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        ClassPathProviderImpl cpp = project.getLookup().lookup(ClassPathProviderImpl.class);
        ClassPath[] cp = cpp.getProjectClassPaths(ClassPath.EXECUTE);
        OutputListener list = OutputUtils.matchStackTraceLine(line, ClassPathSupport.createProxyClassPath(cp));
        if (list != null) {
            visitor.setOutputListener(list);
        }
    }

    public String[] getRegisteredOutputSequences() {
        return EXECGOALS;
    }

    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }

    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
}
