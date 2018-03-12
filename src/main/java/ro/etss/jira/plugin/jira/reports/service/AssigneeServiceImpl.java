package ro.etss.jira.plugin.jira.reports.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import ro.etss.jira.plugin.jira.reports.model.AssigneeInformation;

public class AssigneeServiceImpl implements AssigneeService{
	
	
	
	//it gets all the assisnees from the all the project issues that were selected
		public Set<AssigneeInformation> getAssigneesInformation(JiraAuthenticationContext jiraAuth, SearchService searchService,List<Long> projectIds) throws SearchException {
				Long [] ids = new Long[projectIds.size()];
				ids = projectIds.toArray(ids);
				List<Issue> issues = getTotalIssuesPerProject(jiraAuth, searchService, ids);
				Set<ApplicationUser> assignees = getAssigneesFromIssues(issues);
				Set<AssigneeInformation> assigneesInformation = new HashSet<>();
				for(ApplicationUser assignee: assignees) {
					AssigneeInformation ai = new AssigneeInformation();
					ai.setApplicationUser(assignee);
					Set<Long> projectIdsForAssignee = getProjectIdsForAssignee(issues,assignee);
					ai.setProjectIds(projectIdsForAssignee);
					ai.setIssuecount(getIssueCountForAssigneePerProject( jiraAuth, searchService,assignee,projectIdsForAssignee));
					ai.setIssuePercentagePerProject(getIssuePercentagePerProjectForAssignee( jiraAuth, searchService,assignee,projectIdsForAssignee));
				}
			return assigneesInformation;
		}
			
		private Map<String, String> getIssuePercentagePerProjectForAssignee(JiraAuthenticationContext jiraAuth, SearchService searchService,ApplicationUser assignee,
			Set<Long> projectIdsForAssignee) throws SearchException {
			Map<String,String> map = new HashMap<>();
			
			for(Long projectId: projectIdsForAssignee) {
				double percentage = getIssuesPerProjectForAssignee(jiraAuth, searchService, assignee,projectId).size()/getTotalIssuesPerProject(jiraAuth, searchService,projectId).size();
				map.put(""+projectId, String.format("%.2f%", percentage,"%"));
			}
		return map;
	}

		private Map<String, Integer> getIssueCountForAssigneePerProject(JiraAuthenticationContext jiraAuth, SearchService searchService,ApplicationUser assignee, Set<Long> projectIdsForAssignee) throws SearchException {
			Map<String,Integer> map = new HashMap<>();
			for(Long projectId: projectIdsForAssignee) {
				map.put(""+projectId, getIssuesPerProjectForAssignee(jiraAuth, searchService, assignee,projectId).size());
			}
		return map;
	}

		private Set<Long> getProjectIdsForAssignee(List<Issue> issues, ApplicationUser assignee) {
			Set<Long> projectIds = new HashSet<>();
			for(Issue issue: issues) {
				if(issue.getAssignee()!= null && issue.getAssignee().equals(assignee) && !projectIds.contains(issue.getProjectId())) {
					projectIds.add(issue.getProjectId());
				}
			}
			return projectIds;
		}

		//it extracts all the assignees
		private Set<ApplicationUser> getAssigneesFromIssues(List<Issue> issues) {
			Set<ApplicationUser> assignees = new HashSet<>();
			issues.forEach(issue ->{ 
											if(issue.getAssignee()!= null && !assignees.contains(issue.getAssignee()))
														assignees.add(issue.getAssignee());
									});
			return assignees; 
		}
					
		//it gets all the issues from a project
		private List<Issue> getIssuesPerProjectForAssignee(JiraAuthenticationContext jiraAuth, SearchService searchService, ApplicationUser assignee, Long projectId) throws SearchException{
			JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
			builder.where().project(projectId).and().assigneeUser(assignee.getKey());
			Query query = builder.buildQuery();
			SearchResults result = searchService.search(jiraAuth.getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
			return result.getIssues();
		}
		
		private List<Issue> getTotalIssuesPerProject(JiraAuthenticationContext jiraAuth, SearchService searchService, Long... projectId) throws SearchException{
			JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
			builder.where().project(projectId);
			Query query = builder.buildQuery();
			SearchResults result = searchService.search(jiraAuth.getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
			return result.getIssues();
		}
		
}
