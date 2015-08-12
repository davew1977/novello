package requiem;

import net.sf.xapp.application.api.Command;
import net.sf.xapp.application.api.Node;
import net.sf.xapp.application.api.NodeCommand;
import net.sf.xapp.application.api.SpecialTreeGraphics;
import net.sf.xapp.objectmodelling.core.PropertyChange;
import net.sf.xapp.objectmodelling.core.Tree;
import net.sf.xapp.objectmodelling.core.TreeNode;
import net.sf.xapp.utils.svn.SVNFacade;
import novello.DocumentApp;
import novello.Text;
import novello.TextHolder;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates ...
 */
public class RequiemApp extends DocumentApp<Backlog> {

    public RequiemApp(SVNFacade svnFacade) {
        super(svnFacade);
    }

    @Override
    public SpecialTreeGraphics createSpecialTreeGraphics() {
        return new RequiemTreeGraphics();
    }

    @Override
    public Tree getDocTree() {
        return getDocument().getWork();
    }

    @Override
    public List<Command> getCommands(Node node) {
        List<Command> commands = super.getCommands(node);
        if(node.isA(WorkItem.class)) {
            commands.add(new NodeCommand("Switch State", "", "control D") {
                @Override
                public void execute(Node node) {
                    WorkItem workItem = node.wrappedObject();
                    workItem.setStatus(workItem.getStatus().next());
                }
            });
        }
        return commands;
    }

    private void trimName(TreeNode pChild) {
        String name = pChild.getName();
        if(name.endsWith("ß")) {
            pChild.setName(name.substring(0, name.length() - 1));
        }
    }

    @Override
    public void nodeAdded(Node node) {
        tryTrimName(node);
    }

    private void tryTrimName(Node node) {
        if(node.isA(TreeNode.class)) {
            trimName(node.<TreeNode>wrappedObject());
        }
    }

    @Override
    public void nodeUpdated(Node objectNode, Map<String, PropertyChange> changes) {
        tryTrimName(objectNode);
    }

    public void doSplit(Text chunk)
    {
        doSplit(chunk, getAppContainer().getNode(chunk));
    }

    private void doSplit(Text textChunk, Node node)
    {
        String[] chunks = textChunk.text().split("\n,");

        if (chunks.length > 1)
        {
            /*Tree workItem = (Tree) textChunk;
            List<TreeNode> contentList = workItem.getChildren();
            int index = contentList.indexOf(textChunk);
            textChunk.setText("");
            for (int i = 0; i < chunks.length; i++)
            {
                String chunk = chunks[i];
                String name = chunk.substring(0, Math.min(chunk.length(), 30));
                if (!chunk.startsWith("\n"))
                {
                    String[] s = chunk.split("\n", 2);
                    name = s[0];
                    chunk = s.length > 1 ? s[1] : "";
                }
                Task newTask = classDatabase().newInstance(Task.class);
                newTask.setName(name);
                newTask.setParent(workItem);
                newTask.setText(chunk);
                contentList.add(newTask);
            }
            getAppContainer().refreshNode(getAppContainer().getNode(workItem));
            getAppContainer().expand(textChunk);
            getAppContainer().getMainTree().requestFocusInWindow();*/
        }
    }

    @Override
    public TextHolder getTextHolder(Text text) {
        return (TextHolder) text;
    }
}
