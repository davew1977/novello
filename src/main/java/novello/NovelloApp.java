/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-09
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.api.*;
import com.xapp.application.utils.html.BrowserView;
import com.xapp.application.utils.html.BrowserViewListener;
import com.xapp.application.utils.html.HTML;
import com.xapp.application.utils.html.HTMLImpl;
import com.xapp.application.utils.SwingUtils;
import com.xapp.objectmodelling.api.ClassDatabase;
import com.xapp.objectmodelling.core.ListProperty;
import com.xapp.objectmodelling.core.PropertyChangeTuple;
import com.xapp.objectmodelling.tree.Tree;
import com.xapp.objectmodelling.tree.TreeNode;
import com.xapp.utils.svn.SVNFacade;
import com.xapp.utils.svn.UpdateResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import novello.about.AboutPane;
import org.tmatesoft.svn.core.SVNException;

public class NovelloApp extends SimpleApplication<Book> implements BrowserViewListener
{
    private BrowserView m_browserView;
    private MainEditor m_mainEditor;
    private ClassDatabase<Book> m_classDatabase;
    private AppData m_appData;
    private SaveAction m_saveAction;
    private UpdateAction m_updateAction = new UpdateAction();
    private CommitAction m_commitAction = new CommitAction();
    private RevertAction m_revertAction = new RevertAction();
    private SVNFacade m_svnFacade;

    public NovelloApp(SVNFacade svnFacade)
    {
        m_svnFacade = svnFacade;
    }

    @Override
    public void init(ApplicationContainer<Book> applicationContainer)
    {
        super.init(applicationContainer);
        m_browserView = new BrowserView(this);
        m_browserView.setPreferredSize(new Dimension(600, m_browserView.getPreferredSize().height));
        m_classDatabase = m_appContainer.getGuiContext().getClassDatabase();
        //m_browserView.setHTML(render(applicationContainer.getGuiContext().getInstance()));
        m_mainEditor = new MainEditor(this);
        m_appContainer.setUserPanel(m_mainEditor, false);
        m_mainEditor.setResizeWeight(0.5);

        final File appDataFile = new File("app-data.xml");
        if (appDataFile.exists())
        {
            try
            {
                m_appData = m_classDatabase.createUnmarshaller(AppData.class).unmarshal(appDataFile);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                m_appData = new AppData();
            }
        }
        else
        {
            m_appData = new AppData();
        }

        m_appContainer.addAfterHook(DefaultAction.SAVE, new ApplicationContainer.Hook()
        {
            public void execute()
            {
                m_classDatabase.createMarshaller(AppData.class).marshal(appDataFile, m_appData);
            }
        });

        selectLastEditedContent();

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem(new AbstractAction("About")
        {

            public void actionPerformed(ActionEvent e)
            {
                JFrame f = SwingUtils.createFrame(new AboutPane());
                f.setTitle("About");
                f.setLocationRelativeTo(m_appContainer.getMainFrame());
                f.setVisible(true);
            }
        });
        help.add(about);
        SwingUtils.setFont(help);
        m_appContainer.getMenuBar().add(help);

        m_saveAction = new SaveAction(m_mainEditor, this);
        m_appContainer.getToolBar().add(m_saveAction).setToolTipText("Save changes to disk");
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

    private void selectLastEditedContent()
    {
        Content lastEdited = m_appData.getLastEdited();
        if (lastEdited != null)
        {
            m_mainEditor.setChunk(lastEdited.latest(), lastEdited);
            m_appContainer.expand(lastEdited);
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
            commands.add(new NodeCommand("Set grade to 75", "sets the grade to 75", "ctrl G")
            {
                public void execute(Node params)
                {
                    Content content = (Content) params.wrappedObject();
                    content.setGrade(75);
                }
            });
        }

        return commands;
    }

    @Override
    public void nodeSelected(Node node)
    {
        HTML html = new HTMLImpl();
        //html.size(3).font("Dialog");
        if (node.wrappedObject() instanceof Content)
        {
            Content content = (Content) node.wrappedObject();
            m_mainEditor.setChunk(content.latest(), content);
            m_appData.setLastEdited(content);
        }
        if (node.wrappedObject() instanceof Section)
        {
            Section section = (Section) node.wrappedObject();
            html.p("word count: " + section.wordcount());
            render(html, section, true);
            m_browserView.setHTML(html);
        }
        else if (node.wrappedObject() instanceof TextChunk)
        {
            TextChunk textChunk = (TextChunk) node.wrappedObject();
            m_mainEditor.setChunk(textChunk, (Content) node.getParent().getParent().wrappedObject());

            Content content = (Content) node.getParent().wrappedObject();
            m_appData.setLastEdited(content);
        }

    }

    public void nodeUpdated(Node node, Map<String, PropertyChangeTuple> changes)
    {
        if (node.wrappedObject() instanceof TextChunk)
        {
            doSplit((TextChunk) node.wrappedObject(), node);
            nodeSelected(node);
        }
    }

    @Override
    public void nodeAdded(Node node)
    {
        if (node.wrappedObject() instanceof TextChunk)
        {
            doSplit((TextChunk) node.wrappedObject(), node);
        }
    }

    public boolean shouldSplit(TextChunk chunk)
    {
        return chunk.getText().contains("-->split") && SwingUtils.askUser(m_appContainer.getMainFrame(), "Do splits?");
    }

    public void doSplit(TextChunk chunk)
    {
        doSplit(chunk, m_appContainer.getNode(chunk));
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
                Content newContent = (Content) m_classDatabase.newInstance(Content.class);
                newContent.setName(name);
                newContent.setParent(content.getParent());
                TextChunk newTextChunk = (TextChunk) m_classDatabase.newInstance(TextChunk.class);
                newTextChunk.setText(chunk);
                newContent.getVersions().add(newTextChunk);
                contentList.add(index + i, newContent);
            }
            m_appContainer.refreshNode(m_appContainer.getNode(content.getParent()));
            m_appContainer.expand(textChunk);
        }
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
            int pixels = content.getGrade() * 6;
            String colors = pixels == 0 ? "red,red" : pixels == 600 ? "green,green" : "green,red";
            int noVersions = content.getVersions().size();
            String versionsText = "&nbsp;&nbsp;&nbsp;" + noVersions + " version" + (noVersions > 1 ? "s" : "");
            html.table(pixels + "," + (600 - pixels) + ",200", "&nbsp;,&nbsp;," + versionsText, colors + ",white");
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
                    Content c = (Content) node;
                    html.p(c.getLatestText());
                }

            }
        }
        return html;
    }

    public void linkPressed(String link)
    {

    }

    public void htmlRendered()
    {

    }

    public void formSubmitted(Map<String, String> props)
    {

    }

    public void comboItemChanged(String componentId, String newValue)
    {

    }

    public void textFieldChanged(String compId, String text)
    {

    }

    public void checkBoxChanged(String compID, boolean selected)
    {

    }

    public ApplicationContainer getAppContainer()
    {
        return m_appContainer;
    }

    public Book getBook()
    {
        return m_appContainer.getGuiContext().getInstance();
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
            if(SwingUtils.askUser(m_appContainer.getMainFrame(), "Are you sure you want to send your changes to the server?"))
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
            if(SwingUtils.askUser(m_appContainer.getMainFrame(), "Are you sure you want to undo all changes\nsince your last commit?"))
            {
                m_svnFacade.revert(currentFilePath());

                reloadFile();
            }
        }

    }

    private void reloadFile()
    {
        m_appContainer.disposeAndReload();
        Content lastEdited = m_appData.getLastEdited();
        if (lastEdited != null)
        {
            lastEdited = m_appContainer.getClassDatabase().getInstance(Content.class, lastEdited.getKey());
            if(lastEdited!=null)
            {
                m_appData.setLastEdited(lastEdited);
                selectLastEditedContent();
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
            if(result.isConflict())
            {
                SwingUtils.warnUser(m_appContainer.getMainFrame(),"You have a conflict. You should close Novello and fix it manually\n" +
                        "You can revert the file, but then you will lose your changes");
            }
            else
            {
                reloadFile();
            }
        }
    }


    private String currentFilePath()
    {
        return m_appContainer.getGuiContext().getCurrentFile().getAbsolutePath();
    }

    private class ExitCommitHook implements ApplicationContainer.Hook
    {
        public void execute()
        {
            new SaveAction(m_mainEditor, NovelloApp.this).actionPerformed(null);
            int i = JOptionPane.showOptionDialog(m_appContainer.getMainFrame(),
                    "Would you like to commit your changes?", "SVN Commit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (i == JOptionPane.YES_OPTION)
            {
                commit();
            }
        }

    }

    private void commit()
    {
        m_saveAction.save();
        try
        {
            NovelloLauncher.SVN_FACADE.commit(currentFilePath(), "changes");
        }
        catch (RuntimeException e)
        {
            if(e.getCause() instanceof SVNException)
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
}
