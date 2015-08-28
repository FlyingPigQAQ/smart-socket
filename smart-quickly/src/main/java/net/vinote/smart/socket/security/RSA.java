package net.vinote.smart.socket.security;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.vinote.smart.socket.lang.StringUtils;
import net.vinote.smart.socket.logger.RunLogger;

/**
 * 封装RSA加解密的常用方法
 * 
 * @author Seer
 * @version RSA.java, v 0.1 2015年8月27日 下午3:42:34 Seer Exp.
 */
public class RSA {
	private static final String ALGORITHM = "RSA";
	/** */
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/** */
	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * 生成RSA密钥对
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator
				.getInstance(ALGORITHM);
		return keyPairGenerator.generateKeyPair();
	}

	/**
	 * 对数据进行编码
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] encode(Key key, byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		} catch (Exception e) {
			RunLogger.getLogger().log(e);
		}
		return null;
	}

	/**
	 * 解码
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] decode(Key key, byte[] data) {

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (Exception e) {
			RunLogger.getLogger().log(e);
		}
		return null;

	}

	public static PublicKey generatePublicKey(byte[] keyData) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			return keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			RunLogger.getLogger().log(e);
		} catch (NoSuchAlgorithmException e) {
			RunLogger.getLogger().log(e);
		}
		return null;
	}

	public static PrivateKey generatePrivateKey(byte[] keyData) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			RunLogger.getLogger().log(e);
		} catch (NoSuchAlgorithmException e) {
			RunLogger.getLogger().log(e);
		}
		return null;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		KeyPair keyPair = generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		RunLogger.getLogger().log(Level.FINE,
				StringUtils.toHexString(privateKey.getEncoded()));
		byte[] data = encode(privateKey, privateKey.getEncoded());

		RunLogger.getLogger().log(Level.FINE,
				StringUtils.toHexString(decode(publicKey, data)));
	}
}