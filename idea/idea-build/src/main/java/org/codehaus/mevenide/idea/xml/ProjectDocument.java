package org.codehaus.mevenide.idea.xml;

import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.JavaNameStrategy;
import com.intellij.util.xml.NameStrategy;

import java.util.List;

public interface ProjectDocument {
    Project getProject();

    boolean isWellFormed ();

    @NameStrategy(JavaNameStrategy.class)
    interface Project extends DomElement {
        GenericDomValue<String> getName();
        GenericDomValue<String> getArtifactId();
        Build getBuild();
    }

    interface Build extends DomElement{
        Plugins getPlugins();
    }

    interface Plugins extends DomElement{
        List<Plugin> getPlugins();
    }

    @NameStrategy(JavaNameStrategy.class)
    public interface Plugin extends DomElement {
        GenericDomValue<String> getGroupId();

        GenericDomValue<String> getArtifactId();

        GenericDomValue<String> getVersion();
    }

    public class Factory {
        public static ProjectDocument parse(PsiFile psiFile) {
            return new ProjectDocumentImpl( psiFile );
        }
    }
}
