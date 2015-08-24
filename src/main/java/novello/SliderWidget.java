/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-okt-18
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.application.api.PropertyWidget;
import net.sf.xapp.application.api.WidgetContext;
import net.sf.xapp.objectmodelling.core.Property;

import javax.swing.*;


public class SliderWidget implements PropertyWidget<Integer>
{
    private String m_args; //injected comma separated min and max value
    private int m_min;
    private int m_max;
    private JSlider m_slider;
    private Property m_property;

    @Override
    public void init(WidgetContext<Integer> integerWidgetContext)
    {

        String[] args = m_args.split(",");
        m_min = Integer.decode(args[0]);
        m_max = Integer.decode(args[1]);
        System.out.println(m_min+" "+m_max);
        m_slider = new JSlider(m_min, m_max);

    }

    public JComponent getComponent()
    {
        return m_slider;
    }

    public Integer getValue()
    {
        return m_slider.getValue();
    }

    public void setValue(Integer value, Object target)
    {
        m_slider.setValue(value);
        System.out.println(value);
    }

    public Property getProperty()
    {
        return m_property;
    }

    public String validate()
    {
        return null;
    }

    public void setEditable(boolean b)
    {
        m_slider.setEnabled(b);
    }
}
