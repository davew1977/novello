/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.annotations.objectmodelling.ListType;
import net.sf.xapp.annotations.objectmodelling.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * Contents are nodes that contain real text for the document
 */
public class Content extends Section implements TextHolder
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
    public String getLatestText()
    {
        return latest().getText();
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
        return getLatestText().split("\\s").length;
    }

    public TextChunk latest()
    {
        return m_versions.get(m_versions.size()-1);
    }

    @Override
    public Text content() {
        return latest();
    }
}
