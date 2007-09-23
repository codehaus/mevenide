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
import java.net.URL;
import org.apache.tools.ant.module.run.LoggerTrampoline;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.openide.windows.OutputListener;

/**
 *
 * @author mkleint
 */
public class FakeAntSession implements LoggerTrampoline.AntSessionImpl {

    private Object customData;
    
    public File getOriginatingScript() {
        return new File("fake/maven/build.xml");
    }

    public String[] getOriginatingTargets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getCustomData(AntLogger logger) {
        return customData;
    }

    public void putCustomData(AntLogger logger, Object data) {
        customData = data;
    }

    public void println(String message, boolean err, OutputListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deliverMessageLogged(AntEvent originalEvent, String message, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void consumeException(Throwable t) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isExceptionConsumed(Throwable t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getVerbosity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputListener createStandardHyperlink(URL file, String message, int line1, int column1, int line2, int column2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
