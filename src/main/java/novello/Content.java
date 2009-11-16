/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.Transient;
import com.xapp.objectmodelling.annotations.ListType;
import com.xapp.application.annotations.EditorWidget;

import java.util.List;
import java.util.ArrayList;

public class Content extends Text
{
    private List<TextChunk> m_versions = new ArrayList<TextChunk>();
    private int m_grade;

    public Content()
    {
        setName("blank");
    }

    @ListType(TextChunk.class)
    public List<TextChunk> getVersions()
    {
        return m_versions;
    }

    public void setVersions(List<TextChunk> versions)
    {
        m_versions = versions;
    }

    @Transient
    public String getLatest()
    {
        return m_versions.isEmpty() ? "" : m_versions.get(m_versions.size()-1).getText();
    }

    //@EditorWidget(value=SliderWidget.class, args="0,100")
    public int getGrade()
    {
        return m_grade;
    }

    public void setGrade(int grade)
    {
        m_grade = grade;
    }

    @Override
    public int wordcount()
    {
        return getLatest().split("\\s").length;
    }

    public TextChunk latest()
    {
        return m_versions.isEmpty() ? null : m_versions.get(m_versions.size()-1);
    }
}
