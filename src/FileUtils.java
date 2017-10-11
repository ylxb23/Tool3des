import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 文件工具类
 *
 * @author zero.zeng
 */
public abstract class FileUtils {
	private static final Charset CHARSET = Charset.forName("utf-8");
	private static final String KEYS_SETTING_PATH = "threedeskeys.settings";
	private static final String FILE_OUT_NAME = "history.txt";
	private static final String DATETIME_FORMAT_PATTERN_STRING = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**
	 * 读取配置密钥信息
	 * @return
	 */
	public static LinkedHashMap<String, String> readKeysProperties() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		BufferedReader br = null; 
		try {
			File settingfile = new File(KEYS_SETTING_PATH);
			if(!settingfile.exists()) {
				return map;
			}
			Reader fr = new InputStreamReader(new FileInputStream(settingfile), CHARSET);
			br = new BufferedReader(fr);
			String line = null;
			while((line = br.readLine()) != null) {
				String[] list = line.split("=");
				if(list.length != 2) {
					continue;
				}
				map.put(list[0], list[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Logger.getGlobal().logp(Level.WARNING, "FileUtils", "readKeysProperties", e.getMessage());
					System.exit(-1);
				}
			}
		}
		return map;
	}
	/**
	 * 追加key配置
	 * @param name
	 * @param value
	 */
	public static void addKeySetProperties(String name, String value) {
		try {
			String set = "\n" + name + "=" + value;
			appendToFile(KEYS_SETTING_PATH, set);
		} catch (IOException e) {
			Logger.getGlobal().logp(Level.WARNING, "FileUtils", "addKeySetProperties", e.getMessage());
		}
	}
	
	/**
	 * 获取当前格式化时间
	 * @return
	 */
	private static String getFormatedCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN_STRING);
		return sdf.format(new Date());
	}
	/**
	 * 追加内容到文件
	 * @param text
	 * @throws IOException
	 */
	public static void appendToFile(String path, String text) throws IOException {
		File file = new File(path);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file, true);
		fos.write(text.getBytes(CHARSET));
		fos.flush();
		fos.close();
	}
	public static void appendToLog(String text) {
		try {
			appendToFile(FILE_OUT_NAME, text);
		} catch (IOException e) {
			Logger.getGlobal().logp(Level.WARNING, "FileUtils", "appendToLog", e.getMessage());
		}
	}
	
	/**
	 * 组装日志文本
	 * @param operator
	 * @param key
	 * @param value
	 * @return
	 */
	public static String assemblyText(OperatorEnum operator, String key, String value) {
		StringBuilder now = new StringBuilder();
		now.append(getFormatedCurrentTime());
		now.append(" - ");
		now.append(operator.getName());
		now.append(" - KEY[");
		now.append(key);
		now.append("] VALUE[");
		now.append(value);
		now.append("] \n");
		return now.toString();
	}
	
}
