package cn.dongming8.mmpt.commons.config;


public class JSONManager implements Manager {

	public Object read(String fileName, Class<?> c) throws Exception {
		return ConfigUtil.jsonToObject(ConfigUtil.jsonReadFromFile(fileName), c);
	}

	public void write(String fileName, Object o) throws Exception {
		ConfigUtil.jsonWriteToFile(ConfigUtil.objectToJson(o), fileName);
	}
}
