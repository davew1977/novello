/*
 *
 * Date: 2010-jan-29
 * Author: davidw
 *
 */
package novello.wordpress;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordpressService
{
    private Account m_account;

    public WordpressService(Account account)
    {
        m_account = account;
    }

    public List<Blog> getUserBlogs()
    {
        Object[] blogdata = call("wp.getUsersBlogs", u(), p());
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

    private String p()
    {
        return m_account.getPassword();
    }

    private String u()
    {
        return m_account.getUsername();
    }

    public void listPages()
    {
        Object[] list = call("wp.getPageList", "10186004", u(), p());

        for (int i = 0; i < list.length; i++)
        {
            Object o = list[i];
            System.out.println(o);
        }
    }

    private <T> T call(String method, String... params)
    {
        try
        {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = String.format("%s/xmlrpc.php", m_account.getBlogURL());
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
        WordpressService w = new WordpressService(new Account("davew1977", "findus5", "http://davew1977.wordpress.com"));
        List<Blog> userBlogs = w.getUserBlogs();
        System.out.println(userBlogs);
        w.listPages();
    }
}
