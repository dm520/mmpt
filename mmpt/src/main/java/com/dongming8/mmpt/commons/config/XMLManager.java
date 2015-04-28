package com.dongming8.mmpt.commons.config;


public class XMLManager implements Manager {

	public Object read(String fileName, Class<?> c) throws Exception {
		return ConfigUtil.jsonToObject(ConfigUtil.xmlReadFromFile(fileName), c);
	}

	public void write(String fileName, Object o) throws Exception {
		ConfigUtil.xmlWriteToFile(ConfigUtil.objectToJson(o), fileName);
	}
}
