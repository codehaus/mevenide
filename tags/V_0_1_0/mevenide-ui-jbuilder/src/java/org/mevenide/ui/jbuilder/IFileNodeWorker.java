package org.mevenide.ui.jbuilder;

import java.util.ArrayList;

import com.borland.primetime.node.Project;
import com.borland.primetime.vfs.Url;

public interface IFileNodeWorker {
    public void initMavenFileNode (MavenFileNode mavenFileNode, Project project,
                                   Url url, ArrayList goalNodes,
                                   ArrayList childNodes);

    public void refreshDependencies (Url url, Project project);
}
