package com.dongming8.mmpt.servlet;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.unimas.cs.dao.UrlRoleDao;
//import com.unimas.cs.entity.UrlRole;

public class ShiroUrlFilter extends AdviceFilter {
	private static final Logger log = LoggerFactory.getLogger(ShiroUrlFilter.class);
	// private static UrlRoleDao urlRoleDao = new UrlRoleDao();
	// private static PatternMatcher pathMatcher = new AntPathMatcher();

	public static final String DEFAULT_REDIRECT_URL = "/";
	private String redirectUrl = DEFAULT_REDIRECT_URL;

	public List<String> splitRoles(String roles) {
		// 0,163,200
		if (StringUtils.hasText(roles)) {
			String[] roleArr = roles.split("\\,");
			return Arrays.asList(roleArr);
		}
		return null;
	}

	private List<String> getRoleList(String path) {

		// System.out.println(path);
		// List<UrlRole> urlRoleS = urlRoleDao.getUrlRoleAll();
		// System.out.println(urlRoleS);
		List<String> roleList = null;
		// for (UrlRole urlRole : urlRoleS) {
		// String url = urlRole.getUrl();
		// System.out.println("url = "+url+";path = "+path
		// +";result = "+pathMatcher.matches(url, path));
		// if (pathMatcher.matches(url, path)) {
		// String roles = urlRole.getRoles();
		// roleList = splitRoles(roles);
		// break;
		// }
		// }
		// System.out.print("需要的角色列表");
		// System.out.println(roleList);
		return roleList;
	}

	/**
	 * 检查当前用户是否有该角色
	 * 
	 * @param roleList
	 * @param subject
	 * @return
	 */
	private boolean hasRole(List<String> roleList, Subject subject) {
		if (roleList != null) {
			for (String role : roleList) {
				if (subject.hasRole(role) == true) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		System.out.println("----preHandle----");
		HttpServletRequest req = (HttpServletRequest) request;
		String contextPath = req.getContextPath();
		String path = req.getRequestURI();
		// System.out.println("contextPath = "+contextPath);
		// System.out.println("path = "+path);
		path = path.replaceFirst(contextPath, "");
		List<String> roleList = getRoleList(path);

		if (roleList == null) {
			// 没有发现该资源需要任何角色
			return true;
		}
		Subject subject = getSubject(request, response);

		if (hasRole(roleList, subject)) {
			System.out.println("通过过滤");
			return true;
		} else {
			String redirectUrl = getRedirectUrl();
			issueRedirect(request, response, redirectUrl);
			System.out.println("未通过过滤，跳转至" + redirectUrl);
			return false;
		}
	}

	protected Subject getSubject(ServletRequest request, ServletResponse response) {
		return SecurityUtils.getSubject();
	}

	/**
	 * Issues an HTTP redirect to the specified URL after subject logout. This
	 * implementation simply calls {@code WebUtils.}
	 * {@link WebUtils#issueRedirect(javax.servlet.ServletRequest, javax.servlet.ServletResponse, String)
	 * issueRedirect(request,response,redirectUrl)}.
	 * 
	 * @param request
	 *            the incoming Servlet request
	 * @param response
	 *            the outgoing Servlet response
	 * @param redirectUrl
	 *            the URL to where the browser will be redirected immediately
	 *            after Subject logout.
	 * @throws Exception
	 *             if there is any error.
	 */
	protected void issueRedirect(ServletRequest request, ServletResponse response, String redirectUrl) throws Exception {
		WebUtils.issueRedirect(request, response, redirectUrl);
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
