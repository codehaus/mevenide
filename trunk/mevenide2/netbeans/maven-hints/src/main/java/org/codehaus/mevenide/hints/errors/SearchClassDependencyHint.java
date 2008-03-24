/*
 *  Copyright 2008 Anuradha.
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
package org.codehaus.mevenide.hints.errors;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.codehaus.mevenide.hints.ui.customizers.SearchDependencyCustomizer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
public class SearchClassDependencyHint extends AbstractHint {

    public static final String OPTION_DIALOG = "maven_search_dialog";//NOI18N
    public static SearchClassDependencyHint hint;

    public SearchClassDependencyHint() {
        super(true, false, null);
        synchronized(SearchClassDependencyHint.class){
           hint=this;
        }
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(SearchClassDependencyHint.class, "LBL_Missing_Class_Dec");
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.noneOf(Kind.class);
    }

    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        return null;//should not be called

    }

    public String getId() {
        return "MAVEN_SEARCH_HINT";//NOI18N
    }

    public String getDisplayName() {
        return NbBundle.getMessage(SearchClassDependencyHint.class, "LBL_Missing_Class");
    }

    public void cancel() {
    }

    @Override
    public JComponent getCustomizer(Preferences p) {
        return new SearchDependencyCustomizer(p);
    }

    public static  boolean isSearchDialog() {
        synchronized(SearchClassDependencyHint.class){
            if (hint == null) {
                 hint=new SearchClassDependencyHint();
            }
        }
        return hint.getPreferences(null).getBoolean(OPTION_DIALOG, true);
    }

    public static boolean isHintEnabled() {
        synchronized(SearchClassDependencyHint.class){
            if (hint == null) {
                 hint=new SearchClassDependencyHint();
            }
        }
        return hint.isEnabled();
    }
}
