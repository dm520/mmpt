package cn.dongming8.mmpt.dao;

import java.util.List;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.Constants;
import cn.dongming8.mmpt.commons.config.JSONManager;
import cn.dongming8.mmpt.entity.RoleEntity;
import cn.dongming8.mmpt.entity.UserEntity;

public class UserDao {

	private static final Logger log = LoggerFactory.getLogger(UserDao.class);

	protected static final String USERS_FILE = Constants.CONFIG_PATH + "users.json";
	private static int id = 0;// 用户递增ID

	private void write(List<UserEntity> userS) throws Exception {
		JSONManager json = new JSONManager();
		json.write(USERS_FILE, userS);
	}

	private List<UserEntity> read() throws Exception {
		JSONManager json = new JSONManager();
		@SuppressWarnings("unchecked")
		List<UserEntity> userS = (List<UserEntity>) json.read(USERS_FILE, UserEntity.class);
		return userS;
	}

	public List<UserEntity> getUserAll() {
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
				List<UserEntity> userList = this.read();
				for (UserEntity user : userList) {
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
	public UserEntity getUserById(int id) {

		List<UserEntity> userS = null;
		UserEntity result = null;
		try {
			userS = this.read();
			for (UserEntity user : userS) {
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
	public UserEntity getUserByName(String name) {

		List<UserEntity> userS = null;
		UserEntity result = null;
		try {
			userS = this.read();
			for (UserEntity user : userS) {
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
	public void addRole(int id, RoleEntity userRole) throws Exception {
		UserEntity user = this.getUserById(id);
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
	public void addRole(UserEntity user, RoleEntity userRole) throws Exception {
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
			UserEntity user = new UserEntity();
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
	public boolean isHaveUser(List<UserEntity> userS, UserEntity user) {
		boolean result = false;
		for (UserEntity tmpUser : userS) {
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
	private void add(UserEntity user) throws Exception {
		List<UserEntity> userS = this.read();
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
	public void del(UserEntity user) throws Exception {
		List<UserEntity> userS = this.read();
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
		this.del(new UserEntity(id));
	}

	private void update(int id, UserEntity user) throws Exception {
		List<UserEntity> userS = this.read();
		UserEntity temp = new UserEntity(id);
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
		UserEntity user = this.getUserById(id);
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
		UserEntity user = this.getUserById(id);
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
			UserEntity user = this.getUserById(id);
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
		UserEntity user = this.getUserById(id);
		user.setRoleS(userRoleS);
		this.update(id, user);
	}

	public void addUserRoleS(String name, List<String> userRoleS) throws Exception {
		UserEntity user = this.getUserByName(name);
		user.setRoleS(userRoleS);
		this.update(id, user);
	}

	public void delAll() throws Exception {
		List<UserEntity> userS = this.read();
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
