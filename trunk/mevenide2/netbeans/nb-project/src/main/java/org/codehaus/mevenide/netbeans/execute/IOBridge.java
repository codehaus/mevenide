/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.codehaus.mevenide.netbeans.execute;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.awt.StatusDisplayer;
import org.openide.util.io.NullOutputStream;
import org.openide.windows.InputOutput;

/**
 *
 * @author jglick - copied from netbeans.org ant module by mkleint
 */
public class IOBridge {
    
    // I/O redirection impl. Keyed by thread group (each Ant process has its own TG).
    // Various Ant tasks (e.g. <java fork="false" output="..." ...>) need the system
    // I/O streams to be redirected to the demux streams of the project so they can
    // be handled properly. Ideally nothing would try to read directly from stdin
    // or print directly to stdout/stderr but in fact some tasks do.
    // Could also pass a custom InputOutput to ExecutionEngine, perhaps, but this
    // seems a lot simpler and probably has the same effect.

    private static int delegating = 0;
    private static InputStream origIn;
    private static PrintStream origOut, origErr;
    private static Map delegateIns = new HashMap();
    private static Map delegateOuts = new HashMap();
    private static Map delegateErrs = new HashMap();
    /** list, not set, so can be reentrant - treated as a multiset */
    private static List suspendedDelegationTasks = new ArrayList();
    
    /**
     * Handle I/O scoping for overlapping project runs.
     * You must call {@link #restoreSystemInOutErr} in a finally block.
     * @param in new temporary input stream for this thread group
     * @param out new temporary output stream for this thread group
     * @param err new temporary error stream for this thread group
     * @see "#36396"
     */
    public static synchronized void pushSystemInOutErr(OutputHandler ioput) {
        if (delegating++ == 0) {
            origIn = System.in;
            origOut = System.out;
            origErr = System.err;
            
            System.setIn(new MultiplexInputStream());
            System.setOut(new MultiplexPrintStream(false));
            System.setErr(new MultiplexPrintStream(true));
        }
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        delegateIns.put(tg, ioput.getIn());
        delegateOuts.put(tg, ioput.getOut());
        delegateErrs.put(tg, ioput.getErr());
    }
    
    /**
     * Restore original I/O streams after a call to {@link #pushSystemInOutErr}.
     */
    public static synchronized void restoreSystemInOutErr() {
        assert delegating > 0;
        if (--delegating == 0) {
            System.setIn(origIn);
            System.setOut(origOut);
            System.setErr(origErr);
            origIn = null;
            origOut = null;
            origErr = null;
        }
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        delegateIns.remove(tg);
        delegateOuts.remove(tg);
        delegateErrs.remove(tg);
    }

    /**
     * Temporarily suspend delegation of system I/O streams for the current thread.
     * Useful when running callbacks to IDE code that might try to print to stderr etc.
     * Must be matched in a finally block by {@link #resumeDelegation}.
     * Safe to call when not actually delegating; in that case does nothing.
     * Safe to call in reentrant but not overlapping fashion.
     */
    public static synchronized void suspendDelegation() {
        Thread t = Thread.currentThread();
        //assert delegateOuts.containsKey(t.getThreadGroup()) : "Not currently delegating in " + t;
        // #58394: do *not* check that it does not yet contain t. It is OK if it does; need to
        // be able to call suspendDelegation reentrantly.
        suspendedDelegationTasks.add(t);
    }
    
    /**
     * Resume delegation of system I/O streams for the current thread group
     * after a call to {@link #suspendDelegation}.
     */
    public static synchronized void resumeDelegation() {
        Thread t = Thread.currentThread();
        //assert delegateOuts.containsKey(t.getThreadGroup()) : "Not currently delegating in " + t;
        // This is still valid: suspendedDelegationTasks must have *at least one* copy of t.
        assert suspendedDelegationTasks.contains(t) : "Have not suspended delegation in " + t;
        suspendedDelegationTasks.remove(t);
    }

    
    private static final class MultiplexInputStream extends InputStream {
        
        public MultiplexInputStream() {}
        
        private InputStream delegate() {
            Thread t = Thread.currentThread();
            ThreadGroup tg = t.getThreadGroup();
            while (tg != null && !delegateIns.containsKey(tg)) {
                tg = tg.getParent();
            }
            InputStream is = (InputStream)delegateIns.get(tg);
            if (is != null && !suspendedDelegationTasks.contains(t)) {
                return is;
            } else if (delegating > 0) {
                assert origIn != null;
                return origIn;
            } else {
                // Probably should not happen? But not sure.
                return System.in;
            }
        }
        
        public int read() throws IOException {
            return delegate().read();
        }        
        
        public int read(byte[] b) throws IOException {
            return delegate().read(b);
        }
        
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate().read(b, off, len);
        }
        
        public int available() throws IOException {
            return delegate().available();
        }
        
        public boolean markSupported() {
            return delegate().markSupported();
        }        
        
        public void mark(int readlimit) {
            delegate().mark(readlimit);
        }
        
        public void close() throws IOException {
            delegate().close();
        }
        
        public long skip(long n) throws IOException {
            return delegate().skip(n);
        }
        
        public void reset() throws IOException {
            delegate().reset();
        }
        
    }
    
    private static final class MultiplexPrintStream extends PrintStream {
        
        private final boolean err;
        
        public MultiplexPrintStream(boolean err) {
            this(new NullOutputStream(), err);
        }
        
        private MultiplexPrintStream(NullOutputStream nos, boolean err) {
            super(nos);
            nos.throwException = true;
            this.err = err;
        }
        
        private PrintStream delegate() {
            Thread t = Thread.currentThread();
            ThreadGroup tg = t.getThreadGroup();
            Map delegates = err ? delegateErrs : delegateOuts;
            while (tg != null && !delegates.containsKey(tg)) {
                tg = tg.getParent();
            }
            PrintStream ps = (PrintStream)delegates.get(tg);
            if (ps != null && !suspendedDelegationTasks.contains(t)) {
                return ps;
            } else if (delegating > 0) {
                PrintStream orig = err ? origErr : origOut;
                assert orig != null;
                return orig;
            } else {
                // Probably should not happen? But not sure.
                return err ? System.err : System.out;
            }
        }
        
        public boolean checkError() {
            return delegate().checkError();
        }
        
        public void close() {
            delegate().close();
        }
        
        public void flush() {
            delegate().flush();
        }
        
        public void print(long l) {
            delegate().print(l);
        }
        
        public void print(char[] s) {
            delegate().print(s);
        }
        
        public void print(int i) {
            delegate().print(i);
        }
        
        public void print(boolean b) {
            delegate().print(b);
        }
        
        public void print(char c) {
            delegate().print(c);
        }
        
        public void print(float f) {
            delegate().print(f);
        }
        
        public void print(double d) {
            delegate().print(d);
        }
        
        public void print(Object obj) {
            delegate().print(obj);
        }
        
        public void print(String s) {
            delegate().print(s);
        }
        
        public void println(double x) {
            delegate().println(x);
        }
        
        public void println(Object x) {
            delegate().println(x);
        }
        
        public void println(float x) {
            delegate().println(x);
        }
        
        public void println(int x) {
            delegate().println(x);
        }

        public void println(char x) {
            delegate().println(x);
        }
        
        public void println(boolean x) {
            delegate().println(x);
        }
        
        public void println(String x) {
            delegate().println(x);
        }
        
        public void println(char[] x) {
            delegate().println(x);
        }
        
        public void println() {
            delegate().println();
        }
        
        public void println(long x) {
            delegate().println(x);
        }
        
        public void write(int b) {
            delegate().write(b);
        }
        
        public void write(byte[] b) throws IOException {
            delegate().write(b);
        }
        
        public void write(byte[] b, int off, int len) {
            delegate().write(b, off, len);
        }
    }
    
}
