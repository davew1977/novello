/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import novello.utils.CachedLookup;
import novello.utils.Lookup;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
            Desktop.getDesktop().browse(new URI(link(item)));
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

    public String link(ResultItem item)
    {
        return "http://www.wikipedia.org/wiki/" + item.getTitle().replace(' ','_');
    }
}
