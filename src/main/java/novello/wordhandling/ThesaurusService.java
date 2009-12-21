/*
 *
 * Date: 2009-dec-15
 * Author: davidw
 *
 */
package novello.wordhandling;

import com.xapp.utils.FileUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ThesaurusService
{
    private static final String URL = "http://words.bighugelabs.com/api/2/4b28aa907abd03d83b0a877ddc2f903c/%s/";

    private Map<String, List<String>> m_cache = new HashMap<String, List<String>>();

    public List<String> suggest(String word)
    {
        List<String> options = m_cache.get(word);
        if (options == null)
        {
            options = new ArrayList<String>();
            m_cache.put(word, options);
            String result = FileUtils.downloadToString(String.format(URL, word));
            if (result!=null)
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
        }
        return options;
    }
}
