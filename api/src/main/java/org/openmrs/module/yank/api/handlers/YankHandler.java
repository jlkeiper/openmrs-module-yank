/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api.handlers;

import org.openmrs.module.yank.Yank;

/**
 *
 * @author jkeiper
 */
public interface YankHandler {
	public void process(Yank yank);
	public String summarize(String json);
}
