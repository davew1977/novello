/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import com.xapp.marshalling.annotations.XMLMapping;

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
