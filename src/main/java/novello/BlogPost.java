/*
 *
 * Date: 2010-jan-31
 * Author: davidw
 *
 */
package novello;

public class BlogPost extends Content
{
    private String m_postId;

    public String getPostId()
    {
        return m_postId;
    }

    public void setPostId(String postId)
    {
        m_postId = postId;
    }
}
