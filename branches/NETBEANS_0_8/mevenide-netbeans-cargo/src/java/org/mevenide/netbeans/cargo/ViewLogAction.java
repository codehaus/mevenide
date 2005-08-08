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


package org.mevenide.netbeans.cargo;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ViewLogAction extends AbstractAction {
    private File file;
    private String tabName;
    private InputOutput io;
    /** Creates a new instance of AddContainerAction */
    public ViewLogAction(File logFile, String name, String tabName) {
        putValue(Action.NAME, name);
        putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/cargo/resources/RefreshIcon.gif")));
        putValue(Action.SHORT_DESCRIPTION, "Refresh Log View");
        file = logFile;
        this.tabName = tabName;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if (io == null) {
            io = IOProvider.getDefault().getIO(tabName, new Action[] {this});
        }
        BufferedReader reader = null;
        try {
            io.getOut().reset();
            io.select();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            while (line != null) {
                io.getOut().println(line);
                line = reader.readLine();
            }
        } catch (IOException exc) {
            
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (io != null) {
                    io.getOut().close();
                    io.getErr().close();
                }
            } catch (IOException exc) {
                
            }
        }
    }
    
}
