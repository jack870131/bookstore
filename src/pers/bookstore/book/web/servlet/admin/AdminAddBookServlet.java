package pers.bookstore.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;
import pers.bookstore.book.domain.Book;
import pers.bookstore.book.service.BookService;
import pers.bookstore.category.domain.Category;
import pers.bookstore.category.service.CategoryService;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		/*
		 * 1. 把表單數據封裝到Book對象中
		 *   * 上傳三步
		 */
		// 創建工廠
		DiskFileItemFactory factory = new DiskFileItemFactory(15 * 1024, new File("C:/Users/asus/Pictures/Camera Roll/Other"));
		// 得到解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		// 設置單個文件大小為15KB
		sfu.setFileSizeMax(20 * 1024);
		// 使用解析器去解析request對象，得到List<FileItem>
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request);
			/*
			 * * 把fileItemList中的數據封裝到Book對象中
			 *   > 把所有的普通表單字段數據先封裝到Map中
			 *   > 再把map中的數據封裝到Book對象中
			 */
			Map<String,String> map = new HashMap<String,String>();
			for(FileItem fileItem : fileItemList) {
				if(fileItem.isFormField()) {
					map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
				}
			}

			Book book = CommonUtils.toBean(map, Book.class);
			// 為book指定bid
			book.setBid(CommonUtils.uuid());
			/*
			 * 需要把Map中的cid封裝到Category對象中，再把Category賦給Book
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			
			/*
			 * 2. 保存上傳的文件
			 *   * 保存的目錄
			 *   * 保存的文件名稱
			 */
			// 得到保存的目錄
			String savepath = this.getServletContext().getRealPath("/book_img");
			// 得到文件名稱：給原來文件名稱添加uuid前綴！避免文件名衝突
			String filename = CommonUtils.uuid() + "_" + fileItemList.get(1).getName();
			
			
			/*
			 * 校驗文件的擴展名
			 */
			if(!filename.toLowerCase().endsWith("jpg")) {
				request.setAttribute("msg", "您上傳的圖片不是JPG擴展名！");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}
			
			
			// 使用目錄和文件名稱創建目標文件
			File destFile = new File(savepath, filename);
			// 保存上傳文件到目標文件位置
			fileItemList.get(1).write(destFile);
			
			/*
			 * 3. 設置Book對象的image，即把圖片的路徑設置給Book的image
			 */
			book.setImage("book_img/" + filename);
			
			/*
			 * 4. 使用BookService完成保存
			 */
			bookService.add(book);
			
			/*
			 * 校驗圖片的尺寸
			 */
			Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
			if(image.getWidth(null) > 200 || image.getHeight(null) > 200) {
				destFile.delete();//刪除這個文件！
				request.setAttribute("msg", "您上傳的圖片尺寸超出了200 * 200！");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}
			
			/*
			 * 5. 返回到圖書列表
			 */
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll")
					.forward(request, response);
		} catch (Exception e) { 
			if(e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "您上傳的文件超出了15KB");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
			}
		}
	}
}
