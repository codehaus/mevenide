/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.NotifyFinishOutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessorFactory;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.openide.util.Lookup;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
abstract class AbstractOutputHandler {
    private static final String PRJ_EXECUTE = "project-execute"; //NOI18N
    
    protected HashMap<String, Set> processors;
    protected Set currentProcessors;
    protected Set<NotifyFinishOutputProcessor> toFinishProcessors;
    protected OutputVisitor visitor;

    protected AbstractOutputHandler() {
        processors = new HashMap<String, Set>();
        currentProcessors = new HashSet();
        visitor = new OutputVisitor();
        toFinishProcessors = new HashSet<NotifyFinishOutputProcessor>();
    }

    protected final String getEventId(String eventName, String target) {
        if (PRJ_EXECUTE.equals(eventName)) {
            return eventName;
        }
        return eventName + "#" + target; //NOI18N
    }
    
    protected final void initProcessorList(NbMavenProject proj) {
        // get the registered processors.
        Lookup.Result<OutputProcessorFactory> result  = Lookup.getDefault().lookup(new Lookup.Template<OutputProcessorFactory>(OutputProcessorFactory.class));
        Iterator<? extends OutputProcessorFactory> it = result.allInstances().iterator();
        while (it.hasNext()) {
            OutputProcessorFactory factory = it.next();
            Set procs = factory.createProcessorsSet(proj);
            Iterator it2 = procs.iterator();
            while (it2.hasNext()) {
                OutputProcessor proc = (OutputProcessor)it2.next();
                String[] regs = proc.getRegisteredOutputSequences();
                for (int i = 0; i < regs.length; i++) {
                    String str = regs[i];
                    Set set = processors.get(str);
                    if (set == null) {
                        set = new HashSet();
                        processors.put(str, set);
                    }
                    set.add(proc);
                }
            }
        }
    }
    
    protected final void processStart(String id, OutputWriter writer) {
        Set set = processors.get(id);
        if (set != null) {
            currentProcessors.addAll(set);
        }
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.sequenceStart(id, visitor);
        }
        if (visitor.getLine() != null) {
            if (visitor.getOutputListener() != null) {
                try {
                    writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                writer.println(visitor.getLine());
            }
        }
    }
    
    protected final void processEnd(String id, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.sequenceEnd(id, visitor);
            if (proc instanceof NotifyFinishOutputProcessor) {
                toFinishProcessors.add((NotifyFinishOutputProcessor)proc);
            }
        }
        if (visitor.getLine() != null) {
            if (visitor.getOutputListener() != null) {
                try {
                    writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                writer.println(visitor.getLine());
            }
        }
        Set set = processors.get(id);
        if (set != null) {
            //TODO a bulletproof way would be to keep a list of currently started
            // sections and compare to the list of getRegisteredOutputSequences fo each of the
            // processors in set..
            currentProcessors.removeAll(set);
        }
    }
    
    protected final void processFail(String id, OutputWriter writer) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            if (proc instanceof NotifyFinishOutputProcessor) {
                toFinishProcessors.add((NotifyFinishOutputProcessor)proc);
            }
            proc.sequenceFail(id, visitor);
        }
        if (visitor.getLine() != null) {
            if (visitor.getOutputListener() != null) {
                try {
                    writer.println(visitor.getLine(), visitor.getOutputListener(), visitor.isImportant());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                writer.println(visitor.getLine());
            }
        }
        Set set = processors.get(id);
        if (set != null) {
            Set retain = new HashSet();
            retain.addAll(set);
            retain.retainAll(currentProcessors);
            Set remove = new HashSet();
            remove.addAll(set);
            remove.removeAll(retain);
            currentProcessors.removeAll(remove);
        }
        
    }
    
    protected final void buildFinished() {
        for (NotifyFinishOutputProcessor proc : toFinishProcessors) {
            proc.buildFinished();
        }
    }
    
    protected final void processMultiLine(String input, OutputWriter writer, String levelText) {
        if (input == null) {
            return;
        }
        String[] strs = input.split(System.getProperty("line.separator")); //NOI18N
        for (int i = 0; i < strs.length; i++) {
            processLine(strs[i], writer, levelText);
        }
    }
    
    protected final void processLine(String input, OutputWriter writer, String levelText) {
        visitor.resetVisitor();
        Iterator it = currentProcessors.iterator();
        while (it.hasNext()) {
            OutputProcessor proc = (OutputProcessor)it.next();
            proc.processLine(input, visitor);
        }
        if (!visitor.isLineSkipped()) {
            String line = visitor.getLine() == null ? input : visitor.getLine();
            if (visitor.getOutputListener() != null) {
                try {
                    writer.println((levelText.length() == 0 ? "" : ("[" + levelText + "]")) + line, visitor.getOutputListener(), visitor.isImportant()); //NOI18N
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                writer.println((levelText.length() == 0 ? "" : ("[" + levelText + "]")) + line); //NOI18N
            }
        }
    }
    
    
}
