package org.noos.xing.mydoggy.plaf.support;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author Angelo De Caro
 */
public class ResolvableHashtable<K, V> extends Hashtable<K, V> {
    private Resolver<V> resolver;

    public ResolvableHashtable() {
        this.resolver = new Resolver<V>() {
            public V get(Object key) {
                return null;
            }
        };
    }

    public ResolvableHashtable(final V defaultInstance) {
        this.resolver = new Resolver<V>() {
            public V get(Object key) {
                return defaultInstance;
            }
        };
    }

    public ResolvableHashtable(Resolver<V> resolver) {
        this.resolver = resolver;
    }

    public synchronized V get(Object key) {
        V result = super.get(key);
        return (result != null) ? result : resolver.get(key);
    }


    public interface Resolver<V> extends Serializable {

        V get(Object key);
        
    }
}
