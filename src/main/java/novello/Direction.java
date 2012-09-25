/*
 *
 * Date: 2009-dec-23
 * Author: davidw
 *
 */
package novello;

public enum Direction
{
    forward(1), back(-1);
    private int delta;

    Direction(int i)
    {
        delta = i;
    }

    public int getDelta()
    {
        return delta;
    }
}
