package com.dongming8.mmpt.dao;

import java.util.List;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongming8.mmpt.commons.Constants;
import com.dongming8.mmpt.commons.config.JSONManager;
import com.dongming8.mmpt.entity.Role;
import com.dongming8.mmpt.entity.User;

public class UserDao {

	private static final Logger log = LoggerFactory.getLogger(UserDao.class);

	protected static final String USERS_FILE = Constants.CONFIG_PATH + "users.json";
	private static int id = 0;// 用户递增ID

	private void write(List<User> userS) throws Exception {
		JSONManager json = new JSONManager();
		json.write(USERS_FILE, userS);
	}

	private List<User> read() throws Exception {
		JSONManager json = new JSONManager();
		@SuppressWarnings("unchecked")
		List<User> userS = (List<User>) json.read(USERS_FILE, User.class);
		return userS;
	}

	public List<User> getUserAll() {
		try {
			return this.read();
		} catch (Exception e) {
			log.error("获取全部用户信息失败！", e);
		}
		return null;
	}

	/**
	 * 获取用户的递增ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextId() throws Exception {
		synchronized (UserDao.class) {
			if (id == 0) {
				List<User> userList = this.read();
				for (User user : userList) {
					if (id < user.getId()) {
						id = user.getId();
					}
				}
			}
			return ++id;
		}
	}

	/**
	 * 获取密码盐
	 * 
	 * @param userName
	 * @return
	 */
	public String getNextSalt(String userName) {
		SecureRandomNumberGenerator srng = new SecureRandomNumberGenerator();
		String salt = srng.nextBytes().toHex();
		salt += userName;
		return salt;
	}

	/**
	 * 得到加密后的密码
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	public String getNextPass(String password, String salt) {
		return new Md5Hash(password, salt, 2).toBase64();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getUserById(int id) {

		List<User> userS = null;
		User result = null;
		try {
			userS = this.read();
			for (User user : userS) {
				if (user != null && id == user.getId()) {
					result = user;
					break;
				}
			}
		} catch (Exception e) {
			log.error("获取id={}的用户信息失败！", id, e);
		}
		return result;

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public User getUserByName(String name) {

		List<User> userS = null;
		User result = null;
		try {
			userS = this.read();
			for (User user : userS) {
				if (user != null && name.equals(user.getName())) {
					result = user;
					break;
				}
			}
		} catch (Exception e) {
			log.error("获取{}用户信息失败！", name, e);
		}
		return result;
	}

	/**
	 * 
	 * @param id
	 * @param userRole
	 * @throws Exception
	 */
	public void addRole(int id, Role userRole) throws Exception {
		User user = this.getUserById(id);
		if (user != null) {
			List<String> userRoles = user.getRoleS();
			if (userRoles != null && !userRoles.contains(userRole)) {
				userRoles.add(userRole.getName());
				this.update(id, user);
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @param userRole
	 * @throws Exception
	 */
	public void addRole(User user, Role userRole) throws Exception {
		this.addRole(user.getId(), userRole);
	}

	/**
	 * 
	 * @param userName
	 * @param alias
	 * @param password
	 * @throws Exception
	 */
	public void add(String name, String alias, String password) throws Exception {
		if (this.getUserByName(name) == null) {
			User user = new User();
			String salt = getNextSalt(name);
			String passCipherText = getNextPass(password, salt);
			user.setName(name);
			user.setPassword(passCipherText);
			user.setSalt(salt);
			user.setAlias(alias);
			user.setId(getNextId());
			this.add(user);
		} else {
			throw new Exception("用户名已经存在，请修改后重试！");
		}
	}

	/**
	 * 判断是否已经存在该用户
	 * 
	 * @param userS
	 * @param user
	 * @return
	 */
	public boolean isHaveUser(List<User> userS, User user) {
		boolean result = false;
		for (User tmpUser : userS) {
			if (tmpUser.getId() == user.getId()) {
				result = true;
				break;
			} else if (tmpUser.getName().equals(user.getName())) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	private void add(User user) throws Exception {
		List<User> userS = this.read();
		if (userS != null) {
			if (isHaveUser(userS, user)) {
				throw new Exception("该用户名已经存在,请修改后重试！");
			} else {
				userS.add(user);
				this.write(userS);
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void del(User user) throws Exception {
		List<User> userS = this.read();
		if (userS != null) {
			if (!userS.contains(user)) {
				// 没有该用户数据
			} else {
				userS.remove(user);
				this.write(userS);
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void del(int id) throws Exception {
		this.del(new User(id));
	}

	private void update(int id, User user) throws Exception {
		List<User> userS = this.read();
		User temp = new User(id);
		if (userS != null) {
			if (!userS.contains(temp)) {
				// 没有该用户数据
			} else {
				userS.remove(temp);
			}
			userS.add(user);
			this.write(userS);
		}
	}

	/**
	 * 更新别名
	 * 
	 * @param userName
	 * @param userAliasName
	 * @throws Exception
	 */
	public void updateAliasName(int id, String userAliasName) throws Exception {
		User user = this.getUserById(id);
		user.setAlias(userAliasName);
		this.update(id, user);
	}

	/**
	 * 
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public boolean isAuthenticat(int id, String password) throws Exception {
		User user = this.getUserById(id);
		String pass = user.getPassword();
		String salt = user.getSalt();
		String passwordText = this.getNextPass(password, salt);// 旧密码
		return pass.equals(passwordText);
	}

	/**
	 * 修改密码
	 * 
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public void updatePassword(int id, String oldPassword, String newPassword) throws Exception {

		if (isAuthenticat(id, oldPassword)) {
			User user = this.getUserById(id);
			String userName = user.getName();
			String salt = this.getNextSalt(userName);
			String passCipherText = this.getNextPass(newPassword, salt);
			user.setPassword(passCipherText);
			user.setSalt(salt);
			this.update(id, user);
		} else {
			throw new Exception("输入的原始密码错误！");
		}
	}

	/**
	 * 更新用户角色
	 * 
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public void updateUserRoleS(int id, List<String> userRoleS) throws Exception {
		User user = this.getUserById(id);
		user.setRoleS(userRoleS);
		this.update(id, user);
	}

	public void addUserRoleS(String name, List<String> userRoleS) throws Exception {
		User user = this.getUserByName(name);
		user.setRoleS(userRoleS);
		this.update(id, user);
	}

	public void delAll() throws Exception {
		List<User> userS = this.read();
		if (userS != null) {
			userS.clear();
			this.write(userS);
		}
	}

	public static void main(String[] args) throws Exception {
		String userName = "admin";
		String password = "123456";
		String alias = "dm520";
		UserDao ud = new UserDao();
		ud.add(userName,alias,password);
		userName = "unimas";
		ud.add(userName,alias,password);
	}
}
