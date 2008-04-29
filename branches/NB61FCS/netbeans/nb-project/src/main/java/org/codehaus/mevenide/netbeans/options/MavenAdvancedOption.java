/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.options;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 * advanced option UI,registred in the layer file.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenAdvancedOption extends AdvancedOption {
    
    /** Creates a new instance of NewClass */
    public MavenAdvancedOption() {
    }

    public String getDisplayName() {
        return NbBundle.getMessage(MavenAdvancedOption.class, "TIT_Maven_Category");
    }

    public String getTooltip() {
        return NbBundle.getMessage(MavenAdvancedOption.class, "TIP_Maven_Category");
    }

    public OptionsPanelController create() {
        return new MavenOptionController();
    }
    
}
