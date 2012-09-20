package requiem;

import com.xapp.application.api.SimpleTreeGraphics;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: davidw
 * Date: 9/20/12
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequiemTreeGraphics extends SimpleTreeGraphics
{
    public static final ImageIcon TASK_DONE = loadImage("/tick_green_small.png");
    public static final ImageIcon TASK_TODO = loadImage("/tick_orange_small.png");
    public static final ImageIcon TASK_STALLED = loadImage("/tick_red_small.png");
    public static final ImageIcon TASK_OTHER = loadImage("/tick_blue_small.png");

}
