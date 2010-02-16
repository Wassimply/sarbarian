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
 * Simple Wrapper class to NULL value.
 * @author davi@sarbarian.com
 *
 */
public class JsonNull implements StreamedOutput {

	/**
	 * Return the String "null".
	 */
	public String toString() {
		return "null";
	}
	
	public void write(Writer out) throws IOException {
		out.write(toString());	
	}

}
