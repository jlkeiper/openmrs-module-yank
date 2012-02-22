/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api;

import org.openmrs.module.yank.api.handlers.PatientYankHandler;
import org.openmrs.module.yank.api.handlers.YankHandler;

/**
 *
 * @author jkeiper
 */
public class YankHandlerFactory {
	
	public static YankHandler getHandlerFor(String datatype) {
		return new PatientYankHandler();
	}
}
