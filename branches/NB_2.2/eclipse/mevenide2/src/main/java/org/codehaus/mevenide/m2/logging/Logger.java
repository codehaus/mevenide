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
package org.codehaus.mevenide.m2.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**  
 * 
 * will allow to switch log implementation
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class Logger {

    private static Map loggers = new HashMap();
    
    private Log log;
    
    private Logger(Class clazz) { 
        log = LogFactory.getLog(clazz);
    }
    
    public static synchronized Logger getLogger(Class clazz) {
        if ( !loggers.containsKey(clazz) ) {
           Logger logger = new Logger(clazz);
           loggers.put(clazz, logger);
        }
        return (Logger) loggers.get(clazz);
    }
    
    public void debug(Object o, Throwable t) {
        log.debug(o, t);
    }
    public void debug(Object o) {
        log.debug(o);
    }
    public void error(Object o, Throwable t) {
        log.error(o, t);
    }
    public void error(Object o) {
        log.error(o);
    }
    public void fatal(Object o, Throwable t) {
        log.fatal(o, t);
    }
    public void fatal(Object o) {
        log.fatal(o);
    }
    public void info(Object o, Throwable t) {
        log.info(o, t);
    }
    public void info(Object o) {
        log.info(o);
    }
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }
    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }
    public void trace(Object o, Throwable t) {
        log.trace(o, t);
    }
    public void trace(Object o) {
        log.trace(o);   
    }
    public void warn(Object o, Throwable t) {
        log.warn(o, t);
    }
    public void warn(Object o) {
        log.warn(o);
    }
}
