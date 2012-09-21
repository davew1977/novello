/*
 *
 * Date: 2010-jan-26
 * Author: davidw
 *
 */
package novello.wordhandling;

import java.util.List;

public interface Dictionary
{
    boolean wordOk(String word);

    List<String> findWords(String wordStart, int maxHits);

    List<String> findWords(String wordStart);

    void addWord(String word);
    void addWords(List<String> word);

    void reset();
}
