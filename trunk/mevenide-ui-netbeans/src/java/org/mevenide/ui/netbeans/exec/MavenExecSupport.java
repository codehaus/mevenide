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
package org.mevenide.ui.netbeans.exec;

import org.mevenide.ui.netbeans.MavenSettings;
import org.openide.execution.Executor;
import org.openide.loaders.ExecutionSupport;
import org.openide.loaders.MultiDataObject.Entry;

public class MavenExecSupport extends ExecutionSupport
{
    
    public MavenExecSupport(Entry entry)
    {
        super(entry);
    }
    
    protected Executor defaultExecutor()
    {
        return MavenSettings.getDefault().getExecutor();
    }
}
