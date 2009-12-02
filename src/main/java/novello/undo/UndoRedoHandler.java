/*
 *
 * Date: 2009-dec-01
 * Author: davidw
 *
 */
package novello.undo;

import java.util.List;

public interface UndoRedoHandler
{
    void handleUpdates(List<Update> updates);
}
