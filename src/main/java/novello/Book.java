/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.application.annotations.EditorWidget;
import com.xapp.application.editor.widgets.FreeTextPropertyWidget;

import java.util.List;
import java.util.ArrayList;

import novello.wordpress.Account;

public class Book implements Document
{
    private Section m_section = new Section("workspace");
    private List<String> m_localDictionary = new ArrayList<String>();
    private String m_styleSheet = "p {\n" +
                "color:#222222;  " +
                "line-height: 200%;\n" +
                "font-family:Tahoma, sans-serif;" +
                "}";
    private List<Account> m_bloggerAccounts;


    @TreeMeta(leafTypes = {Section.class, Content.class})
    public Section getSection()
    {
        return m_section;
    }

    public List<Account> getBloggerAccounts()
    {
        return m_bloggerAccounts;
    }

    public void setBloggerAccounts(List<Account> bloggerAccounts)
    {
        m_bloggerAccounts = bloggerAccounts;
    }

    public List<String> getLocalDictionary()
    {
        return m_localDictionary;
    }

    public void setLocalDictionary(List<String> localDictionary)
    {
        m_localDictionary = localDictionary;
    }

    public void setSection(Section section)
    {
        m_section = section;
    }

    @EditorWidget(FreeTextPropertyWidget.class)
    public String getStyleSheet()
    {
        return m_styleSheet;
    }

    public void setStyleSheet(String styleSheet)
    {
        m_styleSheet = styleSheet;
    }

    @Override
    public String toString()
    {
        return "Book";
    }


    public Content step(Direction back, Content next)
    {
        return m_section.step(back.getDelta(), next, Content.class);
    }
}
