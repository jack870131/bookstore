package pers.bookstore.cart.domain;

import java.math.BigDecimal;

import pers.bookstore.book.domain.Book;

/**
 * 購物車條目類
 * @author jack870131
 *
 */
public class CartItem {
	private Book book; //商品
	private int count; //數量
	
	/**
	 * 小計方法
	 * @return
	 * 處理了二進制運算誤差問題
	 */
	public double getSubtotal() { //小計方法，但她沒偶對應的成員
		BigDecimal d1 = new BigDecimal(book.getPrice() + "");
		BigDecimal d2 = new BigDecimal(count + "");
		return d1.multiply(d2).doubleValue();
	}
 	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Cart [book=" + book + ", count=" + count + "]";
	}
}
