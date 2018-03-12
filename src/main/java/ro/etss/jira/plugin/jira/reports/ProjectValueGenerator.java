package ro.etss.jira.plugin.jira.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

public class ProjectValueGenerator implements ValuesGenerator<String> {

	@Override
	public Map<String, String> getValues(Map usersParams) {
		Map<String,String> projectMap = new HashMap<>();
		List<Project> allProjects = ComponentAccessor.getProjectManager().getProjectObjects();
		allProjects.forEach(p -> projectMap.put(p.getId().toString() , p.getName()));
		return projectMap;
	}

}
