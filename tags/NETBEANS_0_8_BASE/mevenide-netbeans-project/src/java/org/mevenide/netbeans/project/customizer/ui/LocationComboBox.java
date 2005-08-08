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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.openide.util.Utilities;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class LocationComboBox extends JButton {
    private LocationWrapper current;
    private LocationWrapper[] all;
    private JPopupMenu currentLoc;
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
        initStatePopup(selected);
        current = selected;
    }
    
    public void setItems(LocationWrapper[] wrappers) {
        all = wrappers;
    }
    
    public LocationWrapper[] getItems() {
        return all;
    }
    
    public void invokePopupAction(LocationWrapper wrap) {
        setSelectedItem(wrap);
        if (observer != null) {
            observer.locationChanged();
        }
    }
    
    public void invokePopupAction(int location) {
        LocationWrapper wrap = findWrapper(location);
        if (wrap != null) {
            invokePopupAction(wrap);
        } else {
            System.out.println("how come, no wrapper for " + location);
        }
    }
    
    private LocationWrapper findWrapper(int location) {
        for (int i = 0; i < all.length; i++) {
            if (all[i].getID() == location) {
                return all[i];
            }
        }
        return null;
    }
    
    public void setChangeObserver(OriginChange.ChangeObserver obs) {
        observer = obs;
    }
    
    private void initStatePopup(LocationWrapper selItem) {
        if (selItem == null) {
            return;
        }
        for (int i = 0; i < all.length; i++) {
            if (all[i] == selItem || !all[i].includeInPopup()) {
                continue;
            }
            String name = all[i].getMoveName();
            Icon icon = all[i].getIcon();
            JMenuItem item = currentLoc.add(name);
            item.setAction(new MyAction(all[i], name, icon));
        }
    }
    
    static class LocationWrapper {
        private String name;
        private Icon icon;
        private File file;
        private int loc;
        private String moveName;
        private Icon moveIcon;
        
        public LocationWrapper(String nm, Icon icn,
                               String mvName, Icon mvIcon,
                               File fil, int id) {
            name = nm;
            icon = icn;
            file = fil;
            loc = id;
            moveName = mvName;
            moveIcon = mvIcon;
        }
        
        public Icon getIcon() {
            return icon;
        }
        public String getName() {
            return name;
        }
        
        public Icon getMoveIcon() {
            return moveIcon;
        }
        
        public String getMoveName() {
            return moveName;
        }
        
        public boolean includeInPopup() {
            // maybe refine?
            return moveName != null;
        }
        
        public File getFile() {
            return file;
        }
        
        public String toString() {
            return getName();
        }
        
        public int getID() {
            return loc;
        }
    }
    
    private class MyAction extends AbstractAction {
        private LocationWrapper wrapper;
        public MyAction(LocationWrapper wrap, String name, Icon icon) {
            wrapper = wrap;
            putValue(Action.NAME, name);
            putValue(Action.SMALL_ICON, icon);
        }
        
        public void actionPerformed(ActionEvent e) {
            invokePopupAction(wrapper);
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
