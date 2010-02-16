/*
 * 
 * Copyright 2008-2010 Sarbarian Software, Davi Baldin Tavares <baldin@gmail.com>
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
package com.sarbarian.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Read a .properties file as class configuration file.<br>
 * The properties file are read in a row, each file found add their property entries on a final properties instance.<br>
 * 1. Try reading from the class package inside the JAR file.<br>
 * 2. Try reading from the root config file. The class package will be used as folder structure.<br>
 *    Package name: com.package.subpackage<br>
 *    Expected file on: ROOT_CONFIG/com/package/subpackage<br>
 * 3. Try reading from the current thread class loader folder.<br><br>
 * 
 * If found duplicated property on each location, the key found on the latest try will override previous properties found.
 * @author davi@sarbarian.com
 *
 */
public class ClassProperties {
	
	/**
	 * Default root folder to read from a config separated files.
	 */
	public static final String ROOT_CONFIG = "conf";
	
	private static Logger log = Logger.getLogger(ClassProperties.class);
	private Properties properties = null;
	private Class<?> targetClass = null;
	
	/**
	 * Create the instance and auto load the properties files.
	 * @param targetClass
	 */
	public ClassProperties(Class<?> targetClass) {
		this.targetClass = targetClass;
		this.properties = new Properties();
		this.load();
	}
	
	/**
	 * Return the properties found during the load process.
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * Return a property value by key or null if the key was not found.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	/**
	 * Return a property value by key or default value if the key was not found.
	 * @param key
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	/**
	 * Try loading the properties file in a row as described on the class information. 
	 */
	public synchronized void load() {
		this.properties.clear();
		Properties cp = new Properties();
		String fileName = this.targetClass.getSimpleName() + ".properties";
		try {
			
			//1. Try loading from the JAR context or class directory.
			log.debug("[" + targetClass.getSimpleName() + "] loading " + fileName  + " from jar context");
			InputStream inputStream = targetClass.getResourceAsStream(fileName);
			if (inputStream == null) {
				log.debug("   not found " + fileName  + " at jar context");	
			}else{
				cp.load(inputStream);
				for(Object key: cp.keySet()) {
					this.properties.put(key, cp.get(key));
				}
			}
						
			//2. Try loading from the Thread ContextClassLoader under config directory
			fileName = getRootConfigurationPath() + File.separator + targetClass.getName().replace('.', File.separatorChar) + ".properties";
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if (inputStream == null) {
				log.info("   not found " + fileName  + " at config directory");	
			}else{
				cp.load(inputStream);
				for(Object key: cp.keySet()) {
					this.properties.put(key, cp.get(key));
				}
			}
			
			//3. Try loading from the Thread ContextClassLoader
			log.debug("[" + this.targetClass.getSimpleName() + "] loading " + fileName  + " from class loader context");
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if (inputStream == null) {
				log.debug("   not found " + fileName  + " at class loader context");	
			}else{
				cp.load(inputStream);
				for(Object key: cp.keySet()) {
					this.properties.put(key, cp.get(key));
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage() + " while reading " + fileName + " to configure the class");
		}
	}
	
	/**
	 * Return current configuration path first by system property 'classproperties.root.path' or by
	 * {@link #ROOT_CONFIG}.
	 * @return
	 */
	public static String getRootConfigurationPath() {
		return System.getProperty("", ROOT_CONFIG);
	}
	
	/**
	 * Set current root configuration path setting the system property 'classproperties.root.path'
	 * @param path Not NULL path string.
	 * @throws Exception
	 */
	public static void setRootConfigurationPath(String path) throws Exception {
		if (path == null) {
			throw new Exception("Cannot set NULL as configuration path");
		}
		System.setProperty("", path);
	}
}