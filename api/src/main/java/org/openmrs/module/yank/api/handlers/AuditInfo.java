/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api.handlers;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import org.openmrs.module.yank.api.JsonUtil;

/**
 *
 * "auditInfo": {
      "creator": {
        "uuid": "5a999ea2-13a9-11df-a1f1-0026b9348838",
        "display": "admin - The Super User",
        "retired": false,
        "links": [
          {
            "uri": "http://.../ws/rest/v1/user/5a999ea2-13a9-11df-a1f1-0026b9348838",
            "rel": "self"
          }
        ]
      },
      "dateCreated": "2011-01-05T15:04:14.000-0500",
      "changedBy": {
        "uuid": "5a999ea2-13a9-11df-a1f1-0026b9348838",
        "display": "admin - The Super User",
        "retired": false,
        "links": [
          {
            "uri": "http://.../ws/rest/v1/user/5a999ea2-13a9-11df-a1f1-0026b9348838",
            "rel": "self"
          }
        ]
      },
      "dateChanged": "2011-01-26T09:55:07.000-0500"
    }
 */
public class AuditInfo {
	private String creator;
	private Date dateCreated;
	private String voidedBy;
	private Date dateVoided;
	private String voidReason;
	private String changedBy;
	private Date dateChanged;
	private String retiredBy;
	private Date dateRetired;
	private String retireReason;

	public AuditInfo(LinkedHashMap json) throws ParseException {
		LinkedHashMap auditInfo = (LinkedHashMap)json.get("auditInfo");
		
		LinkedHashMap jObj = (LinkedHashMap)auditInfo.get("creator");
		this.creator = JsonUtil.getStringOrNull(jObj, "uuid");
		jObj = (LinkedHashMap)auditInfo.get("changedBy");
		this.changedBy = JsonUtil.getStringOrNull(jObj, "uuid");
		jObj = (LinkedHashMap)auditInfo.get("voidedBy");
		this.voidedBy = JsonUtil.getStringOrNull(jObj, "uuid");
		jObj = (LinkedHashMap)auditInfo.get("retiredBy");
		this.retiredBy = JsonUtil.getStringOrNull(jObj, "uuid");

		this.voidReason = JsonUtil.getStringOrNull(auditInfo, "voidReason");
		this.retireReason = JsonUtil.getStringOrNull(auditInfo, "retireReason");
		
		this.dateCreated = JsonUtil.getDateOrNull(auditInfo, "dateCreated");
		this.dateChanged = JsonUtil.getDateOrNull(auditInfo, "dateChanged");
		this.dateVoided = JsonUtil.getDateOrNull(auditInfo, "dateVoided");
		this.dateRetired = JsonUtil.getDateOrNull(auditInfo, "dateRetired");
	}
	
	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateRetired() {
		return dateRetired;
	}

	public void setDateRetired(Date dateRetired) {
		this.dateRetired = dateRetired;
	}

	public Date getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}

	public String getRetireReason() {
		return retireReason;
	}

	public void setRetireReason(String retireReason) {
		this.retireReason = retireReason;
	}

	public String getRetiredBy() {
		return retiredBy;
	}

	public void setRetiredBy(String retiredBy) {
		this.retiredBy = retiredBy;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	public String getVoidedBy() {
		return voidedBy;
	}

	public void setVoidedBy(String voidedBy) {
		this.voidedBy = voidedBy;
	}
	
}
