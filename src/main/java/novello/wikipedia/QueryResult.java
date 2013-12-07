/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import net.sf.xapp.annotations.marshalling.XMLMapping;

import java.util.List;

@XMLMapping("query")
public class QueryResult
{
    private List<ResultItem> m_items;

    @XMLMapping("search")
    public List<ResultItem> getItems()
    {
        return m_items;
    }

    public void setItems(List<ResultItem> items)
    {
        m_items = items;
    }
}
