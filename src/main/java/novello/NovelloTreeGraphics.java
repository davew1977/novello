/*
 *
 * Date: 2009-nov-16
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.api.Node;
import com.xapp.application.api.SimpleTreeGraphics;

import javax.swing.*;

public class NovelloTreeGraphics extends SimpleTreeGraphics
{
    public static final ImageIcon BOOK_ICON = loadImage("/book icon.gif");
    private static final ImageIcon CONTENT = loadImage("/pen2.png");
    private static final ImageIcon CHUNK = loadImage("/small-pen.png");
    public static final ImageIcon OOM_ICON = loadImage("/oom-icon.png");
    public static final ImageIcon SVN_ICON = loadImage("/svn-logo.png");
    public static final ImageIcon WIKIPEDIA_ICON = loadImage("/wikipedia-icn.jpg");
    public static final ImageIcon UPDATE_ICON = loadImage("/update.png");
    public static final ImageIcon COMMIT_ICON = loadImage("/commit.png");
    public static final ImageIcon SAVE_ICON = loadImage("/save.png");
    public static final ImageIcon REVERT_ICON = loadImage("/revert.png");

    public ImageIcon getNodeImage(Node node)
    {
        if(node.wrappedObject() instanceof Content)
        {
            return CONTENT;
        }
        else if(node.wrappedObject() instanceof TextChunk)
        {
            return CHUNK;
        }
        return null;
    }
}
