package cn.dongming8.mmpt.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dongming8.mmpt.commons.config.ConfigUtil;
import cn.dongming8.mmpt.dao.PermissionDao;
import cn.dongming8.mmpt.entity.Permission;

/**
 * 用户管理
 * 
 * @author Administrator
 * 
 */
public class PermissionAction {
	private static final Logger log = LoggerFactory.getLogger(PermissionAction.class);

	public List<?> getTreeInfo(List<Permission> permissionList) {
		return null;
	}

	public String queryPermisslist(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PermissionDao udao = new PermissionDao();

		List<Permission> permissionList = null;

		try {
			permissionList = udao.getPermissionAll();
			// resultMap.put("success", true);
			// resultMap.put("datas", this.getTreeInfo(permissionList));
			result = ConfigUtil.objectToJson(this.getTreeInfo(permissionList)).toString();

		} catch (Exception e) {
			log.error("", e);
			resultMap.put("success", false);
			resultMap.put("message", "");
			result = ConfigUtil.objectToJson(resultMap).toString();

		}

		return result;
	}

	public String updatePermisslist(HttpServletRequest httpReq) {
		String result = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		PermissionDao udao = new PermissionDao();

		List<Permission> permissionList = null;

		try {
			permissionList = udao.getPermissionAll();
			// resultMap.put("success", true);
			// resultMap.put("datas", this.getTreeInfo(permissionList));
			// result =
			// JsonUtil.buildNonDefaultBinder().toJson(this.getTreeInfo(permissionList));
			result = ConfigUtil.objectToJson(this.getTreeInfo(permissionList)).toString();

		} catch (Exception e) {
			log.error("", e);
			resultMap.put("success", false);
			resultMap.put("message", "");
			result = ConfigUtil.objectToJson(resultMap).toString();

		}

		return result;
	}

}
