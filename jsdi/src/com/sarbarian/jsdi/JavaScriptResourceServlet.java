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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;


public class JavaScriptResourceServlet extends HttpServlet {

	private static final long serialVersionUID = -8139026947367605028L;

	private static Logger log = Logger.getLogger(JavaScriptResourceServlet.class);

	private JavaScriptResourceFactory factory = null;

	public JavaScriptResourceServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		factory = JavaScriptResourceFactory.getInstance();

		if (!factory.isInitialized()) {
			String filename = config.getInitParameter("config");
			if (filename == null) {
				filename = "WEB-INF/jsdi.properties";
			}

			InputStream in = config.getServletContext().getResourceAsStream(filename);
			if (in == null) {
				throw new ServletException("Not found " + filename + " to configure JavaScriptResourceFactory.");
			}

			try {
				Properties p = new Properties();
				p.load(in);

				/* Resolve path if necessary */
				if (p.getProperty("jsdi.resource.loader.realpath") != null) {
					String realPath = config.getServletContext().getRealPath("/");

					String loaders[] = p.getProperty("jsdi.resource.loader.realpath").split(",");
					for (String loader : loaders) {
						String key = loader.trim() + ".resource.loader.path";
						if (p.containsKey(key)) {
							log.info("Using RealPath for " + key + ": '" + realPath + "'");
							p.put(key, realPath + p.get(key));
						}
					}
				}

				/* Configure factory */
				factory.configure(p);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} 
		log.info("Sucessfully initialized JavaScriptResourceServlet");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("JavaScriptResource requested: " + request.getPathInfo());
		if (request.getPathInfo() == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested JavaScript resource 'NULL' could not be found.");
			return;
		}

		try {
			Template t = factory.getEngine().getTemplate(request.getPathInfo().substring(1));
			VelocityContext context = new VelocityContext();

			/*
			 * Start dependency injection into the VelocityContext to output to the JavaScript resource.
			 */
			context.put("request", request);

			/*
			 * Resolve i18n locale
			 */
			String lang = factory.getConfiguration("jsdi.locale.default");
			String localeAttr = factory.getConfiguration("jsdi.locale.attribute");
			if (request.getSession().getAttribute(localeAttr) != null) {
				lang = (String) request.getSession().getAttribute(localeAttr);
				log.debug("Found locale configuration on user session: " + lang);
			}
			if (request.getHeader(localeAttr) != null) {
				lang = request.getHeader(localeAttr);
				log.debug("Found locale configuration on request header: " + lang);
			}
			if (request.getParameter(localeAttr) != null) {
				lang = (String) request.getParameter(localeAttr);
				log.debug("Found locale configuration on request parameter: " + lang);
			}
			
			log.debug("Using locale: " + lang);
			Locale locale = null;
			String loc[] = lang.split("_");
			
			switch (loc.length) {
			case 1:
				locale = new Locale(loc[0]);
				log.debug("New locale: " + loc[0]);
				break;
			case 2:
				locale = new Locale(loc[0],loc[1]);
				log.debug("New locale: " + loc[0] + ", " + loc[1]);
				break;
			case 3:
				locale = new Locale(loc[0],loc[1],loc[2]);
				log.debug("New locale: " + loc[0] + ", " + loc[1] + ", " + loc[2]);
				break;
			default:
				locale = Locale.getDefault();
				log.debug("New default locale");
				break;
			}
			
			context.put("i18n", new I18n(ResourceBundle.getBundle("Messages", locale)));


			/* End of injections */
			
			/* Start Hander injections */
			factory.getHandler().doHandler(context, request);
			/* End of injections */

			/* Compile and send to the user the JavaScript resource */
			response.setContentType(factory.getConfiguration("jsdi.content.type"));
			t.merge(context, response.getWriter());
		} catch (ResourceNotFoundException e) {
			log.debug(e.getMessage());
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested JavaScript resource '" + request.getPathInfo() + "' could not be found.");
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}	
	}
}