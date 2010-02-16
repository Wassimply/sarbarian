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
 * StringSerializer must be used in simple cases, when the Bean.toString() is enough 
 * to communicate with client.
 * @author davi@sarbarian.com
 *
 */
public class StringSerializer implements ISerializer {
	
	/**
	 * There is nothing to configure here.
	 */
	public void addProperty(String key, String value) { }
	
	/**
	 * There is nothing to configure here.
	 */
	public void removeProperty(String key) { }
	
	/**
	 * There is nothing to configure here.
	 */
	public String getProperty(String key) {
		return null;
	}
	
	public String serialize(Object target) throws SerializerException {
		return target.toString();
	}
	
	public Object deserialize(String target) throws SerializerException {
		return target;
	}

}
