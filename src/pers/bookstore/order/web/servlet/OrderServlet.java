package pers.bookstore.order.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import pers.bookstore.cart.domain.Cart;
import pers.bookstore.cart.domain.CartItem;
import pers.bookstore.order.domain.Order;
import pers.bookstore.order.domain.OrderItem;
import pers.bookstore.order.service.OrderException;
import pers.bookstore.order.service.OrderService;
import pers.bookstore.user.domain.User;

public class OrderServlet extends BaseServlet {
	OrderService orderService = new OrderService();
	
	/**
	 * 支付(去銀行)
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String pay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("merchantInfo.properties");
		props.load(input);
		/*
		 * 準備13參數
		 */
		String p0_Cmd = "Buy";
		String p1_MerId = props.getProperty("p1_MerId");
		String p2_Order = request.getParameter("oid");
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		String p8_Url = props.getProperty("p8_Url");
		String p9_SAF = "";
		String pa_MP = "";
		String pd_FrpId = request.getParameter("pd_FrpId");
		String pr_NeedResponse = "1";
		/*
		 * 計算 hmac
		 */
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		/*
		 * 連接易寶網址和13+1參數
		 */
		StringBuilder url = new StringBuilder(props.getProperty("url"));
		url.append("?p0_Cmd=").append(p0_Cmd);
		url.append("&p1_MerId=").append(p1_MerId);
		url.append("&p2_Order=").append(p2_Order);
		url.append("&p3_Amt=").append(p3_Amt);
		url.append("&p4_Cur=").append(p4_Cur);
		url.append("&p5_Pid=").append(p5_Pid);
		url.append("&p6_Pcat=").append(p6_Pcat);
		url.append("&p7_Pdesc=").append(p7_Pdesc);
		url.append("&p8_Url=").append(p8_Url);
		url.append("&p9_SAF=").append(p9_SAF);
		url.append("&pa_MP=").append(pa_MP);
		url.append("&pd_FrpId=").append(pd_FrpId);
		url.append("&pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&hmac=").append(hmac);

		System.out.println(url);
		/*
		 * 重定向到易寶
		 */
		response.sendRedirect(url.toString());
		return null;
	}
	
	/**
	 * 這個方法是易寶回調方法
	 * 我們必須要判斷調用本方法的是不是易寶
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String back(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 獲取11+1
		 */
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");

		String hmac = request.getParameter("hmac");
		
		/*
		 * 2. 校驗訪問者是否為易寶
		 */
		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("merchanInfo.properties");
		props.load(input);
		String keyValue = props.getProperty("keyValue");
		
		boolean bool = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, 
				r2_TrxId, r3_Amt, r4_Cur, r5_Pid, 
				r6_Order, r7_Uid, r8_MP, r9_BType, 
				keyValue);
		
		if(!bool) { //如果校驗失敗
			request.setAttribute("msg", "校驗失敗");
		}
		
		/*
		 * 3. 獲取狀態訂單，確定是否要修改訂單狀態，以及添加積分等業務操作
		 */
		orderService.pay(r6_Order); //有可能對數據庫操作，也可能不操作
		/*
		 * 判斷當前回調方式
		 * 如果為點對點，需要回饋已 success 開頭的字符串
		 */
		if(r9_BType.equals("2")) {
			response.getWriter().print("success");
		}
		/*
		 * 5. 保存成功信息，轉發到 msg.jsp
		 */
		request.setAttribute("msg", "支付成功!請等待賣家發貨!");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 確認收貨
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 獲取 oid 參數
		 * 2. 調用 service 方法
		 *  - 如果有異常，保存異常信息，轉發到 msg.jsp
		 * 3. 保存成功信息，轉發到 msg.jsp
		 */
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "確認成功，交易成功!");
		} catch(OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";	
	}
	
	/**
	 * 加載訂單
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 得到 oid 參數
		 * 2. 調用 oid 調用 service 方法得到 Order
		 * 3. 保存到 request 域，轉發到 /jsps/order/desc.jsp
		 */
		request.setAttribute("order", orderService.load(request.getParameter("oid")));
		return "f:/jsps/order/desc.jsp";
	}
	
	public String myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 從 session 中得到當前用戶
		 * 2. 使用 uid 調用 orderService#myOrders(uid) 得到該用戶的所有訂單List<Order>
		 * 3. 把訂單列表保存到 request 域中，轉發到 /jsps/order/list.jsp
		 */
		User user = (User)request.getSession().getAttribute("session_user");
		List<Order> orderList = orderService.myOrders(user.getUid());
		request.setAttribute("orderList", orderList);
		return "f:/jsps/order/list.jsp";
	}
	
	/**
	 * 添加訂單
	 * 把 session 中的車來生成Order對象
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 從 session 中得到 cart
		 * 2. 使用 cart 生成 Order 對象
		 * 3. 調用 service 方法完成訂單
		 * 4. 保存 order 到 request 域中，轉發到 /jsps/order/desc.jsp
		 */
		//從 session 中獲取 cart
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		//把 cart 轉換成 Order 對象
		/*
		 * 創建 Order 對象，並設置屬性
		 * 
		 * Cart -> Order
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid()); //設置編號
		order.setOrdertime(new Date()); //設置下單時間
		order.setState(1); //設置訂單狀態為1, 表示未付款
		User user = (User)request.getSession().getAttribute("session_user");
		order.setOwner(user); //設置訂單所有者
		order.setTotal(cart.getTotal()); //設置訂單的合計，從 cart 中獲取合計
		
		/*
		 * 創建訂單條目集合
		 * 
		 * cartItemList -> orderItemList
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//循環遍歷 Cart 中的所有 cartItem, 使用每一個 cartItem 對象創建 OrderItem 對象，並添加到集合中
		for(CartItem cartItem : cart.getCartItems()) {
			OrderItem oi = new OrderItem(); //創建訂單條目
			
			oi.setIid(CommonUtils.uuid()); //設置條目的 id
			oi.setCount(cartItem.getCount()); //設置條目的數量
			oi.setBook(cartItem.getBook()); //設置條目的圖書
			oi.setSubtotal(cartItem.getSubtotal()); //設置條目的小計
			oi.setOrder(order); //設置所屬訂單
			
			orderItemList.add(oi); //把訂單條目添加到集合中
		}
		
		//把所有的訂單條目添加到訂單中
		order.setOrderItemList(orderItemList);
		
		//清空購物車
		cart.clear();
		
		/*
		 * 3. 調用 orderService 添加到訂單中
		 */
		orderService.add(order);
		/*
		 * 4. 保存 order 到 request 域，轉發到 /jsps/order/desc.jsp
		 */
		request.setAttribute("order", order);
		return "/jsps/order/desc.jsp";
	}
}