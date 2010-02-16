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
package com.sarbarian.serializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * BeanAdapter provide interface to access well formed JavaBean classes. Useful to serialize
 * Beans over HTTP protocol. Each Bean has properties with get/set methods + Field.  
 * @author davi@sarbarian.com
 *
 */
public class BeanAdapter {
	private Logger log = Logger.getLogger(BeanAdapter.class);

	private Object bean = null;
	private BeanInfo beanInfo = null;
	private Map<String,PropertyDescriptor> properties = null;

	/**
	 * Default constructor doing nothing.
	 */
	public BeanAdapter() { }

	/**
	 * Construct a bean adapter based on already create Java Bean
	 * @param bean
	 * @throws BeanAdapterException
	 */
	public BeanAdapter(Object bean) throws BeanAdapterException {
		setBean(bean);
	}

	/**
	 * Create and set Bean by default Bean constructor.
	 * @param className
	 * @throws BeanAdapterException
	 */
	public void setBean(String className) throws BeanAdapterException{
		try {
			Class<?> cls = Class.forName(className);
			Object bean = cls.newInstance();
			setBean(bean);
		} catch (Exception e) {
			log.error("setBean() className " + className + " Exception: " + e.getMessage());
			throw new BeanAdapterException(e);
		}
	}

	/**
	 * Create and set Bean by default Bean constructor, and fill the fields based on the Map.
	 * If the property doesn't exist, it will be ignored.
	 * @param className
	 * @throws BeanAdapterException
	 */
	public void setBean(String className, Map<String,Object> fields) throws BeanAdapterException {
		setBean(className);
		for (String item : fields.keySet()) {
			if (hasBeanField(item)) {
				this.set(item, fields.get(item));
			}
		}
	}

	/**
	 * Set already created bean.
	 * @param bean
	 * @throws BeanAdapterException
	 */
	public void setBean(Object bean) throws BeanAdapterException {
		if (bean == null) {
			log.error("Trying to set a null Object as a bean");
			throw new BeanAdapterException("NULL Bean passed to adapter");
		}
		this.bean = bean;
		properties = new HashMap<String,PropertyDescriptor>();
		try {
			beanInfo = Introspector.getBeanInfo(bean.getClass());
			for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ) {
				if ("class".equals(pd.getName())) {
					continue;
				}
				properties.put(pd.getName(), pd);
			}
			if (log.isDebugEnabled()) {
				StringBuffer buff = new StringBuffer();
				Iterator<String> ite = getBeanFields().iterator();
				while (ite.hasNext()) {
					String field = ite.next();
					buff.append(field);
					if (ite.hasNext()) {
						buff.append(", ");
					}
				}
				log.debug("Properties of the class " + bean.getClass().getName() + ": " + buff.toString());
			}
		} catch (IntrospectionException e) {
			throw new BeanAdapterException(e);
		}
	}

	public Object getBean() {
		return bean;
	}

	public void set(String property,Object value) throws BeanAdapterException {
		try {
			Method m = properties.get(property).getWriteMethod();
			if (m != null) {
				m.invoke(bean, value);
			}else{
				throw new BeanAdapterException("Getter method not found for property: " + property);
			}
		} catch (Exception e) {
			throw new BeanAdapterException(e);
		}
	}

	public Object get(String property) throws BeanAdapterException {
		try {
			Method m = properties.get(property).getReadMethod();
			if (m != null) {
				return m.invoke(bean);
			}else{
				throw new BeanAdapterException("Getter method not found for property: " + property);
			}
		} catch (Exception e) {
			throw new BeanAdapterException(e);
		}
	}
	
	/**
	 * Call a method name without any parameter and get their response.
	 * @param methodName
	 * @return
	 * @throws BeanAdapterException
	 */
	public Object callMethod(String methodName) throws BeanAdapterException {
		try {
			Method m = bean.getClass().getMethod(methodName);
			return m.invoke(bean);
		}catch (Exception e) {
			throw new BeanAdapterException(e);
		}
	}

	/**
	 * Return only the fields where there is the correct get/set method declared in the Bean. 
	 * @return
	 */
	public Set<String> getBeanFields() {
		return properties.keySet();
	}

	/**
	 * Return the bean class name.
	 * @return
	 */
	public String getBeanClassName() {
		if (bean == null) {
			return null;
		}
		return bean.getClass().getName();
	}

	/**
	 * Return true if Bean contains the field name.
	 * @param beanFieldName
	 * @return
	 */
	public boolean hasBeanField (String beanFieldName) {
		return this.properties.containsKey(beanFieldName);
	}

	/**
	 * Dump all bean fields
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		Iterator<String> ite = getBeanFields().iterator();
		while (ite.hasNext()) {
			String field = ite.next();
			try {
				Object value = get(field);
				if (value == null) {
					buff.append(field + " = NULL");
				}else{
					buff.append(field + " = '" + get(field).toString() + "'");
				}
			} catch (BeanAdapterException e) { }
			if (ite.hasNext()) {
				buff.append("; ");
			}
		}
		return "Bean properties: " + buff.toString();
	}
}
