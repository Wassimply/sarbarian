package org.jboss.resteasy.plugins.server.osgi;

import org.jboss.resteasy.plugins.server.osgi.internal.ResteasyService;

/**
 * IResteasyService provides interface to the internal RESTEasy implementation. This interface
 * will be called by bundles in order to register resources into RESTEasy.
 * @author <a href="mailto:baldin@gmail.com">Davi Baldin H. Tavares</a> 
 */
public interface IResteasyService {
	
	/**
	 * Service name inside OSGi namespace service registration.
	 */
	public static final String SERVICE_NAME = ResteasyService.class.getName();

	/**
	 * Add a SingletonResource.
	 * @param resource
	 */
	public void addSingletonResource(Object resource);
	
	/**
	 * Remove a SingletonResource.
	 * @param clazz
	 */
	public void removeSingletonResource(Class<?> clazz);

}