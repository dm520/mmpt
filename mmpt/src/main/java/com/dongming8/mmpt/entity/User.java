package com.dongming8.mmpt.entity;

import java.util.List;

/**
 * 用户实体类
 * 
 * @author dm
 * @Time 2015-04-03 15:16
 */
public class User {
	private int id;// 用户ID
	private String name; // 用户名
	private String alias; // 别名
	private String password; // 密码
	private String salt; // 密码盐
	private List<String> roleS; // 用户角色列表

	public User() {
		super();
	}

	public User(int id) {
		super();
		this.id = id;
	}
	public User(int id, String userName) {
		super();
		this.id = id;
		this.name = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public List<String> getRoleS() {
		return roleS;
	}

	public void setRoleS(List<String> roleS) {
		this.roleS = roleS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
