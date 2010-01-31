/*
 *
 * Date: 2010-jan-31
 * Author: davidw
 *
 */
package novello.wordpress;

public class Blog
{
    String m_name;
    String m_id;
    String m_xmlRpcURL;

    public Blog(String name, String id, String xmlRpcURL)
    {
        m_name = name;
        m_id = id;
        m_xmlRpcURL = xmlRpcURL;
    }

    public String getName()
    {
        return m_name;
    }

    public String getId()
    {
        return m_id;
    }

    public String getXmlRpcURL()
    {
        return m_xmlRpcURL;
    }

    public String toString()
    {
        return "Blog{" +
                "m_name='" + m_name + '\'' +
                ", m_id=" + m_id +
                ", m_xmlRpcURL='" + m_xmlRpcURL + '\'' +
                '}';
    }
}
