package org.noos.xing.mydoggy.plaf.support;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author Angelo De Caro
 */
public class ResolvableHashtable<K, V> extends Hashtable<K, V> {
    protected Resolver<V> resolver;


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
        if  (resolver == null)
            this.resolver = new Resolver<V>() {
                public V get(Object key) {
                    return null;
                }
            };
        else
            this.resolver = resolver;
    }

    public synchronized V get(Object key) {
        V result = super.get(key);
        return (result != null) ? result : resolver.get(key);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ResolvableHashtable that = (ResolvableHashtable) o;

        return resolver.equals(that.resolver);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + resolver.hashCode();
        return result;
    }


    public interface Resolver<V> extends Serializable {

        V get(Object key);
        
    }
}
