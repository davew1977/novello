/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import novello.utils.Lookup;
import novello.utils.CachedLookup;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class WikipediaService implements Lookup<String, WikipediaResponse>
{
    private Lookup<String, WikipediaResponse> m_lookup;

    public WikipediaService()
    {
        m_lookup = new CachedLookup<String, WikipediaResponse>(new WikipediaLookup());
    }

    public WikipediaResponse lookup(String key)
    {
        return m_lookup.lookup(key);
    }

    public void open(ResultItem item)
    {
        try
        {
            Desktop.getDesktop().browse(new URI("http://www.wikipedia.org/wiki/" + item.getTitle().replace(' ','_')));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }
}
