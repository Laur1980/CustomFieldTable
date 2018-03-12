package ro.etss.jira.plugin.jira.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;

import webwork.action.ActionContext;

@Scanned
public class AllIssuesReport extends AbstractReport {
	
	private final JiraAuthenticationContext jiraAuth;
	private final ProjectManager projectManager;
	private final UserUtil userUtil;
	private final SearchService searchService;
	 
	public AllIssuesReport(@ComponentImport JiraAuthenticationContext jiraAuth,
						   @ComponentImport ProjectManager projectManager, 
						   @ComponentImport UserUtil userUtil,
						   @ComponentImport SearchService searchService) {
		super();
		this.jiraAuth = jiraAuth;
		this.projectManager = projectManager;
		this.userUtil = userUtil;
		this.searchService = searchService;
	}

	public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
		Map<String,Object> velocityParams = getVelocityParams(action, reqParams);
         return descriptor.getHtml("view", velocityParams);
    }
	
	List<Issue> getIssuesFromProject(Long pid)throws SearchException{
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project(pid);
		Query query = builder.buildQuery();
		SearchResults results = this.searchService.search(this.jiraAuth.getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
		return results.getIssues();
	}
	
	private Map<String,Object> getVelocityParams(ProjectActionSupport action, Map reqParams)throws SearchException{
		final Map<String,Object> velocityParams = new HashMap<>();
		final String projectid = (String) reqParams.get("projectId");
		final Long pid = new Long(projectid);
		velocityParams.put("report", this);
		velocityParams.put("action", action);
		velocityParams.put("issues", getIssuesFromProject(pid));
		return velocityParams;
	}

	@Override
	public boolean isExcelViewSupported() {
		return true;
	}

	@Override
	public String generateReportExcel(ProjectActionSupport action, Map reqParams) throws Exception {
		final StringBuilder contentDispositionValue = new StringBuilder(50);
		//converting the default .jspa filextension from .jspa to .xls 
		contentDispositionValue.append("attachment;filename=\"");
		contentDispositionValue.append(getDescriptor().getName());
		contentDispositionValue.append(".xls\";");
		final HttpServletResponse response = ActionContext.getResponse();
		response.addHeader("content-diposition", contentDispositionValue.toString());
		return descriptor.getHtml("excel",getVelocityParams(action, reqParams));
	}

	@Override
	public void validate(ProjectActionSupport action, Map reqParams) {
		final String projectid = (String) reqParams.get("projectId");
		final Long pid = new Long(projectid);
		if(this.projectManager.getProjectObj(pid) == null) {
			action.addError("projectId", "No project with id: "+ pid + " exists!");
		}
		super.validate(action, reqParams);
	}
	
	//restricting reports to jira administrators
	@Override
	public boolean showReport() {
		ApplicationUser user = jiraAuth.getLoggedInUser();
		return userUtil.getJiraAdministrators().contains(user);
	}
	
	
	
}
