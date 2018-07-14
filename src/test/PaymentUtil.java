package test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PaymentUtil {

	private static String encodingCharset = "UTF-8";
	
	/**
	 * 生成hmac方法
	 * 
	 * @param p0_Cmd 業務類型
	 * @param p1_MerId 商戶編號
	 * @param p2_Order 商戶訂單號
	 * @param p3_Amt 支付金額
	 * @param p4_Cur 交易幣種
	 * @param p5_Pid 商品名稱
	 * @param p6_Pcat 商品種類
	 * @param p7_Pdesc 商品描述
	 * @param p8_Url 商戶接收支付成功數據的地址
	 * @param p9_SAF 送貨地址
	 * @param pa_MP 商戶擴展信息
	 * @param pd_FrpId 銀行編碼
	 * @param pr_NeedResponse 應答機制
	 * @param keyValue 商戶密鑰
	 * @return
	 */
	public static String buildHmac(String p0_Cmd,String p1_MerId,
			String p2_Order, String p3_Amt, String p4_Cur,String p5_Pid, String p6_Pcat,
			String p7_Pdesc,String p8_Url, String p9_SAF,String pa_MP,String pd_FrpId,
			String pr_NeedResponse,String keyValue) {
		StringBuilder sValue = new StringBuilder();
		// 業務類型
		sValue.append(p0_Cmd);
		// 商戶編號
		sValue.append(p1_MerId);
		// 商戶訂單號
		sValue.append(p2_Order);
		// 支付金額
		sValue.append(p3_Amt);
		// 交易幣種
		sValue.append(p4_Cur);
		// 商品名稱
		sValue.append(p5_Pid);
		// 商品種類
		sValue.append(p6_Pcat);
		// 商品描述
		sValue.append(p7_Pdesc);
		// 商戶接收支付成功數據的地址
		sValue.append(p8_Url);
		// 送貨地址
		sValue.append(p9_SAF);
		// 商戶擴展信息
		sValue.append(pa_MP);
		// 銀行編碼
		sValue.append(pd_FrpId);
		// 應答機制
		sValue.append(pr_NeedResponse);
		
		return PaymentUtil.hmacSign(sValue.toString(), keyValue);
	}
	
	/**
	 * 返回校驗hmac方法
	 * 
	 * @param hmac 支付網關發來的加密驗證碼
	 * @param p1_MerId 商戶編號
	 * @param r0_Cmd 業務類型
	 * @param r1_Code 支付結果
	 * @param r2_TrxId 易寶支付交易流水號
	 * @param r3_Amt 支付金額
	 * @param r4_Cur 交易幣種
	 * @param r5_Pid 商品名稱
	 * @param r6_Order 商戶訂單號
	 * @param r7_Uid 易寶支付會員ID
	 * @param r8_MP 商戶擴展信息
	 * @param r9_BType 交易結果返回類型
	 * @param keyValue 密鑰
	 * @return
	 */
	public static boolean verifyCallback(String hmac, String p1_MerId,
			String r0_Cmd, String r1_Code, String r2_TrxId, String r3_Amt,
			String r4_Cur, String r5_Pid, String r6_Order, String r7_Uid,
			String r8_MP, String r9_BType, String keyValue) {
		StringBuilder sValue = new StringBuilder();
		// 商戶編號
		sValue.append(p1_MerId);
		// 業務類型
		sValue.append(r0_Cmd);
		// 支付結果
		sValue.append(r1_Code);
		// 易寶支付交易流水號
		sValue.append(r2_TrxId);
		// 支付金額
		sValue.append(r3_Amt);
		// 交易幣種
		sValue.append(r4_Cur);
		// 商品名稱
		sValue.append(r5_Pid);
		// 商戶訂單號
		sValue.append(r6_Order);
		// 易寶支付會員ID
		sValue.append(r7_Uid);
		// 商戶擴展信息
		sValue.append(r8_MP);
		// 交易結果返回類型
		sValue.append(r9_BType);
		String sNewString = PaymentUtil.hmacSign(sValue.toString(), keyValue);
		return sNewString.equals(hmac);
	}
	
	/**
	 * @param aValue
	 * @param aKey
	 * @return
	 */
	public static String hmacSign(String aValue, String aKey) {
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = aKey.getBytes(encodingCharset);
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			keyb = aKey.getBytes();
			value = aValue.getBytes();
		}

		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {

			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return toHex(dg);
	}

	public static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}

	/**
	 * 
	 * @param args
	 * @param key
	 * @return
	 */
	public static String getHmac(String[] args, String key) {
		if (args == null || args.length == 0) {
			return (null);
		}
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			str.append(args[i]);
		}
		return (hmacSign(str.toString(), key));
	}

	/**
	 * @param aValue
	 * @return
	 */
	public static String digest(String aValue) {
		aValue = aValue.trim();
		byte value[];
		try {
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			value = aValue.getBytes();
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return toHex(md.digest(value));

	}
	
//	public static void main(String[] args) {
//		System.out.println(hmacSign("AnnulCard1000043252120080620160450.0http://localhost/SZXpro/callback.asp杩?4564868265473632445648682654736324511","8UPp0KE8sq73zVP370vko7C39403rtK1YwX40Td6irH216036H27Eb12792t"));
//	}
}
