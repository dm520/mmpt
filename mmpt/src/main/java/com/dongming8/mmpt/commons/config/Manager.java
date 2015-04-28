package com.dongming8.mmpt.commons.config;

/**
 * 配置文件的读写操作
 * 
 * @date 2009-09-28
 * @version 1.0
 * @author dm
 *
 */
public interface Manager {
	/**
	 * 读配置文件
	 * 
	 * @param fileName
	 *            绝对文件路径
	 * @param c
	 * @return 该配置文件的对应的Object
	 */
	public Object read(String fileName, Class<?> c) throws Exception;

	/**
	 *
	 * 写配置文件
	 * 
	 * @param fileName
	 *            文件名字 绝对文件路径
	 * @param o
	 * @throws X
	 */
	public void write(String fileName, Object o) throws Exception;

}
