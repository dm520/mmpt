package cn.dongming8.mmpt.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermitService {
	private static final Logger log = LoggerFactory.getLogger(PermitService.class);

	public boolean login(String userName, String password, boolean rememberMe) {

		Subject subject = SecurityUtils.getSubject();
		// subject.getSession().setTimeout(1000*3600*3);//3小时超时
		// PasswordService
		subject.login(new UsernamePasswordToken(userName, password, rememberMe));
		return subject.isAuthenticated();
	}

	public void logout() {

		Subject subject = SecurityUtils.getSubject();
		subject.logout();
	}

	public boolean permit(String permitStr) {
		boolean result = false;

		// List<String> tableNameS = SqlParserUtil.getTableList(sql);
		Subject currentUser = SecurityUtils.getSubject();

		if (result = (currentUser.isPermitted(permitStr))) {
			log.info("有{}权限", permitStr);
		} else {
			log.info("没有{}权限", permitStr);
		}

		return result;
	}

	/**
	 * 判断当前用户是否有该角色
	 * 
	 * @param roleIdentifier
	 * @return
	 */
	public boolean hasRole(String roleIdentifier) {
		boolean result = false;

		// List<String> tableNameS = SqlParserUtil.getTableList(sql);
		Subject currentUser = SecurityUtils.getSubject();

		if (result = currentUser.hasRole(roleIdentifier)) {
			// log.info("是{}角色", roleIdentifier);
		} else {
			// log.info("是{}角色", roleIdentifier);
		}

		return result;
	}

	/**
	 * 判断是不是当前用户
	 * 
	 * @param userName
	 * @return
	 */
	public boolean isSelf(String userName) {
		boolean result = false;
		Subject currentUser = SecurityUtils.getSubject();
		String currentUserName = (String) currentUser.getPrincipal();
		if (result = userName.equals(currentUserName)) {
			log.info("{}是当前用户", userName);
		} else {
			log.info("{}不是当前用户", userName);
		}
		return result;
	}

	public String getCurrentUserName() {
		Subject currentUser = SecurityUtils.getSubject();
		String currentUserName = (String) currentUser.getPrincipal();
		return currentUserName;
	}

	public void addPerToCurrentUser(String permit) {
		Subject currentUser = SecurityUtils.getSubject();
		String currentUserName = (String) currentUser.getPrincipal();

	}
}
