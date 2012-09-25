package novello;

import com.xapp.application.api.ApplicationContainer;
import com.xapp.application.api.Node;
import com.xapp.application.utils.SwingUtils;
import com.xapp.objectmodelling.core.ClassModel;
import com.xapp.utils.svn.SVNFacade;
import novello.wordhandling.DictionaryType;

import java.io.File;
import java.util.List;

/**
 * Encapsulates commonalities between novello and requiem
 */
public abstract class DocumentApp<T extends Document> extends SvnApp<T> implements DocumentApplication{
    private MainEditor m_mainEditor;
    private AppData m_appData;

    public DocumentApp(SVNFacade svnFacade) {
        super(svnFacade);
    }

    @Override
    public void init(ApplicationContainer<T> applicationContainer) {

        super.init(applicationContainer);
        m_mainEditor = new MainEditor(this);
        m_appContainer.setUserPanel(m_mainEditor, false);
        m_mainEditor.setResizeWeight(0.5);

        initAppData();
        selectLastEditedContent();
        //m_appContainer.getToolBar().add(new SaveAction(m_mainEditor, this)).setToolTipText("Save changes to disk");

    }



    public void addWordToDict(DictionaryType dictType, String word)
    {
        if(dictType== DictionaryType.local)
        {
            getLocalDictionary().add(word);
        }
        else
        {
            SwingUtils.warnUser(getAppContainer().getMainFrame(), "Feature not implemented yet!");
        }
    }

    @Override
    public List<String> getLocalDictionary() {
        return getDocument().getLocalDictionary();
    }

    @Override
    public String getStyleSheet() {
        return getDocument().getStyleSheet();
    }

    private void initAppData()
    {
        final File appDataFile = new File(m_appContainer.getGuiContext().getCurrentFile().getParentFile(), "app-data.xml");
        if (appDataFile.exists())
        {
            try
            {
                m_appData = classDatabase().createUnmarshaller(AppData.class).unmarshal(appDataFile);
                int div = m_appData.getDividerLocation();
                if (div!=0)
                {
                    m_appContainer.setDividerLocation(div);
                }
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

        m_appContainer.addAfterHook(DefaultAction.SAVE, new ApplicationContainer.Hook() {
            public void execute() {
                m_appData.setDividerLocation(m_appContainer.getDividerLocation());
                classDatabase().createMarshaller(AppData.class).marshal(appDataFile, m_appData);
            }
        });
    }


    public boolean shouldSplit(Text chunk)
    {
        return chunk.text().contains("\n,") && SwingUtils.askUser(m_appContainer.getMainFrame(), "Do splits?");
    }
    @Override
    public boolean nodeSelected(Node node) {
        super.nodeSelected(node);
        Object o = node.wrappedObject();
        if (o!=null)
        {
            ClassModel cm = classDatabase().getClassModel(o.getClass());
            if(cm.hasPrimaryKey())
            {
                m_appData.setLastSelected(o.getClass().getSimpleName() + ":" + cm.getPrimaryKey(o));
            }
        }

        if (node.isA(TextHolder.class))
        {
            TextHolder textHolder = node.wrappedObject();
            m_mainEditor.setChunk(textHolder.content());
            m_appContainer.setUserPanel(m_mainEditor, false);
            return true;
        }
        else if (node.isA(Text.class))
        {
            Text text = node.wrappedObject();
            m_mainEditor.setChunk(text);
            m_appContainer.setUserPanel(m_mainEditor, false);
            return true;
        }
        return false;
    }

    private void selectLastEditedContent()
    {
        String lastEdited = m_appData.getLastSelected();
        if (lastEdited != null)
        {
            String[] s = lastEdited.split(":");
            ClassModel cm = classDatabase().getClassModelBySimpleName(s[0]);
            Object o = cm.getInstanceNoCheck(s[1]);
            if(o!=null) {
                m_appContainer.expand(o);
                if(o instanceof Text) {
                    Text text = (Text) o;
                    m_mainEditor.setChunk(text);
                }
            }
        }
    }

    @Override
    protected void trySave() {
        m_mainEditor.store();
        m_mainEditor.render();
        getAppContainer().save();
    }


    public void reloadFile()
    {
        super.reloadFile();
        selectLastEditedContent();
    }

    public T getDocument() {
        return m_appContainer.getGuiContext().getInstance();
    }


    @Override
    public TextHolder step(Direction pType, TextHolder pParentContent) {
        return getDocTree().step(pType.getDelta(), pParentContent, TextHolder.class);
    }

    @Override
    public void quit() {
        getAppContainer().quit();
    }
}
