package org.mevenide.idea.util.images;

import com.intellij.util.ImageLoader;

import java.awt.Image;

/**
 * @author Arik
 */
public abstract class Images {
    public static final Image ADD;
    public static final Image CLOSE;
    public static final Image COLLAPSE_ALL;
    public static final Image EXPAND_ALL;
    public static final Image FILTER;
    public static final Image MAVEN_ICON;
    public static final Image OPTIONS;
    public static final Image PAUSE;
    public static final Image PLAY;
    public static final Image PLAY_REVERSED;
    public static final Image REMOVE;
    public static final Image RERUN;
    public static final Image STOP;

    static {
        ADD = ImageLoader.loadFromResource("add.png", Images.class);
        CLOSE = ImageLoader.loadFromResource("close.png", Images.class);
        COLLAPSE_ALL = ImageLoader.loadFromResource("collapse-all.png", Images.class);
        EXPAND_ALL = ImageLoader.loadFromResource("expand-all.png", Images.class);
        FILTER = ImageLoader.loadFromResource("filter.png", Images.class);
        MAVEN_ICON = ImageLoader.loadFromResource("maven-icon.png", Images.class);
        OPTIONS = ImageLoader.loadFromResource("options.png", Images.class);
        PAUSE = ImageLoader.loadFromResource("pause.png", Images.class);
        PLAY = ImageLoader.loadFromResource("play.png", Images.class);
        PLAY_REVERSED = ImageLoader.loadFromResource("play-reversed.png", Images.class);
        REMOVE = ImageLoader.loadFromResource("remove.png", Images.class);
        RERUN = ImageLoader.loadFromResource("rerun.png", Images.class);
        STOP = ImageLoader.loadFromResource("stop.png", Images.class);
    }
}
