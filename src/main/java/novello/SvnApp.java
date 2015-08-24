/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-09
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.application.api.ApplicationContainer;
import net.sf.xapp.application.api.Node;
import net.sf.xapp.application.api.NodeCommand;
import net.sf.xapp.application.api.SimpleApplication;
import net.sf.xapp.application.utils.SwingUtils;
import net.sf.xapp.application.utils.html.HTML;
import net.sf.xapp.application.utils.html.HTMLImpl;
import net.sf.xapp.tree.TreeNode;
import net.sf.xapp.utils.svn.SVNFacade;
import net.sf.xapp.utils.svn.UpdateResult;
import novello.help.AboutPane;
import novello.help.ReferenceCard;
import org.tmatesoft.svn.core.SVNException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public abstract class SvnApp<T> extends SimpleApplication<T> implements DocumentApplication {
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

        if (isSVNMode())
        {
            getAppContainer().getToolBar().add(m_updateAction).setToolTipText("Fetch changes from the server");
            getAppContainer().getToolBar().add(m_commitAction).setToolTipText("Saves and sends your changes to the server");
            getAppContainer().getToolBar().add(m_revertAction).setToolTipText("Removes all your changes since your last commit");
            updateViewState();
            getAppContainer().addBeforeHook(DefaultAction.QUIT, new ExitCommitHook());
            Box b = Box.createHorizontalBox();
            b.add(Box.createHorizontalStrut(10));
            b.add(new JLabel("user: " + m_svnFacade.getUsername()));
            SwingUtils.setFont(b);
            getAppContainer().getToolBar().add(b);
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
            getAppContainer().edit(content.latest());
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
            if (SwingUtils.askUser(getAppContainer().getMainFrame(), "Are you sure you want to send your changes to the server?"))
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
            if (SwingUtils.askUser(getAppContainer().getMainFrame(), "Are you sure you want to undo all changes\nsince your last commit?"))
            {
                m_svnFacade.revert(currentFile());

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
            trySave();
            UpdateResult result = m_svnFacade.update(currentFile());
            if (result.isConflict())
            {
                SwingUtils.warnUser(getAppContainer().getMainFrame(), "You have a conflict. You should close Novello and fix it manually\n" +
                        "You can revert the file, but then you will lose your changes");
            }
            else
            {
                reloadFile();
            }
        }
    }

    public void reloadFile() {
        getAppContainer().disposeAndReload();
    }

    public String getCurrentUser()
    {
        return m_svnFacade!=null ? m_svnFacade.getUsername() : "";
    }

    private File currentFile()
    {
        return getAppContainer().getGuiContext().getCurrentFile();
    }

    private class ExitCommitHook implements ApplicationContainer.Hook
    {
        public void execute()
        {
            trySave();
            int i = JOptionPane.showOptionDialog(getAppContainer().getMainFrame(),
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
        trySave();
        try
        {
            m_svnFacade.commit("changes", currentFile());
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof SVNException)
            {
                SVNException svnException = (SVNException) e.getCause();
                SwingUtils.warnUser(getAppContainer().getMainFrame(), svnException.getMessage());
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
            f.setLocationRelativeTo(getAppContainer().getMainFrame());
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
            f.setLocationRelativeTo(getAppContainer().getMainFrame());
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
