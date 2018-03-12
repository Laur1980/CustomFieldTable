package ro.etss.jira.plugin.jira.reports.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.user.ApplicationUser;

public class AssigneeInformation {
	
	private ApplicationUser applicationUser;
	private Set<Long> projectIds;
	private Map<String,Integer> issuecount;
	private Map<String,String> issuePercentagePerProject;
		
	public AssigneeInformation() {
		super();
	}
	
	public AssigneeInformation(ApplicationUser applicationUser, Set<Long> projectIds, Map<String, Integer> issuecount,
			Map<String, String> issuePercentagePerProject) {
		super();
		this.applicationUser = applicationUser;
		this.projectIds = projectIds;
		this.issuecount = issuecount;
		this.issuePercentagePerProject = issuePercentagePerProject;
	}

	public ApplicationUser getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(ApplicationUser applicationUser) {
		this.applicationUser = applicationUser;
	}

	public Set<Long> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(Set<Long> projectIds) {
		this.projectIds = projectIds;
	}

	public Map<String, Integer> getIssuecount() {
		return issuecount;
	}

	public void setIssuecount(Map<String, Integer> issuecount) {
		this.issuecount = issuecount;
	}

	public Map<String, String> getIssuePercentagePerProject() {
		return issuePercentagePerProject;
	}

	public void setIssuePercentagePerProject(Map<String, String> issuePercentagePerProject) {
		this.issuePercentagePerProject = issuePercentagePerProject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationUser == null) ? 0 : applicationUser.hashCode());
		result = prime * result + ((issuePercentagePerProject == null) ? 0 : issuePercentagePerProject.hashCode());
		result = prime * result + ((issuecount == null) ? 0 : issuecount.hashCode());
		result = prime * result + ((projectIds == null) ? 0 : projectIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssigneeInformation other = (AssigneeInformation) obj;
		if (applicationUser == null) {
			if (other.applicationUser != null)
				return false;
		} else if (!applicationUser.equals(other.applicationUser))
			return false;
		if (issuePercentagePerProject == null) {
			if (other.issuePercentagePerProject != null)
				return false;
		} else if (!issuePercentagePerProject.equals(other.issuePercentagePerProject))
			return false;
		if (issuecount == null) {
			if (other.issuecount != null)
				return false;
		} else if (!issuecount.equals(other.issuecount))
			return false;
		if (projectIds == null) {
			if (other.projectIds != null)
				return false;
		} else if (!projectIds.equals(other.projectIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AssigneeInformation [applicationUser=" + applicationUser + "]";
	}
		
}
