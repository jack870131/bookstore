package pers.bookstore.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;
import pers.bookstore.order.dao.OrderDao;
import pers.bookstore.order.domain.Order;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	
	/**
	 * 添加訂單
	 * 需要處理事務
	 * @param order
	 */
	public void add(Order order) {
		try {
			//開啟事務
			JdbcUtils.beginTransaction();
			
			orderDao.addOrder(order); //插入訂單
			orderDao.addOrerItemList(order.getOrderItemList()); //插入訂單中的所有條目
			
			//提交事務
			JdbcUtils.commitTransaction();
		} catch(Exception e) {
			//回滾事務
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * 我的訂單
	 * @param uid
	 * @return
	 */
	public List<Order> myOrders(String uid) {
		return orderDao.findByUid(uid);
	}

	/**
	 * 加載訂單
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		return orderDao.load(oid);
	}
	
	/**
	 * 確認收貨
	 * @param oid
	 * @throws OrderException
	 */
	public void confirm(String oid) throws OrderException {
		/*
		 * 1. 校驗訂單狀態，如果不是3, 拋出異常
		 */
		int state = orderDao.getStateByOid(oid); //獲取訂單狀態
		if(state != 3) throw new OrderException("訂單確認失敗");
		/*
		 * 2. 修改訂單狀態為4
		 */
		orderDao.updateState(oid, 4);
	}

	/**
	 * 支付方法
	 * @param r6_Order
	 */
	public void pay(String oid) {
		/*
		 * 1. 獲取訂單狀態
		 *  - 如果狀態為1, 那麼執行下面代碼
		 *  - 如果狀態不為1, 那麼本方法什麼都不做
		 */
		int state = orderDao.getStateByOid(oid);
		if(state == 1) {
			//修改訂單狀態為2
			orderDao.updateState(oid, 2);
		}
	}
}
