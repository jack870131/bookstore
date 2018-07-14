package pers.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import pers.bookstore.book.domain.Book;
import pers.bookstore.order.domain.Order;
import pers.bookstore.order.domain.OrderItem;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 添加訂單
	 * 
	 * @param order
	 */
	public void addOrder(Order order) {
		try {
			String sql = "insert into orders values(?,?,?,?,?,?)";
			/*
			 * 處理 util 的 Date 轉換成 sql 的 TimeStamp
			 */
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = {order.getOid(), timestamp,
					order.getTotal(), order.getState(), 
					order.getOwner().getUid(), 
					order.getAddress()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 插入訂單條目
	 * @param orderItemList
	 */
	public void addOrerItemList(List<OrderItem> orderItemList) {
		/*
		 * QueryRunner 類的 batch(String sql, Object[][] params)
		 * 其中 params 是多個一維數組
		 * 每個一維數組都與sql在一起執行一次，多個一維數組就執行多次
		 */
		try {
			String sql = "insert into orderitem values(?,?,?,?,?)";
			/*
			 * 把 orderItemList 轉換成二維數組
			 *  把一個OrderItem對象轉換成一個一維數組
			 */
			Object[][] params = new Object[orderItemList.size()][];
			//循環遍歷 orderItemList, 使用每個 orderItem 對象為 params 中每個一維數組賦值
			for(int i = 0; i < orderItemList.size(); i++) {
				OrderItem item =  orderItemList.get(i);
				params[i] = new Object[] {item.getIid(), item.getCount(),
						item.getSubtotal(), item.getOrder().getOid(),
						item.getBook().getBid()};
			}
			qr.batch(sql, params); //執行批處理
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按 uid 查詢訂單
	 * @return
	 */
	public List<Order> findByUid(String uid) {
		/*
		 * 1. 通過 uid 查詢當前用戶的所有List<Order>
		 * 2. 循環遍歷每個 Order, 為其加載他的所有 OrderItem
		 */
		try {
			/*
			 * 1. 得到當前用戶的所有訂單
			 */
			String sql = "select * from orders where uid=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class), uid);
			/*
			 * 2. 循環遍歷每個 Order, 為其加載他自己所有的訂單條目
			 */
			for(Order order : orderList) {
				loadOrderItems(order); //為 order 對象添加他所有的訂單條目
			}
			/*
			 * 3. 返回訂單列表
			 */
			return orderList;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加載指定的訂單所有的訂單條目
	 * @param order
	 * @throws SQLException 
	 */
	private void loadOrderItems(Order order) throws SQLException {
		/*
		 * 查詢兩張表: orderitem、book
		 */
		String sql = "select * from orderitem i, book b where i.bid=b.bid and oid=?";
		/*
		 * 因為一行結果集對應的不再是一個 javabean, 所以不能再使用 BeanListHandler, 而是 MapListHandler
		 */
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		/*
		 * mapList 是多個 Map, 每個 map 對應一行結果集
		 * 
		 * 我們需要使用一個 Map 生成兩個對象: OrderItem, Book, 然後再建立兩者的關係(把 Book 設置給 OrderItem)
		 */
		/*
		 * 循環遍歷每個 Map, 使用 map 生成兩個對象，然後建立關係(最終結果是一個 orderItem), 把 OrderItem 保存起賴
		 */
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}

	/**
	 * 把 mapList 中每個 Map 轉換成兩個對象，並建立關係
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String, Object> map : mapList) {
			OrderItem item = toOrderItemList(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	/**
	 * 把一個 Map 轉換成一個 OrderItem 對象
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItemList(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	/**
	 * 加載訂單
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		try {
			/*
			 * 1. 得到當前用戶的所有訂單
			 */
			String sql = "select * from orders where oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
			/*
			 * 2. 為 order 加載他的所有條目
			 */
			loadOrderItems(order);
			/*
			 * 3. 返回訂單列表
			 */
			return order;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通過 oid 查詢訂單狀態
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid) {
		try {
			String sql = "select state from orders where oid=?";
			return (Integer) qr.query(sql, new ScalarHandler(), oid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改訂單狀態
	 * @param oid
	 * @param state
	 * @return
	 */
	public void updateState(String oid, int state) {
		try {
			String sql = "update orders set state=? where oid=?";
			qr.update(sql, state, oid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
