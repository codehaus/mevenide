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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.ui.eclipse.editors.entries.*;

/**
 * Abstract base class for a section of a page in the POM Editor ui.
 * 
 * @refactor NEEDS MASSIVE OVERHAUL - basically a cut-n-paste from eclipse update.ui.forms
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class PageSection {

	private static final Log log = LogFactory.getLog(PageSection.class);
    
	public static final int SELECTION = 1;
	private String headerColorKey = PageWidgetFactory.DEFAULT_HEADER_COLOR;
	protected Control client;
	protected Label header;
	protected Control separator;

	private String headerText;
	private String description;
	private boolean dirty;
	protected Label descriptionLabel;
//	private ToggleControl toggle;
	private boolean readOnly;
	private boolean addSeparator = true;
	private boolean descriptionPainted = true;
	private boolean headerPainted = true;
	private boolean collapsable = false;
//	private boolean collapsed = false;
	private int widthHint = SWT.DEFAULT;
	private int heightHint = SWT.DEFAULT;
	private Composite control;
//	public boolean compactMode=false;

	private AbstractPomEditorPage page;
	
	private boolean inherited;
	private Project parentPom;

	class EntryChangeListenerAdaptor implements IEntryChangeListener {
		public void entryChanged(PageEntry entry) {
		}
		public void entryDirty(PageEntry entry) {
			getPage().getEditor().setModelDirty(true);
            if (log.isDebugEnabled()) {
                log.debug("entry was changed!");
            }
		}
	}
	
	abstract class OverrideAdaptor implements IOverrideAdaptor, IEntryChangeListener {
        public void refreshUI() {
        	redrawSection();
        }
		public void entryChanged(PageEntry entry) {
			if (log.isDebugEnabled()) {
				log.debug("overridable entry change committed! " + entry.getValue());
			}
			overrideParent(entry.getValue());
		}
		public void entryDirty(PageEntry entry) {
			getPage().getEditor().setModelDirty(true);
			if (log.isDebugEnabled()) {
				log.debug("overridable entry was changed!");
			}
		}
	}

	class SectionLayout extends Layout { //implements ILayoutExtension {
		int vspacing = 3;
		int sepHeight = 2;

		public int getMinimumWidth(Composite parent, boolean flush) {
			return 30;
		}

		public int getMaximumWidth(Composite parent, boolean flush) {
			int maxWidth = 0;
			if (client != null) {
//				if (client instanceof Composite) {
//					Layout cl = ((Composite) client).getLayout();
//					if (cl instanceof ILayoutExtension)
//						maxWidth =
//							((ILayoutExtension) cl).getMaximumWidth(
//								(Composite) client,
//								flush);
//				}
				if (maxWidth == 0) {
					Point csize =
						client.computeSize(SWT.DEFAULT, SWT.DEFAULT, flush);
					maxWidth = csize.x;
				}
			}
			if (headerPainted && header != null) {
				Point hsize =
					header.computeSize(SWT.DEFAULT, SWT.DEFAULT, flush);
				maxWidth = Math.max(maxWidth, hsize.x);
			}
			if (descriptionPainted && descriptionLabel != null) {
				Point dsize =
					descriptionLabel.computeSize(
						SWT.DEFAULT,
						SWT.DEFAULT,
						flush);
				maxWidth = Math.max(maxWidth, dsize.x);
			}
			return maxWidth;
		}

		protected Point computeSize(
			Composite parent,
			int wHint,
			int hHint,
			boolean flush) {
			int width = 0;
			int height = 0;
			int cwidth = 0;
			int collapsedHeight = 0;

			if (wHint != SWT.DEFAULT)
				width = wHint;
			if (hHint != SWT.DEFAULT)
				height = hHint;

			cwidth = width;

			if (client != null && !client.isDisposed()) {
//				if (toggle != null && toggle.getSelection() && compactMode) {
//				}
//				else {
				//Point csize = client.computeSize(SWT.DEFAULT, SWT.DEFAULT, flush);
				Point csize = client.computeSize(wHint, SWT.DEFAULT);
				if (width == 0) {
					width = csize.x;
					cwidth = width;
				}
				if (height == 0)
					height = csize.y;
				}
//			}

			Point toggleSize = null;

//			if (collapsable && toggle != null)
//				toggleSize =
//					toggle.computeSize(SWT.DEFAULT, SWT.DEFAULT, flush);

			if (hHint == SWT.DEFAULT && headerPainted && header != null) {
				int hwidth = cwidth;
				if (toggleSize != null)
					hwidth = cwidth - toggleSize.x - 5;
				Point hsize = header.computeSize(hwidth, SWT.DEFAULT, flush);
				height += hsize.y;
				collapsedHeight = hsize.y;
				height += vspacing;
			}

			if (hHint == SWT.DEFAULT && addSeparator) {
				height += sepHeight;
				height += vspacing;
				collapsedHeight += vspacing + sepHeight;
			}
			if (hHint == SWT.DEFAULT
				&& descriptionPainted
				&& descriptionLabel != null) {
				Point dsize =
					descriptionLabel.computeSize(cwidth, SWT.DEFAULT, flush);
				height += dsize.y;
				height += vspacing;
			}
//			if (toggle != null && toggle.getSelection()) {
//				// collapsed state
//				height = collapsedHeight;
//			}
			return new Point(width, height);
		}
		protected void layout(Composite parent, boolean flush) {
			int width = parent.getClientArea().width;
			int height = parent.getClientArea().height;
			int y = 0;
			Point toggleSize = null;

			if (collapsable) {
//				toggleSize =
//					toggle.computeSize(SWT.DEFAULT, SWT.DEFAULT, flush);
			}
			if (headerPainted && header != null) {
				Point hsize;

				int availableWidth = width;
				if (toggleSize != null)
					availableWidth = width - toggleSize.x - 5;
				hsize = header.computeSize(availableWidth, SWT.DEFAULT, flush);
				int hx = 0;
//				if (toggle != null) {
//					int ty = y + hsize.y - toggleSize.y;
//					toggle.setBounds(0, ty, toggleSize.x, toggleSize.y);
//					hx = toggleSize.x; // + 5;
//				}
				header.setBounds(hx, y, availableWidth, hsize.y);

				y += hsize.y + vspacing;
			}
			if (addSeparator && separator != null) {
				separator.setBounds(0, y, width, 2);
				y += sepHeight + vspacing;
			}
//			if (toggle != null && toggle.getSelection()) {
//				return;
//			}
			if (descriptionPainted && descriptionLabel != null) {
				Point dsize =
					descriptionLabel.computeSize(width, SWT.DEFAULT, flush);
				descriptionLabel.setBounds(0, y, width, dsize.y);
				y += dsize.y + vspacing;
			}
			if (client != null) {
				client.setBounds(0, y, width, height - y);
			}
		}
	}

    public PageSection(AbstractPomEditorPage page) {
        this.page = page;
		
		this.parentPom = page.getEditor().getParentPom();
		if (parentPom != null) inherited = true;
    }

	public abstract Composite createClient(
		Composite parent,
		PageWidgetFactory factory);
		
	public final Control createControl(
		Composite parent,
		final PageWidgetFactory factory) {
			
		Composite section = factory.createComposite(parent);
		SectionLayout slayout = new SectionLayout();
		section.setLayout(slayout);
		section.setData(this);

		if (headerPainted) {
			Color headerColor = factory.getColor(getHeaderColorKey());
			header =
				factory.createHeadingLabel(
					section,
					getHeaderText(),
					headerColor,
					SWT.WRAP);
//			if (collapsable) {
//				toggle = new ToggleControl(section, SWT.NULL);
//				toggle.setSelection(collapsed);
//				toggle.setBackground(factory.getBackgroundColor());
//				toggle.setActiveDecorationColor(factory.getHyperlinkColor());
//				toggle.setDecorationColor(
//					factory.getColor(
//						FormWidgetFactory.COLOR_COMPOSITE_SEPARATOR));
//				toggle.setActiveCursor(factory.getHyperlinkCursor());
//				toggle.addFocusListener(factory.visibilityHandler);
//				toggle.addKeyListener(factory.keyboardHandler);
//				toggle.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						doToggle();
//					}
//				});
//				header.addMouseListener(new MouseAdapter() {
//					public void mouseDown(MouseEvent e) {
//						toggle.setSelection(!toggle.getSelection());
//						toggle.redraw();
//						doToggle();
//					}
//				});
//				header.addMouseTrackListener(new MouseTrackAdapter() {
//					public void mouseEnter(MouseEvent e) {
//						header.setCursor(factory.getHyperlinkCursor());
//					}
//					public void mouseExit(MouseEvent e) {
//						header.setCursor(null);
//					}
//				});
//			}
		}

		if (addSeparator) {
			//separator = factory.createSeparator(section, SWT.HORIZONTAL);
			separator = factory.createCompositeSeparator(section);
		}

		if (descriptionPainted && description != null) {
			descriptionLabel =
				factory.createLabel(section, description, SWT.WRAP);
		}
		client = createClient(section, factory);
		section.setData(this);
		control = section;
		return section;
	}

	protected void reflow() {
		control.setRedraw(false);
		control.getParent().setRedraw(false);
		control.layout(true);
		control.getParent().layout(true);
		control.setRedraw(true);
		control.getParent().setRedraw(true);
	}
	
	protected Label createSpacer(
		Composite parent, 
		PageWidgetFactory factory) {

		return createSpacer(parent, factory, 1);
	}
	
	protected Label createSpacer(
		Composite parent, 
		PageWidgetFactory factory,
		int span) {

		Label spacer = factory.createSpacer(parent);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		data.widthHint = 5;
		data.heightHint = 5;
		spacer.setLayoutData(data);
		return spacer;
	}
	
	protected Label createLabel(
		Composite parent, 
		String label,
		PageWidgetFactory factory) {

		return createLabel(parent, label, null, factory);
	}
	
	protected Label createLabel(
		Composite parent, 
		String label,
		String tooltip,
		PageWidgetFactory factory) {

		Label widget = factory.createLabel(parent, label);
		if (tooltip != null) {
			widget.setToolTipText(tooltip);
		}
		return widget;
	}
	
	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory) {

		return createOverrideToggle(parent, factory, 1);
	}
	
	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory,
		int span) {
		
		return createOverrideToggle(parent, factory, span, false);			
	}

	protected Button createOverrideToggle(
		Composite parent, 
		PageWidgetFactory factory,
		int span,
		boolean alignTop) {

		Button inheritanceToggle = null;
		if (isInherited()) {
			inheritanceToggle = factory.createButton(parent, " ", SWT.CHECK);
			int vAlign = alignTop 
				? GridData.VERTICAL_ALIGN_BEGINNING 
				: GridData.VERTICAL_ALIGN_CENTER;
			GridData data = new GridData(vAlign | GridData.HORIZONTAL_ALIGN_BEGINNING);
			data.horizontalSpan = span;
			data.widthHint = 12;
			data.heightHint = 12;
			inheritanceToggle.setLayoutData(data);
			inheritanceToggle.setSize(SWT.DEFAULT, SWT.DEFAULT);
		}
		return inheritanceToggle;
	}
	
	protected Button createBrowseButton(
		Composite parent, 
		PageWidgetFactory factory,
		String label,
		String tooltip,
		int span) {

		Composite buttonContainer = factory.createComposite(parent);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = span;
		buttonContainer.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		Button browseButton = factory.createButton(buttonContainer, label, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		browseButton.setLayoutData(data);
		browseButton.setToolTipText(tooltip);
		
		return browseButton;
	}

	protected Text createMultilineText(
		Composite parent,
		PageWidgetFactory factory) {

		Text text = factory.createText(parent, "", SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 80;
		text.setLayoutData(data);
		return text;
	}
		
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory) {

		return createText(parent, factory, 1);
	}
		
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory,
		int span) {

		return createText(parent, factory, span, SWT.NONE);
	}
	
	protected Text createText(
		Composite parent,
		PageWidgetFactory factory,
		int span,
		int style) {

		Text text = factory.createText(parent, "", style);
		int hfill = span == 1
			? GridData.FILL_HORIZONTAL
			: GridData.HORIZONTAL_ALIGN_FILL;
		GridData gd = new GridData(hfill | GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = span;
		text.setLayoutData(gd);
		return text;
	}
		
	protected TableViewer createTableViewer(
		Composite parent, 
		PageWidgetFactory factory,
		int style) {
			
		Table table = factory.createTable(parent, style);
		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		
		return viewer;
	}

	public void dispose() {
//		JFaceResources.getFontRegistry().removeListener(this);
	}
	
	public boolean doGlobalAction(String actionId) {
		return false;
	}
	
	public void expandTo(Object object) {
	}
	
	public final void fireChangeNotification(
		int changeType,
		Object changeObject) {
//		if (sectionManager == null)
//			return;
//		sectionManager.dispatchNotification(this, changeType, changeObject);
	}
	
	public final void fireSelectionNotification(Object changeObject) {
		fireChangeNotification(SELECTION, changeObject);
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getHeaderColorKey() {
		return headerColorKey;
	}
	
	public String getHeaderText() {
		return headerText;
	}
	
	public int getHeightHint() {
		return heightHint;
	}
	
	public int getWidthHint() {
		return widthHint;
	}
	
	public void initialize(Object input) {
	}
	
	public boolean isAddSeparator() {
		return addSeparator;
	}
	
	public boolean isDescriptionPainted() {
		return descriptionPainted;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public boolean isHeaderPainted() {
		return headerPainted;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void sectionChanged(
		PageSection source,
		int changeType,
		Object changeObject) {
	}
	
	public void setAddSeparator(boolean newAddSeparator) {
		addSeparator = newAddSeparator;
	}

	private String trimNewLines(String text) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n')
				buff.append(' ');
			else
				buff.append(c);
		}
		return buff.toString();
	}

	public void setDescription(java.lang.String newDescription) {
		// we will trim the new lines so that we can
		// use layout-based word wrapping instead
		// of hard-coded one
		description = trimNewLines(newDescription);
		//description = newDescription;
		if (descriptionLabel != null)
			descriptionLabel.setText(newDescription);
	}
	
	public void setDescriptionPainted(boolean newDescriptionPainted) {
		descriptionPainted = newDescriptionPainted;
	}
	
	public void setDirty(boolean newDirty) {
		dirty = newDirty;
	}
	
	public void setHeaderColorKey(java.lang.String newHeaderColorKey) {
		headerColorKey = newHeaderColorKey;
	}

	public void setHeaderPainted(boolean newHeaderPainted) {
		headerPainted = newHeaderPainted;
	}

	public void setHeaderText(String newHeaderText) {
		headerText = newHeaderText;
		if (header != null)
			header.setText(headerText);
	}

	public void setHeightHint(int newHeightHint) {
		heightHint = newHeightHint;
	}

	public void setWidthHint(int newWidthHint) {
		widthHint = newWidthHint;
	}

	public void update(Project pom) {
		redrawSection();
	}
	
	protected void redrawSection() {
		Display display = getPage().getEditor().getSite().getShell().getDisplay();
		display.asyncExec(
			new Runnable() {
				public void run() {
					client.redraw();
				}
			}
		);
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (control != null && header != null) {
			header.setFont(JFaceResources.getBannerFont());
			control.layout(true);
		}
	}

    public AbstractPomEditorPage getPage() {
        return page;
    }
    
    protected void setIfDefined(TextEntry entry, String text) {
    	if (text != null) {
    		entry.setText(text, true);
    	}
    }
    
	protected void setIfDefined(OverridableTextEntry entry, String text, String parentText) {
		if (text != null) {
			entry.setText(text, true);
			entry.setInherited(false);
		}
		else if (parentText != null) {
			entry.setText(parentText, true);
			entry.setInherited(true);
		}
		else {
			entry.setInherited(false);
		}
	}
    
    protected boolean isDefined(String value) {
    	return (value != null && !"".equals(value.trim()));
    }

	protected boolean isInherited() {
        return inherited;
    }

    protected Project getParentPom() {
        return parentPom;
    }

    protected void setParentPom(Project parentPom) {
        this.parentPom = parentPom;
    }

}
