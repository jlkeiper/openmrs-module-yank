/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api.impl;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * authentication class used to connect to the REST server
 */
public class BasicAuth extends Authenticator {

	private String username;
	private String password;

	public BasicAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * @see java.net.Authenticator#getPasswordAuthentication()
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}
