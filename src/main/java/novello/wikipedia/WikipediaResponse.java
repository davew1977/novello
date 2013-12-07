/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import net.sf.xapp.annotations.marshalling.XMLMapping;

public class WikipediaResponse
{
    private QueryResult m_queryResult;

    @XMLMapping("query")
    public QueryResult getQueryResult()
    {
        return m_queryResult;
    }

    public void setQueryResult(QueryResult queryResult)
    {
        m_queryResult = queryResult;
    }
}
