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
package org.mevenide.netbeans.project.customizer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.mevenide.properties.IPropertyLocator;
import org.openide.util.Utilities;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class LocationComboBox extends JButton {
    private LocationWrapper current;
    private LocationWrapper[] all;
    private JPopupMenu currentLoc;
    private HashMap actionToLoc;
    private OriginChange.ChangeObserver observer;
    private boolean showText;
    
    public LocationComboBox(boolean show) {
        showText = show;
        setMargin(new Insets(0,0,0,0));
        setBorderPainted(true);
        setBorder(new MyBorder());
        setRolloverEnabled(true);
        currentLoc = new JPopupMenu();
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                currentLoc.show(LocationComboBox.this, 0, LocationComboBox.this.getSize().height);
            }
        });
        actionToLoc = new HashMap();
        actionToLoc.put(OriginChange.ACTION_DEFINE_IN_BUILD, new Integer(IPropertyLocator.LOCATION_PROJECT_BUILD));
        actionToLoc.put(OriginChange.ACTION_MOVE_TO_BUILD, new Integer(IPropertyLocator.LOCATION_PROJECT_BUILD));
        actionToLoc.put(OriginChange.ACTION_DEFINE_IN_PROJECT, new Integer(IPropertyLocator.LOCATION_PROJECT));
        actionToLoc.put(OriginChange.ACTION_MOVE_TO_PROJECT, new Integer(IPropertyLocator.LOCATION_PROJECT));
        actionToLoc.put(OriginChange.ACTION_DEFINE_IN_USER, new Integer(IPropertyLocator.LOCATION_USER_BUILD));
        actionToLoc.put(OriginChange.ACTION_MOVE_TO_USER, new Integer(IPropertyLocator.LOCATION_USER_BUILD));
        actionToLoc.put(OriginChange.ACTION_RESET_TO_DEFAULT, new Integer(IPropertyLocator.LOCATION_DEFAULTS));
        actionToLoc.put(OriginChange.ACTION_MOVE_TO_PARENT_PROJECT, new Integer(IPropertyLocator.LOCATION_PARENT_PROJECT));
        actionToLoc.put(OriginChange.ACTION_DEFINE_IN_PARENT_PROJECT, new Integer(IPropertyLocator.LOCATION_PARENT_PROJECT));
        actionToLoc.put(OriginChange.ACTION_MOVE_TO_PARENTBUILD, new Integer(IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD));
        actionToLoc.put(OriginChange.ACTION_DEFINE_IN_PARENT_BUILD, new Integer(IPropertyLocator.LOCATION_PARENT_PROJECT_BUILD));

        actionToLoc.put(OriginChange.ACTION_POM_MOVE_TO_CHILD, new Integer(OriginChange.LOCATION_POM));
        actionToLoc.put(OriginChange.ACTION_POM_MOVE_TO_PARENT, new Integer(OriginChange.LOCATION_POM_PARENT));
        actionToLoc.put(OriginChange.ACTION_POM_MOVE_TO_PP, new Integer(OriginChange.LOCATION_POM_PARENT_PARENT));
        actionToLoc.put(OriginChange.ACTION_REMOVE_ENTRY, new Integer(IPropertyLocator.LOCATION_NOT_DEFINED));
        
//        add(currentLoc);
        setIcon(new ImageIcon(Utilities.loadImage("org/openide/resources/actions/empty.gif")));
        setText("");
    }
    
    public LocationWrapper getSelectedItem() {
        return current;
    }
    
    public void setInitialItem(int location) {
        LocationWrapper selected = findWrapper(location);
        setSelectedItem(selected);
    }
    
    private void setSelectedItem(LocationWrapper selected) {
        setToolTipText(selected.getName());
        setIcon(selected.getIcon());
        setDisabledIcon(selected.getIcon());
        if (showText) {
            setText(selected.getName());
        }
        currentLoc.removeAll();
        initStatePopup(selected.getActions());
        current = selected;
    }
    
    public void setItems(LocationWrapper[] wrappers) {
        all = wrappers;
    }
    
    public LocationWrapper[] getItems() {
        return all;
    }
    
    public void invokePopupAction(int location) {
        setSelectedItem(findWrapper(location));
    }
    
    public void invokePopupAction(String action) {
        Integer loc = (Integer)actionToLoc.get(action);
        if (loc != null) {
            invokePopupAction(loc.intValue());
            if (observer != null) {
                observer.actionSelected(action);
            }
        } else {
            throw new IllegalArgumentException("Unknown action=" + action);
        }
    }
    
    private LocationWrapper findWrapper(int location) {
        for (int i = 0; i < all.length; i++) {
            if (all[i].getID() == location) {
                return all[i];
            }
        }
        throw new IllegalStateException("Wrong location of prop file=" + location);
    }
    
    public void setChangeObserver(OriginChange.ChangeObserver obs) {
        observer = obs;
    }
    
    private void initStatePopup(String[] actions) {
        String name = "";
        Icon icon = null;
        if (actions == null) {
            return;
        }
        for (int i = 0; i < actions.length; i++) {
            if (OriginChange.ACTION_DEFINE_IN_USER.equals(actions[i])) {
                name = "Define in User";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToUser.png"));
            } else if (OriginChange.ACTION_DEFINE_IN_PROJECT.equals(actions[i])) {
                name = "Define in Project";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToProject.png"));
            } else if (OriginChange.ACTION_DEFINE_IN_BUILD.equals(actions[i])) {
                name = "Define in Build";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToBuild.png"));
            } else if (OriginChange.ACTION_MOVE_TO_BUILD.equals(actions[i])) {
                name = "Move to Build";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToBuild.png"));
            } else if (OriginChange.ACTION_MOVE_TO_PROJECT.equals(actions[i])) {
                name = "Move to Project";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToProject.png"));
            } else if (OriginChange.ACTION_MOVE_TO_USER.equals(actions[i])) {
                name = "Move to User";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToUser.png"));
            } else if (OriginChange.ACTION_RESET_TO_DEFAULT.equals(actions[i])) {
                name = "Reset to Default";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
            } else if (OriginChange.ACTION_POM_MOVE_TO_CHILD.equals(actions[i])) {
                name = "Move to POM";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
            } else if (OriginChange.ACTION_POM_MOVE_TO_PARENT.equals(actions[i])) {
                name = "Move to Parent";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
            } else if (OriginChange.ACTION_POM_MOVE_TO_PP.equals(actions[i])) {
                name = "Move to Grand Parent";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
            } else if (OriginChange.ACTION_REMOVE_ENTRY.equals(actions[i])) {
                name = "Remove Definition";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToDefault.png"));
            } else if (OriginChange.ACTION_DEFINE_IN_PARENT_PROJECT.equals(actions[i])) {
                name = "Define in Parent Project";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentProject.png"));
            } else if (OriginChange.ACTION_DEFINE_IN_PARENT_BUILD.equals(actions[i])) {
                name = "Define in Parent Build";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentBuild.png"));
            } else if (OriginChange.ACTION_MOVE_TO_PARENTBUILD.equals(actions[i])) {
                name = "Move to Parent Build";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentBuild.png"));
            } else if (OriginChange.ACTION_MOVE_TO_PARENT_PROJECT.equals(actions[i])) {
                name = "Move to Parent Project";
                icon = new ImageIcon(Utilities.loadImage("org/mevenide/netbeans/project/resources/ToParentProject.png"));
            }
            JMenuItem item = currentLoc.add(name);
            item.setAction(new MyAction(actions[i], name, icon));
        }
    }
    
    static class LocationWrapper {
        private String name;
        private Icon icon;
        private File file;
        private int ID;
        private String[] actions;
        
        public LocationWrapper(String name, Icon icon, File file, int id, String[] actions) {
            this.name = name;
            this.icon = icon;
            this.file = file;
            this.actions = actions;
            ID = id;
            
        }
        public Icon getIcon() {
            return icon;
        }
        public String getName() {
            return name;
        }
        public File getFile() {
            return file;
        }
        
        public String toString() {
            return getName();
        }
        
        public int getID() {
            return ID;
        }
        
        public String[] getActions() {
            return actions;
        }
    }
    
    private class MyAction extends AbstractAction {
        private String id;
        public MyAction(String i, String name, Icon icon) {
            id = i;
            putValue(Action.NAME, name);
            putValue(Action.SMALL_ICON, icon);
        }
        
        public void actionPerformed(ActionEvent e) {
            invokePopupAction(id);
        }
        
    }
    
    private class MyBorder implements Border {
        /**
         * Paints the border for the specified component with the specified
         * position and size.
         * @param c the component for which this border is being painted
         * @param g the paint graphics
         * @param x the x position of the painted border
         * @param y the y position of the painted border
         * @param width the width of the painted border
         * @param height the height of the painted border
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.translate(x, y);
            Color col = null;
            LocationComboBox box = (LocationComboBox)c;
            if (box.getModel().isRollover()) {
                col = UIManager.getColor("InternalFrame.borderHighlight"); //NOI18N
            }
            if (box.isFocusOwner()) {
                col = UIManager.getColor("InternalFrame.borderLight"); //NOI18N
            }
            if (col != null) {
                g.setColor(col); //NOI18N
                g.drawRect(0, 0, width - 1, height - 1);
            }
            
//            g.setColor(UIManager.getColor("InternalFrame.borderDarkShadow")); //NOI18N
//            g.drawLine(1, 0, 1, height - 2);
//            g.setColor(UIManager.getColor("InternalFrame.borderHighlight")); //NOI18N
//            g.drawLine(1, height - 1, width - 1, height - 1);
//            g.drawLine(width - 1, height - 2, width - 1, 0);
//            g.setColor(UIManager.getColor("InternalFrame.borderLight")); //NOI18N
//            g.drawLine(2, height - 2, width - 2, height - 2);
//            g.drawLine(width - 2, height - 3, width - 2, 0);
            g.translate(-x, -y);
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(1,1,1,1);
        }
        
        public boolean isBorderOpaque() {
            return true;
        }
        
    }
}
