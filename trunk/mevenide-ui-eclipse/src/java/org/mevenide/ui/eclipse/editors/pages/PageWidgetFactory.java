/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.pages;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * Factory for creating SWT widgets for the POM editor ui.
 * 
 * @refactor NEEDS MASSIVE OVERHAUL - basically copy-n-paste from eclipse update.ui.forms
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PageWidgetFactory {

	public static final String KEY_DRAW_BORDER = "FormWidgetFactory.drawBorder";
	public static final String TREE_BORDER = "treeBorder";
	public static final String DEFAULT_HEADER_COLOR = "__default__header__";
	public static final String COLOR_BORDER = "__border";
	public static final String COLOR_COMPOSITE_SEPARATOR = "__compSep";

	private Hashtable colorRegistry = new Hashtable();
	private Color backgroundColor;
	private KeyListener deleteListener;
	private Color foregroundColor;
	private Display display;
	public static final int BORDER_STYLE = SWT.NONE; //SWT.BORDER;
	private BorderPainter borderPainter;
	private Color borderColor;
//	private HyperlinkHandler hyperlinkHandler;
	/* default */ VisibilityHandler visibilityHandler;
	/* default */ KeyboardHandler keyboardHandler;

	class BorderPainter implements PaintListener {
		public void paintControl(PaintEvent event) {
			Composite composite = (Composite) event.widget;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control c = children[i];
				boolean inactiveBorder=false;
				if (c.getEnabled() == false && !(c instanceof CCombo))
					continue;
//				if (c instanceof SelectableFormLabel)
//					continue;
				Object flag = c.getData(KEY_DRAW_BORDER);
				if (flag!=null) {
					if (flag.equals(Boolean.FALSE)) continue;
					if (flag.equals(TREE_BORDER)) inactiveBorder=true;
				}
				 
				if (!inactiveBorder && (c instanceof Text || c instanceof Canvas || c instanceof CCombo)) {
					Rectangle b = c.getBounds();
					GC gc = event.gc;
					gc.setForeground(c.getBackground());
					gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1, b.height + 1);
					gc.setForeground(foregroundColor);
					if (c instanceof CCombo)
						gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1, b.height + 1);
					else 
						gc.drawRectangle(b.x - 1, b.y - 2, b.width + 1, b.height + 3);
				} else if (inactiveBorder ||c instanceof Table || c instanceof Tree || c instanceof TableTree) {
					Rectangle b = c.getBounds();
					GC gc = event.gc;
					gc.setForeground(borderColor);
					//gc.drawRectangle(b.x - 2, b.y - 2, b.width + 3, b.height + 3);
					gc.drawRectangle(b.x - 1, b.y - 1, b.width + 2, b.height + 2);
				}
			}
		}
	}

	class VisibilityHandler extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			Widget w = e.widget;
			if (w instanceof Control) {
//				ensureVisible((Control) w);
			}
		}
	}

	class KeyboardHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			Widget w = e.widget;
			if (w instanceof Control) {
//				processKey(e.keyCode, (Control) w);
			}
		}
	}

	public PageWidgetFactory() {
		this(Display.getCurrent());
	}

	public PageWidgetFactory(Display display) {
		this.display = display;
		initialize();
	}

	public static ScrolledComposite getScrolledComposite(Control c) {
		Composite parent = c.getParent();

		while (parent != null) {
			if (parent instanceof ScrolledComposite) {
				return (ScrolledComposite) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

//	public static void ensureVisible(Control c) {
//		ScrolledComposite scomp = getScrolledComposite(c);
//		if (scomp != null) {
//			AbstractSectionForm.ensureVisible(scomp, c);
//		}
//	}
//
//	public static void processKey(int keyCode, Control c) {
//		ScrolledComposite scomp = getScrolledComposite(c);
//		if (scomp != null) {
//			switch (keyCode) {
//				case SWT.ARROW_DOWN :
//					AbstractSectionForm.scrollVertical(scomp, false);
//					break;
//				case SWT.ARROW_UP :
//					AbstractSectionForm.scrollVertical(scomp, true);
//					break;
//				case SWT.ARROW_LEFT :
//					AbstractSectionForm.scrollHorizontal(scomp, true);
//					break;
//				case SWT.ARROW_RIGHT :
//					AbstractSectionForm.scrollHorizontal(scomp, false);
//					break;
//				case SWT.PAGE_UP :
//					AbstractSectionForm.scrollPage(scomp, true);
//					break;
//				case SWT.PAGE_DOWN :
//					AbstractSectionForm.scrollPage(scomp, false);
//					break;
//			}
//		}
//	}

	public Button createButton(Composite parent, String text, int style) {
		int flatStyle = BORDER_STYLE == SWT.BORDER ? SWT.NULL : SWT.FLAT;
		//int flatStyle = SWT.NULL;
		Button button = new Button(parent, style | flatStyle);
		button.setBackground(backgroundColor);
		button.setForeground(foregroundColor);
		if (text != null)
			button.setText(text);
		button.addFocusListener(visibilityHandler);
		return button;
	}
	public Composite createComposite(Composite parent) {
		return createComposite(parent, SWT.NULL);
	}
	public Composite createComposite(Composite parent, int style) {
		Composite composite = new Composite(parent, style);
		composite.setBackground(backgroundColor);
		composite.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				((Control) e.widget).setFocus();
			}
		});
		composite.setMenu(parent.getMenu());
		return composite;
	}
	public Composite createCompositeSeparator(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(getColor(COLOR_COMPOSITE_SEPARATOR));
		return composite;
	}

	public Label createHeadingLabel(Composite parent, String text) {
		return createHeadingLabel(parent, text, null, SWT.NONE);
	}

	public Label createHeadingLabel(Composite parent, String text, int style) {
		return createHeadingLabel(parent, text, null, style);
	}

	public Label createHeadingLabel(Composite parent, String text, Color bg) {
		return createHeadingLabel(parent, text, bg, SWT.NONE);
	}

	public Label createHeadingLabel(
		Composite parent,
		String text,
		Color bg,
		int style) {
		Label label = new Label(parent, style);
		if (text != null)
			label.setText(text);
		label.setBackground(backgroundColor);
		label.setForeground(foregroundColor);
		label.setFont(JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT));
		return label;
	}

//	public Label createHyperlinkLabel(
//		Composite parent,
//		String text,
//		IHyperlinkListener listener) {
//		return createHyperlinkLabel(parent, text, listener, SWT.NULL);
//	}
//	public Label createHyperlinkLabel(
//		Composite parent,
//		String text,
//		IHyperlinkListener listener,
//		int style) {
//		Label label = createLabel(parent, text, style);
//		turnIntoHyperlink(label, listener);
//		return label;
//	}
	public Label createLabel(Composite parent, String text) {
		return createLabel(parent, text, SWT.NONE);
	}
	public Label createLabel(Composite parent, String text, int style) {
		Label label = new Label(parent, style);
		if (text != null)
			label.setText(text);
		label.setBackground(backgroundColor);
		label.setForeground(foregroundColor);
		return label;
	}

//	public SelectableFormLabel createSelectableLabel(
//		Composite parent,
//		String text) {
//		return createSelectableLabel(parent, text, SWT.NONE);
//	}
//
//	public SelectableFormLabel createSelectableLabel(
//		Composite parent,
//		String text,
//		int style) {
//		SelectableFormLabel label = new SelectableFormLabel(parent, style);
//		if (text != null)
//			label.setText(text);
//		label.setBackground(backgroundColor);
//		label.setForeground(foregroundColor);
//		label.addFocusListener(visibilityHandler);
//		label.addKeyListener(keyboardHandler);
//		return label;
//	}
//
//	public FormEngine createFormEngine(Composite parent) {
//		return createFormEngine(parent, false);
//	}
//	
//	public FormEngine createFormEngine(Composite parent, boolean trackFocus) {
//		FormEngine engine = new FormEngine(parent, SWT.WRAP);
//		engine.setBackground(backgroundColor);
//		engine.setForeground(foregroundColor);
//		engine.marginWidth = 1;
//		engine.marginHeight = 0;
//		engine.setHyperlinkSettings(getHyperlinkHandler());
//		if (trackFocus) engine.addFocusListener(visibilityHandler);
//		engine.addKeyListener(keyboardHandler);
//		engine.setMenu(parent.getMenu());
//		return engine;
//	}

	public Label createSeparator(Composite parent, int style) {
		Label label = new Label(parent, SWT.SEPARATOR | style);
		label.setBackground(backgroundColor);
		label.setForeground(borderColor);
		return label;
	}
	public Table createTable(Composite parent, int style) {
		Table table = new Table(parent, BORDER_STYLE | style);
		table.setBackground(backgroundColor);
		table.setForeground(foregroundColor);
		hookDeleteListener(table);
		return table;
	}
	public Text createText(Composite parent, String value) {
		return createText(parent, value, BORDER_STYLE | SWT.SINGLE);
	}
	public Text createText(Composite parent, String value, int style) {
		Text text = new Text(parent, style);
		if (value != null)
			text.setText(value);
		text.setBackground(backgroundColor);
		text.setForeground(foregroundColor);
		text.addFocusListener(visibilityHandler);
		return text;
	}
	public Tree createTree(Composite parent, int style) {
		Tree tree = new Tree(parent, BORDER_STYLE | style);
		tree.setBackground(backgroundColor);
		tree.setForeground(foregroundColor);
		hookDeleteListener(tree);
		return tree;
	}
	private void deleteKeyPressed(Widget widget) {
		if (!(widget instanceof Control))
			return;
		Control control = (Control) widget;
		for (Control parent = control.getParent();
			parent != null;
			parent = parent.getParent()) {
			if (parent.getData() instanceof PageSection) {
				PageSection section = (PageSection) parent.getData();
				section.doGlobalAction(IWorkbenchActionConstants.DELETE);
				break;
			}
		}
	}
	public void dispose() {
		Enumeration colors = colorRegistry.elements();
		while (colors.hasMoreElements()) {
			Color c = (Color) colors.nextElement();
			c.dispose();
		}
//		hyperlinkHandler.dispose();
		colorRegistry = null;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public Color getBorderColor() {
		return borderColor;
	}
//	public Cursor getBusyCursor() {
//		return hyperlinkHandler.getBusyCursor();
//	}
	public Color getColor(String key) {
		return (Color) colorRegistry.get(key);
	}
	public Color getForegroundColor() {
		return foregroundColor;
	}
//	public HyperlinkHandler getHyperlinkHandler() {
//		return hyperlinkHandler;
//	}
//	public Cursor getHyperlinkCursor() {
//		return hyperlinkHandler.getHyperlinkCursor();
//	}
//	public Color getHyperlinkColor() {
//		return hyperlinkHandler.getForeground();
//	}
//	public Color getHyperlinkHoverColor() {
//		return hyperlinkHandler.getActiveForeground();
//	}
//	public int getHyperlinkUnderlineMode() {
//		return hyperlinkHandler.getHyperlinkUnderlineMode();
//	}
	public void hookDeleteListener(Control control) {
		if (deleteListener == null) {
			deleteListener = new KeyAdapter() {
				public void keyPressed(KeyEvent event) {
					if (event.character == SWT.DEL && event.stateMask == 0) {
						deleteKeyPressed(event.widget);
					}
				}
			};
		}
		control.addKeyListener(deleteListener);
	}
	private void initialize() {
		backgroundColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		registerColor(COLOR_BORDER, 195, 191, 179);
		registerColor(COLOR_COMPOSITE_SEPARATOR, 152, 170, 203);
		registerColor(DEFAULT_HEADER_COLOR, 0x48, 0x70, 0x98);
		if (isWhiteBackground())
			borderColor = getColor(COLOR_BORDER);
		else
			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		foregroundColor = display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
//		hyperlinkHandler = new HyperlinkHandler();
//		hyperlinkHandler.setBackground(backgroundColor);
//		updateHyperlinkColors();
		visibilityHandler = new VisibilityHandler();
		keyboardHandler = new KeyboardHandler();
	}
	
	public boolean isWhiteBackground() {
		return backgroundColor.getRed()==255 && backgroundColor.getGreen()==255 &&
			backgroundColor.getBlue()==255;
	}

//	public void updateHyperlinkColors() {
//		Color hyperlinkColor = JFaceColors.getHyperlinkText(display);
//		Color activeHyperlinkColor = JFaceColors.getActiveHyperlinkText(display);
//		hyperlinkHandler.setForeground(hyperlinkColor);
//		hyperlinkHandler.setActiveForeground(activeHyperlinkColor);
//	}

	public void paintBordersFor(Composite parent) {
		if (BORDER_STYLE == SWT.BORDER)
			return;
		if (borderPainter == null)
			borderPainter = new BorderPainter();
		parent.addPaintListener(borderPainter);
	}
	public Color registerColor(String key, int r, int g, int b) {
		Color c = new Color(display, r, g, b);
		colorRegistry.put(key, c);
		return c;
	}
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}
//	public void setHyperlinkColor(Color color) {
//		hyperlinkHandler.setForeground(color);
//	}
//	public void setHyperlinkHoverColor(org.eclipse.swt.graphics.Color hoverColor) {
//		hyperlinkHandler.setActiveForeground(hoverColor);
//	}
//	public void setHyperlinkUnderlineMode(int newHyperlinkUnderlineMode) {
//		hyperlinkHandler.setHyperlinkUnderlineMode(newHyperlinkUnderlineMode);
//	}
//	public void turnIntoHyperlink(Control control, IHyperlinkListener listener) {
//		hyperlinkHandler.registerHyperlink(control, listener);
//	}

}
