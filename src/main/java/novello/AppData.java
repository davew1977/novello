/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.Reference;

public class AppData
{
    private Content m_lastEdited;

    @Reference
    public Content getLastEdited()
    {
        return m_lastEdited;
    }

    public void setLastEdited(Content lastEdited)
    {
        m_lastEdited = lastEdited;
    }
}
