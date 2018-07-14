package pers.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;
import pers.bookstore.cart.domain.Cart;
import pers.bookstore.user.domain.User;
import pers.bookstore.user.service.UserException;
import pers.bookstore.user.service.UserService;

/**
 * User 表述層
 * @author jack870131
 *
 */
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/**
	 * 退出
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/index.jsp";
	}
	
	public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 封裝表單數據到 jsp
		 * 2. 輸入校驗(略)
		 * 3. 調用 servlet 完成激活
		 *  - 保存錯誤信息，form 到 request, 轉發到 login.jsp
		 * 4. 保存用戶信息到 session 中，然後重定向到 index.jsp
		 */
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = userService.login(form);
			request.getSession().setAttribute("session_user", user);
			/*
			 * 給用戶添加一輛購物車，即向 session 中保存一Cart對象
			 */
			request.getSession().setAttribute("cart", new Cart());
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
	}
	
	/**
	 * 激活功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 獲取參數激活碼
		 * 2. 調用 service 方法完成激活
		 * 	- 保存異常信息到 request 域，轉發到 msg.jsp
		 * 3. 保存成功信息到 request 域，轉發到 msg.jsp
		 */
		String code = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "恭喜你激活成功");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 註冊功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws UserException 
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 封裝表單數據到 form 對象中
		 * 2. 補全: uid, code
		 * 3. 輸入校驗
		 *  - 保存錯誤信息, form 到  request 域, 轉發到 regist.jsp 
		 * 4. 調用 service 方法完成註冊
		 *  - 保存錯誤信息, form 到  request 域, 轉發到 regist.jsp
		 * 5. 發郵件
		 * 6. 保存成功信息轉發到 msg.jsp
		 */
		//封裝表單數據
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		//補全
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		/*
		 * 輸入校驗
		 * 1. 創建一個 Map, 用來封裝錯誤信息, 其中 key 為表單字段名稱，值為錯誤信息
		 */
		HashMap<String, String> errors = new HashMap<>();
		/*
		 * 2. 獲取 form 中的 username, password, email 進行校驗
		 */
		String username = form.getUsername();
		if(username == null || username.trim().isEmpty()) {
			errors.put("username", "用戶名不能為空");
		} else if(username.length() < 3 || username.length() > 10) {
			errors.put("username", "用戶名長度必須在 3-10 之間");
		}

		String password = form.getPassword();
		if(password == null || password.trim().isEmpty()) {
			errors.put("password", "密碼不能為空");
		} else if(password.length() < 3 || password.length() > 10) {
			errors.put("password", "密碼長度必須在 3-10 之間");
		}
		
		String email = form.getEmail();
		if(email == null || email.trim().isEmpty()) {
			errors.put("email", "Email不能為空");
		} else if(!email.matches("\\w+@\\w+\\.\\w+")) {
			errors.put("email", "Email長度必須在 3-10 之間");
		}
		/*
		 * 3. 判斷是否存在錯誤信息
		 */
		if(errors.size() > 0) {
			//1. 保存錯誤信息
			//2. 保存表單數據
			//3. 轉發到 regist.jsp
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		
		/*
		 * 調用 service 的 regist() 方法
		 */
		try {
			userService.regist(form);
		} catch(UserException e) {
			/*
			 * 1. 保存異常信息
			 * 2. 保存 form
			 * 3. 轉發到 regist.jsp
			 */
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		
		/*
		 * 發郵件
		 * 準備配置文件 
		 */
		//獲取配置文件內容
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = props.getProperty("host"); //獲取服務器主機
		String uname = props.getProperty("uname"); //獲取用戶名
		String pwd = props.getProperty("pwd"); //獲取密碼
		String from = props.getProperty("from"); //獲取發件人
		String to = form.getEmail(); //獲取收件人
		String subject = props.getProperty("subject"); //獲取主機
		String content = props.getProperty("content"); //獲取郵件內容
		content = MessageFormat.format(content, form.getCode()); //替換{0}
		
		Session session = MailUtils.createSession(host, uname, pwd); //得到 session
		Mail mail = new Mail(from, to, subject, content); //創建郵件對象
		try {
			MailUtils.send(session, mail); //發郵件
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		
		/*
		 * 1. 保存成功信息
		 * 2. 轉發到 msg.jsp
		 */
		request.setAttribute("msg", "恭喜，註冊成功!請馬上到郵箱繳活");
		return "f:/jsps/msg.jsp";
	}
}
