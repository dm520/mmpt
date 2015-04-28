package cn.dongming8.mmpt.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.Constants;
import cn.dongming8.mmpt.commons.config.ConfigUtil;
import cn.dongming8.mmpt.commons.config.JSONManager;
import cn.dongming8.mmpt.entity.Permission;
import cn.dongming8.mmpt.entity.Role;

/**
 * 
 * @author administrator
 * @time 2015年4月3日 下午4:53:25
 */
public class RoleDao {

	private static final Logger log = LoggerFactory.getLogger(RoleDao.class);
	protected static final String ROLES_FILE = Constants.CONFIG_PATH + "roles.json";

	private void write(List<Role> roleS) throws Exception {
		JSONManager json = new JSONManager();
		json.write(ROLES_FILE, roleS);
	}

	private List<Role> read() throws Exception {
		JSONManager json = new JSONManager();
		@SuppressWarnings("unchecked")
		List<Role> userRoleS = (List<Role>) json.read(ROLES_FILE, Role.class);
		return userRoleS;
	}

	// add by llf
	public List<Role> getRoleAll() {

		try {
			return this.read();
		} catch (Exception e) {
			log.error("获取全部用户信息失败！", e);
		}
		return null;
	}

	public List<Map<String, Object>> getRoleAndPermisstAll() {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			List<Role> userRoles = this.read();
			PermissionDao pDao = new PermissionDao();
			for (Role userRole : userRoles) {
				List<String> permissionIdList = userRole.getPermissionS();
				List<Permission> permissionList = new ArrayList<Permission>();
				Map<String, Object> userRoleMap = new HashMap<String, Object>();
				userRoleMap.put("roleName", userRole.getName());
				userRoleMap.put("rolePermissionS", permissionList);
				for (String permissionId : permissionIdList) {
					if (!"".equals(permissionId)) {
						Permission permission = pDao.getPermissionById(permissionId);
						if (permission != null) {
							permissionList.add(permission);
						}/*
						 * else { for (int i = 0; i <
						 * Constants.TABLE_TYPE.length; i++) {
						 * if(Constants.TABLE_TYPE[i].equals(permissionId)){
						 * log.info("----------------------------------");
						 * log.info("permissionId = " + permissionId);
						 * 
						 * //属于数据源分类 Permission perType =new
						 * Permission(permissionId); perType.setChecked(true);
						 * perType.setName(Constants.TABLE_TYPE_NAME[i]);
						 * perType.setType(2);//碰撞数据源类型
						 * permissionList.add(perType); break; } }
						 * 
						 * }
						 */
					}
				}

				resultList.add(userRoleMap);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return resultList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Role getRoleByName(String roleName) {

		List<Role> userRoleS = null;
		Role result = null;
		try {
			userRoleS = this.read();
			for (Role userRole : userRoleS) {
				if (userRole != null && roleName.equals(userRole.getName())) {
					result = userRole;
					break;
				}
			}
		} catch (Exception e) {
			log.error("获取{}角色失败！", roleName, e);
		}
		return result;

	}

	/**
	 * 
	 * @param roleName
	 * @param permission
	 * @throws X
	 */
	public void addPermission(String roleName, Permission permission) throws Exception {
		this.addPermission(roleName, permission.getId());
	}

	/**
	 * 
	 * @param roleName
	 * @param permissionId
	 * @throws X
	 */
	public void addPermission(String roleName, String permissionId) throws Exception {
		List<Role> userRoleS = this.read();
		if (userRoleS != null) {
			for (Role userRole : userRoleS) {
				if (roleName.equals(userRole.getName())) {
					List<String> permissionS = userRole.getPermissionS();
					if (permissionS != null && !permissionS.contains(permissionId)) {
						permissionS.add(permissionId);
						this.write(userRoleS);
					}
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param roleName
	 * @param permissionId
	 * @throws X
	 */
	public void addPerType(String roleName, String perType) throws Exception {

		Role userRole = getRoleByName(roleName);
		List<String> rolePermissionS = userRole.getPermissionS();
		if (!rolePermissionS.contains(perType)) {
			rolePermissionS.add(perType);
		}
		this.update(roleName, userRole);
	}

	/**
	 * 
	 * @param roleName
	 * @param permissionId
	 * @throws X
	 */
	public void delPerType(String roleName, String perType) throws Exception {

		Role userRole = getRoleByName(roleName);
		List<String> rolePermissionS = userRole.getPermissionS();
		if (rolePermissionS.contains(perType)) {
			rolePermissionS.remove(perType);
		}
		this.update(roleName, userRole);
	}

	/**
	 * 
	 * @param roleName
	 * @param permission
	 * @throws X
	 */
	public void delPermission(String roleName, Permission permission) throws Exception {
		this.delPermission(roleName, permission.getId());
	}

	/**
	 * 
	 * @param roleName
	 * @param permissionId
	 * @throws X
	 */
	public void delPermission(String roleName, String permissionId) throws Exception {
		List<Role> userRoleS = this.read();
		if (userRoleS != null) {
			for (Role userRole : userRoleS) {
				if (roleName.equals(userRole.getName())) {
					List<String> permissionS = userRole.getPermissionS();
					if (permissionS != null && permissionS.contains(permissionId)) {
						permissionS.remove(permissionId);
						this.write(userRoleS);
					}
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param userRole
	 * @throws X
	 */
	public void add(Role userRole) throws Exception {
		List<Role> userRoleS = this.read();
		if (userRoleS != null) {
			if (userRoleS.contains(userRole)) {
				throw new Exception("该角色已经存在,请勿重新添加！");
			} else {
				userRoleS.add(userRole);
				this.write(userRoleS);
			}
		}
	}

	public void del(Role userRole) throws Exception {
		List<Role> userRoleS = this.read();
		if (userRoleS != null) {
			if (!userRoleS.contains(userRole)) {
				// 没有该角色数据

			} else {
				userRoleS.remove(userRole);
				this.write(userRoleS);
			}
		}
	}

	public void update(String roleName, Role userRole) throws Exception {
		List<Role> userRoleS = this.read();
		Role temp = new Role(roleName);
		if (userRoleS != null) {
			if (!userRoleS.contains(temp)) {
				// 没有该角色数据
			} else {
				userRoleS.remove(temp);
			}
			userRoleS.add(userRole);
			this.write(userRoleS);
		}
	}

	public void delAll() throws Exception {
		List<Role> userRoleS = this.read();
		if (userRoleS != null) {
			userRoleS.clear();
			this.write(userRoleS);
		}
	}

	/**
	 * 从远程更新角色和权限关系
	 * 
	 * @param url
	 * @return
	 */
	public boolean updateRolePz(String resultStr) {
		try {
			log.debug("更新角色和权限关系，data={}！", resultStr);
			JSON resultJson = ConfigUtil.objectToJson(resultStr);

			JSONArray resultJsonArr = new JSONArray();
			if (resultJson.isArray()) {
				resultJsonArr = (JSONArray) resultJson;
			}
			List<Role> userRoleS = new ArrayList<Role>();
			for (int i = 0; i < resultJsonArr.size(); i++) {
				JSONObject resultObj = resultJsonArr.getJSONObject(i);
				String role = resultObj.getString("role");
				String permissionStr = resultObj.getString("keys");
				String[] permissionArr = StringUtils.split(permissionStr, ",");
				Role userRole = new Role();
				userRole.setName(role);
				userRole.setPermissionS(Arrays.asList(permissionArr));
				userRoleS.add(userRole);
			}
			if (userRoleS != null && userRoleS.size() > 0)
				this.write(userRoleS);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		PermissionDao pd = new PermissionDao();
		RoleDao urd = new RoleDao();
		Role r1 = new Role();
		r1.setName("普通民警");
		Role r2 = new Role();
		r2.setName("科长");
		Role r3 = new Role();
		r3.setName("处长");
		r1.setPermissionS(pd.getPermissionIdByType(1));
		r3.setPermissionS(pd.getPermissionIdByType(2));
		urd.delAll();
		urd.add(r1);
		urd.add(r2);
		urd.add(r3);
		// pd.update("loginlogout", p2);
		// pd.getRoleByType(1);
	}

}
