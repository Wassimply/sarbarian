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

import java.io.IOException;
import java.io.Writer;

/**
 * StreamedOutput define a new kind of serialize objects into a Writer.
 * This could be useful to serialized objects into buffered outputs.
 * @author davi@sarbarian.com
 *
 */
public interface StreamedOutput {
	
	/**
	 * Write the object output into Writer.
	 * @param out Writer out.
	 */
	public void write(Writer out) throws IOException;

}
