package cn.dongming8.mmpt.entity;

import java.util.List;

/**
 * 角色实体类
 * 
 * @author dm
 * @Time 2015-04-03 15:23
 */
public class Role {

	private String id;// 角色ID
	private String name;// 超级管理员、管理员、xx科室民警、科长、处长
	private List<String> permissionS;// 权限

	public Role() {
		super();
	}

	public Role(String id) {
		super();
		this.id = id;
	}

	public Role(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String roleName) {
		this.name = roleName;
	}

	public List<String> getPermissionS() {
		return permissionS;
	}

	public void setPermissionS(List<String> permissionS) {
		this.permissionS = permissionS;
	}

	public void addPermissionS(List<String> permissionS) {

		if (this.permissionS == null) {
			this.setPermissionS(permissionS);
		} else {
			this.permissionS.addAll(permissionS);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
