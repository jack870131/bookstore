package pers.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import pers.bookstore.book.domain.Book;
import pers.bookstore.category.domain.Category;

public class BookDao {
	private QueryRunner qr=  new TxQueryRunner();
	
	/**
	 * 查詢所有圖書
	 * @return
	 */
	public List<Book> findAll() {
		try {
			String sql = "select * from book where del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按分類查詢
	 * @return
	 */
	public List<Book> findByCategory(String cid) {
		try {
			String sql = "select * from book where cid=? and del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class), cid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加載
	 * @param bid
	 * @return
	 */
	public Book findByBid(String bid) {
		try {
			/*
			 * 我們需要在 Book 對象中保存 Category 的信息
			 */
			String sql = "select * from book where bid=?";
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
			/*
			 * 使用一個 Map, 映射出兩個影像，再給這兩個對象建立關係
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			Book book = CommonUtils.toBean(map, Book.class);
			book.setCategory(category);
			return book;
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查詢指定分類下的圖書本數
	 * @param cid
	 * @return
	 */
	public int getCountByCid(String cid) {
		try {
			String sql = "select count(*) from book where cid=?";
			Number num = (Number) qr.query(sql, new ScalarHandler(), cid);
			return num.intValue();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加圖書
	 * @param book
	 */
	public void add(Book book) {
		try {
			String sql = "insert into book values(?,?,?,?,?,?)";
			Object[] params = {book.getBid(), book.getBname(), book.getPrice(), 
					book.getAuthor(), book.getImage(), 
					book.getCategory().getCid()};
			qr.update(sql, params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 刪除圖書
	 * @param bid
	 */
	public void delete(String bid) {
		try {
			String sql = "update book set del=true where bid=?";
			qr.update(sql, bid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * 修改圖書
	 * @param book
	 */
	public void edit(Book book) {
		try {
			String sql = "update book set bname=?, price=?, author=?, image=?, cid=? where bid=?";
			Object[] params = {book.getBname(), book.getPrice(), 
					book.getAuthor(), book.getImage(), 
					book.getCategory().getCid(), book.getBid()};
			qr.update(sql, params);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}