package org.jboss.resteasy.plugins.server.osgi.internal;

import java.util.Hashtable;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.server.osgi.IResteasyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * OSGi Activator class to start RESTEasy inside OSGi container binding into HttpService as servlet 
 * mapping and into OSGi services as service.
 * @author <a href="mailto:baldin@gmail.com">Davi Baldin H. Tavares</a> 
 */
public class Activator implements BundleActivator {

	private static Logger log = Logger.getLogger(ResteasyService.class);
	
	private ServiceTracker httpServiceTracker;
	private ServiceTracker osgiServiceTracker;
	
	private static String servletMapping = "";
	private static ResteasyServlet bridge = null;
	
	@SuppressWarnings("unchecked")
	public void start(BundleContext context) throws Exception {
		log.info("Starting bundle " + context.getBundle().getSymbolicName() + " [" + context.getBundle().getVersion() + "]");
		httpServiceTracker = new HttpServiceTracker(context);
		httpServiceTracker.open();
		log.info("Bundle started sucessfully");
		
		log.info("Starting RESTEasy OSGi service");
		IResteasyService service = new ResteasyService(bridge.getServletContext());
		context.registerService(IResteasyService.SERVICE_NAME,service,new Hashtable());
		
		osgiServiceTracker = new ServiceTracker(context, service.getClass().getName(), null);
		osgiServiceTracker.open();
		log.info("RESTEasy OSGi service started: " + ResteasyService.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping bundle " + context.getBundle().getSymbolicName() + " [" + context.getBundle().getVersion() + "]");
		
		osgiServiceTracker.close();
		osgiServiceTracker = null;
		
		httpServiceTracker.close();
		httpServiceTracker = null;
		log.info("Bundle stopped sucessfully");
	}

	private class HttpServiceTracker extends ServiceTracker {

		public HttpServiceTracker(BundleContext context) {
			super(context, HttpService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			HttpService httpService = (HttpService) context.getService(reference);
			try {
				
				//TODO We need to get a reference to the ServletContext before starting RESTEasy
				// however, the way I'm getting it seems odd.
				ServletContextInterceptor interceptor = new ServletContextInterceptor();
				httpService.registerServlet("/contextinterceptor", interceptor, null, null);
				ServletContext servletContext = interceptor.getServletContext();
								
				log.info("Starting RESTEasy framework");
				
				//TODO Learn if annotation scanning can work inside bundles.
				String scan = servletContext.getInitParameter("resteasy.scan");
				if (scan != null && scan.equals("true")) {
					log.error("Unsupported RESTEasy configuration into OSGi framework. Please remove it");
				}
				
				servletMapping = servletContext.getInitParameter("resteasy.servlet.mapping.prefix");
				bridge = new ResteasyServlet();
				httpService.registerServlet(servletMapping, bridge, null, null);
				log.info("RESTEasy Framework started on " + servletMapping);
				
				httpService.unregister("/contextinterceptor");
				interceptor.destroy();
				interceptor = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return httpService;
		}		
		
		public void removedService(ServiceReference reference, Object service) {
			HttpService httpService = (HttpService) service;
			httpService.unregister(servletMapping);
			bridge = null;
			super.removedService(reference, service);
		}
	}
}