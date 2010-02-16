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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Utility class.
 * @author davi@sarbarian.com
 *
 */
public class SerializerUtil {
	
	private static Logger log = Logger.getLogger(SerializerUtil.class);
	
	/**
	 * FIXME Help me to get rid of this method and start supporting everything.
	 * @param object
	 * @return
	 */
	public static boolean isNotSupported(Object object) {
		
		if (object == null) {
			return false;
		}
		if (HashMap.class == object.getClass()) {
			return true;
		}
		if (object.getClass().getName().contains("org.hibernate.")) {
			return true;
		}
		return false;
	}

	/**
	 * Try to determine if the Object is a Collection of Objects.
	 * @param object
	 * @return
	 */
	public static boolean isCollection (Object object) {
		
		if (object == null) {
			return false;
		}
		if (ArrayList.class == object.getClass()) {
			return true;
		}
		if (TreeSet.class == object.getClass()) {
			return true;
		}
		if (SortedSet.class == object.getClass()) {
			return true;
		}
		if (HashSet.class == object.getClass()) {
			return true;
		}
		if (LinkedList.class == object.getClass()) {
			return true;
		}
		if (Vector.class == object.getClass()) {
			return true;
		}
		if (AbstractCollection.class == object.getClass()) {
			return true;
		}
		Class<?> interfaces[] = object.getClass().getInterfaces();
		log.debug("Interfaces of " + object.getClass().getName());
		if (interfaces != null && interfaces.length > 0) {
			for (int i = 0; i < interfaces.length; i++) {
				Class<?> iface = interfaces[i];
				log.debug(iface.getName());
			}
		}
		return false;
	}
	
	public static long collectionSize(Object object) {
		
		long size = 0L;
		
		if (object == null) {
			size = new Long(0);
		}
		if (object.getClass().isArray()) {
			size = new Long(((Object[]) object).length);
		}
		if (ArrayList.class == object.getClass()) {
			size = new Long(((List<?>) object).size());
		}
		if (TreeSet.class == object.getClass()) {
			size = new Long(((Set<?>) object).size());
		}
		if (SortedSet.class == object.getClass()) {
			size = new Long(((Set<?>) object).size());
		}
		if (HashSet.class == object.getClass()) {
			size = new Long(((Set<?>) object).size());
		}
		if (LinkedList.class == object.getClass()) {
			size = new Long(((List<?>) object).size());
		}
		if (Vector.class == object.getClass()) {
			size = new Long(((Vector<?>) object).size());
		}
		if (AbstractCollection.class == object.getClass()) {
			size = new Long(((Collection<?>) object).size());
		}
		log.debug("SerializerUtil Collection size = " + size);
		return size;
	}
}
