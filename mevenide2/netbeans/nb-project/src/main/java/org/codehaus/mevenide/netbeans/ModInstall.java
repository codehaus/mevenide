/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.util.Date;
import org.apache.maven.repository.indexing.RepositoryIndexException;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.indexer.MavenIndexSettings;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;

/**
 * Module install that checks if the local repo index shall be refreshed.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {
    
    private static int MILIS_IN_SEC = 1000;
    private static int MILIS_IN_MIN = MILIS_IN_SEC * 60;
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }
    
    public void restored() {
        super.restored();
        int freq = MavenIndexSettings.getDefault().getIndexUpdateFrequency();
        if (freq != MavenIndexSettings.FREQ_NEVER) {
            boolean run = false;
            if (freq == MavenIndexSettings.FREQ_STARTUP) {
                System.out.println("to be run on startup");
                run = true;
            } else if (freq == MavenIndexSettings.FREQ_ONCE_DAY && checkDiff(86400000L)) {
                System.out.println("to be run daily");
                run = true;
            }  else if (freq == MavenIndexSettings.FREQ_ONCE_WEEK && checkDiff(604800000L)) {
                System.out.println("to be run weekly");
                run = true;
            }
            if (run) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        System.out.println("running indexing..");
                        try {
                            LocalRepositoryIndexer.getInstance().updateIndex(false);
                        } catch (RepositoryIndexException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, MILIS_IN_MIN * 3);
            }
        }
    }
    
    private boolean checkDiff(long amount) {
        Date date = MavenIndexSettings.getDefault().getLastIndexUpdate();
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        return  (diff < 0 || diff > amount);
    }
    
}
