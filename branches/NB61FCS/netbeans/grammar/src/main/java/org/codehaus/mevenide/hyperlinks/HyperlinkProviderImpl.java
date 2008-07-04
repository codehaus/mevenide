/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.hyperlinks;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * adds hyperlinking support to pom.xml files..
 * @author mkleint
 */
public class HyperlinkProviderImpl implements HyperlinkProvider {
    
    /** Creates a new instance of HyperlinkProvider */
    public HyperlinkProviderImpl() {
    }

    public boolean isHyperlinkPoint(Document doc, int offset) {
        if (!(doc instanceof BaseDocument) || !isPomFile(doc)) {
            return false;
        }
        BaseDocument bdoc = (BaseDocument) doc;
        ExtSyntaxSupport sup = (ExtSyntaxSupport)bdoc.getSyntaxSupport();
        TokenItem token;
        try {
            token = sup.getTokenChain(offset, offset + 1);
            //if (debug) debug ("token: "  +token.getTokenID().getNumericID() + ":" + token.getTokenID().getName());
            // when it's not a value -> do nothing.
            if (token == null) {
                return false;
            }
            TokenItem previous = token.getPrevious();
            if (previous != null && previous.getImage().equals(">")) { //NOI18N
                //we are in element text
                FileObject fo = getProjectDir(doc);
                if (getPath(fo, token.getImage()) != null) {
                    return true;
                } 
            }
            // urls get opened..
            if (token.getImage() != null && 
                    (token.getImage().startsWith("http://") || //NOI18N
                    (token.getImage().startsWith("https://")))) { //NOI18N
                return true;
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
            
        
        return false;
    }

    public int[] getHyperlinkSpan(Document doc, int offset) {
        if (!(doc instanceof BaseDocument) || !isPomFile(doc)) {
            return null;
        }
        
        BaseDocument bdoc = (BaseDocument) doc;
        ExtSyntaxSupport sup = (ExtSyntaxSupport)bdoc.getSyntaxSupport();
        TokenItem token;
        try {
            token = sup.getTokenChain(offset, offset + 1);
            if (token == null) {
                return null;
            }
            TokenItem previous = token.getPrevious();
            if (previous != null && previous.getImage().equals(">")) {//NOI18N
                //we are in element text
                FileObject fo = getProjectDir(doc);
                if (getPath(fo, token.getImage()) != null) {
                    return new int[] {token.getOffset(), token.getNext().getOffset()};
                } 
            }
            if (token.getImage() != null && 
                    (token.getImage().startsWith("http://") || //NOI18N
                    (token.getImage().startsWith("https://")))) { //NOI18N
                return new int[] {token.getOffset(), token.getNext().getOffset()};
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void performClickAction(Document doc, int offset) {
        if (!(doc instanceof BaseDocument) || !isPomFile(doc)) {
            return;
        }
        
        BaseDocument bdoc = (BaseDocument) doc;
        ExtSyntaxSupport sup = (ExtSyntaxSupport)bdoc.getSyntaxSupport();
        TokenItem token;
        try {
            token = sup.getTokenChain(offset, offset + 1);
            if (token == null) {
                return;
            }
            TokenItem previous = token.getPrevious();
            if (previous != null && previous.getImage().equals(">")) { //NOI18N
                //we are in element text
                FileObject fo = getProjectDir(doc);
                String path = token.getImage();
                if (previous.getPrevious().getImage().equals("<module")) { //NOI18N
                    path = path + "/pom.xml"; //NOI18N
                }
                if (getPath(fo, path) != null) {
                    FileObject file = getPath(fo, path);
                    DataObject dobj;
                    try {
                        dobj = DataObject.find(file);
                        EditCookie edit = dobj.getCookie(EditCookie.class);
                        if (edit != null) {
                            edit.edit();
                        }
                    } catch (DataObjectNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } 
            }
            if (token.getImage() != null && 
                    (token.getImage().startsWith("http://") || //NOI18N
                    (token.getImage().startsWith("https://")))) { //NOI18N
                try {
                    URL url = new URL(token.getImage());
                    HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return;
    }
    
    private FileObject getProjectDir(Document doc) {
        DataObject dObject = NbEditorUtilities.getDataObject(doc);
        return dObject.getPrimaryFile().getParent();
    }
    
    private FileObject getPath(FileObject parent, String path) {
        // TODO more substitutions necessary probably..
        if (path.startsWith("${basedir}/")) { //NOI18N
            path = path.substring("${basedir}/".length()); //NOI18N
        }
        while (path.startsWith("../") && parent.getParent() != null) { //NOI18N
            path = path.substring("../".length()); //NOI18N
            parent = parent.getParent();
        }
        return parent.getFileObject(path);
    }

    private boolean isPomFile(Document doc) {
        String type = (String) doc.getProperty("mimeType"); //NOI18N
        if (type != null) {
            if ("text/x-maven-pom+xml".equals(type)) { //NOI18N
                return true;
            }
            if (!"text/xml".equals(type)) { //NOI18N
                return false;
            }
        }

        //TODO this should be eventually abandoned in favour of specific supported mimetypes.
        
        DataObject dObject = NbEditorUtilities.getDataObject(doc);
        if (dObject != null && "pom.xml".equalsIgnoreCase(dObject.getPrimaryFile().getNameExt())) { //NOI18N
            // is that enough?
            return true;
        }
        if (dObject != null && "settings.xml".equals(dObject.getPrimaryFile().getNameExt()) && ".m2".equals(dObject.getPrimaryFile().getParent().getNameExt())) { //NOI18N
            return true;
        }
        return false;
    }
    
}