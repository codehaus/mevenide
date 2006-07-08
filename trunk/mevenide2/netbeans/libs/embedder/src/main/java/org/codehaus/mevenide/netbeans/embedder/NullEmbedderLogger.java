/*
 * NullEmbedderLogger.java
 *
 * Created on July 6, 2006, 7:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.embedder;

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
    
}
