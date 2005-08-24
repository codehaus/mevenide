/*
 * Created on 16.05.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.editors.jelly;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;


/**
 * @author jll
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLDocumentPartitioner implements IDocumentPartitioner, IDocumentPartitionerExtension {

    public static final String CONTENT_TYPES_CATEGORY = "__content_types_category";
    protected IPartitionTokenScanner fScanner;
    protected String fLegalContentTypes[];
    protected IDocument fDocument;
    protected int fPreviousDocumentLength;
    protected DefaultPositionUpdater fPositionUpdater;
    protected int fStartOffset;
    protected int fEndOffset;
    protected int fDeleteOffset;

    public XMLDocumentPartitioner(IPartitionTokenScanner scanner, String legalContentTypes[]) {
        fPositionUpdater = new XMLPositionUpdater(CONTENT_TYPES_CATEGORY);
        fScanner = scanner;
        fLegalContentTypes = legalContentTypes;
    }

    public void connect(IDocument document) {
        Assert.isNotNull(document);
        Assert.isTrue(!document.containsPositionCategory(CONTENT_TYPES_CATEGORY));
        fDocument = document;
        fDocument.addPositionCategory(CONTENT_TYPES_CATEGORY);
        initialize();
    }

    protected void initialize() {
        fScanner.setRange(fDocument, 0, fDocument.getLength());
        try {
            for (IToken token = fScanner.nextToken(); !token.isEOF(); token = fScanner.nextToken()) {
                String contentType = getTokenContentType(token);
                if (isSupportedContentType(contentType)) {
                    TypedPosition p = new XMLNode(fScanner.getTokenOffset(), fScanner.getTokenLength(), contentType, fDocument);
                    fDocument.addPosition(CONTENT_TYPES_CATEGORY, p);
                }
            }
        }
        catch (BadLocationException _ex) {
        }
        catch (BadPositionCategoryException _ex) {
        }
    }

    public void disconnect() {
        Assert.isTrue(fDocument.containsPositionCategory(CONTENT_TYPES_CATEGORY));
        try {
            fDocument.removePositionCategory(CONTENT_TYPES_CATEGORY);
        }
        catch (BadPositionCategoryException _ex) {
        }
    }

    public void documentAboutToBeChanged(DocumentEvent e) {
        Assert.isTrue(e.getDocument() == fDocument);
        fPreviousDocumentLength = e.getDocument().getLength();
        fStartOffset = -1;
        fEndOffset = -1;
        fDeleteOffset = -1;
    }

    public boolean documentChanged(DocumentEvent e) {
        IRegion region = documentChanged2(e);
        return region != null;
    }

    private void rememberRegion(int offset, int length) {
        if (fStartOffset == -1)
            fStartOffset = offset;
        else if (offset < fStartOffset)
            fStartOffset = offset;
        int endOffset = offset + length;
        if (fEndOffset == -1)
            fEndOffset = endOffset;
        else if (endOffset > fEndOffset)
            fEndOffset = endOffset;
    }

    private void rememberDeletedOffset(int offset) {
        fDeleteOffset = offset;
    }

    private IRegion createRegion() {
        if (fDeleteOffset == -1) {
            if (fStartOffset == -1 || fEndOffset == -1) return null;
            return new Region(fStartOffset, fEndOffset - fStartOffset);
        }

        if (fStartOffset == -1 || fEndOffset == -1) {
            return new Region(fDeleteOffset, 0);
        }

        int offset = Math.min(fDeleteOffset, fStartOffset);
        int endOffset = Math.max(fDeleteOffset, fEndOffset);
        return new Region(offset, endOffset - offset);
    }

    public IRegion documentChanged2(DocumentEvent e) {
        try {
            IDocument d = e.getDocument();
            Position category[] = d.getPositions(CONTENT_TYPES_CATEGORY);
            IRegion line = d.getLineInformationOfOffset(e.getOffset());
            int reparseStart = line.getOffset();
            int partitionStart = -1;
            String contentType = null;
            int first = d.computeIndexInCategory(CONTENT_TYPES_CATEGORY, reparseStart);
            if (first > 0) {
                TypedPosition partition = (TypedPosition) category[first - 1];
                if (partition.includes(reparseStart)) {
                    partitionStart = partition.getOffset();
                    contentType = partition.getType();
                    if (e.getOffset() == partition.getOffset() + partition.getLength())
                        reparseStart = partitionStart;
                    first--;
                }
                else if (reparseStart == e.getOffset() && reparseStart == partition.getOffset() + partition.getLength()) {
                    partitionStart = partition.getOffset();
                    contentType = partition.getType();
                    reparseStart = partitionStart;
                    first--;
                }
                else {
                    partitionStart = partition.getOffset() + partition.getLength();
                    contentType = "__dftl_partition_content_type";
                }
            }
            fPositionUpdater.update(e);
            for (int i = first; i < category.length; i++) {
                Position p = category[i];
                if (!p.isDeleted)
                    continue;
                rememberDeletedOffset(e.getOffset());
                d.removePosition(CONTENT_TYPES_CATEGORY, p);
                //                break;
            }
            category = d.getPositions(CONTENT_TYPES_CATEGORY);
            fScanner.setPartialRange(d, reparseStart, d.getLength() - reparseStart, contentType, partitionStart);
            int lastScannedPosition = reparseStart;
            for (IToken token = fScanner.nextToken(); !token.isEOF();) {
                contentType = getTokenContentType(token);
                if (!isSupportedContentType(contentType)) {
                    token = fScanner.nextToken();
                }
                else {
                    int start = fScanner.getTokenOffset();
                    int length = fScanner.getTokenLength();
                    lastScannedPosition = (start + length) - 1;
                    for (; first < category.length; first++) {
                        TypedPosition p = (TypedPosition) category[first];
                        if (lastScannedPosition < ((Position) (p)).offset + ((Position) (p)).length
                                && (!p.overlapsWith(start, length) || d.containsPosition("__content_types_category", start,
                                        length)
                                        && contentType.equals(p.getType())))
                            break;
                        rememberRegion(((Position) (p)).offset, ((Position) (p)).length);
                        p.delete();
                        d.removePosition(CONTENT_TYPES_CATEGORY, p);
                    }
                    if (d.containsPosition(CONTENT_TYPES_CATEGORY, start, length)) {
                        if (lastScannedPosition > e.getOffset())
                            return createRegion();
                        first++;
                    }
                    else {
                        try {
                            d.addPosition(CONTENT_TYPES_CATEGORY, new XMLNode(start, length, contentType, fDocument));
                            rememberRegion(start, length);
                        }
                        catch (BadPositionCategoryException _ex) {
                        }
                        catch (BadLocationException _ex) {
                        }
                    }
                    token = fScanner.nextToken();
                }
            }
            if (lastScannedPosition != reparseStart)
                lastScannedPosition++;
            for (first = d.computeIndexInCategory(CONTENT_TYPES_CATEGORY, lastScannedPosition); first < category.length;) {
                TypedPosition p = (TypedPosition) category[first++];
                p.delete();
                d.removePosition(CONTENT_TYPES_CATEGORY, p);
                rememberRegion(((Position) (p)).offset, ((Position) (p)).length);
            }
        }
        catch (BadPositionCategoryException _ex) {
        }
        catch (BadLocationException _ex) {
        }
        return createRegion();
    }

    protected TypedPosition findClosestPosition(int offset) {
        try {
            int index = fDocument.computeIndexInCategory(CONTENT_TYPES_CATEGORY, offset);
            Position category[] = fDocument.getPositions(CONTENT_TYPES_CATEGORY);
            if (category.length == 0)
                return null;
            if (index < category.length && offset == category[index].offset)
                return (TypedPosition) category[index];
            if (index > 0)
                index--;
            return (TypedPosition) category[index];
        }
        catch (BadPositionCategoryException _ex) {
        }
        catch (BadLocationException _ex) {
        }
        return null;
    }

    public String getContentType(int offset) {
        TypedPosition p = findClosestPosition(offset);
        if (p != null && p.includes(offset))
            return p.getType();
        return "__dftl_partition_content_type";
    }

    public ITypedRegion getPartition(int offset) {
        try {
            Position category[] = fDocument.getPositions(CONTENT_TYPES_CATEGORY);
            if (category == null || category.length == 0)
                return new TypedRegion(0, fDocument.getLength(), "__dftl_partition_content_type");
            int index = fDocument.computeIndexInCategory(CONTENT_TYPES_CATEGORY, offset);
            if (index < category.length) {
                TypedPosition next = (TypedPosition) category[index];
                if (offset == ((Position) (next)).offset)
                    return new TypedRegion(next.getOffset(), next.getLength(), next.getType());
                if (index == 0)
                    return new TypedRegion(0, ((Position) (next)).offset, "__dftl_partition_content_type");
                TypedPosition previous = (TypedPosition) category[index - 1];
                if (previous.includes(offset)) {
                    return new TypedRegion(previous.getOffset(), previous.getLength(), previous.getType());
                }
                int endOffset = previous.getOffset() + previous.getLength();
                return new TypedRegion(endOffset, next.getOffset() - endOffset, "__dftl_partition_content_type");
            }
            TypedPosition previous = (TypedPosition) category[category.length - 1];
            if (previous.includes(offset)) {
                return new TypedRegion(previous.getOffset(), previous.getLength(), previous.getType());
            }
            int endOffset = previous.getOffset() + previous.getLength();
            return new TypedRegion(endOffset, fDocument.getLength() - endOffset, "__dftl_partition_content_type");
        }
        catch (BadPositionCategoryException _ex) {
        }
        catch (BadLocationException _ex) {
        }
        return new TypedRegion(0, fDocument.getLength(), "__dftl_partition_content_type");
    }

    public ITypedRegion[] computePartitioning(int offset, int length) {
        List list = new ArrayList();
        try {
            int endOffset = offset + length;
            Position category[] = fDocument.getPositions(CONTENT_TYPES_CATEGORY);
            TypedPosition previous = null;
            TypedPosition current = null;
            Position gap = null;
            for (int i = 0; i < category.length; i++) {
                current = (TypedPosition) category[i];
                if (current.isDeleted()) {
                    new Exception("w" + current).printStackTrace();
                }
                int gapOffset = previous == null ? 0 : previous.getOffset() + previous.getLength();
                gap = new Position(gapOffset, current.getOffset() - gapOffset);
                if (gap.getLength() > 0 && gap.overlapsWith(offset, length)) {
                    int start = Math.max(offset, gapOffset);
                    int end = Math.min(endOffset, gap.getOffset() + gap.getLength());
                    list.add(new TypedRegion(start, end - start, "__dftl_partition_content_type"));
                }
                if (current.overlapsWith(offset, length)) {
                    int start = Math.max(offset, current.getOffset());
                    int end = Math.min(endOffset, current.getOffset() + current.getLength());
                    list.add(new TypedRegion(start, end - start, current.getType()));
                }
                previous = current;
            }
            if (previous != null) {
                int gapOffset = previous.getOffset() + previous.getLength();
                gap = new Position(gapOffset, fDocument.getLength() - gapOffset);
                if (gap.getLength() > 0 && gap.overlapsWith(offset, length)) {
                    int start = Math.max(offset, gapOffset);
                    int end = Math.min(endOffset, fDocument.getLength());
                    list.add(new TypedRegion(start, end - start, "__dftl_partition_content_type"));
                }
            }
            if (list.isEmpty())
                list.add(new TypedRegion(offset, length, "__dftl_partition_content_type"));
        }
        catch (BadPositionCategoryException _ex) {
        }
        TypedRegion result[] = new TypedRegion[list.size()];
        list.toArray(result);
        return result;
    }

    public String[] getLegalContentTypes() {
        return fLegalContentTypes;
    }

    protected boolean isSupportedContentType(String contentType) {
        if (contentType != null) {
            for (int i = 0; i < fLegalContentTypes.length; i++)
                if (fLegalContentTypes[i].equals(contentType))
                    return true;
        }
        return false;
    }

    protected String getTokenContentType(IToken token) {
        Object data = token.getData();
        if (data instanceof String)
            return (String) data;
        return null;
    }
}