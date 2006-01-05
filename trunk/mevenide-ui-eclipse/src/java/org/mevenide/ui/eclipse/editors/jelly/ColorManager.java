/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.editors.jelly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	protected Map colors = new HashMap();
    protected Map colorKeys = new HashMap();
    
    public ColorManager() {
        setColor("TextColor", IXMLColorConstants.DEFAULT);
        setColor("TagColor", IXMLColorConstants.TAG);
        setColor("NS1Color", IXMLColorConstants.DEFAULT);
        setColor("NS2Color", IXMLColorConstants.DEFAULT);
        setColor("NS3Color", IXMLColorConstants.DEFAULT);
        setColor("NS4Color", IXMLColorConstants.DEFAULT);
        setColor("NS5Color", IXMLColorConstants.DEFAULT);
        setColor("NS6Color", IXMLColorConstants.DEFAULT);
        setColor("NS7Color", IXMLColorConstants.DEFAULT);
        setColor("NS8Color", IXMLColorConstants.DEFAULT);
        setColor("NS9Color", IXMLColorConstants.DEFAULT);
        setColor("NS10Color", IXMLColorConstants.DEFAULT);
        setColor("AVColor", IXMLColorConstants.STRING);
        setColor("StringColor", IXMLColorConstants.STRING);
        setColor("ProcessingColor", IXMLColorConstants.PROC_INSTR);
        setColor("EntityColor", IXMLColorConstants.PROC_INSTR);
        setColor("DefinitionColor", IXMLColorConstants.PROC_INSTR);
        setColor("CommentColor", IXMLColorConstants.XML_COMMENT);
    }

	public Color getColor( RGB rgb ) {
		Color color = (Color) colors.get( rgb );

		if ( color == null ) {
			color = new Color( Display.getCurrent(), rgb );
			colors.put( rgb, color );
		}

		return color;
	}
    
    public void setColor(String key, RGB rgb) {
        colorKeys.put(key, new Color(Display.getCurrent(), rgb));
    }

    public Color getColor(String key) {
        return (Color) colorKeys.get(key);
    }

	public void dispose() {
		Iterator i = colors.values().iterator();
		while ( i.hasNext() ) {
			((Color) i.next()).dispose();
		}
	}
}
