package cn.dongming8.mmpt.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.Constants;
import cn.dongming8.mmpt.commons.config.JSONManager;
import cn.dongming8.mmpt.entity.Permission;

public class PermissionDao {

	private static final Logger log = LoggerFactory.getLogger(PermissionDao.class);
	
	protected static final String PERMISSIONS_FILE = Constants.CONFIG_PATH
			+ "permission.json";

	private void write(List<Permission> permissionS) throws Exception {
		JSONManager json = new JSONManager();
		json.write(PERMISSIONS_FILE, permissionS);
	}

	private List<Permission> read() throws Exception {
		JSONManager json = new JSONManager();
		@SuppressWarnings("unchecked")
		List<Permission> permissionS = (List<Permission>) json.read(PERMISSIONS_FILE, Permission.class);
		return permissionS;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public List<Permission> getPermissionByType(int type) {
		List<Permission> permissionS = null;
		List<Permission> result = new ArrayList<Permission>();
		try {
			permissionS = this.read();
			for (Permission permission : permissionS) {
				if (permission != null && type == permission.getType()) {
					result.add(permission);
				}
			}

		} catch (Exception e) {
			log.error("获取类型{}下面的权限失败！", type, e);
		}
		return result;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public List<String> getPermissionIdByType(int type) {
		List<Permission> permissionS = null;
		List<String> result = new ArrayList<String>();
		try {
			permissionS = this.read();
			for (Permission permission : permissionS) {
				if (permission != null && type == permission.getType()) {
					result.add(permission.getId());
				}
			}

		} catch (Exception e) {
			log.error("获取类型{}下面的权限失败！", type, e);
		}
		return result;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */

	public int getPermissionTypeById(String id) {
		int result = 0;
		Permission permission = getPermissionById(id);
		// System.out.println(permission);
		if (permission == null || "null".equals(permission)) {
			result = 5;

		} else {
			result = permission.getType();

		}
		return result;
	}

	public List<Permission> getPermissionAll() throws Exception {
		return this.read();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Permission getPermissionById(String id) {

		List<Permission> permissionS = null;
		Permission result = null;
		try {
			permissionS = this.read();
			for (Permission permission : permissionS) {
				if (permission != null
						&& id.equalsIgnoreCase(permission.getId())) {
					result = permission;
					break;
				}
			}
		} catch (Exception e) {
			log.error("获取ID={}的权限失败！", id, e);
		}
		return result;

	}

	/**
	 * 
	 * @param permission
	 * @throws X
	 */
	public void add(Permission permission) throws Exception {
		List<Permission> permissionS = this.read();
		if (permissionS != null) {
			if (permissionS.contains(permission)) {
				throw new Exception("该权限已经存在,请勿重新添加！");
			} else {
				permissionS.add(permission);
				this.write(permissionS);
			}
		}
	}

	public void del(Permission permission) throws Exception {
		List<Permission> permissionS = this.read();
		if (permissionS != null) {
			if (!permissionS.contains(permission)) {
				// 没有该权限数据

			} else {
				permissionS.remove(permission);
				this.write(permissionS);
			}
		}
	}

	public void update(String id, Permission permission) throws Exception {
		List<Permission> permissionS = this.read();
		Permission temp = new Permission(id);
		if (permissionS != null) {
			if (!permissionS.contains(temp)) {
				// 没有该权限数据
			} else {
				permissionS.remove(temp);
			}
			permissionS.add(permission);
			this.write(permissionS);
		}
	}

	public void delAll() throws Exception {
		List<Permission> permissionS = this.read();
		if (permissionS != null) {
			permissionS.clear();
			this.write(permissionS);
		}
	}

	public static void main(String[] args) throws Exception {

		// Permission p1 = new Permission();
		// p1.setId("loginlogout");
		// p1.setName("登录登出");
		// p1.setType(1);
		// Permission p2 = new Permission();
		// p2.setId("pz");
		// p2.setName("碰撞全部数据源");
		// p2.setType(2);
		// Permission p3 = new Permission();
		// p3.setId("person");
		// p3.setName("人相关数据源");
		// p3.setType(2);
		// p3.setTableType(1);;
		// pd.delAll();
		// pd.add(p1);
		// pd.add(p2);
		// pd.add(p3);
		// pd.update("loginlogout", p2);
		// pd.getPermissionByType(1);
		System.out.println("----");
		PermissionDao pd = new PermissionDao();
		List<Permission> pdList = pd.getPermissionAll();
		System.out.println(pdList);

	}

}
