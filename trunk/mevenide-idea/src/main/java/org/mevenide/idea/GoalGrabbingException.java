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
package org.mevenide.idea;

/**
 * @author Arik
 */
public class GoalGrabbingException extends Exception {
    private static final Res RES = Res.getInstance(GoalGrabbingException.class);
    private static final String DEFAULT_MESSAGE = RES.get("goal.grabbing.error");

    public GoalGrabbingException() {
        super(DEFAULT_MESSAGE);
    }

    public GoalGrabbingException(final Throwable pCause) {
        super(buildMessage(pCause, DEFAULT_MESSAGE), pCause);
    }

    public GoalGrabbingException(final String pMessage) {
        super(buildMessage(pMessage));
    }

    public GoalGrabbingException(final String pMessage, final Throwable pCause) {
        super(buildMessage(pCause, pMessage), pCause);
    }

    private static String buildMessage(final String pDefault) {
        return buildMessage(null, pDefault);
    }

    private static String buildMessage(final Throwable pCause, final String pDefault) {
        final String defaultMsg;
        if(pDefault != null && pDefault.trim().length() > 0)
            defaultMsg = pDefault;
        else
            defaultMsg = DEFAULT_MESSAGE;

        if(pCause == null)
            return defaultMsg;

        final String msg = pCause.getMessage();
        if(msg == null || msg.trim().length() == 0)
            return defaultMsg;

        return msg;
    }
}
