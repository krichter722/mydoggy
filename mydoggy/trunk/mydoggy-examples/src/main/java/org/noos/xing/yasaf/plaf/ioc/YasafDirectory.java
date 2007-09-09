package org.noos.xing.yasaf.plaf.ioc;

import org.noos.xing.yasaf.ioc.Directory;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class YasafDirectory implements Directory {
    private Object name;
    private Map<Object, Directory> dirs;
    private Map<Object, Object> resources;

    public YasafDirectory(Object name) {
        this.name = name;
        this.dirs = new HashMap<Object, Directory>();
        this.resources = new HashMap<Object, Object>();
    }

    public Directory addDirectory(Object name) {
        return dirs.put(name, new YasafDirectory(name));
    }

    public Directory getDirectory(Object name) {
        return dirs.get(name);
    }

    public void addResource(Object name, Object resource) {
        resources.put(name, resource);
    }

    public Object getResource(Object name) {
        return resources.get(name);
    }
}
