/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea.action;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public abstract class AbstractTreeMouseActionListener extends AbstractBaseActionListener implements MouseListener {

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e Document me!
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e Document me!
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e Document me!
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * Method description
     *
     * @param e Document me!
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Method description
     *
     * @param e Document me!
     */
    public void mouseReleased(MouseEvent e) {

//      maybeShowPopup(e);
    }

    /**
     * Method description
     *
     * @param e Document me!
     */
    protected abstract void processLeftMouseButtonClick(MouseEvent e);

    /**
     * Method description
     *
     * @param e Document me!
     */
    protected void processMiddleMouseButtonClick(MouseEvent e) {}

    /**
     * Method description
     *
     * @param e Document me!
     */
    protected abstract void processRightMouseButtonClick(MouseEvent e);
}
