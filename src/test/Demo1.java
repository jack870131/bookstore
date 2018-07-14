package test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;

import org.junit.Test;

public class Demo1 {
	@Test
	public void fun1() {
		/*
		 * 包含了點位符的字符串就是模板！ 點位符：{0}、{1}、{2} 可變參數，需要指定模板中的點位符的值！有幾個點位符就要提供幾個參數
		 */
		String s = MessageFormat.format("{0}或{1}錯誤！", "用戶名", "密碼");
		System.out.println(s);
	}

	@Test
	public void fun2() {
		System.out.println(2.0 - 1.1);// 0.8999999999999999
	}

	/**
	 * 1000的階乘
	 */
	@Test
	public void fun3() {
		BigInteger sum = BigInteger.valueOf(1);
		for (int i = 1; i <= 1000; i++) {
			BigInteger bi = BigInteger.valueOf(i);
			sum = sum.multiply(bi);
		}
		System.out.println(sum);
	}

	/**
	 * BigDecimal 可以處理二進行運算導致的誤差
	 */
	@Test
	public void fun4() {
		/*
		 * 1. 創建BigDecimal對象時，必須使用String構造器！
		 */
		BigDecimal d1 = new BigDecimal("2.0");
		BigDecimal d2 = new BigDecimal("1.1");
		BigDecimal d3 = d1.subtract(d2);

		System.out.println(d3);
	}

	/**
https://www.yeepay.com/app-merchant-proxy/node?p0_Cmd=Buy&p1_MerId=10001126856&p2_Order=123456&p3_Amt=10&p4_Cur=CNY&p5_Pid=&p6_Pcat=&p7_Pdesc=&p8_Url=http://localhost:8080/bookstore/OrderServlet?method=back&p9_SAF=&pa_MP=&pd_FrpId=ICBC-NET-B2C&pr_NeedResponse=1&hmac=7d8bf573417839e36a151bec660d50c4
	 */
	@Test
	public void fun5() {
		String hmac = PaymentUtil.buildHmac("Buy", "10001126856", "123456", "10", "CNY",
				"", "", "", "http://localhost:8080/bookstore/OrderServlet?method=back", 
				"", "", "ICBC-NET-B2C", "1", "69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl");
		System.out.println(hmac);
	}
}