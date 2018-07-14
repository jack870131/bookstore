package pers.bookstore.user.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import pers.bookstore.user.domain.User;

public class LoginFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		/*
		 * 1. 從 session 中獲取用戶信息
		 * 2. 判斷如題 session 中存在用戶信息，放行
		 * 3. 否則，保存錯誤信息，轉發到 login.jsp，放行
		 */
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		User user = (User) httpRequest.getSession().getAttribute("session_user");
		if(user != null) {
			chain.doFilter(request, response);
		} else {
			httpRequest.setAttribute("msg", "您還沒有登錄");
			httpRequest.getRequestDispatcher("jsps/user/login.jsp")
				.forward(httpRequest, response);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
}
