/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-09
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.api.ApplicationContainer;
import com.xapp.application.api.Command;
import com.xapp.application.api.Node;
import com.xapp.application.api.NodeCommand;
import com.xapp.application.api.SimpleApplication;
import com.xapp.application.api.SpecialTreeGraphics;
import com.xapp.application.utils.SwingUtils;
import com.xapp.application.utils.html.BrowserView;
import com.xapp.application.utils.html.HTML;
import com.xapp.application.utils.html.HTMLImpl;
import com.xapp.objectmodelling.api.ClassDatabase;
import com.xapp.objectmodelling.core.ListProperty;
import com.xapp.objectmodelling.core.PropertyChangeTuple;
import com.xapp.objectmodelling.tree.Tree;
import com.xapp.objectmodelling.tree.TreeNode;
import com.xapp.utils.svn.SVNFacade;
import com.xapp.utils.svn.UpdateResult;
import novello.help.AboutPane;
import novello.help.ReferenceCard;
import novello.wordhandling.DictionaryType;
import org.tmatesoft.svn.core.SVNException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class SvnApp<T> extends SimpleApplication<T> implements DocumentApplication {
    private AppData m_appData;
    private SaveAction m_saveAction;
    private UpdateAction m_updateAction = new UpdateAction();
    private CommitAction m_commitAction = new CommitAction();
    private RevertAction m_revertAction = new RevertAction();
    private SVNFacade m_svnFacade;
    //private Executor m_extraThread = Executors.newFixedThreadPool(1);
    //private BrowserView m_browserView;

    public SvnApp(SVNFacade svnFacade)
    {
        m_svnFacade = svnFacade;
    }

    @Override
    public void init(ApplicationContainer<T> applicationContainer)
    {
        super.init(applicationContainer);

        setupToolbar();
    }

    protected void setupToolbar()
    {
        m_appContainer.getToolBar().add(m_updateAction).setToolTipText("Fetch changes from the server");
        m_appContainer.getToolBar().add(m_commitAction).setToolTipText("Saves and sends your changes to the server");
        m_appContainer.getToolBar().add(m_revertAction).setToolTipText("Removes all your changes since your last commit");
        updateViewState();

        if (isSVNMode())
        {
            m_appContainer.addBeforeHook(DefaultAction.QUIT, new ExitCommitHook());
            Box b = Box.createHorizontalBox();
            b.add(Box.createHorizontalStrut(10));
            b.add(new JLabel("user: " + m_svnFacade.getUsername()));
            SwingUtils.setFont(b);
            m_appContainer.getToolBar().add(b);
        }
    }

    private void updateViewState()
    {
        m_commitAction.setEnabled(isSVNMode());
        m_revertAction.setEnabled(isSVNMode());
        m_updateAction.setEnabled(isSVNMode());
    }

    private boolean isSVNMode()
    {
        return m_svnFacade != null;
    }

    public SpecialTreeGraphics createSpecialTreeGraphics()
    {
        return new NovelloTreeGraphics();
    }

    @Override
    public List<Command> getCommands(Node node)
    {
        List<Command> commands = new ArrayList<Command>();
        if (node.wrappedObject() instanceof Content)
        {
            commands.add(new EditLatestCommand());
            commands.add(new NodeCommand("Set grade to 75", "sets the grade to 75", "ctrl G") {
                public void execute(Node params) {
                    Content content = (Content) params.wrappedObject();
                    content.setGrade(75);
                }
            });
        }

        return commands;
    }

    private void doSplit(TextChunk textChunk, Node node)
    {
        String[] chunks = textChunk.getText().split("-->split");
        if (chunks.length > 1)
        {
            Content content = (Content) node.getParent().getParent().wrappedObject();
            List<TreeNode> contentList = content.getParent().getChildren();
            int index = contentList.indexOf(content);
            textChunk.setText(chunks[0]);
            for (int i = 1; i < chunks.length; i++)
            {
                String chunk = chunks[i];
                String name = chunk.substring(0, Math.min(chunk.length(), 30));
                if (!chunk.startsWith("\n"))
                {
                    String[] s = chunk.split("\n", 2);
                    name = s[0];
                    chunk = s[1];
                }
                Content newContent = (Content) classDatabase().newInstance(Content.class);
                newContent.setName(name);
                newContent.setParent(content.getParent());
                TextChunk newTextChunk = (TextChunk) classDatabase().newInstance(TextChunk.class);
                newTextChunk.setText(chunk);
                newContent.getVersions().add(newTextChunk);
                contentList.add(index + i, newContent);
            }
            m_appContainer.refreshNode(m_appContainer.getNode(content.getParent()));
            m_appContainer.expand(textChunk);
        }
    }

    protected ClassDatabase<T> classDatabase() {
        return m_appContainer.getGuiContext().getClassDatabase();
    }

    @Override
    public void nodeAboutToBeAdded(ListProperty listProperty, Object parent, Object newChild)
    {
        if (parent instanceof Content) //new content version should be inited to last version
        {
            Content content = (Content) parent;
            TextChunk textChunk = (TextChunk) newChild;
            textChunk.setText(content.getLatestText());
        }
        if (newChild instanceof Content)
        {
            Content content = (Content) newChild;
            Tree tree = (Tree) parent;
            content.setName(String.valueOf(tree.getChildren().size() + 1));
            content.getVersions().add(new TextChunk());
        }
    }

    private void render(HTML html, Section section, boolean isRoot)
    {
        if (section instanceof Content)
        {
            if (isRoot)
            {
                html.i().color(Color.BLUE).p(section.getText()).i().color(Color.BLACK);
            }
            Content content = (Content) section;
            /*int pixels = content.getGrade() * 6;
            String colors = pixels == 0 ? "red,red" : pixels == 600 ? "green,green" : "green,red";
            int noVersions = content.getVersions().size();
            String versionsText = "&#160;&#160;&#160;" + noVersions + " version" + (noVersions > 1 ? "s" : "");
            html.table(pixels + "," + (600 - pixels) + ",200", "&#160;,&#160;," + versionsText, colors + ",white");*/
            html.h(2, String.format("%s (%s Versions)", content.getName(), content.getVersions().size()));
            html.p(content.getLatestText());
        }
        else
        {
            Section chapter = section;
            html.h(1, chapter.getName());
            List<Content> list = chapter.enumerate(Content.class);
            for (Content content : list)
            {
                render(html, content, false);
            }
        }
    }

    private HTML render(Book book)
    {
        HTML html = new HTMLImpl();
        Section section = book.getSection();
        List<TreeNode> treeNodes = section.getChildren();
        for (TreeNode treeNode : treeNodes)
        {
            Section t = (Section) treeNode;
            if (!t.isExcluded())
            {
                html.h(1, t.getName());
                List<TreeNode> treeNodeList = t.getChildren();
                for (TreeNode node : treeNodeList)
                {
                    if (node instanceof Content)
                    {
                        Content c = (Content) node;
                        html.p(c.getLatestText());
                    }
                }

            }
        }
        return html;
    }

    public ApplicationContainer getAppContainer()
    {
        return m_appContainer;
    }
    @Override
    public void setStatusMessage(String message) {
        getAppContainer().setStatusMessage(message);
    }

    @Override
    public void expand(Object node) {
        getAppContainer().expand(node);
    }

    private class EditLatestCommand extends NodeCommand
    {
        protected EditLatestCommand()
        {
            super("Edit Latest", "Edit Latest version", "control shift E");
        }

        public void execute(Node node)
        {
            Content content = (Content) node.wrappedObject();
            m_appContainer.edit(content.latest());
        }

    }

    private class CommitAction extends AbstractAction
    {
        private CommitAction()
        {
            super("Commit", NovelloTreeGraphics.COMMIT_ICON);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (SwingUtils.askUser(m_appContainer.getMainFrame(), "Are you sure you want to send your changes to the server?"))
            {
                commit();
            }
        }
    }

    private class RevertAction extends AbstractAction
    {
        private RevertAction()
        {
            super("Revert", NovelloTreeGraphics.REVERT_ICON);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (SwingUtils.askUser(m_appContainer.getMainFrame(), "Are you sure you want to undo all changes\nsince your last commit?"))
            {
                m_svnFacade.revert(currentFilePath());

                reloadFile();
            }
        }

    }


    private class UpdateAction extends AbstractAction
    {
        private UpdateAction()
        {
            super("Update", NovelloTreeGraphics.UPDATE_ICON);
        }

        public void actionPerformed(ActionEvent e)
        {
            m_saveAction.save();
            UpdateResult result = m_svnFacade.update(currentFilePath());
            if (result.isConflict())
            {
                SwingUtils.warnUser(m_appContainer.getMainFrame(), "You have a conflict. You should close Novello and fix it manually\n" +
                        "You can revert the file, but then you will lose your changes");
            }
            else
            {
                reloadFile();
            }
        }
    }

    public void reloadFile() {
        m_appContainer.disposeAndReload();
    }

    public String getCurrentUser()
    {
        return m_svnFacade!=null ? m_svnFacade.getUsername() : "";
    }

    private String currentFilePath()
    {
        return m_appContainer.getGuiContext().getCurrentFile().getAbsolutePath();
    }

    private class ExitCommitHook implements ApplicationContainer.Hook
    {
        public void execute()
        {
            trySave();
            int i = JOptionPane.showOptionDialog(m_appContainer.getMainFrame(),
                    "Would you like to commit your changes?", "SVN Commit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (i == JOptionPane.YES_OPTION)
            {
                commit();
            }
        }

    }

    protected abstract void trySave();

    private void commit()
    {
        m_saveAction.save();
        try
        {
            NovelloLauncher.SVN_FACADE.commit(currentFilePath(), "changes");
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof SVNException)
            {
                SVNException svnException = (SVNException) e.getCause();
                SwingUtils.warnUser(m_appContainer.getMainFrame(), svnException.getMessage());
            }
            else
            {
                throw e;
            }
        }
    }

    private class AboutAction extends AbstractAction
    {
        public AboutAction()
        {
            super("About");
        }

        public void actionPerformed(ActionEvent e)
        {
            JFrame f = SwingUtils.createFrame(new AboutPane());
            f.setTitle("About");
            f.setLocationRelativeTo(m_appContainer.getMainFrame());
            f.setVisible(true);
        }
    }

    private class ReferenceCardAction extends AbstractAction
    {
        public ReferenceCardAction()
        {
            super("Reference Card");
        }

        public void actionPerformed(ActionEvent e)
        {
            JFrame f = SwingUtils.createFrame(new ReferenceCard().wrapInScrollPane());
            f.setAlwaysOnTop(true);
            f.setTitle("Reference Card");
            f.setLocationRelativeTo(m_appContainer.getMainFrame());
            f.setVisible(true);
        }
    }

    private class WordpressAction extends AbstractAction
    {
        private WordpressAction()
        {
            super("Wordpress Actions", NovelloTreeGraphics.WORDPRESS_ICON);
        }

        public void actionPerformed(ActionEvent e)
        {
            System.out.println("hello");
        }
    }
}
