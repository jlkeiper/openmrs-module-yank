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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.api.YankService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * directs traffic for query UI and handles requests for yanking data
 */
@Controller
@RequestMapping(value="/module/yank/process.form")
public class  ProcessController {
	
	protected final Log log = LogFactory.getLog(getClass());
	private final int LIST_THRESHOLD = 5;
	
	@RequestMapping(method = RequestMethod.GET)
	public void show(){
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String process(
			@RequestParam(value="yankIds", required=false) List<Integer> yankIds, 
			@RequestParam(value="submitAll", required=false) String submitAll,
			WebRequest request) throws IOException {
		YankService service = Context.getService(YankService.class);
		
		List<String> successes = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		if (yankIds == null)
			yankIds = new ArrayList<Integer>();
		
		// TODO make this more efficient
		if (!StringUtils.isBlank(submitAll))
			yankIds = service.getAllYankIds();
		
		// process all requested yanks
		int count = 0;
		for (Integer id: yankIds) {
			
			// tidy up for the sake of processing speeds
			if (++count % 20 == 0) {
				Context.flushSession();
				Context.clearSession();
			}
			
			// get the yank and process it
			Yank yank = service.getYank(id);
			Boolean success = service.processYank(yank);
			if (success)
				successes.add("Yank #" + id + " was processed successfully.");
			else
				errors.add("Yank #" + id + " could not be processed. See server log for more information.");
		}
		
		// fill in request attrs with success/failure messages
		if (!successes.isEmpty())
				request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, 
						renderMessage(successes, "Successfully processed $count yanks."),
						WebRequest.SCOPE_SESSION);
		if (!errors.isEmpty())
				request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, 
						renderMessage(errors, "Could not process $count yanks.  See log for details."),
						WebRequest.SCOPE_SESSION);
		
		return "redirect:process.form";
	}
	
	// TODO make this a utility method
	private String renderMessage(List<String> list, String description) {
		if (list.isEmpty())
			return null;
		
		if (list.size() > LIST_THRESHOLD)
			return description.replaceFirst("\\$count", "" + list.size());
		
		return Joiner.on("<br/>").join(list);
	}

}
