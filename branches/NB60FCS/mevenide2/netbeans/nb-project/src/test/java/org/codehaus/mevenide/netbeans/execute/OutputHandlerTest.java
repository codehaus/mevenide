/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.execute;

import java.io.StringWriter;
import junit.framework.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
public class OutputHandlerTest extends TestCase {
    
    public OutputHandlerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(OutputHandlerTest.class);
        
        return suite;
    }

   public void testSequence() {
       HashMap procs = new HashMap();
       HashSet set = new HashSet();
       TestProcessor proc = new TestProcessor();
       set.add(proc);
       procs.put("mojo-execute#test:test", set);
       OutputHandler handler = new OutputHandler();
       handler.setup(procs, new NullOutputWriter(), new NullOutputWriter());
       assertFalse(proc.processing);
       handler.startEvent("mojo-execute", "test:xxx", 0);
       assertFalse(proc.processing);
       handler.startEvent("mojo-execute", "test:test", 0);
       assertTrue(proc.processing);
       handler.error("xxx");
       handler.endEvent("mojo-execute", "test:test", 0);
       assertFalse(proc.processing);
       handler.error("xxx");
//       fail();
   } 
   
   
   private class TestProcessor implements OutputProcessor {
       boolean processing = false;
        public String[] getRegisteredOutputSequences() {
            return new String[] {
                "mojo-execute#test:test"
            };
        }

        public void processLine(String line, OutputVisitor visitor) {
            if (!processing) {
                fail();
            }
        }

        public void sequenceStart(String sequenceId, OutputVisitor visitor) {
            processing = true;
        }

        public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
            processing = false;
        }

        public void sequenceFail(String sequenceId, OutputVisitor visitor) {
            processing = false;
        }
       
   }
   
    
   private class NullOutputWriter extends OutputWriter {
       
       NullOutputWriter() {
           super(new StringWriter());
       }
       
        public void println(String string, OutputListener outputListener) throws IOException {}

        public void reset() throws IOException {}

        public void print(Object obj) {}

        public void println(Object x) {}

        public void println(boolean x) {}

        public void print(boolean b) {
        }

        public void print(double d) {
        }

        public void println(double x) {
        }

        public void println(char x) {
        }

        public void print(char c) {
        }

        public void write(char[] buf, int off, int len) {
        }

        public void print(float f) {
        }

        public void println(float x) {
        }

        public void print(String s) {
        }

        public void println(String x) {
        }

        public void write(String s) {
        }

        public void print(int i) {
        }

        public void println(int x) {
        }

        public void write(int c) {
        }

        public void println(String s, OutputListener l, boolean important) throws IOException {
        }

        public void print(long l) {
        }

        public void println(long x) {
        }

        public void println(char[] x) {
        }

        public void print(char[] s) {
        }

        public void write(char[] buf) {
        }

        public void write(String s, int off, int len) {
        }

        public void println() {
        }

        protected void setError() {
        }
   }
    
}
