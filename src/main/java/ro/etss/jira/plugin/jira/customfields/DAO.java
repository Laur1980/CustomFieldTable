package ro.etss.jira.plugin.jira.customfields;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

public class DAO {
	
	private static final Logger log = LoggerFactory.getLogger(DAO.class);
	
	private static PropertySet ofbizPs ;
	
	private static PropertySet getPS(FieldConfig fieldConfig) {
        if (ofbizPs == null) {
            Map<String,Object> ofbizArgs = new HashMap();
            ofbizArgs.put("delegator.name", "default");
            ofbizArgs.put("entityName", "table_fields");
            ofbizArgs.put("entityId",fieldConfig.getCustomField().getIdAsLong() );
            ofbizPs = PropertySetManager.getInstance("ofbiz", ofbizArgs);
        }
        return ofbizPs;
	}
	
	private static String getEntityName(FieldConfig fieldConfig){
		if(fieldConfig == null)
			log.error("++++++++++++++++++++++++++++++++++++++++++DAO: getEntityName: fieldConfig is null!!!++++++++++++++++++++++++++++++++++++++++++");
		Long context = fieldConfig.getId();
        String psEntityName = fieldConfig.getCustomField().getId() + "_" + context + "_config";
        return psEntityName;
	}
	
	public static String getStoredValue(FieldConfig fieldConfig){
		String entityName = getEntityName(fieldConfig);
		String value = getPS(fieldConfig).getString(entityName);
		if(value == null) 
			value ="";
		return  value;
	}
	
	public static void updateStoredValue(FieldConfig fieldConfig, String value){
		String entityName = getEntityName(fieldConfig);
        getPS(fieldConfig).setString(entityName, value);
	}
	
}
