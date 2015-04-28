package com.dongming8.mmpt.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongming8.mmpt.commons.config.ConfigUtil;
import com.dongming8.mmpt.service.PermitService;

public class LogoutAction {
	private static final Logger log = LoggerFactory.getLogger(LogoutAction.class);

	/**
	 * 用户登出
	 * 
	 * @param httpReq
	 * @return
	 */
	public String logout(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			PermitService ps = new PermitService();
			ps.logout();
			resultMap.put("success", true);
		} catch (Exception e) {
			log.error("用户退出失败!", e);
			resultMap.put("success", false);
			resultMap.put("message", "用户退出失败!");
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}
}
