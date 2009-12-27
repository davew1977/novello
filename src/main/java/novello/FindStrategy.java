/*
 *
 * Date: 2009-dec-23
 * Author: davidw
 *
 */
package novello;

import java.util.regex.Pattern;

public interface FindStrategy
{
    Pattern getPattern();
    boolean accept(String s);
}
