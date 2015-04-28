package cn.dongming8.mmpt.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.config.ConfigUtil;
import cn.dongming8.mmpt.dao.UserDao;
import cn.dongming8.mmpt.entity.UserEntity;
import cn.dongming8.mmpt.service.PermitService;

/**
 * 用户管理
 * 
 * @author Administrator
 * 
 */
public class User {
	private static final Logger log = LoggerFactory.getLogger(User.class);

	/**
	 * 添加用户信息
	 */
	public String add(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		PermitService ps = new PermitService();
		if (ps.permit("user-add")) {
			// 有添加用户的权限
			/**
			 * @String userName;
			 * @String userAliasName;
			 * @String password;
			 * @String passwordSalt;
			 * @List<String> userRoleS;
			 */
			String name = httpReq.getParameter("userName");
			String alias = httpReq.getParameter("userAlias");
			String password = httpReq.getParameter("password");
			String userRoles[] = httpReq.getParameterValues("userRole");
			UserDao udao = new UserDao();
			try {
				udao.add(name, alias, password);

				if ((userRoles.length) != 0) {
					List<String> userRoleS = new ArrayList<String>();
					for (int i = 0; i < userRoles.length; i++) {
						userRoleS.add(userRoles[i]);
					}
					udao.addUserRoleS(name, userRoleS);
				}
				resultMap.put("success", true);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error("用户信息写入配置文件失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "用户信息写入配置文件失败！");
				resultMap.put("error", e.getMessage());
			}
		} else {
			// 没有添加用户的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有添加用户的权限");
		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	/**
	 * 修改用户信息
	 */
	public String update(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		PermitService ps = new PermitService();
		if (ps.permit("user-update")) {
			// 有更新用户的权限
			/**
			 * @String userName;
			 * @String userAliasName;
			 * @String password;
			 * @String passwordSalt;
			 * @List<String> userRoleS;
			 */
			String userName = httpReq.getParameter("userName");
			String userIdStr = httpReq.getParameter("userId");
			int userId = Integer.parseInt(userIdStr);
			try {
				userName = new String(userName.getBytes("ISO-8859-1"), "UTF-8");
			} catch (Exception e1) {
				log.error("查看异常信息", e1);
			}

			String userAlias = httpReq.getParameter("userAlias");
			String userRoles[] = httpReq.getParameterValues("userRole");

			UserDao udao = new UserDao();
			try {

				if (userRoles != null) {
					List<String> userRoleS = new ArrayList<String>();
					for (int i = 0; i < userRoles.length; i++) {
						userRoleS.add(userRoles[i]);
					}
					udao.updateUserRoleS(userId, userRoleS);
				}
				if (StringUtils.isNotEmpty(userAlias)) {
					udao.updateAliasName(userId, userAlias);
				}
				resultMap.put("success", true);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error("更新用户信息写入配置文件失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "更新用户信息写入配置文件失败！");
				resultMap.put("error", e.getMessage());
			}
		} else {
			// 没有更新用户的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有更新用户的权限");
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	/**
	 * 修改用户密码
	 */
	public String updatePassword(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userIdStr = httpReq.getParameter("userId");
		String userName = httpReq.getParameter("userName");
		String oldPassword = httpReq.getParameter("oldPassword");
		String newPassword = httpReq.getParameter("newPassword");

		PermitService ps = new PermitService();
		if (ps.isSelf(userName)) {
			UserDao udao = new UserDao();
			try {
				int userId = Integer.parseInt(userIdStr);
				udao.updatePassword(userId, oldPassword, newPassword);
				resultMap.put("success", true);
				resultMap.put("message", "更新用户密码成功！");
			} catch (Exception e) {
				log.error("更新用户密码失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "更新用户密码失败！");
				resultMap.put("error", e.getMessage());
			}
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	/**
	 * 删除用户信息
	 */
	public String del(HttpServletRequest httpReq) {

		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		PermitService ps = new PermitService();
		if (ps.permit("user-del")) {
			// 有删除用户的权限
			/**
			 * @String userName;
			 * @String userAliasName;
			 * @String password;
			 * @String passwordSalt;
			 * @List<String> userRoleS;
			 */
			String userName = httpReq.getParameter("userName");
			String userNames = null;
			try {
				userNames = new String(userName.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			System.out.println(userNames);
			UserEntity user = new UserEntity();
			user.setName(userNames);
			UserDao udao = new UserDao();
			try {
				udao.del(user);
				resultMap.put("success", true);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error("用户信息写回配置文件失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "用户信息写回配置文件失败");
				resultMap.put("error", e.getMessage());
			}
		} else {
			// 没有删除用户的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有删除用户的权限");
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;

	}

	/**
	 * 获取用户信息
	 */
	public String query(HttpServletRequest httpReq) {

		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userName = httpReq.getParameter("userName");

		PermitService ps = new PermitService();

		if (ps.permit("user-query") || ps.isSelf(userName)) {
			// 有权限
			UserDao udao = new UserDao();
			UserEntity user = udao.getUserByName(userName);
			resultMap.put("success", true);
			resultMap.put("datas", user);

		} else {
			// 没有权限
			resultMap.put("success", false);
			resultMap.put("message", "没有查询用户的权限");
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;

	}

	/**
	 * 获取所有用户信息
	 */
	public String queryAll(HttpServletRequest httpReq) {
		// String start = httpReq.getParameter("start");
		// String limit = httpReq.getParameter("limit");
		// String limitStr = "limit " + start + "," + limit;

		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// log.info("queryAll");
		PermitService ps = new PermitService();
		// log.info(" have user-queryAll" + ps.permit("user-queryAll"));
		if (ps.permit("user-queryAll")) {
			// 有查看全部用户的权限

			UserDao udao = new UserDao();
			List<UserEntity> userList = udao.getUserAll();
			for (UserEntity user : userList) {
				user.setPassword("");
				user.setSalt("");
				;
			}
			resultMap.put("success", true);
			resultMap.put("datas", userList);

		} else {
			// 没有查看全部用户的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有查询用户的权限");
		}
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;

	}

	/**
	 * 
	 * @param httpReq
	 * @return
	 */
	public String getCurrentUser(HttpServletRequest httpReq) {
		PermitService ps = new PermitService();
		String userName = ps.getCurrentUserName();
		UserDao uDao = new UserDao();
		UserEntity user = uDao.getUserByName(userName);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("userId", user.getId());
		resultMap.put("userName", userName);
		resultMap.put("userAlias", user.getAlias());
		return "showUser(" + ConfigUtil.objectToJson(resultMap).toString() + ")";
	}
}
