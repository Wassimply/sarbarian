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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sarbarian.serializer.StreamedOutput;

/**
 * JsonArray represents an array: [Item1,Item2,ItemN].
 * @author davi@sarbarian.com
 *
 */
public class JsonArray implements StreamedOutput {
	
	private List<Object> items;
	private String spacer;
	
	public JsonArray() {
		this.items = new ArrayList<Object>();
		this.spacer = "";
	}
	
	/**
	 * Add new item to array. If the value is NULL, new JsonNull() will be inserted.
	 * @param item
	 */
	public void add(Object item) {
		if (item == null) {
			item = new JsonNull();
		}
		this.items.add(item);
	}
	
	/**
	 * Remove the object instance if exist.
	 * @param item
	 */
	public void remove(Object item) {
		if (item != null) {
			this.items.remove(item);	
		}
	}
	
	/**
	 * Set the string spacer between Json elements.
	 * @param spacer
	 */
	public void setSpacer(String spacer) {
		this.spacer = spacer;
	}
	
	/**
	 * Return all items.
	 * @return
	 */
	public List<Object> getItems() {
		return items;
	}
	
	public void write(Writer out) throws IOException {
		Iterator<Object> ite = this.items.iterator();
		out.write("[" + spacer);
		while (ite.hasNext()) {
			Object item = ite.next();
			try {
				//TODO Implementar writer recursivo
				out.write(spacer + item.toString() + spacer);
			}catch (Exception e) {
				out.write(spacer + "null" + spacer);
			}
			if (ite.hasNext()) {
				out.write(spacer + "," + spacer);
			}
		}
		out.write(spacer + "]");
	}
	
	/**
	 * Dump the JSON array notation.
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
