/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import com.xapp.marshalling.annotations.XMLMapping;

@XMLMapping("p")
public class ResultItem
{
    private String m_title;
    private String m_snippet;

    public String getTitle()
    {
        return m_title;
    }

    public void setTitle(String title)
    {
        m_title = title;
    }

    public String getSnippet()
    {
        return m_snippet;
    }

    public void setSnippet(String snippet)
    {
        m_snippet = snippet;
    }
}
