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

/**
 * Interface ISerializer used to Serialize/Deserialize Object (i.e. Entity)
 * into String or other media type to client.
 * @author davi@sarbarian.com
 *
 */
public interface ISerializer {
	
	/**
	 * Serialize target into String.
	 * @param target Object or collection of objects.
	 * @return String serialization.
	 */
	public String serialize(Object target) throws SerializerException;
	
	/**
	 * Deserialize target into Object or Object collection.
	 * @param target String of objects.
	 * @return Object real Object implementation.
	 */
	public Object deserialize(String target) throws SerializerException;
	
	/**
	 * Add serializer configuration property.
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value);
	
	/**
	 * Remove serializer configuration property.
	 * @param key
	 */
	public void removeProperty(String key);
	
	/**
	 * Retrieve the property value by property key.
	 * @param key
	 * @return
	 */
	public String getProperty(String key);

}