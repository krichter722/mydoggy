package org.noos.common.object;

import org.noos.common.context.Context;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ObjectCreator<R> {

    R create(Context context);

}
