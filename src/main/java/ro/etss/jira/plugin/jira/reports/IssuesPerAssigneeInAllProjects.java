package ro.etss.jira.plugin.jira.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import ro.etss.jira.plugin.jira.reports.model.AssigneeInformation;
import ro.etss.jira.plugin.jira.reports.service.AssigneeService;

@Scanned
public class IssuesPerAssigneeInAllProjects extends AbstractReport {
    
	private final JiraAuthenticationContext jiraAuth;
	private final ProjectManager projectManager;
	private final UserUtil userUtil;
	private final SearchService searchService;
	private final AssigneeService assigneeService;
	 
	public IssuesPerAssigneeInAllProjects(@ComponentImport JiraAuthenticationContext jiraAuth,
						   				  @ComponentImport ProjectManager projectManager, 
						   				  @ComponentImport UserUtil userUtil,
						   				  @ComponentImport SearchService searchService,
						   				  AssigneeService assigneeService) {
		super();
		this.jiraAuth = jiraAuth;
		this.projectManager = projectManager;
		this.userUtil = userUtil;
		this.searchService = searchService;
		this.assigneeService = assigneeService;
	}
	
	public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
        Map<String,Object> velocityParams = getVelocityParams(action,reqParams);
    	return descriptor.getHtml("view", velocityParams);
    }

	private Map<String, Object> getVelocityParams(ProjectActionSupport action, Map reqParams) throws SearchException {
		Map<String,Object> velocityParams = new HashMap<>();
		String[] pids = (String[]) reqParams.get("projectId");
		List<Long> projectIds = getProjectIds(pids);
		Set<AssigneeInformation> assignees = assigneeService.getAssigneesInformation(jiraAuth, searchService, projectIds);
		velocityParams.put("report", this);
		velocityParams.put("action", action);
		velocityParams.put("assignees", assignees);
		return velocityParams;
	}
	
	//it convers the string ids to long project ids
	private List<Long> getProjectIds(String[] pids) {
				List<Long> projectIds = new ArrayList<>();
				for(String id: pids) {
					projectIds.add(new Long(id));
				}
				return projectIds;
	}
}
