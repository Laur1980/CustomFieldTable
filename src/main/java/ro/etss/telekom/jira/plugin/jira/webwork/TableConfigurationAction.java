package ro.etss.telekom.jira.plugin.jira.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import ro.etss.telekom.jira.plugin.jira.customfields.DAO;
@Scanned
public class TableConfigurationAction extends AbstractEditConfigurationItemAction
{
	
	private static final Logger log = LoggerFactory.getLogger(TableConfigurationAction.class);
	private String defaultValue;
	
    protected TableConfigurationAction(@ComponentImport ManagedConfigurationItemService managedConfigurationItemService) {
		super(managedConfigurationItemService);
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	protected void doValidation(){
		String str = getDefaultValue();
		if(str == null)
			return;
		
		String [] parts = str.split("(|)");
		if(parts.length <1){
			addErrorMessage("Invalid configuration input");
		}
	}
	
	protected String doExecute()throws Exception {
		
		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		GlobalPermissionManager globalPermissionManager = ComponentAccessor.getGlobalPermissionManager();
		//verifying that the user has administrator permissions
		if (!globalPermissionManager.hasPermission( GlobalPermissionKey.ADMINISTER, user)) {
				return "securitybreach";
			}
		FieldConfig config = getFieldConfig();
		if(config == null)
			log.error("++++++++++++++++++++++++++++++TableConfigurationAction:doExecute --->  doExecute is NULL!++++++++++++++++++++++++++++++");
		if(getDefaultValue() == null){
			//setDefaultValue(DAO.getStoredValue(getFieldConfig()));
			setDefaultValue("");
		}
		//updating the default custom field value
		DAO.updateStoredValue(getFieldConfig(), getDefaultValue());
		
		String save = getHttpRequest().getParameter("Save");
		
		if (save != null && save.equals("Save")) {
			setReturnUrl("/secure/admin/ConfigureCustomField!default.jspa?customFieldId="
			+ getFieldConfig().getCustomField().getIdAsLong().toString());
			return getRedirect("not used");
		}
		
		return "input";
	}
	
}
