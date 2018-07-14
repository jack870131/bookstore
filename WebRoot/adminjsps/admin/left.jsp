<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>菜單</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script type="text/javascript" src="<c:url value='/menu/mymenu.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/menu/mymenu.css'/>"
	type="text/css" media="all">
<script language="javascript">
	/*
	bar1: 必須與對象名相同
	Jack網絡圖書商城: 大標題
	*/
	var bar1 = new Q6MenuBar("bar1", "Jack網絡圖書商城");
	function load() {
		/*
		設置配色方案
		配置方案一共有4種:0, 1, 2, 3
		*/
		bar1.colorStyle = 3;
		/*
		指定圖片目錄
		*/
		bar1.config.imgDir = "<c:url value='/menu/img/'/>";
		/*
		菜單之間是否相互排斥
		*/
		bar1.config.radioButton = false;
		/*
		分類管理: 指定要添加的菜單名稱(如果這個名稱的菜單已經存在，不會重複添加)
		查看分類: 指定要添加的菜單項名稱
		<c:url value='/adminjsps/admin/category/list.jsp'/>: 指定菜單項時要請求的地址
		body: 結果的顯示框架頁名稱
		*/
		bar1.add("分類管理", "查看分類", "<c:url value='/admin/AdminCategoryServlet?method=findAll'/>", "body");
		bar1.add("分類管理", "添加分類", "<c:url value='/adminjsps/admin/category/add.jsp'/>", "body");

		bar1.add("圖書管理", "查看圖書", "<c:url value='/admin/AdminBookServlet?method=findAll'/>", "body");
		bar1.add("圖書管理", "添加圖書", "<c:url value='/admin/AdminBookServlet?method=addPre'/>", "body");

		bar1.add("訂單管理", "所有訂單", "<c:url value='/adminjsps/admin/order/list.jsp'/>", "body");
		bar1.add("訂單管理", "未付款訂單", "<c:url value='/adminjsps/admin/order/list.jsp'/>", "body");
		bar1.add("訂單管理", "已付款訂單", "<c:url value='/adminjsps/admin/order/list.jsp'/>", "body");
		bar1.add("訂單管理", "未收貨訂單", "<c:url value='/adminjsps/admin/order/list.jsp'/>", "body");
		bar1.add("訂單管理", "已完成訂單", "<c:url value='/adminjsps/admin/order/list.jsp'/>", "body");
		/*
		獲取 div 元素
		*/
		var d = document.getElementById("menu");
		/*
		把菜單對象轉換成字符串，賦給<div>元素做內容
		*/
		d.innerHTML = bar1.toString();
	}
</script>

</head>

<body onload="load()" style="margin: 0px; background: rgb(254,238,189);">
	<div id="menu"></div>
</body>
</html>
