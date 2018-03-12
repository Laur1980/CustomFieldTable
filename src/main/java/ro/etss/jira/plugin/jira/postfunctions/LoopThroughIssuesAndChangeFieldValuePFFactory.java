package ro.etss.jira.plugin.jira.postfunctions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 */
@Scanned
public class LoopThroughIssuesAndChangeFieldValuePFFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{	
	private static final Logger log = LoggerFactory.getLogger(LoopThroughIssuesAndChangeFieldValuePFFactory.class);
    private static final String ISSUE_TYPES = "issueTypes";
    private static final String ISSUE_TYPE = "selectedIssueType";
    private static final String ISSUES= "issues";
    private static final String PROJECTS= "projects";
    private static final String PROJECT= "selectedProject";
    
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;
    
	public LoopThroughIssuesAndChangeFieldValuePFFactory(@ComponentImport IssueTypeManager issueTypeManager,
														 @ComponentImport ProjectManager projectManager) {
		this.issueTypeManager = issueTypeManager;
		this.projectManager = projectManager;
	}

	@Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		/*
        Collection<IssueType> issueTypes = getIssueTypes();
        velocityParams.put(ISSUE_TYPES, issueTypes);
        velocityParams.put(PROJECTS, getProject());
        */
    }

	@Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
    	/*
    	velocityParams.put(ISSUE_TYPE, (IssueType)getIssueTypeOrProject(descriptor,ISSUE_TYPE));
    	velocityParams.put(PROJECT, (Project)getIssueTypeOrProject(descriptor,PROJECT));
    	
    	try {
			velocityParams.put(ISSUES, getIssuesFromProject(descriptor));
		} catch (SearchException e) {
			Log.error("JQL query has failed, reason: "+e.getMessage());
			e.printStackTrace();
		}
		*/
    }
    
    private Collection<IssueType> getIssueTypes() {
		return issueTypeManager.getIssueTypes();
	}
    
    private List<Project> getProject() {
    	return projectManager.getProjectObjects().stream().filter(project -> this.projectManager.getCurrentCounterForProject(project.getId()) != 0)
    													  .collect(Collectors.toList());
    }
    
    private List<Issue> getIssuesFromProject(AbstractDescriptor descriptor) throws SearchException{
    	IssueType issueType = (IssueType)getIssueTypeOrProject(descriptor,ISSUE_TYPE);
    	Project project = (Project)getIssueTypeOrProject(descriptor,PROJECT);
    	JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project(project.getId()).and().issueType().eq(issueType.getId());
		Query query = builder.buildQuery();
		SearchResults result = ComponentAccessor.getComponent(SearchService.class).search(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
		List<Issue> issues = result.getIssues();
		return issues;
    }
    
    //it return either an IssueType object or a Project by the user or ""
	private Object getIssueTypeOrProject(AbstractDescriptor descriptor, String key) {
		if (!(descriptor instanceof FunctionDescriptor)) {
			log.error("Descriptor must be a FunctionDescriptor.");		
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }
		FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;
		String id = (String) functionDescriptor.getArgs().get(key);
		if(id != null && id.trim().length()>0) {
				if(key.equals(ISSUE_TYPE)) {
					log.error("\n\n Id for issueType"+id+"\n\n");
					IssueType issueType = issueTypeManager.getIssueType(id)  ;
					log.error("\n\n"+issueType.getName()+"\n\n");
					return issueType;
				}
				else {
					log.error("\n\n Id for project"+id+"\n\n");
					Project project = projectManager.getProjectObj(new Long(id));
					log.error("\n\n"+project.getName() +"\n\n");
					return project;
				}
						
		}
		log.error("The selected type id is null!");				
		return null;

	}

	@Override
	public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {
		if(formParams != null && formParams.containsKey(ISSUE_TYPE) && formParams.containsKey(PROJECT)){
					List<String> params = Arrays.asList(ISSUE_TYPE,PROJECT);
					log.error("\n\n"+params.toString()+"\n\n");
		        	return extractMultipleParams(formParams, params);
		}
		        return MapBuilder.emptyMap();
	}



}