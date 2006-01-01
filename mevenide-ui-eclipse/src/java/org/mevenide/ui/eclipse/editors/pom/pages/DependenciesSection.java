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
package org.mevenide.ui.eclipse.editors.pom.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.adapters.properties.DependencyPropertySource;
import org.mevenide.ui.eclipse.adapters.properties.PropertyProxy;
import org.mevenide.ui.eclipse.editors.pom.entries.IPomCollectionAdaptor;
import org.mevenide.ui.eclipse.editors.pom.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.TableEntry;
import org.mevenide.ui.eclipse.repository.view.RepositoryBrowser;
import org.mevenide.ui.eclipse.wizard.NewDependencyWizard;
import org.mevenide.util.MevenideUtils;
import org.mevenide.util.StringUtils;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DependenciesSection extends PageSection {

	private static final Log log = LogFactory.getLog(DependenciesSection.class);
    
    private class LinkListener extends HyperlinkAdapter {
        /**
         * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        public void linkActivated(HyperlinkEvent e) {
            String href = (String) e.getHref();
            if (!StringUtils.isNull(href) && "openBrowser".equals(href)) {
                try {
                    Mevenide.getInstance().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RepositoryBrowser.ID);
                } catch (Exception ex) {
                }
            }
        }
        
    }
    
    private final IHyperlinkListener listener;
    
	private TableEntry  dependenciesTable;
    private TableViewer dependenciesViewer;
	private TableEntry  propertiesTable;
	
	public DependenciesSection(
		DependenciesPage page,
	    Composite parent,
	    FormToolkit toolkit)
	{
		super(page, parent, toolkit, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.FOCUS_TITLE | ExpandableComposite.TITLE_BAR);
		setTitle(MevenideResources.DEPENDENCIES_SECTION_HEADER);
//		setDescription(MevenideResources.DEPENDENCIES_SECTION_DESC);
        
        listener = new LinkListener();
	}

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;

		Composite container = factory.createComposite(parent);
		container.setLayout(layout);

		final Project pom = getPage().getPomEditor().getPom();
		
        createDescriptionWithLink(container, factory, layout.numColumns);
        
		// POM dependencies table
		Button toggle = createOverrideToggle(container, factory, 1, true);
        dependenciesViewer = createTableViewer(container, factory, 1, SWT.MULTI);
		dependenciesTable = new TableEntry(dependenciesViewer, toggle, Mevenide.getResourceString("DependenciesSection.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				List dependencies = (List) value;
				pom.setDependencies(dependencies);
				if ( dependencies != null ) {
				    for (int i = 0; i < dependencies.size(); i++) {
				        Artifact artifact = DefaultArtifactFactory.createArtifact((Dependency) dependencies.get(i));
				        if ( pom.getArtifacts() == null ) {
				            pom.setArtifacts(new ArrayList());
				        }
						pom.getArtifacts().add(artifact);
				    }
				}
			}
			public Object acceptParent() {
				return getParentPom().getDependencies();
			}
		};
		dependenciesTable.addEntryChangeListener(adaptor);
		dependenciesTable.addOverrideAdaptor(adaptor);
		dependenciesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					if ( pom.getDependencies() == null ) {
					    pom.setDependencies(new ArrayList());
					}
                    NewDependencyWizard wizard = new NewDependencyWizard();
					WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	            	dialog.create();
	            	int result = dialog.open();
	            	Dependency dependency = wizard.getDependency();
	            	if ( result == Window.OK && dependency != null ) {
	            		pom.addDependency(dependency);
						Artifact artifact = DefaultArtifactFactory.createArtifact(dependency);
						if ( pom.getArtifacts() == null ) {
						    pom.setArtifacts(new ArrayList());
						}
						pom.getArtifacts().add(artifact);	
	            	}
					return dependency;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					List dependencies = pom.getDependencies();
					if (dependencies != null) {
						dependencies.remove(object);
						dependencies.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					List dependencies = pom.getDependencies();
					if (dependencies != null) {
						dependencies.remove(object);
					}
				}
				public List getDependents(Object parentObject) { return null; }
			}
		);
        
        // Drag-n-Drop support
        int operations = DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getInstance() };
        dependenciesViewer.addDropSupport(
            operations,
            transfers,
            new ViewerDropAdapter(dependenciesViewer) {
                public boolean validateDrop(Object target, int operation, TransferData transferType) {
                    return (operation == DND.DROP_MOVE && LocalSelectionTransfer.getInstance().isSupportedType(transferType));
                }
                public boolean performDrop(Object data) {
                    if (data instanceof IStructuredSelection) {
                        IStructuredSelection selection = (IStructuredSelection) data;
                        List items = new ArrayList();
                        for (Iterator i = selection.iterator(); i.hasNext(); ) {
                            Object item = i.next();
                            if (item instanceof RepoPathElement) {
                                RepoPathElement element = (RepoPathElement) item;
                                if (element.isLeaf()) {
                                    Dependency dep = new Dependency();
                                    dep.setArtifactId(element.getArtifactId());
                                    dep.setGroupId(element.getGroupId());
                                    dep.setType(element.getType());
                                    dep.setVersion(element.getVersion());
                                    
                                    pom.addDependency(dep);
                                    Artifact artifact = DefaultArtifactFactory.createArtifact(dep);
                                    if ( pom.getArtifacts() == null ) {
                                        pom.setArtifacts(new ArrayList());
                                    }
                                    pom.getArtifacts().add(artifact);
                                    
                                    items.add(dep);
                                }
                            }
                            dependenciesTable.addEntries(items);
                            getPage().getPomEditor().setModelDirty(true);
                        }
                    }
                    return true;
                }
            }
        );
		
		// whitespace
		createSpacer(container, factory, isInherited() ? 3 : 2);
		
		// Dependency Property header label
		if (isInherited()) createSpacer(container, factory);
		factory.createLabel(container, Mevenide.getResourceString("DependenciesSection.properties.label"), SWT.BOLD); //$NON-NLS-1$
		createSpacer(container, factory);
		
		// POM dependency properties table
		if (isInherited()) createSpacer(container, factory);
		TableViewer viewer = createTableViewer(container, factory, 1, SWT.SINGLE);
		propertiesTable = new TableEntry(viewer, null, Mevenide.getResourceString("DependenciesSection.properties.tableEntry.tooltip"), container, factory, this); //$NON-NLS-1$
		dependenciesTable.addDependentTableEntry(propertiesTable);
		propertiesTable.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					List properties = (List) propertiesTable.getValue();
					if (log.isDebugEnabled()) {
						log.debug("properties = " + properties); //$NON-NLS-1$
					}
					Dependency dependency = (Dependency) ((TableEntry) entry).getParentPomObject();
					dependency.setProperties(properties);
					dependency.resolvedProperties().clear();
					Iterator itr = properties.iterator();
					while (itr.hasNext()) {
						String[] property = MevenideUtils.resolveProperty((String) itr.next());
						dependency.resolvedProperties().put(property[0], property[1]);
					}
					if (log.isDebugEnabled()) {
						log.debug("resolved properties = " + dependency.resolvedProperties()); //$NON-NLS-1$
					}
				}
			}
		);
		propertiesTable.addPomCollectionAdaptor(
			new IPomCollectionAdaptor() {
				public Object addNewObject(Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					String newPropertyStr = Mevenide.getResourceString("AbstractPomEditorPage.Element.Unknown") + ":" + Mevenide.getResourceString("AbstractPomEditorPage.Element.Unknown"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					PropertyProxy newProperty = new PropertyProxy(newPropertyStr);
					if ( dependency.getProperties() == null ) {
					    pom.setProperties(new ArrayList());
					}
					dependency.addProperty(newPropertyStr);
					return newProperty;
				}
				public void moveObjectTo(int index, Object object, Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					if (properties != null) {
						properties.remove(object);
						properties.add(index, object);
					}
				}
				public void removeObject(Object object, Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					if (properties != null) {
						properties.remove(object);
					}
				}
				public List getDependents(Object parentObject) {
					Dependency dependency = (Dependency) parentObject;
					List properties = dependency.getProperties();
					List propertyProxies = new ArrayList(properties.size());
					Iterator itr = properties.iterator();
					while (itr.hasNext()) {
						propertyProxies.add(new PropertyProxy((String) itr.next()));
					}
					return propertyProxies;
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
	}
    
    private void createDescriptionWithLink(Composite parent, FormToolkit factory, int nColumns) {
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = nColumns;
        
        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        data.horizontalSpan = nColumns;

        Composite container = factory.createComposite(parent);
        container.setLayout(layout);
        container.setLayoutData(data);
//        container.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        
        FormText text = factory.createFormText(container, true);
        text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        text.setText(MevenideResources.DEPENDENCIES_SECTION_REPOSITORY_LINK, true, false);
        text.addHyperlinkListener(this.listener);
    }

	public void update(Project pom) {
		dependenciesTable.removeAll();
		List dependencies = pom.getDependencies();
		List parentDependencies = isInherited() ? getParentPom().getDependencies() : null;
		if (dependencies != null && !dependencies.isEmpty()) {
		    for (Iterator iter = dependencies.iterator(); iter.hasNext(); ) {
		        Dependency element = (Dependency) iter.next();
		        if (element.getType() == null) {
		            element.setType("jar"); //$NON-NLS-1$
		        }
		    }
			dependenciesTable.addEntries(dependencies);
			dependenciesTable.setInherited(false);
		}
		else if (parentDependencies != null) {
			dependenciesTable.addEntries(dependencies, true);
			dependenciesTable.setInherited(true);
		}
		else {
			dependenciesTable.setInherited(false);
		}
	}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        if (input != null && input instanceof Dependency) {
            Dependency dep = (Dependency) input;
            TableItem[] items = dependenciesViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                DependencyPropertySource src = (DependencyPropertySource) items[i].getData();
                if (src.getSource().equals(dep)) {
                    ensureExpanded();
                    dependenciesViewer.getTable().select(i);
                    return true;
                }
            }
        }
        return super.setFormInput(input);
    }

}
