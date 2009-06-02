package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PersistenceDelegateFilter;

/**
 * Internal wrapper for the PersistenceDelegateFilter interface. Used to modify specific behaviour of PersistenceDelegate. 
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public interface InternalPersistenceDelegateFilter extends PersistenceDelegateFilter {

    boolean saveSelectedContent();

}
