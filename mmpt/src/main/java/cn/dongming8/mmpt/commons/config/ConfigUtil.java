package cn.dongming8.mmpt.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONNull;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;
import net.sf.json.xml.XMLSerializer;

public class ConfigUtil {
	/**
	 * 锁
	 */
	private static Object lock = new Object();
	/**
	 * 日志
	 */
	// private static Logger log =
	// LogManager.getLogger(FileUtil.class.getName());
	// private static Logger log = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 配置文件映射成内存对象
	 */
	private static Map<String, JSON> objectMap = Collections
			.synchronizedMap(new HashMap<String, JSON>());

	public static boolean isWin = "\\".equals(File.separator);

	// public static String ROOT_PATH = (isWin ? "C:" : "") + File.separator
	// + "etc" + File.separator + "unimas" + File.separator + "cs"
	// + File.separator + "config" + File.separator;

	// private static boolean isTest = true;

	/**
	 * 将配置文件实体对象转成JSONObject
	 * 
	 * @param object
	 * @return
	 */
	public static JSON objectToJson(Object object) {
		JSON json = JSONNull.getInstance();
		if (object != null) {
			json = JSONSerializer.toJSON(object);
			/*
			 * if(JSONUtils.isObject(object)){ json =
			 * JSONObject.fromObject(object); }else
			 * if(JSONUtils.isArray(object)){ json =
			 * JSONArray.fromObject(object);; }
			 */
		}
		return json;
	}

	public static Map<String, Class<?>> dealList(Class<?> c) {

		Field[] declaredField = c.getDeclaredFields();

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

		for (int i = 0; i < declaredField.length; i++) {
			// System.out.println("---className=");
			// System.out.println("字段类型="+declaredField[i].getType());
			// System.out.println("字段名字="+declaredField[i].getName());
			// System.out.println("字段实际类型="+declaredField[i].getGenericType());
			if ("interface java.util.List".equals(declaredField[i].getType()
					.toString())) {
				ParameterizedType pType = (ParameterizedType) declaredField[i]
						.getGenericType(); //
				// System.out.println("ParameterizedType = " + pType);
				// System.out.println("getActualTypeArguments = " +
				// Arrays.deepToString(pType.getActualTypeArguments()));
				Type actualType = pType.getActualTypeArguments()[0];// 获取泛型对象类型
				if (actualType instanceof Class<?>) {
					//
					Class<?> type = (Class<?>) actualType;

					String tClassName = type.getName();
					// System.out.println("tClassName = "+tClassName);
					try {

						Class<?> cIn = Class.forName(tClassName);

						if (!cIn.isPrimitive()) {
							classMap.put(declaredField[i].getName(), cIn);
							classMap.putAll(dealList(cIn));
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} else if (actualType instanceof List) {
					classMap.putAll(dealList(actualType.getClass()));
				}

			}
		}
		return classMap;
	}

	/**
	 * 将JSONObject转成配置文件实体对象
	 * 
	 * @param json
	 * @param c
	 * @return
	 */
	public static Object jsonToObject(JSON json, Class<?> c) {
		Object object = new Object();
		JsonConfig jc = new JsonConfig();
		jc.setRootClass(c);
		if (json != null) {
			if (json.isArray()) {
				jc.setCollectionType(List.class);
			}
			/*
			 * Field[] declaredField = c.getDeclaredFields(); Map<String,
			 * Class<?>> classMap = new HashMap<String, Class<?>>();
			 * 
			 * for (int i = 0; i < declaredField.length; i++) { //
			 * System.out.println("---className="); //
			 * System.out.println("字段类型="+declaredField[i].getType()); //
			 * System.out.println("字段名字="+declaredField[i].getName()); //
			 * System.out.println("字段实际类型="+declaredField[i].getGenericType());
			 * if ("interface java.util.List".equals(declaredField[i].getType().
			 * toString())) { ParameterizedType pType = (ParameterizedType)
			 * declaredField[i].getGenericType(); // Class<?> type = (Class<?>)
			 * pType.getActualTypeArguments()[0];// 获取泛型对象类型 String tClassName =
			 * type.getName(); System.out.println("tClassName = "+tClassName);
			 * try { classMap.put(declaredField[i].getName(),
			 * Class.forName(tClassName)); } catch (ClassNotFoundException e) {
			 * e.printStackTrace(); } } }
			 */
			Map<String, Class<?>> classMap = dealList(c);
			// System.out.println("classMap = "+classMap);
			// classMap.put("inputKeys", DeviceInputKey.class);
			// classMap.put("returnKeys", DeviceReturnKey.class);
			if (classMap != null && classMap.size() != 0) {
				jc.setClassMap(classMap);
			}
			object = JSONSerializer.toJava(json, jc);
		}
		return object;
	}

	/**
	 * 将jsonObject以json格式写入文件
	 * 
	 * @param json
	 * @param fileName
	 * @throws X
	 */
	public static void jsonWriteToFile(JSON json, String fileName)
			throws Exception {
		FileOutputStream fos = null;
		try {
			synchronized (lock) {
				// System.out.println(fileName);
				String tempFileName = fileName;
				if (!isWin) {
					tempFileName += ".swp";
				}
				File file = new File(tempFileName);
				mkdirs(file);
				fos = new FileOutputStream(file);
				// PrintWriter pw = new PrintWriter(fos);
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(file, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				json.write(pw);
				pw.flush();
				if (!isWin) {
					File newFile = new File(fileName);
					// if (isWin) {
					// newFile.delete();
					// }
					boolean b = file.renameTo(newFile);
					if (!b) {
						throw new Exception("将临时文件修改文件名称发生错误！");
					}
				}
			}
			objectMap.remove(fileName);
			// log.info("-----写配置文件成功！");
		} catch (FileNotFoundException e) {
			throw new Exception("将json对象写入配置文件发生异常！详细信息：" + e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}

	}

	/**
	 * 
	 * 创建文件夹
	 * 
	 * @param file
	 * @return
	 */
	public static boolean mkdirs(File file) {
		if (!file.exists()) {
			File pfile = file.getParentFile();
			if (!pfile.exists()) {
				return pfile.mkdirs();
			}
		}
		return true;
	}

	/**
	 * 读取配置文件生成String
	 * 
	 * @param fileName
	 * @return
	 * @throws X
	 */
	public static String read(String fileName) throws Exception {
		InputStreamReader fin = null;
		String result = "";
		try {
			synchronized (lock) {
				File file = new File(fileName);
				if (file.exists() && file.canRead()) {
					// 文件存在并且可读
					fin = new InputStreamReader(new FileInputStream(file),
							Charset.forName("UTF-8"));

					// byte[] b = new byte[fin.available()];
					// log.debug("fin.available() = " + fin.available());
					// fin.read(b);
					// result = new String(b);

					StringBuffer sb = new StringBuffer();
					char[] b = new char[1024];
					int i = 0;
					while ((i = fin.read(b)) != -1) {
						sb.append(b, 0, i);
					}
					result = sb.toString();

					// log.debug("从配置文件［" + fileName + "］读取的对象！");
				} else {
					throw new Exception("文件［" + fileName + "］不存在或者不可读！");
				}
			}

		} catch (Exception e) {
			throw new Exception("读取配置文件发生异常！详细信息：" + e.getMessage(), e);
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fin = null;
			}
		}
		return result;
	}

	/**
	 * 从json格式的文件中读取生成jsonObject
	 * 
	 * @param fileName
	 * @return
	 * @throws X
	 * @throws
	 */
	public static JSON jsonReadFromFile(String fileName) throws Exception {
		JSON json = null;
		json = objectMap.get(fileName);
		if (json == null) {
			String resultStr = read(fileName);
			// System.out.println("------------+"+resultStr+"+-------");
			// System.out.println("mayBeJSON = "+
			// JSONUtils.mayBeJSON(resultStr));

			if (resultStr.endsWith("\r\n")) {
				// System.out.println("endsWith(\\r\\n)");
				resultStr = resultStr.substring(0, resultStr.length() - 2);
			}
			if (resultStr.endsWith("\n")) {
				// System.out.println("endsWith(\\n)");
				resultStr = resultStr.substring(0, resultStr.length() - 1);
			}
			if (resultStr.endsWith("\r")) {
				// System.out.println("endsWith(\\r)");
				resultStr = resultStr.substring(0, resultStr.length() - 1);
			}

			// System.out.println("------------+"+resultStr+"+-------");
			if (JSONUtils.mayBeJSON(resultStr)) {
				json = JSONSerializer.toJSON(resultStr);
			}
			// System.out.println(json);
			objectMap.put(fileName, json);
			// log.info("从配置文件中读取的对象，并存入内存中！");
		} else {
			// log.info("从Map中读取的对象[" + fileName + "]！");
			// System.out.println("从Map中读取的对象！");
		}
		// System.out.println("json.isArray() = "+json.isArray());
		return json;
	}

	/**
	 * 将jsonObject转换为xmlString
	 * 
	 * @param json
	 * @return
	 */
	public static String jsonObjectToXmlString(JSON json) {
		String result = null;
		result = new XMLSerializer().write(json);
		return result;
	}

	/**
	 * 将xmlString转换为jsonObject
	 * 
	 * @param xml
	 * @return
	 */
	public static JSON xmlStringToJsonObject(String xml) {
		JSON json = new XMLSerializer().read(xml);
		return json;
	}

	/**
	 * 从xml格式文件中读取出JSONObject
	 * 
	 * @param fileName
	 * @return
	 * @throws X
	 */
	public static JSON xmlReadFromFile(String fileName) throws Exception {
		JSON json = null;
		json = objectMap.get(fileName);
		if (json == null) {
			json = xmlStringToJsonObject(read(fileName));
			objectMap.put(fileName, json);
		} else {
			// log.debug("从Map中读取的对象[" + fileName + "]！");
			// System.out.println("从Map中读取的对象！");
		}
		return json;
	}

	/**
	 * 将JSONObject对象以xml格式写入文件
	 * 
	 * @param json
	 * @param fileName
	 * @throws X
	 */
	public static void xmlWriteToFile(JSON json, String fileName)
			throws Exception {

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			synchronized (lock) {
				String tempFileName = fileName;
				if (!isWin) {
					tempFileName += ".swp";
				}
				File file = new File(tempFileName);
				mkdirs(file);
				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				pw.write(jsonObjectToXmlString(json));
				pw.flush();
				if (!isWin) {
					File newFile = new File(fileName);
					// if (isWin) {
					// newFile.delete();
					// }
					boolean b = file.renameTo(newFile);
					if (!b) {
						throw new Exception("将临时文件修改文件名称发生错误！");
					}
				}
			}
			objectMap.remove(fileName);
			// log.debug("-----写配置文件成功！");
		} catch (Exception e) {
			throw new Exception("将json对象写入配置文件发生异常！详细信息：" + e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		// UserEntity user =new UserEntity();
		// user.setUserId("1");
		// user.setUserDesc("sdfsdfsdf");
		// List<UserEntity> listUser = new ArrayList<UserEntity>();
		// listUser.add(user);
		// System.out.println(JSONUtils.isArray(listUser));
		// System.out.println(JSONUtils.isObject(user));

		// JSON jsonObject =FileUtil.objectToJson(listUser);
		// JSONBuilder jb =new JSONBuilder();

		// System.out.println(jsonObject);
		// FileUtil.jsonToObject

		// List list = new ArrayList();
		// list.add("first");
		// list.add("second");
		// list.add(user);
		// JSONArray jsonArray2 = new JSONArray();// .fromObject(list);

		// System.out.println(jsonArray2.addAll(list));
		// System.out.println(jsonArray2.toString());

		/*
		 * // FileUtil.objectMap.remove("dm"); JSONObject json =
		 * FileUtil.jsonReadFromFile("/root/calendarConfig.json");
		 * FileUtil.jsonToObject(json, CalendarConfig.class);
		 * System.out.println(json.toString()); CalendarConfig
		 * c=(CalendarConfig)JSONObject.toBean(json, CalendarConfig.class);
		 * System.out.println(c.getUserList().size());
		 * List<UserEntity>userList=c.getUserList(); for (Iterator iterator =
		 * userList.iterator(); iterator.hasNext();) { if(iterator.next()
		 * instanceof UserEntity) { System.out.println(iterator.next().toString());
		 * UserEntity name = (UserEntity) iterator.next();
		 * System.out.println(name.getUserName()); }else{
		 * System.out.println("-------------------"); } }
		 */
		JSON j = ConfigUtil
				.jsonReadFromFile("/etc/unimas/las/config/systemServiceConfig.json");
		System.out.println(j);
	}
}
