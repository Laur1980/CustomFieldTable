package ro.etss.jira.plugin.jira.jql;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.util.MessageSetImpl;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import com.google.common.collect.Iterables;

/**
 * Echoes the string passed in as an argument.
 */
public class ProjectsFunction extends AbstractJqlFunction
{
    private static final Logger log = LoggerFactory.getLogger(ProjectsFunction.class);

    public MessageSet validate(ApplicationUser searcher, FunctionOperand operand, TerminalClause terminalClause)
    {
    	List<String> projectKeys = operand.getArgs();
    	MessageSet messages = new MessageSetImpl();
    	if(projectKeys.isEmpty()) {
    		messages.addErrorMessage("At least one project key is needed!");
    	}
    	projectKeys.forEach(k -> {
    		if(k == null)
    			messages.addErrorMessage("Invalid project key: "+k);
    	});
        return messages;
    }

    public List<QueryLiteral> getValues(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause)
    {
        return Collections.singletonList(new QueryLiteral(operand, Iterables.get(operand.getArgs(), 0)));
    }

    public int getMinimumNumberOfExpectedArguments()
    {
        return 1;
    }

    public JiraDataType getDataType()
    {
        return JiraDataTypes.PROJECT;
    }
}