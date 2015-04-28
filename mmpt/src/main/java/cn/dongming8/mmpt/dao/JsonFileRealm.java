package cn.dongming8.mmpt.dao;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.entity.Role;
import cn.dongming8.mmpt.entity.User;

public class JsonFileRealm extends AuthorizingRealm {

	private static final Logger log = LoggerFactory.getLogger(JsonFileRealm.class);


	public enum SaltStyle {
		NO_SALT, CRYPT, COLUMN, EXTERNAL
	};

	protected SaltStyle saltStyle = SaltStyle.EXTERNAL;

	public JsonFileRealm() {
		super();
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}

		String username = (String) getAvailablePrincipal(principals);

		List<String> userRoleNameS = null;
		Set<String> permissions = null;
		Set<String> roleNames = new LinkedHashSet<String>();

		try {

			// Retrieve roles and permissions from database
			userRoleNameS = getUserRoleSForUser(username);

			for (String userRoleName : userRoleNameS) {
				roleNames.add(userRoleName);
			}
			// if (permissionsLookupEnabled) {
			permissions = getPermissions(userRoleNameS);
			// }

		} catch (Exception e) {
			final String message = "There was a error while authorizing user [" + username + "]";
			if (log.isErrorEnabled()) {
				log.error(message, e);
			}

			throw new AuthorizationException(message, e);
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
		info.setStringPermissions(permissions);

		return info;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		if (username == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}

		SimpleAuthenticationInfo info = null;

		char[] password = upToken.getPassword();
		String salt = null;
		switch (saltStyle) {
		case NO_SALT:
			//password = getPassForUser(username)[0];
			break;
		case CRYPT:
			// TODO: separate password and hash from getPasswordForUser[0]
			throw new ConfigurationException("Not implemented yet");
			// break;
		case COLUMN:
			String[] queryResults = getPassForUser(username);
			//password = queryResults[0];
			salt = queryResults[1];
			break;
		case EXTERNAL:
			String[] passAndSalt = getPassForUser(username);
			//password = passAndSalt[0];
			salt = passAndSalt[1];
			//System.out.println("password = "+password);
			//System.out.println("salt = "+salt);
			break;
		}

		if (password == null) {
			throw new UnknownAccountException("No account found for user [" + username + "]");
		}

		info = new SimpleAuthenticationInfo(username, password, getName());

		if (salt != null) {
			log.info("salt = {}",salt);
			info.setCredentialsSalt(ByteSource.Util.bytes(salt));
		}

		return info;
	}

	/**
	 * Sets the salt style. See {@link #saltStyle}.
	 * 
	 * @param saltStyle
	 *            new SaltStyle to set.
	 */
	public void setSaltStyle(SaltStyle saltStyle) {
		this.saltStyle = saltStyle;

	}

	private String[] getPassForUser(String userName) {

		String[] result = new String[2];
		UserDao ud = new UserDao();
		User user = ud.getUserByName(userName);
		result[0] = user.getPassword();
		result[1] = user.getSalt() ;
		return result;
	}

	protected List<String> getUserRoleSForUser(String userName) throws Exception {
		UserDao ud = new UserDao();
		User user = ud.getUserByName(userName);
		List<String> userRoleNameS = user.getRoleS();
		return userRoleNameS;
	}

	protected Set<String> getPermissions(Collection<String> roleNames) {
		Set<String> permissions = new LinkedHashSet<String>();
		for (String roleName : roleNames) {
			RoleDao urd = new RoleDao();
			Role userRole = urd.getRoleByName(roleName);
			List<String> rolePermissionIdS = userRole.getPermissionS();
			for (String permissionId : rolePermissionIdS) {
				permissions.add(permissionId);
			}
		}
		return permissions;
	}
}
