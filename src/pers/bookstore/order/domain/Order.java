package pers.bookstore.order.domain;

import java.util.Date;
import java.util.List;

import pers.bookstore.user.domain.User;

/**
 * 訂單類
 * @author jack870131
 *
 */
public class Order {
	private String oid;
	private Date ordertime; //下單時間
	private double total; //合計
	private int state; //訂單狀態有四種: 未付款、已付款未發貨、已發貨未確認、已確認交易成功
	private User owner; //訂單所有者!
	private String address; //收穫地址
	
	private List<OrderItem> orderItemList; //當前訂單下所有條目
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "Order [oid=" + oid + ", ordertime=" + ordertime + ", total=" + total + ", state=" + state + ", owner="
				+ owner + ", address=" + address + "]";
	}
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
}
