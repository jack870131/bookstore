package pers.bookstore.user.service;

import pers.bookstore.user.dao.UserDao;
import pers.bookstore.user.domain.User;

/**
 * User 業務層
 * @author jack870131
 *
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	/**
	 * 註冊功能
	 * @param form
	 */
	public void regist(User form) throws UserException {
		User user = userDao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("用戶名已被註冊");

		//校驗 Email
		user = userDao.findByEmail(form.getEmail());
		if(user != null) throw new UserException("Email已被註冊");
		
		//插入用戶到數據庫
		userDao.add(form);
	}
	
	/**
	 * 激活碼
	 * @param code
	 * @throws UserException 
	 */
	public void active(String code) throws UserException {
		/*
		 * 1. 使用 code 查詢數據庫，得到 user
		 */
		User user = userDao.findByCode(code);
		/*
		 * 2. 如果 user 不存在，說明激活碼錯誤
		 */
		if(user == null) throw new UserException("激活碼無效");
		/*
		 * 3. 校驗用戶的狀態是否為未激活狀態，如果已激活，拋出異常
		 */
		if(user.isState()) throw new UserException("您已經激活過了");
		/*
		 * 4. 修改用戶的狀態
		 */
		userDao.updateState(user.getUid(), true);
	}
	
	/**
	 * 登錄功能
	 * @param form
	 * @return
	 * @throws UserException 
	 */
	public User login(User form) throws UserException {
		/*
		 * 1. 使用 username 查詢，得到 User
		 * 2. 如果 user 為 null, 拋出異常(用戶名不存在)
		 * 3. 比較 form 和 user 的密碼，若不同，拋出異常(密碼錯誤)
		 * 4. 查看用戶狀態，若為 false, 拋出異常(尚未激活)
		 * 5. 返回 user
		 */	
		User user = userDao.findByUsername(form.getUsername());
		if(user == null) throw new UserException("用戶名不存在");
		if(!user.getPassword().equals(form.getPassword())) throw new UserException("密碼錯誤");
		if(!user.isState()) throw new UserException("尚未激活");
		
		return user;
	}
}
