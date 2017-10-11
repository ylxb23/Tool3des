
/**
 * 随机字串生成工具
 * @author zero.zeng
 */
public abstract class RandomStringUtil {
	/** 短信验证码长度 */
	public final static int SMS_CAPTCHA_LENGTH = 6;
	/** 验证码候选字集合 */
	private final static String SMS_CAPTCHA_CANDIDATE = "0123456789";

	/** mar_cid 候选字集合 */
	private final static String CID_CANDIDATE = "0123456789abcdef";
	
	/**
	 * 随机生成6位数字
	 * @return
	 */
	public static String generatorRandomCaptcha() {
		int candidateLength = SMS_CAPTCHA_CANDIDATE.length();
		StringBuilder sb = new StringBuilder(SMS_CAPTCHA_LENGTH);
		for(int i=0; i<SMS_CAPTCHA_LENGTH; i++) {
			int index = (int) (Math.random() * candidateLength);
			sb.append(SMS_CAPTCHA_CANDIDATE.charAt(index));
		}
		return sb.toString();
	}
	
	/**
	 * 随机字串
	 * @return
	 */
	public static String generateRandomString() {
		return Md5Encrypt.md5(rand(32));
	}
	
	/**
	 * 生成随机 mar_cid
	 * @return
	 */
	public static String generateCid4FQ() {
		Long timestamp = System.currentTimeMillis();
		String marId = rand(32);
		int timestampSum = sumOfNumbers(timestamp);
		int replaceIndex = timestampSum % 32;
		int dechexSum = timestampSum;
		for(int i=0; i<marId.length(); i++) {
			if(i != replaceIndex) {
				dechexSum += Integer.parseInt(""+marId.charAt(i), 16);
			}
		}
		String replaceValue = Integer.toHexString(dechexSum % 16 );
		StringBuffer sb = new StringBuffer();
		sb.append(timestamp);
		sb.append('_');
		sb.append(marId.substring(0, replaceIndex));
		sb.append(replaceValue);
		sb.append(marId.substring(replaceIndex + 1, marId.length()));
		return sb.toString();
	}
	
	/**
	 * 整数所有位数数字之和
	 * @param value
	 * @return
	 */
	public static int sumOfNumbers(Long value) {
		int sum = 0;
		sum += value % 10;
		while((value /= 10) != 0) {
			sum += value % 10;
		}
		return sum;
	}
	/**
	 * 指定长度的随机16位进制字符串
	 * @param len
	 * @return
	 */
	public static String rand(int len) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len; i++) {
			sb.append(CID_CANDIDATE.charAt((int)(Math.random()*100000000) % CID_CANDIDATE.length()));
		}
		return sb.toString();
	}
	
}
