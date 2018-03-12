package ro.etss.jira.plugin.jira.reports.service;

import java.util.List;
import java.util.Set;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.security.JiraAuthenticationContext;

import ro.etss.jira.plugin.jira.reports.model.AssigneeInformation;

public interface AssigneeService {
	
	Set<AssigneeInformation> getAssigneesInformation(JiraAuthenticationContext jiraAuth, SearchService searchService,List<Long> projectIds) throws SearchException;
		
}
