package cn.dongming8.mmpt.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.config.ConfigUtil;
import cn.dongming8.mmpt.service.PermitService;

public class LoginAction {
	private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

	/**
	 * 用户登录Action
	 * 
	 * @param httpReq
	 * @return
	 */
	public String login(HttpServletRequest httpReq) {
		String userName = httpReq.getParameter("userName");
		String password = httpReq.getParameter("password");
		String rememberMeStr = httpReq.getParameter("rememberMe");

		String result = "";
		PermitService ps = new PermitService();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(userName)) {
			resultMap.put("success", false);
			resultMap.put("message", "用户名为空！");
		} else if (StringUtils.isEmpty(password)) {
			resultMap.put("success", false);
			resultMap.put("message", "密码为空！");
		} else {
			boolean loginSuccess = false;
			boolean rememberMe = false;
			if (StringUtils.isNotEmpty(rememberMeStr)) {
				rememberMe = Boolean.parseBoolean(rememberMeStr);
			}
			try {
				loginSuccess = ps.login(userName, password, rememberMe);
				if (loginSuccess) {
					resultMap.put("success", true);
				} else {
					resultMap.put("success", false);
					resultMap.put("message", "登录失败，用户名或密码错误！");
				}
			} catch (Exception e) {
				log.error("", e);
				resultMap.put("success", false);
				resultMap.put("message", "登录失败，用户名或密码错误！");
			}
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}
}
