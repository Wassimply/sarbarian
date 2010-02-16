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
package com.sarbarian.webproxy.test;

import com.sarbarian.serializer.BeanAdapter;
import com.sarbarian.serializer.BeanAdapterException;

public class BeanAdapterTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SampleBean bean = new SampleBean();
		
		try {
			BeanAdapter adapter = new BeanAdapter(bean);
			System.out.println(adapter.toString());
			System.out.println("vazio");
		} catch (BeanAdapterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bean.setDescription("DESCRIPTION");
		bean.setId(1000);
		bean.setName("NAME");
		bean.setNumber(1000000L);
		bean.setUnid("UNID");
		
		try {
			BeanAdapter adapter = new BeanAdapter(bean);
			System.out.println(adapter.get("name").toString());
			System.out.println(adapter.toString());
			System.out.println("deveria");
		} catch (BeanAdapterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
