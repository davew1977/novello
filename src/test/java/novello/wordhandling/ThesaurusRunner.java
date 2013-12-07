/*
 *
 * Date: 2009-dec-15
 * Author: davidw
 *
 */
package novello.wordhandling;

import net.sf.xapp.utils.FileUtils;

public class ThesaurusRunner
{
    public static final String URL = "http://words.bighugelabs.com/api/2/4b28aa907abd03d83b0a877ddc2f903c/ecstatic/";

    public static void main(String[] args)
    {
        String s = FileUtils.downloadToString(URL);
        System.out.println(s);
    }
}
