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

import java.util.HashMap;
import java.util.Map;

/**
 * AbstractSerializer is a default base class to create a new specialized serializer. 
 * @author davi@sarbarian.com
 *
 */
public abstract class AbstractSerializer {
	
	/**
	 * Property name to define the spacer string between each serialized element.
	 */
	public static final String SPACE_STRING = "spacer";
	
	/**
	 * Property name to add the field "class" representing the real class name of each serialized Bean.
	 */
	public static final String ADD_CLASS_FIELD = "addFieldClass";
	
	/**
	 * Property name to quote JSON Object key.
	 */
	public static final String QUOTE_KEY = "quoteKey";
	
	/**
	 * Property name to define the list of ignored bean fields. The list must be separated by comma char ','.
	 * This property is incompatible with BEAN_FIELDS_ONLY.
	 */
	public static final String BEAN_FIELDS_IGNORE = "bean.fields.ignore";
	
	/**
	 * Property name to define the list of only serialized bean fields. The list must be separated by comma char ','.
	 * This property is incompatible with BEAN_FIELDS_IGNORE.
	 */
	public static final String BEAN_FIELDS_ONLY = "bean.fields.only";
	
	/**
	 * Property name to define the list of methods to be called to get their value and serialized. The value must be separated by comma char ','.
	 */
	public static final String BEAN_METHOD_SERIALIZE_VALUE = "bean.methods.serialize";
	
	
	private String[] beanFieldsIgnore = null;
	private String[] beanFieldsOnly = null;
	
	private Map<String,String> properties = null;
	
	public AbstractSerializer() {
		this.properties = new HashMap<String, String>();
		this.beanFieldsIgnore = null;
		this.beanFieldsOnly = null;
		//Setting default values
		addProperty(SPACE_STRING, "");
		addProperty(QUOTE_KEY, "false");
		addProperty(ADD_CLASS_FIELD, "false");
	}
	
	public void removeProperty(String key) {
		this.properties.remove(key);
	}
	
	public String getProperty(String key) {
		return this.properties.get(key);
	}

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
		if (key.equals(BEAN_FIELDS_IGNORE)) {
			beanFieldsIgnore = value.split(",");
			beanFieldsOnly = null;
		}
		if (key.equals(BEAN_FIELDS_ONLY)) {
			beanFieldsOnly = value.split(",");
			beanFieldsIgnore = null;
		}
	}
	
	/**
	 * Return true if the field name was defined to be ignored.
	 * @param fieldName
	 * @return
	 */
	public boolean isBeanFieldIgnore(String fieldName) {
		if (beanFieldsIgnore == null) {
			return false;
		}
		for (String field : this.beanFieldsIgnore) {
			if (field.equals(fieldName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the only allowed bean fields to be serialized.
	 * @return
	 */
	public String[] getBeanFieldsOnly() {
		return beanFieldsOnly;
	}

}
