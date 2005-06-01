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
