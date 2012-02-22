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
public class YankArchive implements Yankish {

	// from Yank
	private Integer yankArchiveId;
	private String datatype;
	private String summary;
	private String data;
	private User creator;
	private Date dateCreated = new Date();

	// new to archive
	Date dateArchived;
	User archivedBy;
	
	public YankArchive() {
		// pass
	}
	
	public YankArchive(Yank yank) {
		this.datatype = yank.getDatatype();
		this.summary = yank.getSummary();
		this.data = yank.getData();
		this.creator = yank.getCreator();
		this.dateCreated = yank.getDateCreated();
	}

	public Integer getYankArchiveId() {
		return yankArchiveId;
	}

	public void setYankArchiveId(Integer yankArchiveId) {
		this.yankArchiveId = yankArchiveId;
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

	public User getArchivedBy() {
		return archivedBy;
	}

	public void setArchivedBy(User archivedBy) {
		this.archivedBy = archivedBy;
	}

	public Date getDateArchived() {
		return dateArchived;
	}

	public void setDateArchived(Date dateArchived) {
		this.dateArchived = dateArchived;
	}
	
}
