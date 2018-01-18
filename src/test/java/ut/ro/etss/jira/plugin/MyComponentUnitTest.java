package ut.ro.etss.jira.plugin;

import org.junit.Test;
import ro.etss.jira.plugin.api.MyPluginComponent;
import ro.etss.jira.plugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}