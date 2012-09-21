package requiem;

import com.xapp.application.api.Node;
import com.xapp.application.api.SimpleTreeGraphics;

import javax.swing.*;

/**
 */
public class RequiemTreeGraphics extends SimpleTreeGraphics
{
    public static final ImageIcon TASK_DONE = loadImage("/tick_green_small.png");
    public static final ImageIcon TASK_TODO = loadImage("/tick_orange_small.png");
    public static final ImageIcon TASK_STALLED = loadImage("/tick_red_small.png");
    public static final ImageIcon TASK_OTHER = loadImage("/tick_blue_small.png");

    @Override
    public ImageIcon getNodeImage(Node node) {
        if(node.isA(WorkItem.class)) {
            WorkItem workItem = node.wrappedObject();
            switch (workItem.getStatus()) {
                case DONE: return TASK_DONE;
                case TODO: return TASK_TODO;
                case STALLED: return TASK_STALLED;
            }
        }
        return super.getNodeImage(node);
    }
}
