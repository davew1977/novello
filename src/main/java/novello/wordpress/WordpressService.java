/*
 *
 * Date: 2010-jan-29
 * Author: davidw
 *
 */
package novello.wordpress;


import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class WordpressService
{
    private String m_username;
    private String m_password;
    private String m_blogURL;

    public WordpressService(String username, String password, String blogURL)
    {
        m_username = username;
        m_password = password;
        m_blogURL = blogURL;
    }

    public List<Blog> getUserBlogs()
    {
        Object[] blogdata = call("wp.getUsersBlogs", m_username, m_password);
        ArrayList<Blog> blogs = new ArrayList<Blog>();
        for (Object o : blogdata)
        {
            Map<String, String> m = (Map<String, String>) o;
            String name = m.get("blogName");
            String id = m.get("blogid");
            String xmlrpcurl = m.get("xmlrpc");
            blogs.add(new Blog(name, id, xmlrpcurl));
        }
        return blogs;
    }

    private <T> T call(String method, String... params)
    {
        try
        {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = String.format("%s/xmlrpc.php", m_blogURL);
            config.setServerURL(new URL(url));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            return (T) client.execute(method, params);
        }
        catch (XmlRpcException e)
        {
            throw new RuntimeException(e);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws MalformedURLException, XmlRpcException
    {
        WordpressService w = new WordpressService("davew1977", "findus5", "http://davew1977.wordpress.com");
        List<Blog> userBlogs = w.getUserBlogs();
        System.out.println(userBlogs);
    }
}
