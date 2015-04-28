package cn.dongming8.mmpt.commons;

import java.io.File;

public class Constants {

	/**
	 * 判断操作系统
	 */
	public static boolean isWin = "\\".equals(File.separator);
	/**
	 * 根目录
	 */
	public static final String ROOT = (isWin ? "C:" : "") + File.separator;
	/**
	 * 系统路径标志
	 */
	public static final String APP_ROOT = File.separator + "unimas" + File.separator + "ufs" + File.separator;
	/**
	 * 配置文件路径
	 */
	public static final String CONFIG_PATH = ROOT + "etc" + APP_ROOT + "config" + File.separator;
	/**
	 * 下载文件路径
	 */
	public static final  String DOWNLOAD_PATH = ROOT + "var" + APP_ROOT + "download" + File.separator;
	/**
	 * 上传文件路径
	 */
	public static final String UPLOAD_PATH = ROOT + "var" + APP_ROOT + "upload" + File.separator;

}
