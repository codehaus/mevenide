/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.netbeans.modules.maven.embedder;

import org.apache.maven.embedder.MavenEmbedderLogger;

/**
 *
 * @author mkleint
 */
public class NullEmbedderLogger implements MavenEmbedderLogger {
    
    /** Creates a new instance of NullEmbedderLogger */
    public NullEmbedderLogger() {
    }

    public void debug(String string) {
    }

    public void debug(String string, Throwable throwable) {
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void info(String string) {
    }

    public void info(String string, Throwable throwable) {
    }

    public boolean isInfoEnabled() {
        return false;
    }

    public void warn(String string) {
    }

    public void warn(String string, Throwable throwable) {
    }

    public boolean isWarnEnabled() {
        return false;
    }

    public void error(String string) {
    }

    public void error(String string, Throwable throwable) {
    }

    public boolean isErrorEnabled() {
        return false;
    }

    public void fatalError(String string) {
    }

    public void fatalError(String string, Throwable throwable) {
    }

    public boolean isFatalErrorEnabled() {
        return false;
    }

    public void setThreshold(int i) {
        level = i;
    }

    private int level = 0;
    
    public int getThreshold() {
        return level;
    }

    public void close() {
    }
    
}
