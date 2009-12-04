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
import java.io.File;

import novello.startup.BookFile;

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
