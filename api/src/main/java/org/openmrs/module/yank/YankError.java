/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank;

import java.util.Date;
import org.openmrs.User;

/**
 * @author jkeiper
 */
public class YankError implements Yankish {

	// from Yank
	private Integer yankErrorId;
	private String datatype;
	private String summary;
	private String data;
	private User creator;
	private Date dateCreated = new Date();

	// new to error
	String error;
	String stacktrace;
	Date dateAttempted;
	User attemptedBy;
	
	public YankError() {
		// pass
	}
	
	public YankError(Yank yank) {
		this.datatype = yank.getDatatype();
		this.summary = yank.getSummary();
		this.data = yank.getData();
		this.creator = yank.getCreator();
		this.dateCreated = yank.getDateCreated();
	}

	public Integer getYankErrorId() {
		return yankErrorId;
	}

	public void setYankErrorId(Integer yankErrorId) {
		this.yankErrorId = yankErrorId;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public User getAttemptedBy() {
		return attemptedBy;
	}

	public void setAttemptedBy(User attemptedBy) {
		this.attemptedBy = attemptedBy;
	}

	public Date getDateAttempted() {
		return dateAttempted;
	}

	public void setDateAttempted(Date dateAttempted) {
		this.dateAttempted = dateAttempted;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
}
