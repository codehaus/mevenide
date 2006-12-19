/*
 * TextValueCompleter.java
 *
 * Created on December 19, 2006, 8:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author mkleint
 */
/** inner class does the matching of the JTextField's
 * document to completion strings kept in an ArrayList
 */

public class TextValueCompleter implements DocumentListener {
    private Pattern pattern;
    private Collection<String> completions;
    private JList completionList;
    private DefaultListModel completionListModel;
    private JScrollPane listScroller;
    private Popup popup;
    private JTextField field;
    
    public TextValueCompleter(Collection<String> completions, JTextField fld) {
        this.completions = completions;
        this.field = fld;
        field.getDocument().addDocumentListener(this);
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                hidePopup();
            }
        });
        completionListModel = new DefaultListModel();
        completionList = new JList(completionListModel);
        completionList.setPrototypeCellValue("lets have it at least this wide and add some more just in case"); //NOI18N
        completionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    field.getDocument().removeDocumentListener(TextValueCompleter.this);
                    field.setText(completionList.getSelectedValue().toString());
                    hidePopup();
                    field.getDocument().addDocumentListener(TextValueCompleter.this);
                }
            }
        });
        completionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScroller =new JScrollPane(completionList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "listdown");
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "listup");
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "listpageup");
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "listpagedown");
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), "showpopup");
        field.getActionMap().put("listdown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (popup == null) {
                    buildAndShowPopup();
                }
                completionList.setSelectedIndex(Math.min(completionList.getSelectedIndex() + 1, completionList.getModel().getSize()));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put("listup",  new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (popup == null) {
                    buildAndShowPopup();
                }
                completionList.setSelectedIndex(Math.max(completionList.getSelectedIndex() - 1, 0));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put("listpagedown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                completionList.setSelectedIndex(Math.min(completionList.getSelectedIndex() + completionList.getVisibleRowCount(), completionList.getModel().getSize()));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put("listpageup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                completionList.setSelectedIndex(Math.max(completionList.getSelectedIndex() - completionList.getVisibleRowCount(), 0));
                completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
            }
        });
        field.getActionMap().put("fill-in", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                field.getDocument().removeDocumentListener(TextValueCompleter.this);
                if (completionList.getSelectedValue() != null) {
                    field.setText(completionList.getSelectedValue().toString());
                }
                hidePopup();
                field.getDocument().addDocumentListener(TextValueCompleter.this);
            }
        });
        field.getActionMap().put("hidepopup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                hidePopup();
            }
        });
        field.getActionMap().put("showpopup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                buildAndShowPopup();
            }
        });
    }
    
    private void buildPopup() {
        pattern = Pattern.compile(field.getText().trim() + ".+");
        int entryindex = 0;
        for (String completion : completions) {
            // check if match
            Matcher matcher = pattern.matcher(completion);
            if (matcher.matches()) {
                if (!completionListModel.contains(completion)) {
                    completionListModel.add(entryindex,
                            completion);
                }
                entryindex++;
            } else {
                completionListModel.removeElement(completion);
            }
        }
    }
    
    private void showPopup() {
        hidePopup();
        if (completionListModel.getSize() == 0) {
            return;
        }
        // figure out where the text field is,
        // and where its bottom left is
        java.awt.Point los = field.getLocationOnScreen();
        int popX = los.x;
        int popY = los.y + field.getHeight();
        popup = PopupFactory.getSharedInstance().getPopup(field, listScroller, popX, popY);
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "hidepopup");
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "fill-in");
        popup.show();
        if (completionList.getSelectedIndex() != -1) {
            completionList.ensureIndexIsVisible(completionList.getSelectedIndex());
        }
    }
    
    private void hidePopup() {
        field.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        field.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }
    
    private void buildAndShowPopup() {
        buildPopup();
        showPopup();
    }
    
    // DocumentListener implementation
    public void insertUpdate(DocumentEvent e) { buildAndShowPopup(); }
    public void removeUpdate(DocumentEvent e) { buildAndShowPopup(); }
    public void changedUpdate(DocumentEvent e) { buildAndShowPopup(); }
    
    public void setValueList(Collection<String> values) {
        completions = values;
    }
}

