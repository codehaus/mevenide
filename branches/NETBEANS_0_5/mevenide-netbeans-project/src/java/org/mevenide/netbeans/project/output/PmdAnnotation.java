/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.netbeans.project.output;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.WeakSet;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.mevenide.reports.PmdResult;


/**
 *
 */
public final class PmdAnnotation extends Annotation implements PropertyChangeListener, OutputListener {
    
    private static final Set hyperlinks = new WeakSet(); // Set<Hyperlink>
    private boolean dead = false;
    
    public static void detachAllAnnotations() {
        synchronized (hyperlinks) {
            Iterator it = hyperlinks.iterator();
            while (it.hasNext()) {
                ((PmdAnnotation)it.next()).destroy();
            }
        }
    }

    private final PmdResult.Violation violation;
    
    public PmdAnnotation(PmdResult.Violation violation) {
        this.violation= violation;
        synchronized (hyperlinks) {
            hyperlinks.add(this);
        }
    }
    
        
    public void outputLineSelected(OutputEvent ev) {
        //           cookie.getLineSet().getCurrent(line).show(Line.SHOW_SHOW);
    }
    
    /** Called when some sort of action is performed on a line.
     * @param ev the event describing the line
     */
    public void outputLineAction(OutputEvent ev) {
        FileObject fo = FileUtil.toFileObject(violation.getFile());
        try {
            DataObject dobj = DataObject.find(fo);
            EditorCookie cook = (EditorCookie)dobj.getCookie(EditorCookie.class);
            if (cook != null) {
                cook.open();
                attachAllInFile(cook, this);
                int lineInt = Integer.parseInt(violation.getLine());
                if (lineInt != -1) {
                    Line l = cook.getLineSet().getOriginal(lineInt - 1);
                    if (! l.isDeleted()) {
                        l.show(Line.SHOW_GOTO);
                    }
                }
            }
        } catch (DataObjectNotFoundException exc) {
            
        }
    }
    
    PmdResult.Violation getViolation() {
        return violation;
    }
    
    private static void attachAllInFile(EditorCookie cook, PmdAnnotation annot) {
        Set newSet = null;
        synchronized (hyperlinks) {
            newSet = new HashSet(hyperlinks);
        }
        Iterator it = newSet.iterator();
        while (it.hasNext()) {
            PmdAnnotation ann = (PmdAnnotation)it.next();
            PmdResult.Violation violation = ann.getViolation();
            if (violation.getFile().equals(annot.getViolation().getFile())) {
                int lineInt = Integer.parseInt(violation.getLine());
                if (lineInt != -1) {
                    Line l = cook.getLineSet().getOriginal(lineInt - 1);
                    if (! l.isDeleted()) {
                        ann.attachAsNeeded(l);
                    }
                }
                
            }
        }
    }
    
    /** Called when a line is cleared from the buffer of known lines.
     * @param ev the event describing the line
     */
    public void outputLineCleared(OutputEvent ev) {
        doDetach();
    }
    
    
    
    
    void destroy() {
        doDetach();
        dead = true;
    }    
    
    private synchronized void attachAsNeeded(Line l) {
        if (getAttachedAnnotatable() == null) {
            Annotatable ann = l;
            attach(ann);
/**            Iterator it = hyperlinks.iterator();
            while (it.hasNext()) {
                PmdAnnotation h = (PmdAnnotation)it.next();
                if (h != this) {
                    h.doDetach();
                }
            }
 */
            ann.addPropertyChangeListener(this);
        }
    }

   
    private synchronized void doDetach() {
        Annotatable ann = getAttachedAnnotatable();
        if (ann != null) {
            ann.removePropertyChangeListener(this);
            detach();
        }
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
        if (dead) return;
        String prop = ev.getPropertyName();
        if (    prop == null ||
                prop.equals(Annotatable.PROP_TEXT) ||
                prop.equals(Annotatable.PROP_DELETED)) {
            doDetach();
        }
    }
    
    public String getAnnotationType() {
        return "org-mevenide-netbeans-project-pmd"; // NOI18N
    }
    
    public String getShortDescription() {
        return violation.getViolationText();
    }
    
    public String toString() {
        return "pmd[" + violation.getViolationId() + ":" + violation.getLine() + "]"; // NOI18N
    }
    
}
