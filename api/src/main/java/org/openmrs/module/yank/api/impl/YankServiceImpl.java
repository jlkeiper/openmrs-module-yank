/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.yank.api.impl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.YankArchive;
import org.openmrs.module.yank.YankError;
import org.openmrs.module.yank.Yankish;
import org.openmrs.module.yank.api.YankHandlerFactory;
import org.openmrs.module.yank.api.YankService;
import org.openmrs.module.yank.api.db.YankDAO;
import org.openmrs.module.yank.api.handlers.YankHandler;
import org.openmrs.util.OpenmrsUtil;

/**
 * It is a default implementation of {@link YankService}.
 */
public class YankServiceImpl extends BaseOpenmrsService implements YankService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private YankDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(YankDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public YankDAO getDao() {
	    return dao;
    }

	@Override
	public String yankFromServer(String server, String username, String password, String type, String uuid) {
		try {
			return RestClient.getResource(server, username, password, type, uuid);
		} catch (Exception ex) {
			log.error("oops", ex);
		}
		return null;
	}

	@Override
	public List<Yank> getAllYanks() {
		return dao.getAllYanks();
	}

	@Override
	public Yank saveYank(Yank yank) {
		
		// set creator if not already set
		if (yank.getCreator() == null)
			yank.setCreator(Context.getAuthenticatedUser());
		
		// put the data to the filesystem
		// writeYankToFolder(yank, "pending");
		
		return dao.saveYank(yank);
	}

	@Override
	public YankError saveYankError(YankError error) {
		// put the data to the filesystem
		// writeYankToFolder(error, "errors");
		
		return dao.saveYankError(error);
	}

	@Override
	public YankArchive saveYankArchive(YankArchive archive) {
		// put the data to the filesystem
		// writeYankToFolder(archive, "archives");
		
		return dao.saveYankArchive(archive);
	}

	/**
	 * generate a filename for a yank
	 * 
	 * @param yank
	 * @return 
	 */
	private String generateFilename(Yankish yank) {
		return new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss-").format(yank.getDateCreated())
				+ Math.random() + ".json";
	}

	@Override
	public Yank getYank(Integer id) {
		return dao.getYank(id);
	}

	@Override
	public Boolean processYank(Yank yank) {
		YankService service = Context.getService(YankService.class);
		YankHandler processor = YankHandlerFactory.getHandlerFor(yank.getDatatype());

		try {
			processor.process(yank);
			service.moveYankToArchives(yank);
		} catch (Throwable ex) {
			service.moveYankToErrors(yank, ex);
			return false;
		}
		
		return true;
	}

	@Override
	public void moveYankToErrors(Yank yank, Throwable ex) {
		YankService service = Context.getService(YankService.class);
		
		YankError error = new YankError(yank);
		error.setDateAttempted(new Date());
		error.setAttemptedBy(Context.getAuthenticatedUser());
		error.setError(ex.getLocalizedMessage());
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		pw.flush();
		sw.flush();
		error.setStacktrace(OpenmrsUtil.shortenedStackTrace(sw.toString()));
		
		service.saveYankError(error);
		service.purgeYank(yank);
	}

	@Override
	public void purgeYank(Yank yank) {
		if (yank == null)
			return;
		
//		if (!StringUtils.isBlank(yank.getFilename())) {
//			File file = new File(yank.getFilename());
//			if (file.exists())
//				file.delete();
//		}
		
		dao.purgeYank(yank);
	}

	@Override
	public void moveYankToArchives(Yank yank) {
		YankService service = Context.getService(YankService.class);
		
		YankArchive archive = new YankArchive(yank);
		archive.setDateArchived(new Date());
		archive.setArchivedBy(Context.getAuthenticatedUser());
		
		service.saveYankArchive(archive);
		service.purgeYank(yank);
	}

	@Override
	public String getSummaryForType(String datatype, String data) {
		YankHandler handler = YankHandlerFactory.getHandlerFor(datatype);
		return handler.summarize(data);
	}

//	@Override
//	public void hydrateYankData(Yank yank) {
//		try {
//			File datafile = new File(yank.getFilename());
//			yank.setData(OpenmrsUtil.getFileAsString(datafile));
//		} catch (IOException ex) {
//			throw new APIException("could not hydrate yank data for Yank#" + yank.getYankId(), ex);
//		}
//	}

//	/**
//	 * writes a yank's data to a folder
//	 * 
//	 * @param yank
//	 * @param folder 
//	 */
//	private void writeYankToFolder(Yankish yank, String folder) {
//		// get the yanks main folder
//		File yanks = OpenmrsUtil.getDirectoryInApplicationDataDirectory("yanks");
//		if (!yanks.exists())
//			yanks.mkdir();
//		
//		// get the requested folder in yanks
//		File destination = new File(yanks, folder);
//		if (!destination.exists())
//			destination.mkdir();
//		
//		// create a new file with this name
//		String filename = generateFilename(yank);
//		File data = new File(destination, filename);
//		if (!data.exists())
//			try {
//				if (!data.createNewFile())
//					throw new APIException("could not create yank at " + filename);
//			} catch(IOException ex) {
//				throw new APIException("error accessing yank at " + filename, ex);
//			}	
//		
//		// create a buffer
//		Writer output = null;
//		try {
//			output = new BufferedWriter(new FileWriter(data));
//			output.write(yank.getData());
//		} catch (IOException ex) {
//			throw new APIException("could not write yank to file at " + filename, ex);
//	    } finally {
//			if (output != null) {
//				try {
//					output.close();
//				} catch (IOException ex) {
//					log.warn("could not close output stream, writing yank to file at " + filename);
//				}
//			}
//		}
//		
//		yank.setFilename(data.getAbsolutePath());
//	}

	@Override
	public List<Yank> getYankBatch(int start, int length, String query) {
		return dao.getYankBatch(start, length, query);
	}

	@Override
	public Integer countYanks(String query) {
		return dao.countYanks(query);
	}

	@Override
	public List<Integer> getAllYankIds() {
		return dao.getAllYankIds();
	}

}


