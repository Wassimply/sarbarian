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
import java.io.Writer;

import com.sarbarian.serializer.StreamedOutput;

/**
 * JsonDate convert know well formed class "Date" into JavaScript Date object.
 * This class can be useful with EXTJS Framework.
 * @author davi@sarbarian.com
 *
 */
public class JsonDate implements StreamedOutput {
	private Object date;
	
	/**
	 * Create new JsonDate thru the date Object.
	 * @param date
	 */
	public JsonDate(Object date) {
		this.date = date;
	}
	
	public Object getDate() {
		return date;
	}
	
	public void setDate(Object date) {
		this.date = date;
	}
	
	/**
	 * Dump the JavaScript "new Date(number)" when number is the Time in milliseconds
	 * since 01/01/1970. 
	 */
	public String toString() {
		if (date.getClass() == java.util.Date.class) {
			return "new Date(" + ((java.util.Date)date).getTime() + ")";
		}
		if (date.getClass() == java.sql.Date.class) {
			return "new Date(" + ((java.sql.Date)date).getTime() + ")";
		}
		if (date.getClass() == java.util.Calendar.class) {
			return "new Date(" + ((java.util.Calendar)date).getTimeInMillis() + ")";
		}
		if (date.getClass() == java.sql.Time.class) {
			return "new Date(" + ((java.sql.Time)date).getTime() + ")";
		}
		if (date.getClass() == java.sql.Timestamp.class) {
			return "new Date(" + ((java.sql.Timestamp)date).getTime() + ")";
		}
		return date.toString();
	}
	
	public void write(Writer out) throws IOException {
		out.write(toString());
	}
	
}
