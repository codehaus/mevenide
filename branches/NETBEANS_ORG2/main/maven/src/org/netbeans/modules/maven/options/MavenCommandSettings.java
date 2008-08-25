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
package org.netbeans.modules.maven.options;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * Preferences class for externalizing the hardwired goal executions to
 * allow minor (mostly version) changes by advanced users?)
 * @author mkleint
 */
public final class MavenCommandSettings {
    private static final MavenCommandSettings INSTANCE = new MavenCommandSettings();
    
    public static final String COMMAND_CREATE_ARCHETYPE = "createArchetype"; //NOI18N
    public static final String COMMAND_CREATE_ARCHETYPENG = "createArchetypeNG"; //NOI18N
    public static final String COMMAND_INSTALL_FILE = "installFile"; //NOI18N
    public static final String COMMAND_SCM_CHECKOUT = "scmCheckout"; //NOI18N
    
    public static MavenCommandSettings getDefault() {
        return INSTANCE;
    }
    
    protected final Preferences getPreferences() {
        return NbPreferences.root().node("org/netbeans/modules/maven/commands"); //NOI18N
    }
    
    protected final String putProperty(String key, String value) {
        String retval = getProperty(key);
        if (value != null) {
            getPreferences().put(key, value);
        } else {
            getPreferences().remove(key);
        }
        return retval;
    }

    protected final String getProperty(String key) {
        return getPreferences().get(key, null);
    }    
    
    private MavenCommandSettings() {
    }
    
    public String getCommand(String command) {
        String toRet = getProperty(command);
        if (toRet == null) {
            //TODO is there some other way to do this?
            if (COMMAND_CREATE_ARCHETYPE.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-4:create";//NOI18N
            }
            else if (COMMAND_INSTALL_FILE.equals(command)) {
                toRet = "install:install-file";//NOI18N
            }
            else if (COMMAND_CREATE_ARCHETYPENG.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-archetype-plugin:2.0-alpha-3:generate";//NOI18N
            }
            else if (COMMAND_SCM_CHECKOUT.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-scm-plugin:1.0:checkout";//NOI18N
            }
        }
        assert toRet != null : "Command " + command + " needs implementation."; //NOI18N
        return toRet;
    }
    
}
