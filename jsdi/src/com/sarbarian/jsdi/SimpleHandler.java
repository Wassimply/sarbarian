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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

public class SimpleHandler implements IJavaScriptResourceHandler {
	
	public SimpleHandler() {
		log.debug("SimpleHandler initialized");
	}
	
	private static Logger log = Logger.getLogger(JavaScriptResourceFactory.class);

	public void doHandler(VelocityContext context, HttpServletRequest request) {
		log.debug("Simple handler nothing to do");
	}

}
