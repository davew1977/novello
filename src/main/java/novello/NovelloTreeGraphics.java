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
    private static final ImageIcon CONTENT = loadImage("/pen2.png");
    private static final ImageIcon CHUNK = loadImage("/small-pen.png");

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
