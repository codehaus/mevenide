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

package org.codehaus.mevenide.indexer;

import java.util.Date;
import org.apache.maven.execution.MavenExecutionRequest;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * a netbeans settings for global options that cannot be put into the settings file.
 * @author mkleint
 */
public class MavenIndexSettings extends SystemOption {
    public static final String PROP_INDEX_FREQ = "indexUpdateFrequency"; //NOI18N
    public static final String PROP_LAST_INDEX_UPDATE = "lastIndexUpdate"; //NOI18N
    
    public static final int FREQ_ONCE_WEEK = 0;
    public static final int FREQ_ONCE_DAY = 1;
    public static final int FREQ_STARTUP = 2;
    public static final int FREQ_NEVER = 3;
    
    private static final long serialVersionUID = -4857548487373437L;

    
    protected void initialize() {
        super.initialize();
        setIndexUpdateFrequency(FREQ_ONCE_WEEK);
        setLastIndexUpdate(new Date(0));
    }
    
    public String displayName() {
        return "ExecutionSettings"; //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public static MavenIndexSettings getDefault() {
        return (MavenIndexSettings) findObject(MavenIndexSettings.class, true);
    }

    
    public void setIndexUpdateFrequency(int fr) {
        putProperty(PROP_INDEX_FREQ, new Integer(fr), true);
    }
    
    public int getIndexUpdateFrequency() {
        return ((Integer)getProperty(PROP_INDEX_FREQ)).intValue();
    }

    public Date getLastIndexUpdate() {
        return (Date)getProperty(PROP_LAST_INDEX_UPDATE);
    }
    
    public void setLastIndexUpdate(Date date) {
        putProperty(PROP_LAST_INDEX_UPDATE, date, true);
    }
    
}
