package cn.dongming8.mmpt.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.config.ConfigUtil;
import cn.dongming8.mmpt.dao.PermissionDao;
import cn.dongming8.mmpt.dao.RoleDao;
import cn.dongming8.mmpt.entity.PermissionEntity;
import cn.dongming8.mmpt.entity.RoleEntity;
import cn.dongming8.mmpt.service.PermitService;

/**
 * 角色管理
 * 
 * @author Administrator
 * 
 */
public class Role {
	private static final Logger log = LoggerFactory.getLogger(Role.class);

	/**
	 * 添加角色信息
	 */
	public String add(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		PermitService ps = new PermitService();
		if (ps.permit("role-add")) {
			// 有添加角色并赋予权限
			String roleName = httpReq.getParameter("rolename");
			String rolelist = httpReq.getParameter("roleList");
			List<String> list = new ArrayList<String>();

			String str[] = rolelist.split(",");

			// by add llf
			String results = "";
			PermissionDao udao = new PermissionDao();
			List<PermissionEntity> permissionList = null;
			try {
				permissionList = udao.getPermissionAll();
				// results =
				// JsonUtil.buildNonDefaultBinder().toJson(permissionList);
				result = ConfigUtil.objectToJson(permissionList).toString();

				JSONArray jsonArr = JSONArray.fromObject(results);
				for (int i = 0; i < jsonArr.size(); i++) {
					JSONObject obj = (JSONObject) jsonArr.get(i);
					for (int j = 0; j < str.length; j++) {
						if (str[j].equals(obj.getString("name").toString())) {
							list.add(obj.getString("id").toString());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			RoleEntity userRole = new RoleEntity();
			userRole.setName(roleName);
			userRole.setPermissionS(list);
			RoleDao uRdao = new RoleDao();
			try {
				uRdao.add(userRole);
				resultMap.put("success", true);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error("用户信息写入配置文件失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "用户信息写入配置文件失败！");
				resultMap.put("error", e.getMessage());
			}
		} else {
			// 没有添加角色的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有添加用户的权限");
		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	/**
	 * 修改角色信息
	 */
	public String update(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PermitService ps = new PermitService();
		if (ps.permit("role-update")) {
			// 有修改角色权限的权限
			String roleName = httpReq.getParameter("roleName");
			System.out.println(roleName);
			String rolelist = httpReq.getParameter("roleList");
			List<String> list = new ArrayList<String>();

			String str[] = rolelist.split(",");

			// by add llf
			String results = "";
			PermissionDao udao = new PermissionDao();
			List<PermissionEntity> permissionList = null;
			try {
				permissionList = udao.getPermissionAll();
				// results =
				// JsonUtil.buildNonDefaultBinder().toJson(permissionList);
				result = ConfigUtil.objectToJson(permissionList).toString();
				JSONArray jsonArr = JSONArray.fromObject(results);
				for (int i = 0; i < jsonArr.size(); i++) {
					JSONObject obj = (JSONObject) jsonArr.get(i);
					for (int j = 0; j < str.length; j++) {
						if (str[j].equals(obj.getString("name").toString())) {
							list.add(obj.getString("id").toString());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			RoleEntity userRole = new RoleEntity();
			userRole.setName(roleName);
			userRole.setPermissionS(list);
			RoleDao uRdao = new RoleDao();
			try {
				uRdao.update(roleName, userRole);
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
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	public String updateTablePermission(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean strs = true;
		String roleName = httpReq.getParameter("roleName");
		String rolelist = httpReq.getParameter("roleList");
		String selectedarrids = httpReq.getParameter("selectedids");
		System.out.println(selectedarrids);
		log.info("rolelist = " + rolelist);
		if (strs) {
			// 有修改角色的权限
			RoleDao uRdao = new RoleDao();
			try {

				roleName = new String(roleName.getBytes("ISO-8859-1"));

				RoleEntity userRole = uRdao.getRoleByName(roleName);
				List<String> rolePermissionS = userRole.getPermissionS();
				List<String> tempList = new ArrayList<String>();
				for (String rolePermissionId : rolePermissionS) {
					PermissionDao pdaoDao = new PermissionDao();
					int pType = pdaoDao.getPermissionTypeById(rolePermissionId);
					if (pType == 2) {
						// rolePermissionS.remove(rolePermissionId);
						tempList.add(rolePermissionId);
					}
				}
				rolePermissionS.removeAll(tempList);

				// String[] permissionArr = rolelist.split(",");
				String[] permissionArr = selectedarrids.split(",");
				for (String permission : permissionArr) {
					log.info("permission = " + permission);
					if (permission.contains("||")) {
						String[] perArr = permission.split("\\|\\|");
						if (perArr.length == 2) {
							permission = perArr[1];
						}
					}
					log.info("permission = " + permission);
					rolePermissionS.add(permission);
				}
				uRdao.update(roleName, userRole);
				resultMap.put("success", true);
				resultMap.put("message", "保存角色权限成功！");
			} catch (Exception e) {
				log.error("", e);
				resultMap.put("success", false);
				resultMap.put("message", "保存角色权限失败！");
			}
		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();

		return result;
	}

	public String updatePerType(HttpServletRequest httpReq) {
		String result = "";
		String perType = httpReq.getParameter("perType");
		String roleName = httpReq.getParameter("roleName");

		String checked = httpReq.getParameter("checked");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		RoleDao uRdao = new RoleDao();
		try {
			resultMap.put("success", true);
			if ("true".equals(checked)) {
				// 添加权限类
				uRdao.addPerType(roleName, perType);
				resultMap.put("message", "添加角色权限类成功！");
			} else {
				// 删除权限类
				uRdao.delPerType(roleName, perType);
				resultMap.put("message", "删除角色权限类成功！");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("", e);
			resultMap.put("success", false);
			resultMap.put("error", e.getMessage());
			if ("1".equals(checked)) {
				resultMap.put("message", "添加角色权限类失败！");
			} else if ("0".equals(checked)) {
				resultMap.put("message", "删除角色权限类失败！");
			}

		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	/**
	 * 修改角色信息
	 */
	public String updateOnePer(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PermitService ps = new PermitService();
		if (ps.permit("role-updateOneRole")) {
			// 有修改角色权限的权限
			String roleName = httpReq.getParameter("roleName");
			String permissionId = httpReq.getParameter("perId");
			String checked = httpReq.getParameter("checked");

			// List<String> list = new ArrayList<String>();
			// String roleNames = null;

			RoleDao udao = new RoleDao();
			try {
				if ("true".equals(checked)) {
					udao.addPermission(roleName, permissionId);
					resultMap.put("message", "添加角色权限成功");
				} else {
					udao.delPermission(roleName, permissionId);
					resultMap.put("message", "删除角色权限成功");
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
			// 当前用户没有修改角色的权限
			resultMap.put("success", false);
			resultMap.put("message", "当前用户没有修改角色的权限");
		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;

	}

	/**
	 * 删除角色信息
	 */
	public String del(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		PermitService ps = new PermitService();
		if (ps.permit("role-del")) {
			// 有删除角色的权限
			/**
			 * @String roleName;
			 */
			String roleName = httpReq.getParameter("roleName");
			RoleEntity userrole = new RoleEntity();
			userrole.setName(roleName);
			RoleDao udao = new RoleDao();
			try {
				udao.del(userrole);
				resultMap.put("success", true);
			} catch (Exception e) {
				// e.printStackTrace();
				log.error("用户信息写回配置文件失败", e);
				resultMap.put("success", false);
				resultMap.put("message", "用户信息写回配置文件失败");
				resultMap.put("error", e.getMessage());
			}
		} else {
			// 没有删除角色的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有删除角色的权限");
		}
		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;
	}

	public String query() {

		return null;
	}

	/**
	 * 获取角色信息
	 */
	public String queryAll(HttpServletRequest httpReq) {
		String result = "";

		Map<String, Object> resultMap = new HashMap<String, Object>();
		PermitService ps = new PermitService();
		log.info("have user-queryAll" + ps.permit("userRole-queryAll"));
		if (ps.permit("RoleEntity-queryAll")) {
			// String start = httpReq.getParameter("start");
			// String limit = httpReq.getParameter("limit");
			// String limitStr = "limit " + start + "," + limit;
			// 有查看全部角色的权限
			RoleDao urDao = new RoleDao();

			// 获取权限
			PermissionDao perDao = new PermissionDao();

			List<PermissionEntity> permissionList = null;
			List<Map<String, Object>> userRoleS = null;
			try {
				permissionList = perDao.getPermissionAll();
				userRoleS = urDao.getRoleAndPermisstAll();
				for (Map<String, Object> userRoleMap : userRoleS) {
					@SuppressWarnings("unchecked")
					List<PermissionEntity> userRoleperList = (List<PermissionEntity>) userRoleMap.get("rolePermissionS");
					// userRoleperList.addAll(permissionList);
					if (userRoleperList == null) {
						userRoleperList = new ArrayList<PermissionEntity>();
						userRoleMap.put("rolePermissionS", userRoleperList);
					}
					for (PermissionEntity p : permissionList) {
						if (!userRoleperList.contains(p)) {
							userRoleperList.add(p);
						}
					}
					// Collections.sort();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			resultMap.put("success", true);
			resultMap.put("datas", userRoleS);
		} else {
			// 没有查看全部角色的权限
			resultMap.put("success", false);
			resultMap.put("message", "没有查看角色的权限");
		}
		// 获取全部角色信息

		// result = JsonUtil.buildNonDefaultBinder().toJson(resultMap);
		result = ConfigUtil.objectToJson(resultMap).toString();
		return result;

	}
}
