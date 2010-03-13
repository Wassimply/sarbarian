package org.jboss.resteasy.plugins.server.osgi.internal;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;

import org.jboss.resteasy.plugins.server.osgi.IResteasyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi Activator class to start RESTEasy inside OSGi container binding into HttpService as servlet 
 * mapping and into OSGi services as service.
 * @author <a href="mailto:baldin@gmail.com">Davi Baldin H. Tavares</a> 
 */
public class Activator implements BundleActivator, ServiceListener {

	private static Logger log = LoggerFactory.getLogger(ResteasyService.class);
	
	private ServiceTracker httpServiceTracker;
	private ServiceRegistration sr;
	
	private IResteasyService service;
	
	private static String servletMapping = "";
	private static ResteasyServlet bridge = null;
	
	private BundleContext context = null;
	private String serviceFilter = null;
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.serviceFilter = "(objectclass=*)";
		
		httpServiceTracker = new ServiceTracker(context, HttpService.class.getName(), null);
		httpServiceTracker.open();
		HttpService httpService = (HttpService) httpServiceTracker.getService();
		
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
			throw e;
		}
		
		service = new ResteasyService(bridge.getServletContext());
		sr = context.registerService(IResteasyService.SERVICE_NAME, service, null);
		
		try {
			context.addServiceListener(this, serviceFilter);
			ServiceReference[] srl = context.getServiceReferences(null, serviceFilter);
			for(int i = 0; srl != null && i < srl.length; i++) {
				addSingletons(context.getService(srl[i]));
				context.ungetService(srl[i]);
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		sr = null;
		bridge = null;
		servletMapping = "";
		httpServiceTracker.close();
		httpServiceTracker = null;
	}
	
	/**
	 * Listen to service Registrations/Unregistrations and call addSingletons, removeSingletons for all services found.
	 */
	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();
		switch(event.getType()) {
			case ServiceEvent.REGISTERED: 
				try {
					addSingletons(context.getService(sr));
					context.ungetService(sr);
					//if (!addSingletons(context.getService(sr))) {
					//	context.ungetService(sr);
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}
			break;
			
			case ServiceEvent.UNREGISTERING:
				try {
					//if (!removeSingletons(context.getService(sr))) {
					//	context.ungetService(sr);
					//}
					removeSingletons(context.getService(sr));
					context.ungetService(sr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			break;
		}
	}
	
	/**
	 * Add a service as a singleton into the RESTEasy service if the service class is annotated with 
	 * javax.ws.rs.Path which means the the service is a REST Resource class. Return true if
	 * the class is annotated and added from the RESTEasy service.
	 * @param service
	 * @return
	 */
	private boolean addSingletons(Object service) {
		if (service != null && service.getClass().isAnnotationPresent(Path.class)) {
			log.info("Adding JAX-RS resource: " + service.getClass().getName());
			this.service.addSingletonResource(service);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a service from the RESTEasy service if the service class is annotated with 
	 * javax.ws.rs.Path which means the the service is a REST Resource class. Return true if
	 * the class is annotated and removed from the RESTEasy service.
	 * @param service
	 * @return
	 */
	private boolean removeSingletons(Object service) {
		if (service != null && service.getClass().isAnnotationPresent(Path.class)) {
			log.info("Removing JAX-RS resource: " + service.getClass().getName());
			this.service.removeSingletonResource(service.getClass());
			return true;
		}
		return false;
	}
}