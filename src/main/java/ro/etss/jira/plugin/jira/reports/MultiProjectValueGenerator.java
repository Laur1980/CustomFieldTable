package ro.etss.jira.plugin.jira.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

public class MultiProjectValueGenerator implements ValuesGenerator<String> {

	@Override
	public Map<String, String> getValues(Map arg0) {
		Map<String,String> values = new HashMap<>();
		List<Project> projects = ComponentAccessor.getProjectManager().getProjectObjects();
		projects.forEach(p -> values.put(p.getId().toString() , p.getName()));	
		return values;
	}

}
