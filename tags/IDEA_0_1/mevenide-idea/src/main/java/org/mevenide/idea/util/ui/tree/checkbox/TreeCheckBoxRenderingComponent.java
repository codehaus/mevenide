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
package org.mevenide.idea.util.ui.tree.checkbox;

import javax.swing.*;
import java.awt.Rectangle;

/**
 * @author Arik
 */
public class TreeCheckBoxRenderingComponent extends AbstractTreeCheckBoxComponent {

    public TreeCheckBoxRenderingComponent() {
        super(new TreeCheckBox(), new TreeLabel());
    }

    private static class TreeLabel extends JLabel {
        public TreeLabel() {
            setOpaque(false);
        }

        public void firePropertyChange(String propertyName,
                                       boolean oldValue, boolean newValue) {
        }

        public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        }

        public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        }

        public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        }

        public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        }

        public void firePropertyChange(String propertyName,
                                       int oldValue, int newValue) {
        }

        public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        }

        protected void firePropertyChange(String propertyName,
                                          Object oldValue, Object newValue) {
            if (propertyName != null && propertyName.equals("text"))
                super.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        }

        public void repaint() {
        }

        public void repaint(Rectangle r) {
        }

        public void revalidate() {
        }

        public void invalidate() {
        }

        public void validate() {
        }
    }

    private static class TreeCheckBox extends JCheckBox {
        public TreeCheckBox() {
            setOpaque(false);
        }

        public void firePropertyChange(String propertyName,
                                       boolean oldValue, boolean newValue) {
        }

        public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        }

        public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        }

        public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        }

        public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        }

        public void firePropertyChange(String propertyName,
                                       int oldValue, int newValue) {
        }

        public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        }

        protected void firePropertyChange(String propertyName,
                                          Object oldValue, Object newValue) {
        }

        public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        }

        public void repaint() {
        }

        public void repaint(Rectangle r) {
        }

        public void revalidate() {
        }

        public void invalidate() {
        }

        public void validate() {
        }
    }
}
