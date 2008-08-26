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

import org.netbeans.modules.maven.output.CompileAnnotation;
import org.netbeans.modules.maven.output.JavaOutputListenerProvider;
import java.io.File;
import junit.framework.*;
import org.netbeans.modules.maven.api.output.OutputVisitor;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint
 */
public class JavaOutputListenerProviderTest extends TestCase {
    private JavaOutputListenerProvider provider;
    public JavaOutputListenerProviderTest(java.lang.String testName) {
        super(testName);
    }
   
    public static Test suite() {
        TestSuite suite = new TestSuite(JavaOutputListenerProviderTest.class);
        return suite;
    }

    protected void setUp() throws java.lang.Exception {
        provider = new JavaOutputListenerProvider();
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public void testRecognizeLine() {
        OutputVisitor visitor = new OutputVisitor();
        visitor.resetVisitor();
        provider.sequenceStart("mojoexecute#compiler:testCompile", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("Compiling 1 source file to /home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/target/test-classes", visitor);
        assertNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("/home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/src/test/java/org/codehaus/mevenide/netbeans/output/JavaOutputListenerProviderTest.java:[31,1] illegal start of type", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        // happens with external command line parsing sometimes..
        provider.processLine("[WARNING] /home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/src/test/java/org/codehaus/mevenide/netbeans/output/JavaOutputListenerProviderTest.java:[31,1] deprecated", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("K:\\jsr144-private\\common-1_4\\workspace\\ri\\oss_common_j2eesdk-1_4-src-ri\\oss_cbe_party_ri\\..\\src\\main\\java\\ossj\\common\\cbe\\party\\PartyValueIteratorImpl.java:[22,7] ossj.common.cbe.party.PartyValueIteratorImpl is not abstract and does not override abstract method getNextPartys(int) in javax.oss.cbe.party.PartyValueIterator", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        provider.processLine("C:\\lfo\\pers\\projects\\mojos\\maven-hello-plugin\\src\\main\\java\\org\\laurentforet\\mojos\\hello\\GreetingMojo.java:[14,8] cannot find symbol", visitor);
        assertNotNull(visitor.getOutputListener());
        visitor.resetVisitor();
        
        //MEVENIDE-473
        provider.processLine("Compilation failure\r\n\r\n/home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/src/test/java/org/codehaus/mevenide/netbeans/output/JavaOutputListenerProviderTest.java:[14,8] cannot find symbol", visitor);
        assertNotNull(visitor.getOutputListener());
        CompileAnnotation ann = (CompileAnnotation) visitor.getOutputListener();
        assertEquals(ann.clazzfile.getAbsolutePath(), 
                FileUtil.normalizeFile(new File("/home/mkleint/src/mevenide/mevenide2/netbeans/nb-project/src/test/java/org/codehaus/mevenide/netbeans/output/JavaOutputListenerProviderTest.java")).getAbsolutePath());
        visitor.resetVisitor();
        provider.sequenceFail("mojoexecute#compiler:testCompile", visitor);
    }
}
