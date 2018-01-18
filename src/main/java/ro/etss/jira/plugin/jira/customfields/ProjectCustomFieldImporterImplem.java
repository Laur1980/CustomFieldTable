package ro.etss.jira.plugin.jira.customfields;

import com.atlassian.jira.imports.project.customfield.ExternalCustomFieldValue;
import com.atlassian.jira.imports.project.customfield.ProjectCustomFieldImporter;
import com.atlassian.jira.imports.project.mapper.ProjectImportMapper;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSet;

public class ProjectCustomFieldImporterImplem implements ProjectCustomFieldImporter {
	
	public ProjectCustomFieldImporterImplem() {}

	@Override
	public MessageSet canMapImportValue(ProjectImportMapper arg0, ExternalCustomFieldValue arg1, FieldConfig arg2,
			I18nHelper arg3) {
		return null;
	}

	@Override
	public MappedCustomFieldValue getMappedImportValue(ProjectImportMapper arg0, ExternalCustomFieldValue arg1,
			FieldConfig arg2) {
		return new MappedCustomFieldValue(arg1.getValue());
	}

}
