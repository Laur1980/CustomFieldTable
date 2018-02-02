package ro.etss.jira.plugin.jira.customfields;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
public class TableConfigItem  implements FieldConfigItemType {
	
	private static final Logger log = LoggerFactory.getLogger(FieldConfigItemType.class);
	/*
	private final ManagedConfigurationItemService managedConfigurationItemService;
	
	public TableConfigItem(@ComponentImport ManagedConfigurationItemService managedConfigurationItemService){
		this.managedConfigurationItemService = managedConfigurationItemService;
	}
	*/
	//The name of this kind of configuration, 
		//As seen in the field configuration scheme
		@Override
		public String getDisplayName() {
			log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getDisplayName++++++++++++++++++++++++++++++++");
			return "Tabel";
		}
		
		// This is the text shown in the field configuration screen
		@Override
		public String getDisplayNameKey() {
			log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getDisplayNameKey++++++++++++++++++++++++++++++++");
			return "Configureaza tabelul: ";
		}
		
		//The current value as shown in the config screen
		@Override
		public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {
			log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getViewHtml++++++++++++++++++++++++++++++++");
			return DAO.getStoredValue(fieldConfig);
		}
		
		@Override
		public String getObjectKey() {
			log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getObjectKey++++++++++++++++++++++++++++++++");
			return "tableconfig";
		}
	
	@Override
	public String getBaseEditUrl() {
		log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getBaseEditUrl++++++++++++++++++++++++++++++++");
		return "EditTableConfig.jspa"; 
	}

	@Override
	public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {
		Map<String,Object> result = new HashMap<>();
		result.put("initialValue", DAO.getStoredValue(fieldConfig));
		log.info("++++++++++++++++++++++++++++++++ TableConfigItem: getConfigurationObject++++++++++++++++++++++++++++++++");
		return result;
	}
	
	

}
