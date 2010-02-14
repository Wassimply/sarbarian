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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Convenient class to format i18n Strings and resources.
 * @author Davi
 *
 */
public class I18n {
	
	private ResourceBundle bundle = null;
	private static Logger log = Logger.getLogger(I18n.class);
	
	public I18n(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	/**
	 * Shortcut to bundle.getString(key)
	 * @param key
	 * @return
	 */
	public String get(String key) {
		try {
			return bundle.getString(key);
		}catch (Exception e) {
			log.warn(e.getMessage());
			return "";
		}
	}
	
	/**
	 * Convert String date to Date based on the Locale format.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public Date format(String date) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat();
		if (date != null && !date.equals("")) {
			if (get("date.format") != null) {
				df = new SimpleDateFormat(get("date.format"));
			}
			return df.parse(date);
		}else{
			return null;
		}
	}
	
	/**
	 * Convert Date to String date based on the Locale format.
	 * @param date
	 * @return
	 */
	public String format(Date date) {
		SimpleDateFormat df = new SimpleDateFormat();
		if (date != null && !date.equals("")) {
			if (get("date.format") != null) {
				df = new SimpleDateFormat(get("date.format"));
			}
			return df.format(date);
		}else{
			return null;
		}
	}
}