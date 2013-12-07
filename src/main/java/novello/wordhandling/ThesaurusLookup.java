/*
 *
 * Date: 2009-dec-15
 * Author: davidw
 *
 */
package novello.wordhandling;

import net.sf.xapp.utils.FileUtils;
import novello.utils.CachedLookup;
import novello.utils.Lookup;

import java.util.Collection;
import java.util.LinkedHashSet;

public class ThesaurusLookup implements Lookup<String, Collection<String>>
{
    private static final String URL = "http://words.bighugelabs.com/api/2/4b28aa907abd03d83b0a877ddc2f903c/%s/";



    public Collection<String> lookup(String word)
    {
        Collection<String> options = new LinkedHashSet<String>();
        String result = FileUtils.downloadToString(String.format(URL, word));
        if (result != null)
        {
            String[] lines = result.split("\n");
            for (String line : lines)
            {
                int i = line.lastIndexOf('|');
                if (i != -1)
                {
                    String option = line.substring(i + 1);
                    options.add(option);
                }
            }
        }
        return options;
    }

    public static Lookup<String, Collection<String>> create()
    {
        return new CachedLookup<String, Collection<String>>(new ThesaurusLookup());
    }
}
