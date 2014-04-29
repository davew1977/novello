/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-09
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.application.api.*;
import net.sf.xapp.application.utils.SwingUtils;
import net.sf.xapp.application.utils.html.BrowserView;
import net.sf.xapp.application.utils.html.HTML;
import net.sf.xapp.application.utils.html.HTMLImpl;
import net.sf.xapp.objectmodelling.api.ClassDatabase;
import net.sf.xapp.objectmodelling.core.ContainerProperty;
import net.sf.xapp.objectmodelling.core.ListProperty;
import net.sf.xapp.objectmodelling.core.PropertyChangeTuple;
import net.sf.xapp.tree.Tree;
import net.sf.xapp.tree.TreeNode;
import net.sf.xapp.utils.svn.SVNFacade;
import novello.help.AboutPane;
import novello.help.ReferenceCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NovelloApp extends DocumentApp<Book> implements DocumentApplication {
    private ClassDatabase<Book> m_classDatabase;
    private WordpressAction m_wordpressAction = new WordpressAction();
    private BrowserView m_browserView;

    public NovelloApp(SVNFacade svnFacade)
    {
        super(svnFacade);
    }

    @Override
    public void init(ApplicationContainer<Book> applicationContainer)
    {
        super.init(applicationContainer);
        m_browserView = new BrowserView();
        m_classDatabase = getAppContainer().getGuiContext().getClassDatabase();
        createHelpMenu();

    }

    private void createHelpMenu()
    {
        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem(new AboutAction());
        JMenuItem referenceCard = new JMenuItem(new ReferenceCardAction());
        help.add(about);
        help.add(referenceCard);
        SwingUtils.setFont(help);
        getAppContainer().getMenuBar().add(help);
    }

    private void setHtml(HTML html)
    {
        html.setStyle(getBook().getStyleSheet());
        String content = html.htmlDoc();
        m_browserView.setHTML(content);
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
    public boolean nodeSelected(final Node node)
    {
        boolean consumed = super.nodeSelected(node);
        //html.size(3).font("Dialog");

        if (!consumed && node.wrappedObject() instanceof Section)
        {
            HTML html = new HTMLImpl();
            Section section = (Section) node.wrappedObject();
            html.p("word count: " + section.wordcount());
            render(html, section, true);
            setHtml(html);
            getAppContainer().setUserPanel(m_browserView, true);
        }

       return false;
    }

    @Override
    public TextHolder getTextHolder(Text text) {
        Node node = getAppContainer().getNode(text);
        return node.getParent().getParent().wrappedObject();
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
        if (node.wrappedObject() instanceof Text)
        {
            doSplit(node.<Text>wrappedObject(), node);
        }
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
            Content content = (Content) node.getParent().getParent().wrappedObject();
            List<TreeNode> contentList = content.parent().getChildren();
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
                newContent.setParent(content.parent());
                TextChunk newTextChunk = (TextChunk) m_classDatabase.newInstance(TextChunk.class);
                newTextChunk.setText(chunk);
                newContent.getVersions().add(newTextChunk);
                contentList.add(index + i, newContent);
            }
            getAppContainer().refreshNode(getAppContainer().getNode(content.parent()));
            getAppContainer().expand(textChunk);
        }
    }

    @Override
    public void nodeAboutToBeAdded(ContainerProperty listProperty, Object parent, Object newChild)
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

    public void linkPressed(String link)
    {

    }


    public Book getBook()
    {
        return getAppContainer().getGuiContext().getInstance();
    }


    @Override
    public Section getDocTree() {
        return getBook().getSection();
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

    private String currentFilePath()
    {
        return getAppContainer().getGuiContext().getCurrentFile().getAbsolutePath();
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
