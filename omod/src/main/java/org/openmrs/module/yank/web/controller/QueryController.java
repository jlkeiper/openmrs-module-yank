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
package org.openmrs.module.yank.web.controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.api.YankService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * directs traffic for query UI and handles requests for yanking data
 */
@Controller
@RequestMapping(value="/module/yank/query.form")
public class  QueryController {
	
	protected final Log log = LogFactory.getLog(getClass());
	private final int LIST_THRESHOLD = 5;
	
	@RequestMapping(method = RequestMethod.GET)
	public void show(){
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String yank(
			@RequestParam(value="server", required=true) String server,
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="password", required=true) String password,
			@RequestParam(value="datatype", required=true) String datatype,
			@RequestParam(value="uuids") String uuids, 
			@RequestParam(value="file") MultipartFile file, WebRequest request) 
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		YankService service = Context.getService(YankService.class);

		List<String> successes = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		// build uuids from file if it was uploaded
		if (file != null) {
			String fileUuids = new String(file.getBytes());
			if (!StringUtils.isBlank(fileUuids))
				uuids = fileUuids;
		}

		if (StringUtils.isNotEmpty(uuids))
			for (String uuid: uuids.split(",")) {
				// clean it up
				uuid = uuid.trim();
				
				try {
					// look it up 
					// TODO encrypt username and password here and decrypt in RestUtil
					String data = service.yankFromServer(server, username, password, datatype, uuid);
					
					if (StringUtils.isBlank(data))
						throw new APIException("nothing retrieved from server");
					
					// make a yank out of it
					Yank yank = new Yank();
					yank.setDatatype(datatype);
					yank.setData(data);
					yank.setSummary(service.getSummaryForType(datatype, data));
					service.saveYank(yank);
					
					successes.add("\"" + uuid + "\" successfully yanked.");
					
				} catch (Exception ex) {
					log.warn("error querying for uuid " + uuid, ex);
					errors.add("\"" + uuid + "\" could not be yanked [" + ex.getLocalizedMessage() + "]");
				}

			}
		
		if (!successes.isEmpty())
				request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, 
						renderMessage(successes, "Successfully yanked $count items."),
						WebRequest.SCOPE_SESSION);
		if (!errors.isEmpty())
				request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, 
						renderMessage(errors, "Could not yank $count items. See log for details."),
						WebRequest.SCOPE_SESSION);
		
		return "redirect:process.form";
	}

	// TODO make this a utility method
	private String renderMessage(List<String> list, String description) {
		if (list.isEmpty())
			return null;
		
		if (list.size() > LIST_THRESHOLD) {
			if (list.size() == 1)
				description = description.replaceAll("items", "item");
			
			return description.replaceFirst("\\$count", "" + list.size());
		}
		
		return Joiner.on("<br/>").join(list);
	}
	
}
