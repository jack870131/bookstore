package pers.bookstore.category.service;

import java.util.List;

import pers.bookstore.book.dao.BookDao;
import pers.bookstore.category.dao.CategoryDao;
import pers.bookstore.category.domain.Category;
import pers.bookstore.category.web.servlet.admin.CategoryException;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();

	public List<Category> findAll() {
		return categoryDao.findAll();
	}

	/**
	 * 添加分類
	 * @param category
	 */
	public void add(Category category) {
		categoryDao.add(category);
	}

	/**
	 * 刪除分類
	 * @param cid
	 * @throws CategoryException 
	 */
	public void delete(String cid) throws CategoryException {
		//獲取該分類下圖書的參數
		int count = bookDao.getCountByCid(cid);
		//如果該分類下存在圖書，不讓刪除，拋出異常
		if(count > 0) throw new CategoryException("該分類下還有圖書，不能刪除");
		//刪除該分類
		categoryDao.delete(cid);
	}

	/**
	 * 加載分類
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		return categoryDao.load(cid);
	}

	/**
	 * 修改分類
	 * @param category
	 */
	public void edit(Category category) {
		categoryDao.edit(category);
	}
}
