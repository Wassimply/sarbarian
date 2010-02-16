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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.sarbarian.serializer.AbstractSerializer;
import com.sarbarian.serializer.BeanAdapter;
import com.sarbarian.serializer.BeanAdapterException;
import com.sarbarian.serializer.ISerializer;
import com.sarbarian.serializer.SerializerException;
import com.sarbarian.serializer.SerializerUtil;

/**
 * Serialize/Deserialize Objects using JSON notation.
 * @author davi@sarbarian.com
 *
 */
public class JsonSerializer extends AbstractSerializer implements ISerializer {
	
	private static Logger log = Logger.getLogger(JsonSerializer.class);
	
	public JsonSerializer() {
		super();
	}
	
	/**
	 * Return new Real Object instance from JsonObject instance.
	 * Use BeanAdapter to normalize the interface with others Pojo/Bean classes.
	 * @param jsonObject Valid object
	 * @return
	 */
	public Object deserializeObject(Object jsonObject) throws SerializerException {	
		if (jsonObject.getClass() == JsonObject.class) {
			BeanAdapter adapter = new BeanAdapter();
			JsonObject obj = (JsonObject) jsonObject;
			if (obj.hasItem("class")) {
				try {
					//Create new Bean
					adapter.setBean(obj.getItem("class").toString());
					for (String item : obj.getItemsName()) {
						if (obj.getItem(item).getClass() == JsonObject.class) {
							if (adapter.hasBeanField(item)) {
								adapter.set(item, deserializeObject(obj.getItem(item)));
							}
						}else{
							if (obj.getItem(item) != null) {
								if (adapter.hasBeanField(item) && obj.getItem(item).getClass() != JsonNull.class) {
									adapter.set(item, obj.getItem(item));
								}
							}else{
								//System.out.println("Item value NULL ");
							}
						}
					}
				} catch (BeanAdapterException e) {
					throw new SerializerException(e);
				}
			}else{
				//FIXME Open new ticket for this issue
				throw new SerializerException("TODO Nao tem class, o que fazer?");
			}
			return adapter.getBean();
		}
		if (jsonObject.getClass() == JsonArray.class) {
			List<Object> list = new ArrayList<Object>();
			
			for (Object item : ((JsonArray)jsonObject).getItems()) {
				list.add(deserializeObject(item));
			}
			return list;
		}

		return jsonObject;
	}

	/**
	 * Try to serialize the Object into JSON notation.
	 * @param target
	 * @return Json Object Structure.
	 * @throws SerializerException
	 */
	@SuppressWarnings("unchecked")
	public Object serializeJson(Object target) throws SerializerException {
		
		if (target == null) {
			return new JsonNull();
		}

		if (target.getClass() == String.class) {
			return new JsonString(target.toString());
		}

		if (target.getClass() == Boolean.class) {
			return target;
		}

		if (target.getClass() == Integer.class) {
			return target;			
		}

		if (target.getClass() == Long.class) {
			return target;
		}

		if (target.getClass() == Float.class) {
			return target;
		}

		if (target.getClass() == Double.class) {
			return target;
		}

		if (SerializerUtil.isCollection(target)) {
			JsonArray array = new JsonArray();
			Collection<Object> collection = (Collection<Object>) target;
			if (collection == null || collection.isEmpty()) {
				return array;
			}
			for (Object item : collection) {
				array.add(serializeJson(item));
			}
			return array;
		}

		try {
			log.debug("Serializing target " + target.getClass().getName() + " -> " + target.toString());
			BeanAdapter beanAdapter = new BeanAdapter(target);
			JsonObject jsonObject = new JsonObject();
			if (Boolean.parseBoolean(getProperty(QUOTE_KEY))) {
				jsonObject.setQuoteKey(true);
			}
			
			String[] fieldsToSerialize = null;
			if (getBeanFieldsOnly() == null) {
				fieldsToSerialize = beanAdapter.getBeanFields().toArray(new String[beanAdapter.getBeanFields().size()]);
			}else{
				fieldsToSerialize = getBeanFieldsOnly();
			}
			
			for (String field : fieldsToSerialize) {
				Object fieldValue = beanAdapter.get(field);
				
				//Ignore to serialize the field if it was defined to be ignored.
				if (isBeanFieldIgnore(field)){
					continue;
				}
				
				log.debug("  Serializing the field " + field + " >>>>>>>>>>>>>");

				if (SerializerUtil.isCollection(fieldValue)) {
					log.debug("    Serializing field name: " + field + " as collection");
					jsonObject.add(field,serializeJson((Collection<Object>) fieldValue));
					continue;
				}
				log.debug("    Serializing field name: " + field);
				if (SerializerUtil.isNotSupported(fieldValue)) {
					log.debug("Skiiping this bad object");
					continue;
				}
				jsonObject.add(field, serializeJson(fieldValue));
			}
			
			//Repeat the serialization process to declared methods.
			String serializeMethods = getProperty(BEAN_METHOD_SERIALIZE_VALUE);
			if (serializeMethods != null) {
				for (String methodName : serializeMethods.split(",")) {
					Object methodValue = beanAdapter.callMethod(methodName);
					
					log.debug("  Serializing the method " + methodName + " >>>>>>>>>>>>>");

					if (SerializerUtil.isCollection(methodValue)) {
						log.debug("    Serializing field name: " + methodName + " as collection");
						jsonObject.add(methodName,serializeJson((Collection<Object>) methodValue));
						continue;
					}
					log.debug("    Serializing field name: " + methodName);
					if (SerializerUtil.isNotSupported(methodValue)) {
						log.debug("Skiiping this bad object");
						continue;
					}
					jsonObject.add(methodName, serializeJson(methodValue));
					
				}
			}
			
			if (Boolean.parseBoolean(getProperty(ADD_CLASS_FIELD))) {
				jsonObject.add("class", new JsonString(target.getClass().getName()));
			}
			jsonObject.setSpacer(getProperty(SPACE_STRING));
			return jsonObject;
		} catch (BeanAdapterException e) {
			throw new SerializerException(e);
		}
	}

	/**
	 * Return JsonValue implementation instance from the JSON String peace.
	 * @param value
	 * @return
	 * @throws ResultParserException
	 */
	private Object deserializeJsonValue(String value) throws SerializerException {
		try {

			if ("null".equals(value)) {
				return new JsonNull();
			}

			if ("true".equals(value)) {
				return new Boolean(true);
			}

			if ("false".equals(value)) {
				return new Boolean(false);
			}

			//Long
			//^[-+]?[0-9]*$
			if (value.matches("^[-]?[0-9]*$")) {
				return Long.parseLong(value);
			}

			//Float point
			//^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$
			if (value.matches("^[-]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
				return Float.parseFloat(value);
			}

			//Java JSON Virtual Object
			//^{.*}$
			if (value.matches("^\\{.*\\}$")) {
				value = value.trim();
				JsonObject object = new JsonObject();

				// remove the {}
				value = value.substring(1, value.length()-1);
				List<String> items = recursiveSplit(value,',');
				for (String item : items) {
					String pair[] = recursiveSplit(item,':').toArray(new String[2]);
					if (pair.length == 2) {
						object.add(pair[0], deserializeJsonValue(pair[1].trim()));
					}
				}
				return object;
			}

			//Java JSON Virtual Array
			//^\[.*\]$
			if (value.matches("^\\[.*\\]$")) {
				value = value.trim();
				JsonArray array = new JsonArray();

				// remove the []
				value = value.substring(1, value.length()-1);
				List<String> items = recursiveSplit(value,',');
				for (String item : items) {
					array.add(deserializeJsonValue(item));
				}
				return array;
			}

			//Else is always String.
			return JsonString.unquoteString(value);

		}catch (Exception e) {
			throw new SerializerException(e);
		}
	}

	/**
	 * Split Strings avoiding JSON Object and Arrays.
	 * @param value
	 * @param token
	 * @return
	 */
	private List<String> recursiveSplit(String value, char token) {
		List<String> items = new ArrayList<String>();
		int ignore = 0;
		int a1 = 0;
		char c = 0;
		int i;
		for (i=0; i<value.length(); i++) {
			c = value.charAt(i);
			if (c == '{') {
				ignore++;
				continue;
			}
			if (c == '}') {
				ignore--;
				continue;
			}
			if (c == '[') {
				ignore++;
				continue;
			}
			if (c == ']') {
				ignore--;
				continue;
			}
			if (c == token) {
				if (ignore == 0) {
					items.add(value.substring(a1, i));
					a1 = i+1;
				}
			}
		}
		if (ignore == 0) {
			items.add(value.substring(a1, i));
		}
		return items;
	}
	
	public Object deserialize(String target) throws SerializerException {
		return deserializeObject(deserializeJsonValue(target));
	}
	
	public String serialize(Object target) throws SerializerException {
		return serializeJson(target).toString();
	}
}