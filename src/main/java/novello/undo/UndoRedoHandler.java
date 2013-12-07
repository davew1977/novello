/*
 *
 * Date: 2009-dec-01
 * Author: davidw
 *
 */
package novello.undo;

public interface UndoRedoHandler
{
    boolean canUndo();
    boolean canRedo();
    public Update pullUndo();
    public Update pullRedo();

    
}
