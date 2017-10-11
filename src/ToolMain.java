import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * @date 2017-04-19
 * @author zero.zeng
 */
public class ToolMain extends JFrame {
	private final int TEXT_LENGTH_OP = 27;
	private final int TEXT_LENGTH = 32;

	private static final long serialVersionUID = 0x2f02d6b1431d3920L;
	JTextField enterEncrypted;
	JTextField enterNoEncrypt;
	JButton buttonEncrypt;
	JButton buttonDecrypt;
	JComboBox<String> keySelect;
	JTextArea showEncryptResult;
	JTextArea showDecryptResult;
	JTextField enterKey;
	
	
	public static Map<String, String> keyMap = null;
	private final static String KEY_SELECT_CUSTOM = "自定义";

	private static void reloadKeySet() {
		keyMap = new LinkedHashMap<>();
		keyMap.put(KEY_SELECT_CUSTOM, "");
		keyMap.putAll(FileUtils.readKeysProperties());
	}

	JFrame _this = this;
	
	public ToolMain() {
		// frame setting
		initFrame();
		// 菜单按钮
		panelMenuBar();
		buildKeySetFrame();	// 
		
		// 加解密功能面板
		endecryptOpPanelLayout();
		// 功能集成
		collectePanels();
		
		// 监听
		addListener();
	}
	
	private void collectePanels() {
		this.setJMenuBar(menubar);
		this.add(panelDeEncryptMain);
	}

	/**
	 * 菜单栏
	 */
	JMenuBar menubar = new JMenuBar();
	private void panelMenuBar() {
		JMenu menusys = new JMenu();
		menusys.setMnemonic('E');
		menusys.setText("文件");
		JMenuItem sysaddkey = new JMenuItem("添加密钥");
		sysaddkey.setMnemonic('K');
		menusys.add(sysaddkey);
		
		JMenu menuhelp = new JMenu();
		menuhelp.setMnemonic('H');
		menuhelp.setText("帮助");
		JMenuItem helpinfo = new JMenuItem();
		helpinfo.setMnemonic('I');
		helpinfo.setText("操作说明");
		menuhelp.add(helpinfo);
		
		menubar.add(menusys);
		menubar.add(menuhelp);
		
		sysaddkey.addActionListener(new MenuAddKeyActionListener());
		helpinfo.addActionListener(new OpenURLAction("https://github.com/ylxb23/Tool3des/blob/master/README.md"));
	}
	
	class OpenURLAction implements ActionListener {
		String url;
		OpenURLAction(String uri) {
			url = uri;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(Desktop.isDesktopSupported()){
				URI uri = URI.create(url);
				Desktop desktop = Desktop.getDesktop();
				if(desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(uri);
					} catch (IOException ioe) {
						Logger.getGlobal().logp(Level.WARNING, "ToolMain", "OpenURLAction.actionPerformed", ioe.getMessage());
					}
				}
			}
		}
	}
	
	class MenuAddKeyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			keySetFrameDialog.setVisible(true);
		}
	}

	/**
	 * 
	 */
	JDialog keySetFrameDialog=new JDialog(_this,ModalityType.APPLICATION_MODAL);
	JTextField nameText = new JTextField(TEXT_LENGTH_OP);
	JTextField keyText = new JTextField(TEXT_LENGTH_OP);
	private void buildKeySetFrame() {
		JLabel nameLabel = new JLabel();
		nameLabel.setText("密钥名称: ");
		JLabel keyLabel = new JLabel();
		keyLabel.setText("密钥内容: ");
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(nameLabel, BorderLayout.WEST);
		namePanel.add(nameText, BorderLayout.CENTER);
		JPanel keyPanel = new JPanel(new BorderLayout());
		keyPanel.add(keyLabel, BorderLayout.WEST);
		keyPanel.add(keyText, BorderLayout.CENTER);
		JPanel addKeyPanel = new JPanel(new BorderLayout());
		JPanel enterPanel = new JPanel(new GridLayout(2, 1));
		enterPanel.add(namePanel);
		enterPanel.add(keyPanel);
		JPanel generatePanel = new JPanel(new GridLayout(2, 1));
		generatePanel.add(new JLabel());
		JButton generateKey = new JButton("生成");
		generatePanel.add(generateKey);
		addKeyPanel.add(enterPanel, BorderLayout.CENTER);
		addKeyPanel.add(generatePanel, BorderLayout.EAST);
		JPanel opPanel = new JPanel();
		JButton jbtComfirm = new JButton("添加");
		JButton jbtConcel = new JButton("取消");
		opPanel.add(jbtComfirm);
		opPanel.add(jbtConcel);
		addKeyPanel.add(opPanel, BorderLayout.SOUTH);
		keySetFrameDialog.add(addKeyPanel);
		keySetFrameDialog.setSize(330, 120);
		keySetFrameDialog.setLocationRelativeTo(_this);
		keySetFrameDialog.setLocationByPlatform(false);
		keySetFrameDialog.setTitle("添加密钥");
		keySetFrameDialog.setResizable(false);
		
		generateKey.addActionListener(new AddKeyOpActionListener(1));
		jbtComfirm.addActionListener(new AddKeyOpActionListener(2));
		jbtConcel.addActionListener(new AddKeyOpActionListener(3));
	}
	
	/**
	 * 
	 * @author zero.zeng
	 *
	 */
	class AddKeyOpActionListener implements ActionListener {
		int op;
		public AddKeyOpActionListener(int op) {
			this.op = op;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (op) {
			case 1:	// 生成密钥
				keyText.setText(RandomStringUtil.generateRandomString());
				break;
			case 2:	// 添加
				String key = keyText.getText();
				if(keyMap.containsValue(key)) {
					JOptionPane.showMessageDialog(_this, "密钥已存在!");
					return;
				}
				String name = nameText.getText();
				if(keyMap.containsKey(name)) {
					JOptionPane.showMessageDialog(_this, "密钥名称已存在!");
					return;
				}
				FileUtils.addKeySetProperties(name, key);
				keyMap.put(name, key);
				keySelect.addItem(name);
				hideAddKeyFrame();
				break;
			case 3:	// 取消
				hideAddKeyFrame();
				break;
			default:
				break;
			}
		}
	}

	private void hideAddKeyFrame() {
		keySetFrameDialog.setVisible(false);
		keyText.setText(null);
		nameText.setText(null);
	}
	
	/**
	 * 加解密功能面板
	 */
	JPanel panelDeEncryptMain = new JPanel();
	private void endecryptOpPanelLayout() {
		panelDeEncryptMain.setLayout(new BorderLayout());
		JPanel panelKey = new JPanel();
		JPanel panelDeEnCrypt = new JPanel();
		JPanel panelEncrypt = new JPanel();
		JPanel panelDecrypt = new JPanel();
		enterEncrypted = new JTextField(TEXT_LENGTH_OP);
		enterNoEncrypt = new JTextField(TEXT_LENGTH_OP);
		buttonEncrypt = new JButton("加密");
		buttonDecrypt = new JButton("解密");
		showEncryptResult = new JTextArea(2, TEXT_LENGTH);
		showDecryptResult = new JTextArea(2, TEXT_LENGTH);
		enterKey = new JTextField();
		panelKey.setLayout(new BorderLayout());
		panelKey.add(enterKey, BorderLayout.CENTER);
		// 增加选择
		loadKeySet();
		panelKey.add(keySelect, BorderLayout.WEST);
		panelDeEncryptMain.add(panelKey, BorderLayout.NORTH);
		Border borderKeySelect = BorderFactory.createTitledBorder("选择密钥");
		panelKey.setBorder(borderKeySelect);
		panelDeEncryptMain.add(panelDeEnCrypt, BorderLayout.CENTER);
		panelDeEnCrypt.setLayout(new GridLayout(2, 1));
		panelDeEnCrypt.add(panelEncrypt);
		panelDeEnCrypt.add(panelDecrypt);
		Border borderEncrypt = BorderFactory.createTitledBorder("DES加密");
		Border borderDecrypt = BorderFactory.createTitledBorder("DES解密");
		panelEncrypt.setBorder(borderEncrypt);
		panelDecrypt.setBorder(borderDecrypt);
		showEncryptResult.setBorder(BorderFactory.createTitledBorder("加密结果"));
		showDecryptResult.setBorder(BorderFactory.createTitledBorder("解密结果"));
		showEncryptResult.setEditable(false);
		showDecryptResult.setEditable(false);
		JPanel leftTop = new JPanel(new BorderLayout());
		leftTop.add(enterEncrypted, BorderLayout.CENTER);
		leftTop.add(buttonEncrypt, BorderLayout.EAST);
		panelEncrypt.add(leftTop, BorderLayout.NORTH);
		panelEncrypt.add(showEncryptResult, BorderLayout.CENTER);
		JPanel rightTop = new JPanel(new BorderLayout());
		rightTop.add(enterNoEncrypt, BorderLayout.CENTER);
		rightTop.add(buttonDecrypt, BorderLayout.EAST);
		panelDecrypt.add(rightTop, BorderLayout.NORTH);
		panelDecrypt.add(showDecryptResult, BorderLayout.CENTER);
	}

	ActionListener keySelectListener = null;
	private void loadKeySet() {
		keySelect = new JComboBox<>();
		reloadKeySet();
		Iterator<Entry<String, String>> it = keyMap.entrySet().iterator();
		while(it.hasNext()) {
			keySelect.addItem(it.next().getKey());
		}
		keySelect.setSelectedItem(KEY_SELECT_CUSTOM);
	}

	private void addListener() {
		// 加解密
		buttonEncrypt.addActionListener(new EncryptActionListener());
		buttonDecrypt.addActionListener(new DecryptActionListener());
		// 选择密钥
		keySelect.addActionListener(new KeySelectActionListener());
		// 回车键响应
		KeyListener keyListener = new EnterKeyListener();
		enterEncrypted.addKeyListener(keyListener);
		enterNoEncrypt.addKeyListener(keyListener);
		// 点击展示面板，将文本内容放到粘贴板
		SendToClipboardListener sendToClipboardListener = new SendToClipboardListener();
		showEncryptResult.addMouseListener(sendToClipboardListener);
		showDecryptResult.addMouseListener(sendToClipboardListener);
	}

	/**
	 * 将结果内容置于粘贴板
	 * @author leo.zeng
	 *
	 */
	class SendToClipboardListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JTextArea source = (JTextArea) e.getSource();
			String text = source.getText();
			if(text != null && !"".equals(text.trim())) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable cliptf =  new StringSelection(text);
				source.setSelectionStart(0);
				source.setSelectionEnd(text.length());
				clipboard.setContents(cliptf, null);
				JOptionPane.showMessageDialog(source, "文字已复制到粘贴板", "提示", JOptionPane.PLAIN_MESSAGE);
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	/**
	 * 回车键响应
	 *
	 * @author leo.zeng
	 */
	class EnterKeyListener implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyChar() == KeyEvent.VK_ENTER) {
				if(e.getSource() == enterEncrypted) {
					doEncrypt();
				} else if(e.getSource() == enterNoEncrypt) {
					doDecrypt();
				}
			}
		}
	}

	private void initFrame() {
		setDefaultCloseOperation(3);
		setSize(400, 363);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("ThreeDES 加解密工具");
	}
	

	class EncryptActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doEncrypt();
		}
	}
	/**
	 * 加密
	 */
	private void doEncrypt() {
		String phone = enterEncrypted.getText().trim();
		try {
			String des = ThreeDES.encryptMode(phone, getKey());
			showEncryptResult.setText(des);
			FileUtils.appendToLog(FileUtils.assemblyText(OperatorEnum.ENCRYPT, des, phone));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "未知错误", 0);
		}
	}
	
	class KeySelectActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doSelectKey();
		}
	}
	/**
	 * 选择密钥
	 */
	private void doSelectKey() {
		String k = (String) keySelect.getSelectedItem();
		String key = keyMap.get(k);
		if(k.equalsIgnoreCase(KEY_SELECT_CUSTOM)) {
			enterKey.setEditable(true);
		} else {
			enterKey.setEditable(false);
		}
		enterKey.setText(key);
	}

	class DecryptActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doDecrypt();
		}
	}
	/**
	 * 解密
	 */
	private void doDecrypt() {
		try {
			String des = enterNoEncrypt.getText().trim();
			String phone = ThreeDES.decryptMode(des, getKey());
			showDecryptResult.setText(phone);
			FileUtils.appendToLog(FileUtils.assemblyText(OperatorEnum.DECODE, des, phone));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "请输入正确密文!", "密文输入有误", 0);
		}
	}
	
	/**
	 * 获取密钥
	 * @return
	 */
	private String getKey() {
		String key = enterKey.getText();
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("密钥不能为空！");
		} else {
			if(key.length() != ThreeDES.keySize) {
				throw new IllegalArgumentException("密钥长度应该为 32位");
			}
		}
		return key.trim();
	}
	
	public static void main(String args[]) {
		ToolMain tool = new ToolMain();
		tool.setVisible(true);
	}


}
