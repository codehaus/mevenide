package org.mevenide.idea.project.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.util.PathUtil;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.ui.MultiLineLabel;

/**
 * @author Arik
 */
public final class PomUtils {
    private static final String DEFAULT_TITLE = "Please select a project";

    public static String selectPom(final Project pProject) {
        return selectPom(pProject, null, null, null);
    }

    public static String selectPom(final Project pProject,
                                   final String pTitle) {
        return selectPom(pProject, null, pTitle, null);
    }

    public static String selectPom(final Project pProject,
                                   final String[] pPomUrls) {
        return selectPom(pProject, pPomUrls, null, null);
    }

    public static String selectPom(final Project pProject,
                                   final String[] pPomUrls,
                                   final String pTitle) {
        return selectPom(pProject, pPomUrls, pTitle, null);
    }

    public static String selectPom(final Project pProject,
                                   final String pTitle,
                                   final String pLabel) {
        return selectPom(pProject, null, pTitle, pLabel);
    }

    public static String selectPom(final Project pProject,
                                   String[] pPomUrls,
                                   String pTitle,
                                   final String pLabel) {
        if (pPomUrls == null || pPomUrls.length == 0) {
            final PomManager mgr = PomManager.getInstance(pProject);
            pPomUrls = mgr.getFileUrls();
        }

        if (pPomUrls == null || pPomUrls.length == 0)
            return null;
        else {
            final Set<String> urls = new HashSet<String>(pPomUrls.length);
            for (String url : pPomUrls)
                if (url != null && url.trim().length() > 0)
                    urls.add(url);

            pPomUrls = urls.toArray(new String[urls.size()]);
            if (pPomUrls.length == 1)
                return pPomUrls[0];
        }

        if (pTitle == null || pTitle.trim().length() == 0)
            pTitle = DEFAULT_TITLE;

        final SelectFromListDialog dlg = new SelectFromListDialog(
                pProject,
                pPomUrls,
                new SelectFromListDialog.ToStringAspect() {
                    public String getToStirng(Object obj) {
                        return PathUtil.toPresentableUrl(obj.toString());
                    }
                },
                pTitle,
                ListSelectionModel.SINGLE_SELECTION);

        if (pLabel != null && pLabel.trim().length() > 0)
            dlg.addToDialog(new MultiLineLabel(pLabel), BorderLayout.PAGE_START);

        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.show();

        if (!dlg.isOK())
            return null;

        return dlg.getSelection()[0].toString();
    }
}
