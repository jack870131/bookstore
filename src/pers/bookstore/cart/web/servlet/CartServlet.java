package pers.bookstore.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import pers.bookstore.book.domain.Book;
import pers.bookstore.book.service.BookService;
import pers.bookstore.cart.domain.Cart;
import pers.bookstore.cart.domain.CartItem;

public class CartServlet extends BaseServlet {
	/**
	 * 添加購物條目
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 得到車
		 * 2. 得到條目(得到圖書和數量)
		 * 3. 把條目添加到車中
		 */
		/*
		 * 1. 得到車
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		/*
		 * 表單傳遞的只有 bid 和數量
		 * 2. 得到條目
		 *  - 得到圖書的數量
		 *  - 先得到圖書的 bid，然後我們需要通過 bid 查詢數據庫得到 Book
		 *  - 數量表單中有
		 */
		String bid = request.getParameter("bid");
		Book book = new BookService().load(bid);
		int count = Integer.parseInt(request.getParameter("count"));
		CartItem cartItem = new CartItem();
		cartItem.setBook(book);
		cartItem.setCount(count);
		/*
		 * 3. 把條目添加到車中
		 */
		cart.add(cartItem);
		
		return "f:/jsps/cart/list.jsp";
	}
	
	/**
	 * 清空購物條目
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String clear(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 得到車
		 * 2. 調用車的的 clear
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	
	/**
	 * 刪除購物條目
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 得到車
		 * 2. 得到要刪除的 bid
		 */
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		String bid = request.getParameter("bid");
		cart.delete(bid);
		return "f:/jsps/cart/list.jsp";
	}
}
