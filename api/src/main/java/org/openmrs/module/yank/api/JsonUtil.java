/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import org.joda.time.format.ISODateTimeFormat;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.api.handlers.AuditInfo;

/**
 *
 * @author jkeiper
 */
public class JsonUtil {
	
	public static Object getFieldOrNull(LinkedHashMap map, String key) {
		if (map != null && map.containsKey(key))
			return map.get(key);
		return null;
	}

	public static String getStringOrNull(LinkedHashMap json, String key) {
		Object o = getFieldOrNull(json, key);
		if (o == null)
			return null;
		return (String) o;
	}

	public static Boolean getBooleanOrFalse(LinkedHashMap json, String key) {
		Object o = getFieldOrNull(json, key);
		if (o == null)
			return false;
		return (Boolean) o;
	}

	public static Date getDateOrNull(LinkedHashMap json, String key) throws ParseException {
		String jDate = getStringOrNull(json, key);
		if (jDate == null)
			return null;
		return ISODateTimeFormat.dateTime().parseDateTime(jDate).toDate();
	}

	public static Number getNumberOrNull(LinkedHashMap json, String key) {
		Object o = getFieldOrNull(json, key);
		if (o == null)
			return null;
		return (Number) o;
	}

	public static void applyAuditInfoTo(AuditInfo ai, BaseOpenmrsObject obj) {
		UserService service = Context.getUserService();
		
		if (obj instanceof BaseOpenmrsData) {
			((BaseOpenmrsData)obj).setCreator(service.getUserByUuid(ai.getCreator()));
			((BaseOpenmrsData)obj).setChangedBy(service.getUserByUuid(ai.getChangedBy()));
			((BaseOpenmrsData)obj).setVoidedBy(service.getUserByUuid(ai.getVoidedBy()));
			((BaseOpenmrsData)obj).setDateCreated(ai.getDateCreated());
			((BaseOpenmrsData)obj).setDateChanged(ai.getDateChanged());
			((BaseOpenmrsData)obj).setDateVoided(ai.getDateVoided());
			((BaseOpenmrsData)obj).setVoidReason(ai.getVoidReason());
		} else if (obj instanceof BaseOpenmrsMetadata) {
			((BaseOpenmrsMetadata)obj).setCreator(service.getUserByUuid(ai.getCreator()));
			((BaseOpenmrsMetadata)obj).setChangedBy(service.getUserByUuid(ai.getChangedBy()));
			((BaseOpenmrsMetadata)obj).setRetiredBy(service.getUserByUuid(ai.getRetiredBy()));
			((BaseOpenmrsMetadata)obj).setDateCreated(ai.getDateCreated());
			((BaseOpenmrsMetadata)obj).setDateChanged(ai.getDateChanged());
			((BaseOpenmrsMetadata)obj).setDateRetired(ai.getDateRetired());
			((BaseOpenmrsMetadata)obj).setRetireReason(ai.getRetireReason());
		}
	}
	
}
