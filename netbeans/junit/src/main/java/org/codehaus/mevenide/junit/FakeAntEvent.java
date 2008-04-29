/*
 *  Copyright 2007 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.junit;

import java.io.File;
import java.util.Set;
import org.apache.tools.ant.module.run.LoggerTrampoline;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
public class FakeAntEvent implements LoggerTrampoline.AntEventImpl {
    private String taskName;
    private AntSession session;
    private TaskStructure taskStructure;
    private NbMavenProject project;
    private String message;
    private int logLevel = AntEvent.LOG_INFO;


    FakeAntEvent(AntSession session, NbMavenProject prj) {
        this.session = session;
        project = prj;
    }

    public AntSession getSession() {
        return session;
    }

    public void consume() throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isConsumed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getScriptLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLine() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTargetName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void setMessage(String string) {
        message = string;
    }

    void setTaskName(String string) {
        taskName = string;
    }
    
    public String getTaskName() {
        return taskName;
    }

    public TaskStructure getTaskStructure() {
        return taskStructure;
    }
    
    public void setTaskStructure(TaskStructure struct) {
        taskStructure = struct;
    }

    public String getMessage() {
        return message;
    }

    public int getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public Throwable getException() {
        return null;
    }

    public String getProperty(String name) {
        if ("basedir".equals(name)) {
            return FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath();
        }
        throw new UnsupportedOperationException("Not supported yet. - " + name);
    }

    public Set<String> getPropertyNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String evaluate(String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static class FakeTaskStructure implements LoggerTrampoline.TaskStructureImpl {
        private TaskStructure[] children;
        private String name;

        public FakeTaskStructure(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }

        public String getAttribute(String name) {
            return null;
        }

        public Set<String> getAttributeNames() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getText() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public TaskStructure[] getChildren() {
            return children;
        }
        
        public void setChildren(TaskStructure[] childs) {
            children = childs;
        }
        
    }


}
