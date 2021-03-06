package cn.dongming8.mmpt.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.shiro.web.servlet.ServletContextSupport;
//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionFilter extends ServletContextSupport implements Filter {

	private static final Logger log = LoggerFactory.getLogger(ActionFilter.class);
	private static Map<String, Object> actionObjectMap = new HashMap<String, Object>();
	private static String pathStr = null;

	public void destroy() {
		log.info("destroy");
		actionObjectMap = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		String servletPath = httpReq.getServletPath();
		log.info("servletPath = " + servletPath);
		if (httpReq.getCharacterEncoding() != null) {
			// System.out.println(httpReq.getCharacterEncoding());
			log.info(httpReq.getCharacterEncoding());
		} else {
			httpReq.setCharacterEncoding("UTF-8");// //设置request的编码
		}
		// String scheme = request.getScheme();
		// String getServerName = request.getServerName();
		// String url = httpReq.getRequestURI();
		// String contextPath = httpReq.getContextPath();
		// System.out.println(url);
		// System.out.println(contextPath);
		// System.out.println(scheme);
		// System.out.println(getServerName);

		// get action Object
		Object actionObject = actionObjectMap.get(servletPath);
		// do method
		String method = httpReq.getParameter("method");
		log.info("servletPath = " + servletPath + "; method = " + method);

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		if (actionObject != null) {
			if (method != null) {
				Method m;
				try {
					// if("login".equals(method)){
					// String loginFlag = httpReq.getParameter("loginFlag");
					// String dateStr = httpReq.getParameter("dateStr");
					// String reUrl = httpReq.getParameter("reUrl");
					// if(StringUtils.isNotEmpty(loginFlag)){
					// String result = new MD5().getMD5ofStr(dateStr+"unimas");
					// if(result.equalsIgnoreCase(loginFlag)){
					// PermitService ps = new PermitService();
					// ps.login("admin", "unimas");
					// if(StringUtils.isEmpty(reUrl) ||
					// "compare".equalsIgnoreCase(reUrl)){
					// reUrl = httpReq.getContextPath() +
					// "/app/compare/module.html";
					// }else if("index".equalsIgnoreCase(reUrl)){
					// reUrl = httpReq.getContextPath() +
					// "/app/index/module.html";
					// }else if("zjst".equalsIgnoreCase(reUrl)){
					// reUrl = httpReq.getContextPath() +
					// "/app/zjst/module.html";
					// }
					// httpRes.sendRedirect(reUrl);
					// }
					// }
					// }
					if ("fileUpload".equals(method) || "resultDownload".equals(method)) {
						try {
							// response.setContentType("application/json");
							m = actionObject.getClass().getMethod(method, HttpServletRequest.class,
									HttpServletResponse.class);
							m.invoke(actionObject, httpReq, httpRes);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("上传下载失败！", e);
						}
						return;
					}
					m = actionObject.getClass().getMethod(method, HttpServletRequest.class);
					String result = (String) m.invoke(actionObject, httpReq);

					log.info("servletPath=" + servletPath);
					log.info("method=" + method);
					// log.info("result=" + result);
					// response.setContentType("application/json");
					response.getWriter().write(result);
					// }

				} catch (Exception e) {
					log.error("执行{}的{}方法出错", httpReq.getRequestURI(), method, e);
					response.getWriter().write(
							"{\"success\":false,\"message\":\"执行请求错误！\",\"error\":" + e.getMessage() + "}");
				}
			} else {
				log.error("请求{}里面方法名为空！", httpReq.getRequestURI());
				response.getWriter().write("{\"success\":false,\"message\":\"请求的方法不存在！\"}");

			}
		} else {
			log.error("未找到请求{}里面的执行类！", httpReq.getRequestURI());
			response.getWriter().write("{\"success\":false,\"message\":\"请求的执行类不存在！\"}");
		}
		response.flushBuffer();
	}

	/**
	 * 
	 * @param rootFile
	 * @return
	 */
	private File getActionFile(File rootFile) {
		Collection<File> fileList = FileUtils.listFilesAndDirs(rootFile, TrueFileFilter.TRUE, TrueFileFilter.TRUE);
		//System.out.println(fileList);
		for (File file : fileList) {
			if (file.isDirectory() && file.getName().equals("action")) {
				return file;
			}
		}
		return null;
	}

	public void init(FilterConfig config) throws ServletException {
		log.info("init action class");
		URL url = this.getClass().getClassLoader().getResource("");
		String rootPath = url.getPath();
		File rootFile = new File(rootPath);
		File actionFile = getActionFile(rootFile);
		log.info("rootPath = {}", rootPath);
		log.info("Instance action class");
		log.info("actionFile = {}", actionFile);
		pathStr = actionFile.getAbsolutePath().split("classes\\"+File.separator)[1].replaceAll(File.separator, ".")+".";
		log.info("pathStr = {}", pathStr);
		this.getActionObject(actionFile, actionObjectMap);
	}

	private void getActionObject(File file, Map<String, Object> actionObjectMap) {
		if (file != null) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					this.getActionObject(files[i], actionObjectMap);
				}
			} else if (file.isFile()) {
				String filePath = file.getAbsolutePath();
				String[] filePathArr = filePath.split("\\" + File.separator + "action" + "\\" + File.separator);
				if (filePathArr != null && filePathArr.length == 2) {
					String actionFileName = filePathArr[1].substring(0, filePathArr[1].length() - 6);
					
					String actionKey = "/action/"+actionFileName.replace(File.separator, "/") + ".do";
					actionFileName = actionFileName.replace(File.separator, ".");
					//System.out.println("actionFileName = " + actionFileName);
					//System.out.println("actionKey = " + actionKey);
					actionObjectMap.put(actionKey, this.InstanceObject(pathStr + actionFileName));
				}
			}
		}
	}

	/**
	 * 实例化action对象
	 * 
	 * @param className
	 * @return
	 */
	private Object InstanceObject(String className) {
		log.info("className = " + className);
		Object result = null;
		try {
			Class<?> cLassObject = Class.forName(className);
			result = cLassObject.newInstance();
		} catch (ClassNotFoundException e) {
			log.error("", e);
		} catch (InstantiationException e) {
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
		}
		return result;
	}
}
