package pers.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * 購物車類
 * @author jack870131
 *
 */
public class Cart {
	private LinkedHashMap<String, CartItem> map = new LinkedHashMap<>();
	
	/**
	 * 計算合計
	 * @return
	 */
	public double getTotal() {
		//合計=所有條目的小計之和
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : map.values()) {
			BigDecimal subtotal = new BigDecimal("" + cartItem.getSubtotal());
			total = total.add(subtotal);
		}
		return total.doubleValue();
	}
	
	/**
	 * 添加條目
	 * @param cartItem
	 */
	public void add(CartItem cartItem) {
		if(map.containsKey(cartItem.getBook().getBid())) { //判斷原來車中是否存在該條目
			CartItem _cartItem = map.get(cartItem.getBook().getBid()); //返回原條目
			_cartItem.setCount(_cartItem.getCount() + cartItem.getCount()); //設置老條目的數量為，其自己的數量 + 新條目的數量
			map.put(cartItem.getBook().getBid(), _cartItem);
		} else {
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	
	/**
	 * 清空所有條目
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 * 刪除指定條目
	 * @param bid
	 */
	public void delete(String bid) {
		map.remove(bid);
	}
	
	/**
	 * 獲取所有條目
	 * @return
	 */
	public Collection<CartItem> getCartItems() {
		return map.values();
	}
}
