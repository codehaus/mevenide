package org.mevenide.idea.psi.util;

import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public final class XmlFilterExpression {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(XmlFilterExpression.class);

    private String tagName;
    private Integer index;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(final String pTagName) {
        tagName = pTagName;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final Integer pIndex) {
        index = pIndex;
    }

    public XmlTag findChildTag(final XmlTag pParent) {
        if (index == null)
            return pParent.findFirstSubTag(tagName);
        else {
            final XmlTag[] tags = pParent.getSubTags();
            if (index < 0 || index >= tags.length)
                return null;
            return tags[index];
        }
    }

    public static XmlFilterExpression create(final String pExpression) {
        final XmlFilterExpression exp = new XmlFilterExpression();
        final int startExprTokenCount = StringUtils.countMatches(pExpression, "[");
        final int endExprTokenCount = StringUtils.countMatches(pExpression, "]");
        if (startExprTokenCount != endExprTokenCount || startExprTokenCount > 1)
            throw new IllegalExpressionException(RES.get("illegal.expr", pExpression));

        if (startExprTokenCount == 0) {
            exp.setTagName(pExpression);
            return exp;
        }

        if (!pExpression.endsWith("]"))
            throw new IllegalExpressionException(RES.get("illegal.expr", pExpression));

        final int startExprIndex = pExpression.indexOf("[");
        exp.setTagName(pExpression.substring(0, startExprIndex));

        final String rowExpr = pExpression.substring(startExprIndex + 1,
                                                     pExpression.length() - 1);
        final int row = Integer.valueOf(rowExpr);
        exp.setIndex(row);

        return exp;
    }
}
