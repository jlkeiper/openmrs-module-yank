/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.web.controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.api.YankService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author jkeiper
 */
@Controller(value="yankAjaxController")
public class AjaxController {
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Returns a list of pending Yanks according to parameters
	 * 
	 * @param start beginning index of list
	 * @param length length of list
	 * @param query filtering query
	 * @param sEcho return value for coordination with browser
	 * @return list of Yanks
	 * @throws IOException 
	 */
	@RequestMapping("/module/yank/getYanks.json")
	public @ResponseBody Map<String, Object> getYanksAsJson(
			@RequestParam("iDisplayStart") int start,
	        @RequestParam("iDisplayLength") int length, 
			@RequestParam("sSearch") String query,
	        @RequestParam("sEcho") int sEcho) throws IOException {
		
		YankService service = Context.getService(YankService.class);
		
		// get the data
		List<Yank> yanks = service.getYankBatch(start, length, query);
		
		// form the results dataset
		List<Object> results = new ArrayList<Object>();
		for (Yank yank: yanks)
			results.add(splitYank(yank));
		
		// build the response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("iTotalRecords", 
				service.countYanks(null));
		response.put("iTotalDisplayRecords", 
				service.countYanks(query));
		response.put("sEcho", sEcho);
		response.put("aaData", results.toArray());
		
		// send it
		return response;
	}

	/**
	 * Returns the JSON data of a Yank
	 * 
	 * @param yankId the id of the Yank to get data from
	 * @return JSON data from the referenced Yank
	 * @throws IOException 
	 */
	@RequestMapping("/module/yank/getYankData.json")
	public @ResponseBody String getYankDataAsString(
			@RequestParam("yankId") Integer yankId) throws IOException {
		Yank yank = Context.getService(YankService.class).getYank(yankId);
		String data = yank.getData();
		return prettyPrintJson(data);
	}
	
	@RequestMapping("/module/yank/queryForYanks.json")
	public @ResponseBody String queryForYanks(
			@RequestParam("uuids") String uuids) throws IOException {
		
		return Joiner.on("\n").join(uuids.split(","));
	}
	
	/**
	 * create an object array for a given Yank
	 * 
	 * @param q Yank object
	 * @return object array for use with datatables
	 */
	private Object[] splitYank(Yank q) {
		// try to stick to basic types; String, Integer, etc (not Date)
		return new Object[] { 
			Integer.toString(q.getYankId()), 
			q.getDatatype(),
			q.getSummary(),
		    Context.getDateFormat().format(q.getDateCreated())
		};
	}
	
	// TODO make this a utility method
	private String prettyPrintJson(String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement je = (new JsonParser()).parse(json);
		return gson.toJson(je);
	}
}
