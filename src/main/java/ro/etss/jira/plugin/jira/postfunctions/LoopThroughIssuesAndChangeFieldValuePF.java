package ro.etss.jira.plugin.jira.postfunctions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
@Scanned
public class LoopThroughIssuesAndChangeFieldValuePF extends AbstractJiraFunctionProvider
{
    private static final Logger log = LoggerFactory.getLogger(LoopThroughIssuesAndChangeFieldValuePF.class);
    
    //private static final String ISSUE_TYPE = "selectedIssueType";
    
    private final IssueTypeManager issueTypeManager;
    private final FieldLayoutManager fieldLayoutManage;
    private final JiraAuthenticationContext jiraAuth;
    private final FieldLayoutManager fieldLayoutManager;
    
    public LoopThroughIssuesAndChangeFieldValuePF(@ComponentImport FieldLayoutManager fieldLayoutManage,
    											  @ComponentImport JiraAuthenticationContext jiraAuth,
    											  @ComponentImport IssueTypeManager issueTypeManager,
    											  @ComponentImport FieldLayoutManager fieldLayoutManager) {
		super();
		this.fieldLayoutManage = fieldLayoutManage;
		this.jiraAuth = jiraAuth;
		this.issueTypeManager = issueTypeManager;
		this.fieldLayoutManager = fieldLayoutManager;
	}



	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException
    {
        try {
        	MutableIssue issue = getIssue(transientVars);
        	//IssueType issueType = getIssueType(args);
        	IssueType issueType = issue.getIssueType();
        	CustomField numberField = getNumberField();
        	if(numberField != null) {
        		Long projectId = issue.getProjectId() ;
				List<MutableIssue> issues = getIssues(issueType,projectId);
				issues.add(0,issue);
				changeIssues(issues,numberField);
        	}else {
        		log.error("Number field not present!");
        	}
		} catch (SearchException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
    }
	
	private CustomField getNumberField() {
		CustomFieldManager cfManager = ComponentAccessor.getCustomFieldManager();
		CustomField numberField = cfManager.getCustomFieldObjects().stream()
			    .filter(cf -> cf.getCustomFieldType()
			    				.getName()
			    				.toLowerCase().contains("number"))
			    .findFirst()
			    .get();
		log.error("\n\n Number CustomField: "+numberField+"\n\n");
		return numberField;
	}
	
	//it updates the CustomField Number Field with incremental values
	private void changeIssues(List<MutableIssue> issues, CustomField numberField) {
		double count=0;
		for(MutableIssue issue: issues) {
			count++;
			log.warn("\n\n count is: "+count+"\n\n");
			issue.setCustomFieldValue(numberField,count);
			Map modifiedFields = issue.getModifiedFields();
			FieldLayoutItem fieldLayoutItem = fieldLayoutManager.getFieldLayout(issue).getFieldLayoutItem(numberField);
			DefaultIssueChangeHolder issueChangeHolder = new DefaultIssueChangeHolder();
			final ModifiedValue modifiedValue = (ModifiedValue) modifiedFields.get(numberField.getId());
			
			numberField.updateValue(fieldLayoutItem, issue, modifiedValue, issueChangeHolder);	
		}
	}
	/*
	private IssueType getIssueType(Map args) {
		String issueTypeId = (String) args.get(ISSUE_TYPE);
		return issueTypeManager.getIssueType(issueTypeId);
	}
*/
	//it gets a List<MutableIssue> from a certain project of a certain IssueType
	private List<MutableIssue> getIssues(IssueType issueType, Long projectId) throws SearchException {
		JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project(projectId).and().issueType().eq(issueType.getId());
		Query query = builder.buildQuery();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchResults result = searchService.search(jiraAuth.getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
		List<Issue> issues = result.getIssues();
		List<MutableIssue> mutableIssues = issuesToMutableIssues(issues);
		return mutableIssues;
	}


	//it converts issues from Issue type to MutableIssue type
	private List<MutableIssue> issuesToMutableIssues(List<Issue> issues) {
		List<MutableIssue> mutableIssues = new LinkedList<>();
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		for(Issue issue:issues) {
			mutableIssues.add(issueManager.getIssueObject(issue.getKey()));
		}
		return mutableIssues;
	}
}