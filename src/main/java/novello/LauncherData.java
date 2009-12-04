/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello;

import novello.startup.BookFile;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import com.xapp.marshalling.Unmarshaller;
import com.xapp.marshalling.Marshaller;

public class LauncherData
{
    private List<BookFile> m_recentlyOpened = new ArrayList<BookFile>();
    private BookFile m_lastOpened;
    private static String FILE;

    public List<BookFile> getRecentlyOpened()
    {
        return m_recentlyOpened;
    }

    public void setRecentlyOpened(List<BookFile> recentlyOpened)
    {
        m_recentlyOpened = recentlyOpened;
    }

    public BookFile getLastOpened()
    {
        return m_lastOpened;
    }

    public void setLastOpened(BookFile lastOpened)
    {
        m_lastOpened = lastOpened;
    }

    public static LauncherData load()
    {
        FILE = "launcher-data.xml";
        File f = new File(FILE);
        if(f.exists())
        {
            return Unmarshaller.load(LauncherData.class, FILE);
        }
        else
        {
            return new LauncherData();
        }
    }

    public void addRecentlyOpened(BookFile bookfile)
    {
        if(!m_recentlyOpened.contains(bookfile))
        {
            m_recentlyOpened.add(bookfile);
        }
    }

    public static void save(LauncherData launcherData)
    {
        new Marshaller<LauncherData>(LauncherData.class).marshal(FILE, launcherData);
    }
}
