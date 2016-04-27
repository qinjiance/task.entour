package com.qinjiance.tourist.task.util;

import java.security.MessageDigest;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtil {

	private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

	/**
	 * 唯一识别码
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(uuid.substring(0, 8));
		sb.append(uuid.substring(9, 13));
		sb.append(uuid.substring(14, 18));
		sb.append(uuid.substring(19, 23));
		sb.append(uuid.substring(24));
		return sb.toString();
	}

	/**
	 * MD5加密
	 * @param source
	 * @return
	 */
	public static String md5(String source) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes("UTF-8"));
			byte bytes[] = md.digest();
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			logger.error("md5 Exceptoion", e);
		}
		return sb.toString();
	}
	
	/**
	 * 安全哈希签名
	 * @param source
	 * @return
	 */
	public static String sha1(String source){
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = messageDigest.digest(source.getBytes("UTF-8"));
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			logger.error("sha1 Exceptoion", e);
		}
		return sb.toString();
	}
	
	/**
	 * HmacMD5
	 * @param key
	 * @param params
	 * @return
	 */
	public static String hmacMD5(String key, String... strs){
		StringBuilder sb = new StringBuilder();
		try {
			Mac mac = Mac.getInstance("HmacMD5");
	        mac.init(new SecretKeySpec(key.getBytes(), "HmacMD5"));
	        for(String str : strs){
	            mac.update(str.getBytes());
	        }
	        byte[] bytes = mac.doFinal();
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			logger.error("hmacMD5 Exceptoion", e);
			return "";
		}
		return sb.toString();
	}

	/**
	 * base64编码
	 * @param inStr
	 * @return
	 */
	public static String base64Encode(String inStr) {
		Base64 base64 = new Base64(false);
		try {
			return (new String(base64.encode(inStr.getBytes("UTF-8")), "UTF-8")).replaceAll("\\r\\n", "");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * base64解码
	 * @param inStr
	 * @return
	 */
	public static String base64Decode(String inStr) {
		Base64 base64 = new Base64(false);
		try {
			return new String(base64.decode(inStr.getBytes("UTF-8")), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
	
	//计算密码的强度
	public static int getPasswordStrength(String password) {
			boolean hasNum = false;
			boolean hasUpperLetter = false;
			boolean hasLowerLetter = false;
			for(int i=0;i<password.length();i++) {
				int chr = password.charAt(i); 
				if(chr>=48 && chr<=57) {//数字
					hasNum = true;
				}
				if(chr>=65 && chr<=90) {//大写字母
					hasUpperLetter = true;
				}
				if(chr>=97 && chr<=122) {//小写字母
					hasLowerLetter = true;
				}
			}
			int count = 0;
			if(hasNum) {
				count ++;
			}
			if(hasUpperLetter) {
				count ++;
			}
			if(hasLowerLetter) {
				count ++;
			}
			if(count==3 && password.length()<9)  {
				count --;
			}
			return count;
	}
	
	public static void main(String[] args) {
//		ObjectMapper mapper = new ObjectMapper();
//		User user = new User();
//		user.setId(10000L);
//		user.setUsername("123123");
//		user.setNickname("2134123");
//		try {
//			System.out.println(mapper.writeValueAsString(user));
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println(hmacMD5("123456", "appid", "30023", "uid", "537353"));
		
		System.out.println(md5("123456"));
	}

}
