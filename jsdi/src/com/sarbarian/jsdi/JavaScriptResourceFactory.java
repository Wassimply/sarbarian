/*
 * 
 * Copyright 2010 Sarbarian Software, Davi Baldin Tavares <baldin@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *  
 */
package com.sarbarian.jsdi;

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author Davi
 *
 */
public class JavaScriptResourceFactory {
	
	private static Logger log = Logger.getLogger(JavaScriptResourceFactory.class);
	
	private static JavaScriptResourceFactory instance;
	private static Object classLock = JavaScriptResourceFactory.class;
	
	private VelocityEngine ve = null;
	private Properties config = null;
	private boolean initialized = false;
	
	private JavaScriptResourceFactory() {
		log.info("Factory instance created");
	}
	
	public static JavaScriptResourceFactory getInstance() {
		synchronized (classLock) {
			if (instance == null) {
				instance = new JavaScriptResourceFactory();
			}
			return instance;
		}
	}
	
	public VelocityEngine getEngine() {
		return this.ve;
	}
	
	/**
	 * Configure Velocity template engine.
	 * @param config Properties file to configure the engine.
	 * @throws Exception
	 */
	public synchronized void configure(Properties properties) throws Exception {
		if (isInitialized()) {
			return;
		}
		config = properties;
		log.debug("-------- Factory configuration properties --------");
		Iterator<?> ite = config.keySet().iterator(); 
		while(ite.hasNext()) {
			String key = (String) ite.next();
			log.debug(key + " -> '" + config.getProperty(key) + "'");
		}
		log.debug("--------------------------------------------------");
		
		ve = new VelocityEngine();
		ve.init(config);
		initialized = true;
		log.info("JavaScript resource engine started");
	}
	
	public String getConfiguration(String key) {
		return this.config.getProperty(key);
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public IJavaScriptResourceHandler getHandler() {
		String className = this.getConfiguration("jsdi.resource.handler.class");
		try {
			Class<?> clazz = Class.forName(className);
			return (IJavaScriptResourceHandler) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			log.error("Handler class '" + className + "' not found.");
		} catch (InstantiationException e) {
			log.error("Unable to create resource handler: " + e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("Unable to create resource handler: " + e.getMessage());
		}
		return null;
	}
}