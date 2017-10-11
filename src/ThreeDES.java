
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreeDES.java

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public abstract class ThreeDES {

	private static final String Algorithm = "DESede";
	public static final int keySize = 32;
	
	private ThreeDES() {
	}

	/**
	 * 加密
	 * @param sdata	需要加密的字符串
	 * @param skey	密钥
	 * @return
	 * @throws Exception
	 */
	public static String encryptMode(String sdata, String skey) throws Exception {
		if(skey == null || skey.isEmpty()) {
			throw new IllegalArgumentException("密钥不可为空");
		}
		byte key[] = (new BASE64Decoder()).decodeBuffer(skey);
		byte data[] = sdata.getBytes("UTF-8");
		java.security.Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
		cipher.init(1, deskey);
		byte bOut[] = cipher.doFinal(data);
		return (new BASE64Encoder()).encode(bOut);
	}

	/**
	 * 解密
	 * @param sdata	需要解密的字符串
	 * @param skey	密钥
	 * @return
	 * @throws Exception
	 */
	public static String decryptMode(String sdata, String skey) throws Exception {
		if(skey == null || skey.isEmpty()) {
			throw new IllegalArgumentException("密钥不可为空");
		}
		byte key[] = (new BASE64Decoder()).decodeBuffer(skey);
		byte data[] = (new BASE64Decoder()).decodeBuffer(sdata);
		java.security.Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
		cipher.init(2, deskey);
		byte bOut[] = cipher.doFinal(data);
		return new String(bOut, "UTF-8");
	}
}
