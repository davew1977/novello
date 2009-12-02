/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello.undo;

import java.util.List;

public interface UpdateListener
{
    void updates(List<Update> updates);      
}
