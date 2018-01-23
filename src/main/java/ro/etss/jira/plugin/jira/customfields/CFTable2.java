package ro.etss.jira.plugin.jira.customfields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.imports.project.customfield.ProjectCustomFieldImporter;
import com.atlassian.jira.imports.project.customfield.ProjectImportableCustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import java.util.List;
import java.util.Map;

public class CFTable2 extends AbstractSingleFieldType<String> implements ProjectImportableCustomField{
    private static final Logger log = LoggerFactory.getLogger(CFTable2.class);

    private final ProjectCustomFieldImporter projectCustomFieldImporter; 

    public CFTable2(@ComponentImport CustomFieldValuePersister customFieldValuePersister, 
    					    @ComponentImport GenericConfigManager genericConfigManager) {
    		super(customFieldValuePersister, genericConfigManager);
    		projectCustomFieldImporter = new ProjectCustomFieldImporterImplem();
    }
    
    @Override
	public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
		Map<String, Object> velocityParameters = super.getVelocityParameters(issue, field, fieldLayoutItem);
		log.info("\n\n++++++++++++++++++++INSIDE getVelocityParameters++++++++++++++++++++\n\n");
		log.info("Parameters: \n\n");
		for(String s:velocityParameters.keySet())log.info(s+"\n\n");
		log.info("\n\n++++++++++++++++++++END getVelocityParameters++++++++++++++++++++\n\n");
		return velocityParameters;
	}

	@Override
	public String getSingularObjectFromString(String arg0) throws FieldValidationException {
		log.info("\n\n++++++++++++++++++++INSIDE getSingularObjectFromString++++++++++++++++++++\n\n");
		log.info(arg0);
		log.info("\n\n++++++++++++++++++++END getSingularObjectFromString++++++++++++++++++++\n\n");
		return arg0;
	}

	@Override
	public String getStringFromSingularObject(String arg0) {
		log.info("\n\n++++++++++++++++++++INSIDE getStringFromSingularObject++++++++++++++++++++\n\n");
		log.info(arg0);
		log.info("\n\n++++++++++++++++++++END getStringFromSingularObject++++++++++++++++++++\n\n");
		return arg0;
	}

	@Override
	public ProjectCustomFieldImporter getProjectImporter() {
		log.info("++++++++++++++++++++getProjectImporter++++++++++++++++++++");
		return projectCustomFieldImporter;
	}

	@Override
	protected PersistenceFieldType getDatabaseType() {
		log.info("++++++++++++++++++++getDatabaseType++++++++++++++++++++");
		return PersistenceFieldType.TYPE_UNLIMITED_TEXT;
	}

	@Override
	protected Object getDbValueFromObject(String arg0) {
		log.info("\n\n++++++++++++++++++++INSIDE getDbValueFromObject++++++++++++++++++++\n\n");
		log.info(arg0);
		log.info("\n\n++++++++++++++++++++END getDbValueFromObject++++++++++++++++++++\n\n");
		return arg0;
	}

	@Override
	protected String getObjectFromDbValue(Object arg0) throws FieldValidationException {
		log.info("\n\n++++++++++++++++++++INSIDE getObjectFromDbValue++++++++++++++++++++\n\n");
		log.info(arg0.toString());
		log.info("\n\n++++++++++++++++++++END getObjectFromDbValue++++++++++++++++++++\n\n");
		return arg0.toString();
	}
}