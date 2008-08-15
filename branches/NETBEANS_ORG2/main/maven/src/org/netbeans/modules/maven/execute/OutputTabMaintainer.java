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

package org.netbeans.modules.maven.execute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Action;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * an output tab manager. 
 * @author mkleint
 */
public abstract class OutputTabMaintainer {

    /**
     * All tabs which were used for some process which has now ended.
     * These are closed when you start a fresh process.
     * Map from tab to tab display name.
     */
    protected static final Map<InputOutput, Collection<String>> freeTabs =
            new WeakHashMap<InputOutput, Collection<String>>();
    
    protected InputOutput io;
    private String name;
    
    protected OutputTabMaintainer(String name) {
        this.name = name;
    }
    
    
    protected final void markFreeTab() {
        synchronized (freeTabs) {
            assert io != null;
            freeTabs.put(io, createContext());
        }
    }
    
    protected void reassignAdditionalContext(Iterator it) {
        
    }
    
    protected Collection<String> createContext() {
        Collection<String> toRet = new ArrayList<String>();
        toRet.add(name);
        toRet.add(this.getClass().getName());
        return toRet;
    }
    
    protected Action[] createNewTabActions() {
        return new Action[0];
    }
    
    public final InputOutput getInputOutput() {
        if (io == null) {
            io = createInputOutput();
        }
        return io;
    }
    
    protected final InputOutput createInputOutput() {
        synchronized (freeTabs) {
            for (Map.Entry<InputOutput, Collection<String>> entry : freeTabs.entrySet()) {
                InputOutput free = entry.getKey();
                Iterator<String> vals = entry.getValue().iterator();
                String freeName = vals.next();
                String type = vals.next();
                if (io == null && freeName.equals(name) && type.equals(this.getClass().getName())) {
                    // Reuse it.
                    io = free;
                    reassignAdditionalContext(vals);
                    try {
                        io.getOut().reset();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // useless: io.flushReader();
                } else {
                    // Discard it.
                    free.closeInputOutput();
                }
            }
            freeTabs.clear();
        }
        //                }
        if (io == null) {
            io = IOProvider.getDefault().getIO(name, createNewTabActions());
            io.setInputVisible(true);
        }
        return io;
    }    

}
