/*
 *
 * Date: 2010-jan-09
 * Author: davidw
 *
 */
package novello.goodreads;

import java.util.List;

public class BookMeta
{
    private String m_name;
    private String m_author;
    private String m_isbn;
    private List<String> m_tags;
    private String m_email;

    public String getEmail()
    {
        return m_email;
    }

    public void setEmail(String email)
    {
        m_email = email;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public String getAuthor()
    {
        return m_author;
    }

    public void setAuthor(String author)
    {
        m_author = author;
    }

    public String getIsbn()
    {
        return m_isbn;
    }

    public void setIsbn(String isbn)
    {
        m_isbn = isbn;
    }

    public List<String> getTags()
    {
        return m_tags;
    }

    public void setTags(List<String> tags)
    {
        m_tags = tags;
    }

    public String toString()
    {
        return m_name + " by " + m_author;
    }
}
