/*
 *
 * Date: 2010-jan-31
 * Author: davidw
 *
 */
package novello.wordpress;

public class Account
{
    private String m_username;
    private String m_password;
    private String m_blogURL;

    public Account(String username, String password, String blogURL)
    {
        m_username = username;
        m_password = password;
        m_blogURL = blogURL;
    }

    public Account()
    {
    }

    public String getUsername()
    {
        return m_username;
    }

    public void setUsername(String username)
    {
        m_username = username;
    }

    public String getPassword()
    {
        return m_password;
    }

    public void setPassword(String password)
    {
        m_password = password;
    }

    public String getBlogURL()
    {
        return m_blogURL;
    }

    public void setBlogURL(String blogURL)
    {
        m_blogURL = blogURL;
    }
}
