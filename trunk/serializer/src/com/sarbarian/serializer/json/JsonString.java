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
 * JSON String used in Objects and Arrays.
 * @author davi@sarbarian.com
 *
 */
public class JsonString implements StreamedOutput {
	
	private String string;
	
	public JsonString(String string) {
		this.string = string;
	}
	
	public String getString() {
		return string;
	}
		
	/**
	 * Remove quotes around special characters added by JsonObject.
	 * @param quoted
	 * @return
	 */
	public static String unquoteString(String quoted) {
		String tmp = quoted.trim();
		tmp = tmp.replaceAll("^\"", "");
		tmp = tmp.replaceAll("\"$", "");
		return tmp;
	}
	
	public static String quoteString(String string, char quoteChar) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}
		char         b;
		char         c = 0;
		int          i;
		int          len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String       t;

		sb.append(quoteChar);
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append(quoteChar);
		return sb.toString();
	}
	
	/**
	 * Quote safe string.
	 * @param string
	 * @return
	 */
	public static String quoteString(String string) {
		return quoteString(string, '"');
	}
	
	/**
	 * Return the quoted JsonString. 
	 */
	public String toString() {
		return quoteString(string);
	}
	
	public void write(Writer out) throws IOException {
		out.write(toString());
		
	}
}
