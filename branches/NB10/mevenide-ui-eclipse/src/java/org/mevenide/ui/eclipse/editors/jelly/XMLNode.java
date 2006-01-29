/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.editors.jelly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author jll
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLNode extends TypedPosition implements IAdaptable, IWorkbenchAdapter, Comparable {

    public static final int TAG = 0;
    public static final int ATTR = 1;
    public static final int DOUBLEQUOTE = 2;
    public static final int SINGLEQUOTE = 3;
    public static final int ATTRIBUTE = 4;
    public static final int ATT_VALUE = 5;
    public static final int AFTER_ATTRIBUTE = 6;
    public static final int AFTER_ATT_VALUE = 7;
    private boolean added = false;
    private boolean modified = false;
    private XMLNode parent = null;
    private List children = new ArrayList();
    private XMLNode correspondingNode = null;
    private IDocument document = null;

    public XMLNode(int offset, int length, String type, IDocument document) {
        super(offset, length, type);
        added = true;
        this.document = document;
    }

    public XMLNode(ITypedRegion region) {
        super(region);
        added = true;
    }

    public void setLength(int length) {
        super.setLength(length);
        added = false;
        modified = true;
    }

    public void setOffset(int offset) {
        super.setOffset(offset);
        added = false;
        modified = true;
    }

    public boolean isAdded() {
        return added;
    }

    public boolean isModified() {
        return modified;
    }

    public void setAdded(boolean b) {
        added = b;
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public synchronized List getChildren() {
        return children;
    }

    public XMLNode getParent() {
        return parent;
    }

    public void setParent(XMLNode node) {
        parent = node;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.addChild(this);
        }
    }

    public synchronized void addChild(XMLNode child) {
        for (int i = 0; i < children.size(); i++) {
            if (((XMLNode) children.get(i)).getOffset() > child.getOffset()) {
                children.add(i, child);
                return;
            }
        }
        children.add(child);
    }

    public List getChildrenAfter(XMLNode child) {
        List result = new ArrayList();
        for (int i = 0; i < children.size(); i++) {
            if (((XMLNode) children.get(i)).getOffset() > child.getOffset()) {
                result.add(child);
            }
        }
        return result;
    }

    public synchronized void removeChild(XMLNode child) {
        children.remove(child);
    }

    public Object getAdapter(Class adapter) {
        if (adapter.equals(IWorkbenchAdapter.class)) {
            return this;
        }
        return null;
    }

    public Object[] getChildren(Object o) {
        if (o instanceof XMLNode) {
            List filteredChildren = new ArrayList();
            for (Iterator it = ((XMLNode) o).getChildren().iterator(); it.hasNext();) {
                XMLNode n = (XMLNode) it.next();
                if (!ITypeConstants.ENDTAG.equals(n.getType()) && !ITypeConstants.END_DECL.equals(n.getType())
                        && (!ITypeConstants.TEXT.equals(n.getType()) || !n.containsOnlyWhitespaces())) {
                    filteredChildren.add(n);
                }
            }
            if (ITypeConstants.TAG.equals(((XMLNode) o).getType()) || ITypeConstants.PI.equals(((XMLNode) o).getType())
                    || ITypeConstants.EMPTYTAG.equals(((XMLNode) o).getType())) {
                filteredChildren.addAll(0, ((XMLNode) o).getAttributes());
            }
            return filteredChildren.toArray(new XMLNode[0]);
        }
        return null;
    }

    public boolean containsOnlyWhitespaces() {
        String content = "";
        try {
            content = document.get(getOffset(), getLength());
        }
        catch (BadLocationException e) {
            return true;
        }
        return (content.trim().length() == 0);
    }

    public String getContent() {
        String content = "";
        try {
            content = document.get(getOffset(), getLength());
        }
        catch (BadLocationException e) {
        }
        return content;
    }

    public String getContentTo(int to) {
        String content = "";
        try {
            content = document.get(getOffset(), to - getOffset());
        }
        catch (BadLocationException e) {
        }
        return content.substring(1);
    }

    public String getContentFrom(int from) {
        String content = "";
        try {
            content = document.get(from, getOffset() - from + getLength() - 1);
        }
        catch (BadLocationException e) {
        }
        return content;
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return null;
    }

    public String getLabel(Object o) {
        return getName();
    }

    public Object getParent(Object o) {
        return this;
    }

    public int compareTo(Object o) {
        XMLNode n = null;
        if (!(o instanceof XMLNode)) {
            return 0;
        }
        n = (XMLNode) o;
        if (this == o) {
            return 0;
        }
        return (getOffset() > n.getOffset()) ? 1 : ((getOffset() < n.getOffset()) ? -1 : 0);
    }

    public XMLNode getCorrespondingNode() {
        return correspondingNode == null ? this : correspondingNode;
    }

    public void setCorrespondingNode(XMLNode node) {
        correspondingNode = node;
    }

    public String getName() {
        String name = "<unknown type>";
        if (getType().equals(ITypeConstants.TEXT)) {
            return "#TEXT";
        }
        else if (getType().equals(ITypeConstants.TAG)) {
            return getTagName();
        }
        else if (getType().equals(ITypeConstants.PI)) {
            return getTagName();
        }
        else if (getType().equals(ITypeConstants.ATTR)) {
            return getAttributeName();
        }
        else if (getType().equals(ITypeConstants.COMMENT)) {
            return "#COMMENT";
        }
        else if (getType().equals(ITypeConstants.DECL) || ITypeConstants.START_DECL.equals(getType())) {
            return getTagName();
        }
        else if (getType().equals(ITypeConstants.ENDTAG)) {
            return getTagName();
        }
        else if (getType().equals(ITypeConstants.EMPTYTAG)) {
            return getTagName();
        }
        return name;
    }

    public String getValue() {
        String name = "<unknown type>";
        if (getType().equals(ITypeConstants.TEXT)) {
        }
        else if (getType().equals(ITypeConstants.TAG)) {
            if (children.size() > 0 && ((XMLNode) children.get(0)).getType().equals(ITypeConstants.TEXT)) {
                name = ((XMLNode) children.get(0)).getContent().trim();
            }
        }
        else if (getType().equals(ITypeConstants.PI)) {
        }
        else if (getType().equals(ITypeConstants.ATTR)) {
            return getAttributeValue();
        }
        else if (getType().equals(ITypeConstants.COMMENT)) {
        }
        else if (getType().equals(ITypeConstants.DECL)) {
        }
        else if (getType().equals(ITypeConstants.ENDTAG)) {
        }
        else if (getType().equals(ITypeConstants.EMPTYTAG)) {
        }
        return name;
    }

    private String getAttributeName() {
        String content = null;
        int index = 0;
        try {
            content = document.get(getOffset(), getLength());
            index = content.indexOf("=");
            if (index == -1) {
                index = content.indexOf("\"");
                if (index == -1) {
                    index = content.indexOf("'");
                    if (index == -1) {
                        index = content.length();
                    }
                }
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return content.substring(0, index).trim();
    }

    private String getTagName() {
        String content = null;
        String name = null;
        try {
            content = document.get(getOffset(), getLength());
        }
        catch (BadLocationException e) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(content, " \t\n\r<>/");
        if (st.hasMoreTokens()) {
            name = st.nextToken();
        }
        if (name == null) {
            name = "";
        }
        return name;
    }

    private String getAttributeValue() {
        String content = null;
        int index = 0;
        try {
            content = document.get(getOffset(), getLength());
            index = content.indexOf("\"");
            if (index == -1) {
                index = content.indexOf("'");
                if (index == -1) {
                    return "";
                }
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        content = content.substring(index).trim();
        return content.substring(1, content.length() - 1);
    }

    public XMLNode getAttributeAt(int offset) {
        List attrs = getAttributes();
        for (Iterator it = attrs.iterator(); it.hasNext();) {
            XMLNode node = (XMLNode) it.next();
            if (node.getOffset() <= offset && offset <= node.getOffset() + node.getLength()) {
                return node;
            }
        }
        return null;
    }

    public List getAttributes() {
        List attrs = new ArrayList();
        String content = null;
        int state = TAG;
        int start = -1;
        int startLength = 0;
        int endLength = 0;
        if (ITypeConstants.PI.equals(getType())) {
            startLength = 2;
            endLength = 2;
        }
        else if (ITypeConstants.DECL.equals(getType())) {
            startLength = 2;
            endLength = 1;
        }
        else if (ITypeConstants.TAG.equals(getType())) {
            startLength = 1;
            endLength = 1;
        }
        else if (ITypeConstants.EMPTYTAG.equals(getType())) {
            startLength = 1;
            endLength = 2;
        }
        else {
            return attrs;
        }
        try {
            content = document.get(getOffset(), getLength());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return attrs;
        }
        if (getName() == null) {
            return attrs;
        }
        for (int i = startLength + getName().length(); i < content.length() - endLength; i++) {
            char c = content.charAt(i);
            switch (c) {
                case '"' :
                    if (state == DOUBLEQUOTE) {
                        attrs.add(new XMLNode(getOffset() + start, i - start + 1, ITypeConstants.ATTR, document));
                        start = -1;
                        state = TAG;
                    }
                    else {
                        state = DOUBLEQUOTE;
                    }
                    break;
                case '\'' :
                    if (state == SINGLEQUOTE) {
                        attrs.add(new XMLNode(getOffset() + start, i - start + 1, ITypeConstants.ATTR, document));
                        start = -1;
                        state = TAG;
                    }
                    else {
                        state = SINGLEQUOTE;
                    }
                    break;
                default :
                    if (!Character.isWhitespace(c) && state == TAG) {
                        start = i;
                        state = ATTR;
                    }
            }
        }
        if (start != -1) {
            attrs.add(new XMLNode(getOffset() + start, content.length() - startLength - start, ITypeConstants.ATTR, document));
        }
        return attrs;
    }

    public int getStateAt(int offset) {
        String content = null;
        int state = TAG;
        try {
            content = document.get(getOffset(), offset - getOffset());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return TAG;
        }
        if (getName() == null) {
            return TAG;
        }
        for (int i = getName().length(); i < content.length(); i++) {
            char c = content.charAt(i);
            switch (c) {
                case '=' :
                    if (state == AFTER_ATTRIBUTE || state == ATTRIBUTE) {
                        state = ATT_VALUE;
                    }
                    break;
                case '"' :
                    if (state == DOUBLEQUOTE) {
                        state = AFTER_ATT_VALUE;
                    }
                    else {
                        state = DOUBLEQUOTE;
                    }
                    break;
                case '\'' :
                    if (state == SINGLEQUOTE) {
                        state = AFTER_ATT_VALUE;
                    }
                    else {
                        state = SINGLEQUOTE;
                    }
                    break;
                default :
                    if (Character.isWhitespace(c)) {
                        switch (state) {
                            case TAG :
                                state = ATTRIBUTE;
                                break;
                            case ATTR :
                                state = AFTER_ATTRIBUTE;
                                break;
                            case AFTER_ATT_VALUE :
                                state = ATTRIBUTE;
                                break;
                        }
                    }
            }
        }
        return state;
    }

    /**
     * For !DOCTYPE:
     * 
     * [28] doctypedecl ::= ' <!DOCTYPE' S Name (S ExternalID)? S? ('['
     * (markupdecl | DeclSep)* ']' S?)? '>' [28a] DeclSep ::= PEReference | S
     * [29] markupdecl ::= elementdecl | AttlistDecl | EntityDecl | NotationDecl |
     * PI | Comment [75] ExternalID ::= 'SYSTEM' S SystemLiteral | 'PUBLIC' S
     * PubidLiteral S SystemLiteral
     */
    public String getDTDLocation() {
        //TODO: this must be changed to include inner DTDs
        String content = getContent();
        String location = null;
        int index = -1;
        int endIndex = -1;
        content = content.substring("<!DOCTYPE".length());
        index = content.indexOf("SYSTEM");
        if (index != -1) {
            index = content.indexOf("\"", index + "SYSTEM".length());
            if (index != -1) {
                endIndex = content.indexOf("\"", index + 1);
            }
            else {
                index = content.indexOf("'", index + "SYSTEM".length());
                if (index == -1) {
                    return null;
                }
                endIndex = content.indexOf("'", index + 1);
            }
        }
        else {
            index = content.indexOf("PUBLIC");
            if (index == -1) {
                return null;
            }
            index = content.indexOf("\"", index + "PUBLIC".length());
            if (index != -1) {
                // skip public ID
                index = content.indexOf("\"", index + 1);
            }
            else {
                index = content.indexOf("'", index + "PUBLIC".length());
                if (index == -1) {
                    return null;
                }
                // skip public ID
                index = content.indexOf("'", index + 1);
            }
            index = content.indexOf("\"", index + 1);
            if (index != -1) {
                endIndex = content.indexOf("\"", index + 1);
            }
            else {
                index = content.indexOf("'", index + 1);
                if (index != -1) {
                    endIndex = content.indexOf("'", index + 1);
                }
            }
        }
        if (index == -1 || endIndex == -1) {
            return null;
        }
        location = content.substring(index + 1, endIndex);
        return location;
    }

    public IDocument getDocument() {
        return document;
    }

    public void setDocument(IDocument document) {
        this.document = document;
    }

    public String toString() {
        String s = super.toString();
        s += "[";
        s += "name=" + getName() + ";";
        s += "type=" + getType() + ";";
        s += "content=" + getContent() + ";";
        s += "isDeleted=" + isDeleted() + ";";
        s += "[" + getOffset() + "," + getLength() + "]";
        s += "]";
        return s;
    }
}