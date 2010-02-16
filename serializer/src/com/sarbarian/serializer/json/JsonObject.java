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
package com.sarbarian.serializer.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sarbarian.serializer.StreamedOutput;

/**
 * JsonObject wrapper to the JSON Object notation { item1: value1, itemN: valueN }
 * @author davi@sarbarian.com
 *
 */
public class JsonObject implements StreamedOutput {

	private Map<String,Object> items;
	private String spacer;
	private Boolean quoteKey;
	private char quoteValueChar;

	public JsonObject() {
		this.items = new HashMap<String, Object>();
		this.spacer = "";
		this.quoteKey = false;
		this.quoteValueChar = '"';
	}

	/**
	 * Set the string Spacer between the JSON elements.
	 * @param spacer
	 */
	public void setSpacer(String spacer) {
		this.spacer = spacer;
	}
	
	/**
	 * Set true to quote the key element name 
	 * @param quoteKey
	 */
	public void setQuoteKey(Boolean quoteKey) {
		this.quoteKey = quoteKey;
	}
	
	/**
	 * Set the quote character to be used to quote string value.
	 * @param quoteValueChar
	 */
	public void setQuoteValueChar(char quoteValueChar) {
		this.quoteValueChar = quoteValueChar;
	}

	/**
	 * Add new item pair key/value.
	 * @param key
	 * @param value
	 */
	public void add(String key, Object value) {
		//System.out.println("DEBUG add(): key = '"+ key + "', value = '"+ value.toString() + "', class = '"+ value.getClass().getName() + "'");
		if (key == null) {
			return;
		}
		if (!key.equals("")) {
			this.items.put(key, value);	
		}
	}
	
	/**
	 * Add new item value to JsonArray key if exist, or create new JsonArray instance.
	 * @param key
	 * @param value
	 * @param asArray
	 */
	public void add(String key, Object value, boolean asArray) {
		if (asArray == true) {
			if (hasItem(key)) {
				if (getItem(key).getClass() == JsonArray.class) {
					((JsonArray)getItem(key)).add(value);
					return;
				}else{
					JsonArray array = new JsonArray();
					array.add(getItem(key));
					add(key,array);
					return;
				}
			}
		}
		add(key,value);
	}

	public void remove(String key) {
		this.items.remove(key);
	}
	
	public Object getItem(String key) {
		return items.get(key);
	}
	
	/**
	 * Return all elements items name.
	 * @return
	 */
	public Set<String> getItemsName() {
		return items.keySet();
	}
	
	/**
	 * Return all items.
	 * @return
	 */
	public Map<String, Object> getItems() {
		return items;
	}
	
	/**
	 * Return true if the Object contains the item name.
	 * @param key
	 * @return
	 */
	public boolean hasItem(String key) {
		return this.items.containsKey(key);
	}
	
	public void write(Writer out) throws IOException {
		Iterator<String> ite = this.items.keySet().iterator();
		out.write("{" + spacer);
		while (ite.hasNext()) {
			String key = ite.next();
			Object value = items.get(key);
			if (quoteKey) {
				key = "\"" + key + "\"";
			}
			try {
				if (value.getClass() == String.class) {
					out.write(key + spacer + ":" + spacer + JsonString.quoteString(value.toString(),quoteValueChar));
				}else{
					//TODO Implementar writer recursivo
					out.write(key + spacer + ":" + spacer + value.toString());
				}
			}catch (Exception e) {
				out.write(key + spacer + ":" + spacer + "null");
			}
			if (ite.hasNext()) {
				out.write(spacer + "," + spacer);
			}
		}
		out.write(spacer + "}");
	}
	
	/**
	 * Output the JSON Object.
	 */
	public String toString() {
		StringWriter out = new StringWriter();
		try {
			write(out);
			return out.toString();
		} catch (IOException e) {
			return null;
		}
	}
	
}