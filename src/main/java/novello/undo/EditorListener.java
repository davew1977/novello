/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello.undo;

public interface EditorListener
{
    void textAdded(int offs, String newText);

    void textRemoved(int offs, String removedText);
}
