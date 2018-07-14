package pers.bookstore.book.service;

import java.util.List;

import pers.bookstore.book.dao.BookDao;
import pers.bookstore.book.domain.Book;

public class BookService {
	BookDao bookDao = new BookDao();
	
	/**
	 * 查詢所有圖書
	 * @return
	 */
	public List<Book> findAll() {
		return bookDao.findAll();
	}

	/**
	 * 按分類查詢
	 * @param cid
	 * @return
	 */
	public List<Book> findCategory(String cid) {
		return bookDao.findByCategory(cid);
	}

	public Book load(String bid) {
		return bookDao.findByBid(bid);
	}

	/**
	 * 添加圖書
	 * @param book
	 */
	public void add(Book book) {
		bookDao.add(book);
	}
	
	/**
	 * 刪除圖書
	 * @param bid
	 */
	public void delete(String bid) {
		bookDao.delete(bid);
	}

	public void edit(Book book) {
		bookDao.edit(book);
	}
}
