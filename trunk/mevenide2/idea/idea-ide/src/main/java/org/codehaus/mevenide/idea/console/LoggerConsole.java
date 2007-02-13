/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.codehaus.mevenide.idea.console;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.JTextPane;
import javax.swing.Icon;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * A class that sets up the logging console for the plugin.
 *
 * @author bkate
 */
public class LoggerConsole extends JPanel implements LoggerListener {

    // swing components
    private LogPanel embedderLog;
    private LogPanel pluginLog;


    /**
     * Default constructor.
     */
    public LoggerConsole() {
        init();
    }


    /**
     * Initializes the log panels.
     */
    private void init() {

        setLayout(new BorderLayout());

        embedderLog = new LogPanel();
        pluginLog = new LogPanel();

        // put them into their own tabs
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Embedder", embedderLog);
        tabs.add("Plugin", pluginLog);

        add(tabs, BorderLayout.CENTER);
    }


    /** {@inheritDoc} */
    public void logEmbedderMessage(LogMessage message) {
        embedderLog.logMessage(message);
    }


    /** {@inheritDoc} */
    public void logPluginMessage(LogMessage message) {
        pluginLog.logMessage(message);
    }


    /**
     * A class that defines an individual logging panel.
     */
    private static class LogPanel extends JPanel {

        // log display
        private JTextPane display = new JTextPane(new LoggerStyledDocument());

        // icon location for the clear button
        private static final String CLEAR_ICON = "/images/clear.png";

        // for timestamping
        private static final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");


        /**
         * Default constructor.
         */
        public LogPanel() {
            init();
        }


        /**
         * Initializes the swing components that make up the logging panel.
         */
        private void init() {

            setLayout(new BorderLayout());

            display.setEditable(false);
            display.setBackground(Color.WHITE);

              // a toolbar for user interaction
            JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);

            toolbar.setFloatable(false);
            toolbar.setPreferredSize(new Dimension(30, 90));
            toolbar.setMaximumSize(new Dimension(30, 1000));
            toolbar.setMinimumSize(new Dimension(30, 30));

            // add a clear button to the toolbar
            JButton clearButton = new JButton(new ImageIcon(LoggerConsole.class.getResource(CLEAR_ICON)));

            clearButton.setToolTipText("Clear Log");

            // an action listener that clears the selected log console
            clearButton.addActionListener(new ActionListener () {

                public void actionPerformed(ActionEvent e) {

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {

                            display.setStyledDocument(new LoggerStyledDocument());
                            display.validate();
                        }
                    });
                }
            });

            toolbar.add(clearButton);

            // add the toolbar and the log console
            add(toolbar, BorderLayout.WEST);
            add(new JScrollPane(display), BorderLayout.CENTER);
        }


        /**
         * Records a message to the logging panel.
         *
         * @param message The message to be logged.
         */
        public void logMessage(final LogMessage message) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    // get the logger styles
                    StyledDocument doc = display.getStyledDocument();

                    try {

                        Style level;

                        // get the right level icon
                        switch(message.getLogLevel()) {

                            case LogMessage.DEBUG:
                                level = doc.getStyle(LoggerStyledDocument.DEBUG_ICON_STYLE);
                                break;

                            case LogMessage.WARN:
                                level = doc.getStyle(LoggerStyledDocument.WARN_ICON_STYLE);
                                break;

                            case LogMessage.ERROR:
                                level = doc.getStyle(LoggerStyledDocument.ERROR_ICON_STYLE);
                                break;

                            case LogMessage.FATAL:
                                level = doc.getStyle(LoggerStyledDocument.FATAL_ICON_STYLE);
                                break;

                            case LogMessage.INFO:
                            default:
                                level = doc.getStyle(LoggerStyledDocument.INFO_ICON_STYLE);
                                break;
                        }

                        // show the level, timestamp, and message
                        doc.insertString(doc.getLength(), " ", level);
                        doc.insertString(doc.getLength(), " " + getTimeStamp(), doc.getStyle(LoggerStyledDocument.TIMESTAMP_STYLE));
                        doc.insertString(doc.getLength(), " - ", doc.getStyle(LoggerStyledDocument.REGULAR_STYLE));
                        doc.insertString(doc.getLength(), message.getMessage() + "\n", doc.getStyle(LoggerStyledDocument.REGULAR_STYLE));
                    }
                    catch(BadLocationException ble) {
                        // intentionally empty - nothing we can do...
                    }
                }
            });
        }


        /**
         * Constructs a timestamp at the time of invocation.
         *
         * @return The current time.
         */
        private String getTimeStamp() {
            return formatter.format(new Date());
        }


        /**
         * A StyledDocument with built in styles for the logging messages.
         */
        private class LoggerStyledDocument extends DefaultStyledDocument {

            // basic styles
            public static final String REGULAR_STYLE = "regular";
            public static final String BOLD_STYLE = "bold";
            public static final String ITALIC_STYLE = "italic";

            // logger-specific styles
            public static final String TIMESTAMP_STYLE = "timestamp";
            public static final String DEBUG_STYLE = "debug";
            public static final String DEBUG_ICON_STYLE = "debug-icon";
            public static final String INFO_STYLE = "info";
            public static final String INFO_ICON_STYLE = "info-icon";
            public static final String WARN_STYLE = "warn";
            public static final String WARN_ICON_STYLE = "warn-icon";
            public static final String ERROR_STYLE = "error";
            public static final String ERROR_ICON_STYLE = "error-icon";
            public static final String FATAL_STYLE = "fatal";
            public static final String FATAL_ICON_STYLE = "fatal-icon";

            // icon locations
            private static final String DEBUG_ICON = "/images/debug.png";
            private static final String INFO_ICON = "/images/info.png";
            private static final String WARN_ICON = "/images/warn.png";
            private static final String ERROR_ICON = "/images/error.png";
            private static final String FATAL_ICON = "/images/fatal.png";


            /**
             * Default constructor.
             */
            public LoggerStyledDocument() {
                initStyles();
            }


            /**
             * Adds the logging styles to the document.
             */
            private void initStyles() {

                Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

                Style regular = addStyle(REGULAR_STYLE, def);
                StyleConstants.setFontFamily(def, "SansSerif");

                Style italic = addStyle(ITALIC_STYLE, regular);
                StyleConstants.setItalic(italic, true);

                Style bold = addStyle(BOLD_STYLE, regular);
                StyleConstants.setBold(bold, true);

                Style timestamp = addStyle(TIMESTAMP_STYLE, bold);
                Style debug = addStyle(DEBUG_STYLE, regular);
                Style info = addStyle(INFO_STYLE, regular);
                Style warn = addStyle(WARN_STYLE, regular);
                Style fatal = addStyle(FATAL_STYLE, regular);
                Style error = addStyle(ERROR_STYLE, regular);

                Style debugIcon = addStyle(DEBUG_ICON_STYLE, regular);
                StyleConstants.setAlignment(debugIcon, StyleConstants.ALIGN_CENTER);

                Icon icon = new ImageIcon(LoggerStyledDocument.class.getResource(DEBUG_ICON));

                if (debugIcon != null) {
                    StyleConstants.setIcon(debugIcon, icon);
                }

                Style infoIcon = addStyle(INFO_ICON_STYLE, regular);
                StyleConstants.setAlignment(infoIcon, StyleConstants.ALIGN_CENTER);

                icon = new ImageIcon(LoggerStyledDocument.class.getResource(INFO_ICON));

                if (infoIcon != null) {
                    StyleConstants.setIcon(infoIcon, icon);
                }

                Style warnIcon = addStyle(WARN_ICON_STYLE, regular);
                StyleConstants.setAlignment(warnIcon, StyleConstants.ALIGN_CENTER);

                icon = new ImageIcon(LoggerStyledDocument.class.getResource(WARN_ICON));

                if (warnIcon != null) {
                    StyleConstants.setIcon(warnIcon, icon);
                }

                Style errorIcon = addStyle(ERROR_ICON_STYLE, regular);
                StyleConstants.setAlignment(errorIcon, StyleConstants.ALIGN_CENTER);

                icon = new ImageIcon(LoggerStyledDocument.class.getResource(ERROR_ICON));

                if (errorIcon != null) {
                    StyleConstants.setIcon(errorIcon, icon);
                }

                Style fatalIcon = addStyle(FATAL_ICON_STYLE, regular);
                StyleConstants.setAlignment(fatalIcon, StyleConstants.ALIGN_CENTER);

                icon = new ImageIcon(LoggerStyledDocument.class.getResource(FATAL_ICON));

                if (fatalIcon != null) {
                    StyleConstants.setIcon(fatalIcon, icon);
                }
            }
        }

    }

}

