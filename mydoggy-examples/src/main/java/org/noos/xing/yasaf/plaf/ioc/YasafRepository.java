package org.noos.xing.yasaf.plaf.ioc;

import org.noos.xing.yasaf.ioc.Repository;
import org.noos.xing.yasaf.ioc.Directory;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class YasafRepository implements Repository {
    private static Repository instance = new YasafRepository();

    public static Repository getInstance() {
        return instance;
    }

    protected Directory root;

    public YasafRepository() {
        this.root = new YasafDirectory("root");
    }

    public Directory getRoot() {
        return root;
    }

    public Directory goTo(Object... path) {
        Directory cursor = getRoot();
        for (Object element : path) {
            cursor = cursor.getDirectory(element);
            if (cursor == null)
                throw new IllegalArgumentException("Invalid path!!!");
        }
        return cursor;
    }

}
