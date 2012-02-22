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

import java.net.Authenticator;
import java.util.Map;

import javax.ws.rs.core.MediaType;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Helper class for making REST calls
 */
public class RestClient {
	private static Log log = LogFactory.getLog(RestClient.class);

	static Client client = Client.create();

	static {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager(){
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string) throws CertificateException {
					// PASS
				}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string) throws CertificateException {
					// PASS
				}
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			;
		}
	}

	static {
		if (log.isDebugEnabled())
			client.addFilter(new LoggingFilter(System.out));
	}

	private static WebResource getRootResource(String serverName) {
		return client.resource(serverName).path("ws").path("rest").path("v1");
	}

    public static String listQuery(
			String serverName, String username, String password, 
			String resourceName, String queryParamName, String query) {
		Authenticator.setDefault(new BasicAuth(username, password));
	    return getRootResource(serverName)
				.path(resourceName)
				.queryParam(queryParamName, query)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(String.class);
    }
    
    public static String getResource(
			String serverName, String username, String password, 
			String resourceName, String uuid) {
		Authenticator.setDefault(new BasicAuth(username, password));
	    return getRootResource(serverName)
				.path(resourceName)
				.path(uuid)
				.queryParam("v", "full")
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(String.class);
    }
    
    public static void post(String serverName, String resource, Map<String, String> properties) {
    	getRootResource(serverName).path(resource).post(properties);
    }
	
}