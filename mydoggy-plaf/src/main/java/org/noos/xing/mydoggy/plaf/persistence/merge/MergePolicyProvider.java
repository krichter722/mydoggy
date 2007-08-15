package org.noos.xing.mydoggy.plaf.persistence.merge;

import org.noos.xing.mydoggy.PersistenceDelegate;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @TODO: think again this
 */
public class MergePolicyProvider {
    private static Map<PersistenceDelegate.MergePolicy, MergePolicyApplier> appliers;

    static {
        appliers = new HashMap<PersistenceDelegate.MergePolicy, MergePolicyApplier>();
        appliers.put(PersistenceDelegate.MergePolicy.RESET, new ResetMergePolicy());
        appliers.put(PersistenceDelegate.MergePolicy.UNION, new UnionMergePolicy());
    }

    public static MergePolicyApplier getMergePolicyApplier(PersistenceDelegate.MergePolicy mergePolicy) {
        return appliers.get(mergePolicy);
    }


}
