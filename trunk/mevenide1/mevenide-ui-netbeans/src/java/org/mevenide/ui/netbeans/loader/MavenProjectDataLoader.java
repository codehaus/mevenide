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
package org.mevenide.ui.netbeans.loader;

import java.io.IOException;
import org.mevenide.ui.netbeans.ShowProjectPropertiesAction;
import org.mevenide.ui.netbeans.exec.RunGoalsAction;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.ExecuteAction;
import org.openide.actions.FileSystemAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.actions.SaveAsTemplateAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/** Recognizes single files in the Repository as being of a certain type.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenProjectDataLoader extends UniFileLoader {
    
    private static final long serialVersionUID = 14845757573883L;
    
    public MavenProjectDataLoader() {
        super("org.mevenide.ui.netbeans.MavenProjectDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(MavenProjectDataLoader.class, "LBL_loaderName");
    }
    
    protected void initialize() {
        super.initialize();
        ExtensionList extensions = new ExtensionList();
        extensions.addExtension("xml");
        setExtensions(extensions);
    }
    
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(EditAction.class),
            SystemAction.get(ShowProjectPropertiesAction.class),
            SystemAction.get(MountSourcesAction.class),
            SystemAction.get(MountDependenciesAction.class),
            // SystemAction.get(CustomizeBeanAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            /*
            SystemAction.get(CompileAction.class),
            null,
            SystemAction.get(BuildAction.class),
            null,
             */
            SystemAction.get(ExecuteAction.class),
            SystemAction.get(RunGoalsAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new MavenProjectDataObject(primaryFile, this);
    }
    
    protected FileObject findPrimaryFile(FileObject fileObject)
    {
        FileObject retValue;
        if (! "project.xml".equals(fileObject.getNameExt())) {
            retValue = null;
        } else {
            retValue = super.findPrimaryFile(fileObject);
        }
        return retValue;
    }    
    
    // Additional user-configurable properties:
    /*
    public String getMyProp() {
        return (String)getProperty("myProp");
    }
    public void setMyProp(String nue) {
        putProperty("myProp", nue, true);
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeUTF(getMyProp());
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        setMyProp(in.readUTF());
    }
     */
    
}
