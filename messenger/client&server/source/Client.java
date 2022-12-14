
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.text.ParseException;
import java.time.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;

public class Client extends Thread {
	final JFrame jfr;

	final JTextPane tpChatOutput;
	final JScrollPane spChatOutput;

	final JTextPane tpUserList;
	final JScrollPane spUserList;

	final JTextField tfChatInput;
	final JScrollPane spChatInput;

	final JTextField tfPort;
	final JTextField tfAddress;
	final JTextField tfName;

	final JButton btnConnect;
	final JButton btnDisconnect;
	final JButton btnSend;

	final JLabel lblWeatherInfo;
	final JLabel lblStockInfo;

	private String oldMsg = "";
	private Thread read;

	private String SERVER;
	private int PORT;
	private String NICKNAME;

	Weather weather = new Weather();
	Stock stock = new Stock();
	BufferedReader input;
	PrintWriter output;
	Socket server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		new Client();
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		// set server nickname, port number, and nickname
		this.SERVER = "localhost";
		this.PORT = 12345;
		this.NICKNAME = "nickname";

		// set font
		String fontfamily = "Arial, sans-serif";
		Font font = new Font(fontfamily, Font.PLAIN, 15);

		/* Frame: Chat */
		jfr = new JFrame("Chat");
		jfr.getContentPane().setLayout(null);
		jfr.setSize(500, 800);
		jfr.setResizable(false);
		jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Label: Stock Info (USDKRW) */
		lblStockInfo = new JLabel();
		lblStockInfo.setBorder(null);
		lblStockInfo.setFont(font);
		lblStockInfo.setBounds(250, 10, 150, 40);
		try {
			String price = stock.getStockPrice();
			lblStockInfo.setText("USDKRW " + price + "₩");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		/* Label: Weather Info (Temperature) */
		lblWeatherInfo = new JLabel();
		lblWeatherInfo.setBorder(null);
		lblWeatherInfo.setFont(font);
		lblWeatherInfo.setBounds(400, 10, 100, 40);
		try {
			String weatherInfo = weather.getWeather();

			String temp = weatherInfo.substring(weatherInfo.indexOf("\"category\":\"T1H\""),
					weatherInfo.indexOf("\"category\":\"UUU\""));
			temp = temp.substring(temp.indexOf("\"obsrValue\":\"") + "\"obsrValue\":\"".length(), temp.indexOf("\"}"));

			lblWeatherInfo.setText(temp + '\u00B0' + "C");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/* Text Pane: Chat Output */
		tpChatOutput = new JTextPane();
		tpChatOutput.setBounds(10, 70, 350, 640);
		tpChatOutput.setFont(font);
		tpChatOutput.setMargin(new Insets(6, 6, 6, 6));
		tpChatOutput.setEditable(false);
		tpChatOutput.setContentType("text/html");
		tpChatOutput.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		tpChatOutput.setBackground(Color.LIGHT_GRAY);
		// chat info
		appendToPane(tpChatOutput,
				"<h3><b>The possible commands in the chat are:</b></h4>" + "<ul>"
						+ "<h4>Insert <b>@nickname</b> to send a private message.</h4>"
						+ "<h4>Type hexadecimal code to change the color of profile.</h4>"
						+ "<h4>Some emojis are implemented.</h4>" + "</ul><br/>");

		/* Scroll Pane: Chat Output */
		spChatOutput = new JScrollPane(tpChatOutput);
		spChatOutput.setBounds(10, 70, 350, 640);

		/* Text Pane: User list */
		tpUserList = new JTextPane();
		tpUserList.setBounds(360, 70, 130, 640);
		tpUserList.setEditable(true);
		tpUserList.setFont(font);
		tpUserList.setMargin(new Insets(6, 6, 6, 6));
		tpUserList.setEditable(false);
		tpUserList.setContentType("text/html");
		tpUserList.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		tpUserList.setBackground(Color.LIGHT_GRAY);

		/* Scroll Pane: User list */
		spUserList = new JScrollPane(tpUserList);
		spUserList.setBounds(360, 70, 130, 640);

		/* Text Field: Chat Input */
		tfChatInput = new JTextField();
		tfChatInput.setBounds(10, 690, 500, 50);
		tfChatInput.setFont(font);
		tfChatInput.setMargin(new Insets(6, 6, 6, 6));
		// keboard input listener
		tfChatInput.addKeyListener(new KeyAdapter() {
			// send message on Enter key pressed
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
				// get last message on Up key pressed
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					String currentMessage = tfChatInput.getText().trim();
					tfChatInput.setText(oldMsg);
					oldMsg = currentMessage;
				}
				// get last message on Down key pressed
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					String currentMessage = tfChatInput.getText().trim();
					tfChatInput.setText(oldMsg);
					oldMsg = currentMessage;
				}
			}
		});

		/* Scroll Pane: Chat Input */
		spChatInput = new JScrollPane(tfChatInput);
		spChatInput.setBounds(10, 720, 350, 40);

		/* Button: Send */
		btnSend = new JButton("Send");
		btnSend.setFont(font);
		btnSend.setBounds(360, 720, 130, 40);
		// send mseesage on Send button clicked
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				sendMessage();
			}
		});

		/* Button: Disconnect */
		btnDisconnect = new JButton("X");
		btnDisconnect.setFont(font);
		btnDisconnect.setBounds(10, 10, 40, 40);
		// on disconnection
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// add elements
				jfr.add(tfName);
				jfr.add(tfPort);
				jfr.add(tfAddress);
				jfr.add(btnConnect);
				jfr.add(lblWeatherInfo);
				jfr.add(lblStockInfo);

				// remove elements
				jfr.remove(btnSend);
				jfr.remove(spChatInput);
				jfr.remove(btnDisconnect);
				jfr.remove(tpChatOutput);

				jfr.revalidate();
				jfr.repaint();
				read.interrupt();

				// clear ChatOutput TextPane
				tpChatOutput.setText(null);
				tpChatOutput.setBackground(Color.LIGHT_GRAY);
				appendToPane(tpChatOutput, "<span>Connection closed.</span>");

				// clear UserList TextPane
				tpUserList.setText(null);
				tpUserList.setBackground(Color.LIGHT_GRAY);

				output.close();
			}
		});

		/* Text Field: SERVER */
		tfAddress = new JTextField(this.SERVER);
		tfAddress.setBounds(10, 720, 110, 40);

		/* Text Field: PORT */
		tfPort = new JTextField(Integer.toString(this.PORT));
		tfPort.setBounds(130, 720, 110, 40);

		/* Text Field: NICKNAME */
		tfName = new JTextField(this.NICKNAME);
		tfName.setBounds(240, 720, 120, 40);

		/* Button: Connect */
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(360, 720, 130, 40);
		btnConnect.setFont(font);
		// on connection
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					SERVER = tfAddress.getText();
					String port = tfPort.getText();
					PORT = Integer.parseInt(port);
					NICKNAME = tfName.getText();

					server = new Socket(SERVER, PORT);
					input = new BufferedReader(new InputStreamReader(server.getInputStream()));
					output = new PrintWriter(server.getOutputStream(), true);

					// send nickname to server
					output.println(NICKNAME);

					// create new Read thread
					read = new Read();
					read.start();

					// remove elements
					jfr.remove(tfName);
					jfr.remove(tfPort);
					jfr.remove(tfAddress);
					jfr.remove(btnConnect);

					// add elements
					jfr.add(btnSend);
					jfr.add(spChatInput);
					jfr.add(btnDisconnect);
					jfr.add(lblWeatherInfo);
					jfr.add(lblStockInfo);

					jfr.revalidate();
					jfr.repaint();

					// set ChatOutput TextPane
					tpChatOutput.setBackground(Color.WHITE);
					appendToPane(tpChatOutput, "<span>Connecting to " + SERVER + " on port " + PORT + "...</span>");
					appendToPane(tpChatOutput, "<span>Connected to " + server.getRemoteSocketAddress() + "</span>");

					// set UserList TextPane
					tpUserList.setBackground(Color.WHITE);

				} catch (Exception ex) {
					appendToPane(tpChatOutput, "<span>Could not connect to Server</span>");
					JOptionPane.showMessageDialog(jfr, ex.getMessage());
				}
			}
		});

		// make sure all fields are not empty
		tfName.getDocument().addDocumentListener(new TextListener(tfName, tfPort, tfAddress, btnConnect));
		tfPort.getDocument().addDocumentListener(new TextListener(tfName, tfPort, tfAddress, btnConnect));
		tfAddress.getDocument().addDocumentListener(new TextListener(tfName, tfPort, tfAddress, btnConnect));

		// add elements
		jfr.add(btnConnect);
		jfr.add(spChatOutput);
		jfr.add(spUserList);
		jfr.add(tfName);
		jfr.add(tfPort);
		jfr.add(tfAddress);
		jfr.add(lblWeatherInfo);
		jfr.add(lblStockInfo);

		// show frame
		jfr.setVisible(true);
	}

	/**
	 * Check if if all fields are not empty.
	 */
	public class TextListener implements DocumentListener {
		JTextField tf1;
		JTextField tf2;
		JTextField tf3;
		JButton btnConnect;

		public TextListener(JTextField tf1, JTextField tf2, JTextField tf3, JButton btnConnect) {
			this.tf1 = tf1;
			this.tf2 = tf2;
			this.tf3 = tf3;
			this.btnConnect = btnConnect;
		}

		public void changedUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
			if (tf1.getText().trim().equals("") || tf2.getText().trim().equals("") || tf3.getText().trim().equals("")) {
				btnConnect.setEnabled(false);
			} else {
				btnConnect.setEnabled(true);
			}
		}

		public void insertUpdate(DocumentEvent e) {
			if (tf1.getText().trim().equals("") || tf2.getText().trim().equals("") || tf3.getText().trim().equals("")) {
				btnConnect.setEnabled(false);
			} else {
				btnConnect.setEnabled(true);
			}
		}
	}

	/**
	 * Send messages.
	 */
	public void sendMessage() {
		try {
			String message = tfChatInput.getText().trim();
			if (message.equals("")) {
				return;
			}
			this.oldMsg = message;
			output.println(message);
			tfChatInput.requestFocus();
			tfChatInput.setText(null);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Read new incoming messages.
	 */
	class Read extends Thread {
		/**
		 * Implement the behavior of a thread.
		 */
		public void run() {
			String message;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					message = input.readLine();
					if (message != null) {
						if (message.charAt(0) == '[') {
							message = message.substring(1, message.length() - 1);
							ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
							tpUserList.setText(null);
							for (String user : ListUser) {
								appendToPane(tpUserList, "@" + user);
							}
						} else {
							appendToPane(tpChatOutput, message);
						}
					}
				} catch (IOException ex) {
					System.err.println("Failed to parse incoming message");
				}
			}
		}
	}

	/**
	 * Send HTML to pane.
	 */
	private void appendToPane(JTextPane tp, String msg) {
		HTMLDocument doc = (HTMLDocument) tp.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			tp.setCaretPosition(doc.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Weather {
	public String getWeather() throws IOException {
		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
		String serviceKey = "sJPKLBcskxzTA1YIFY9yM%2BxKfSTav%2FyOBpvLdaIDPMdleSCAQQFFb6NKxa%2Fy1xKUYggSxnrvV%2BpaVWegwWnBgQ%3D%3D";
		// 가천대 좌표 37.45, 127.13
		// 서울 좌표 37.57142, 126.9658
		// 정상 좌표 38, 128
		String nx = "38"; // 위도
		String ny = "128"; // 경도

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

		LocalDateTime ldtNow = LocalDateTime.now();
		// String now = ldtNow.format(formatter);
		// System.out.println(now);

		LocalDateTime ldtEarlier = ldtNow.minusMinutes(30);
		String earlier = ldtEarlier.format(formatter);
		// System.out.println(earlier);

		String baseDate = earlier.substring(0, 8); // 조회하고싶은 날짜
		String baseTime = earlier.substring(8, 12); // 조회하고싶은 시간

		String type = "JSON"; // 타입 (xml, json 등)

		StringBuilder urlBuilder = new StringBuilder(apiUrl); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /* Service Key */
		urlBuilder
				.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지번호 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode("1000", "UTF-8")); /* 한 페이지 결과 수 */
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "="
				+ URLEncoder.encode(type, "UTF-8")); /* 요청자료형식(XML/JSON) Default: XML */
		urlBuilder.append(
				"&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*  */
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "="
				+ URLEncoder.encode(baseTime, "UTF-8")); /* 06시 발표(정시단위) */
		urlBuilder.append(
				"&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /* 예보지점의 X 좌표값 */
		urlBuilder.append(
				"&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /* 예보지점의 Y 좌표값 */

		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		// System.out.println("Response code: " + conn.getResponseCode());

		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		rd.close();
		conn.disconnect();

		// POP 강수확률 %
		// PTY 강수형태 코드값
		// R06 6시간 강수량 mm
		// REH 습도 %
		// RN1 1시간 강수량 mm
		// S06 6시간 신적설 cm
		// SKY 하늘상태 코드값
		// T1H 1시간 기온 C
		// T3H 3시간 기온 C
		// TMN 아침 최저기온 C
		// TMX 낮 최고기온 C
		// UUU 풍속(동서성분) m/s
		// VVV 풍속(남북성분) m/s
		// WSD 풍속 m/s
		// VEC 풍향 deg

		// System.out.println(sb.toString());

		return sb.toString();
	}
}

class Stock {
	public String getStockPrice() throws ParseException {
		Document doc;
		String value = "";

		String URL = "https://finance.naver.com/marketindex/exchangeDetail.naver?marketindexCd=FX_USDKRW";
		try {
			doc = Jsoup.connect(URL).get();

			Elements html = doc.select(".selectbox-default[selected]");

			// check if the element was found
			if (html.size() > 0) {
				// get the first element
				Element juga = html.first();

				// get the value of the value attribute
				value = juga.val();
				// System.out.println(value);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return value;
	}
}
