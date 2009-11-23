/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.Reference;

import java.util.List;
import java.util.ArrayList;

public class AppData
{
    private Content m_lastEdited;

    private List<NovelloFile> m_recentlyOpened = new ArrayList<NovelloFile>();

    @Reference
    public Content getLastEdited()
    {
        return m_lastEdited;
    }

    public void setLastEdited(Content lastEdited)
    {
        m_lastEdited = lastEdited;
    }

    public List<NovelloFile> getRecentlyOpened()
    {
        return m_recentlyOpened;
    }

    public void setRecentlyOpened(List<NovelloFile> recentlyOpened)
    {
        m_recentlyOpened = recentlyOpened;
    }
}
