package main;

import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public enum NicoLiveManager{
	I;
	
	public static int MAX_NUM = 1;
	public static int LEFT_UP_COUNT = 0;
	public static int LEFT_DOWN_COUNT = 0;
	public static int RIGHT_UP_COUNT = 0;
	public static int RIGHT_DOWN_COUNT = 0;
	
	public static int FLAG = 0;
	
	private final static String LOGINPAGE_URL = "https://secure.nicovideo.jp/secure/login?site=niconico";
	public static String login(String mail, String password)
   {

           String param = String.format("mail_tel=%s&password=%s&next_url=", mail, password);
           HttpsURLConnection http = null;
           try {

   			http = (HttpsURLConnection) Http.postConnection(LOGINPAGE_URL,
   					param, null);

   			String userSession = "";

   			Map<String, List<String>> headers = http.getHeaderFields();
   			Iterator<String> it = headers.keySet().iterator();
   			while (it.hasNext()) {
   				String key = (String) it.next();
   				//System.out.println(key + ":" + headers.get(key));
   				if (key != null && key.equals("Set-Cookie")) {
   					userSession = headers.get(key).toString();
   					
   				}
   			}
   			String s = analyze(userSession);
   			System.out.println(userSession);
   			System.out.println(s);
   			
            return s;
           } catch (MalformedURLException e) {
           	System.out.println("aaa");
                   Logger.writeException(e);
           } catch (IOException e) {
                   System.out.println("ioe");
           } finally {
                   if(http != null) http.disconnect();
           }
           
           return null;
           
   }
	
	public static String analyze(String mes){
		char[] array = mes.toCharArray();
		int i = 1;
		while(true){
			if(array[i] == 'u'){
				String s = "";
				for(int j = 0; j < 12; j++){
					s += array[i+j];
				}
				if(s.equals("user_session") && array[i + 12] == '='){
					String us = "";
					int k = 0;
					while(true){
						us += array[i + 13 + k];
						k++;
						if(array[i + 13 + k] == ';') break;
					}
					
					if(!us.equals("deleted"))return us;
				}
			}
			i++;
		}
	}
	
	public static void main(String[] args) {
		//ニコニコ動画にログインした状態でCookieからuser_sessionをとってくる
		
		String mail = JOptionPane.showInputDialog("mail");
		String pass = JOptionPane.showInputDialog("password");
		String lv = JOptionPane.showInputDialog("放送URL");
		
		while(true){
			try{
				MAX_NUM = Integer.parseInt(JOptionPane.showInputDialog("コメント最大値"));
				if(MAX_NUM < 1){
					MAX_NUM = 1;
				}
				break;
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null, "もういっかい");
			}
		}
		
		String userSession = login(mail, pass);

/////////////////////////////////////////////////////////////////////////////////
		
        NXTConnector conn = new NXTConnector();
        
        conn.addLogListener(new NXTCommLogListener(){
            public void logEvent(String message) {
                System.out.println("BTSend Log.listener: "+message);
                
            }
            public void logEvent(Throwable throwable) {
                System.out.println("BTSend Log.listener - stack trace: ");
                 throwable.printStackTrace();
                
            }
            
        } 
        );
        // Connect to any NXT over Bluetooth
        boolean connected = conn.connectTo("btspp://");
    
        
        if (!connected) {
            System.err.println("Failed to connect to any NXT");
            System.exit(1);
        }
        
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
/////////////////////////////////////////////////////////////////////////////////
        
		// 指定したクッキーからマイページの情報を取得
		if (NicoLiveManager.I.connectMyPageAndGetUserInfo(userSession))
			NicoLiveManager.I.printMyPageInfo();
		// 1.コメント取得準備
		NicoLiveManager.I.readyConnect(userSession, lv);
		// 2.コメント取得開始
		NicoLiveManager.I.startConnect();
		
		Keyevent ke = new Keyevent();
		
		while (true) {
			// System.out.println(NicoLiveManager.INST.getChat());
			// 3.コメント取得
			NicoLiveManager.ChatAbsolute chatAbsolute = NicoLiveManager.I.getChatAbsolute();
			System.out.println(chatAbsolute.content);
			
			//コメント処理
			String command = readComment(chatAbsolute.content);
			
			//命令数字
			int i = returnProcNum(command).ordinal();
			
			//送信用の命令系(出力は整数)
			System.out.println(i);
			
			ke.checkFLAG();
			
            try {
//              System.out.println("Sending " + i);
            	if (controlMachine(ke.keyData) == 9) {
                    dos.writeInt(Proc.STOP_MACHINE.ordinal() + 4);//5 + 4;//車では9でstop
				}else{
	                dos.writeInt(i);					
				}
                dos.flush();            
                
            } catch (IOException ioe) {
                System.out.println("IO Exception writing bytes:");
                System.out.println(ioe.getMessage());
            }           
/*
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	*/		
			if(ke.FLAG){//収束条件[qボタン押したら終了.]
				try {
		            dos.close();
		            conn.close();    
		            System.out.println("System finished.");
		            break;
		        } catch (IOException ioe) {
		            System.out.println("IOException closing connection:");
		            System.out.println(ioe.getMessage());
		            break;
		        }
			}
		}
		
		
	}

	public static String readComment(String str){
		char[] charArray = str.toCharArray();
	
		String val = "";//結果出力用変数
		for (char ch : charArray) {//先頭の文字から一つずつ取ってくる
			
			//送る信号と同じ整数も受け付ける
			if(ch == '1' || ch == '１' || ch == '2' || ch == '２' || ch == '3' || ch == '３' || ch == '4' || ch == '４'){
				val = convertInttoOrder(ch);
				return val;
			}
//			System.out.println(ch); //1文字ずつとれてるかチェック
			if (ch == '左' || ch == '右') {
				if (val.length() == 0) {
					val += ch;
				}else {
					val = "";
				}
			}
			
			if (ch == '上' || ch == '下') {
				if (val.length() == 1) {
					val += ch;
					return val;
				}else {
					val = "";
				}
			}
			
		}
		
		return "";
	}
	
	public static String convertInttoOrder(char c){
		switch (c) {
		case '1':
			return "左上";
		case '１':
			return "左上";
		case '2':
			return "左下";
		case '２':
			return "左下";
		case '3':
			return "右上";
		case '３':
			return "右上";
		case '4':
			return "右下";
		case '４':
			return "右下";
		default:
			return "";
		}
	}
	
	public enum Proc{
		NO_ACTION,//0
		TOP_LEFT,//1
		BUTTOM_LEFT,//2
		TOP_RIGHT,//3
		BUTTOM_RIGHT,//4
		STOP_MACHINE//5
	}
	
	public static Proc returnProcNum(String str){
		switch (str) {
		case "":
			return Proc.NO_ACTION;		
		case "左上":
			LEFT_UP_COUNT++;
			if(LEFT_UP_COUNT == MAX_NUM){
				LEFT_UP_COUNT = 0;
				return Proc.TOP_LEFT;
			}
			return Proc.NO_ACTION;
		case "左下":
			LEFT_DOWN_COUNT++;
			if(LEFT_DOWN_COUNT == MAX_NUM){
				LEFT_DOWN_COUNT = 0;
				return Proc.BUTTOM_LEFT;
			}
			return Proc.NO_ACTION;
		case "右上":
			RIGHT_UP_COUNT++;
			if(RIGHT_UP_COUNT == MAX_NUM){
				RIGHT_UP_COUNT = 0;
				return Proc.TOP_RIGHT;
			}
			return Proc.NO_ACTION;
		case "右下":
			RIGHT_DOWN_COUNT++;
			if(RIGHT_DOWN_COUNT == MAX_NUM){
				RIGHT_DOWN_COUNT = 0;
				return Proc.BUTTOM_RIGHT;
			}
			return Proc.NO_ACTION;
		default:
			return Proc.NO_ACTION;//命令操作なし
		}
	}

	public static int controlMachine(char key){
		switch (key) {
		case 'q': //'Quit'
			return Proc.STOP_MACHINE.ordinal();

		default:
			return 0;
		}
		
	}
	/*
	 * メッセージサーバに接続するために必要な情報など
	 */
	private String user_id;
	private String nickname;
	private String addr;
	private String port;
	private String thread;
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;

	/*
	 * マイページの情報
	 */
	private String profURL;
	private String profImageMiniURL;
	private String userID;
	private String userName;

	/**
	 * ユーザーセッションIDと番組IDを使ってコメントサーバに接続するための準備をする
	 * 
	 * @param cookieUserSession
	 * @param broadCastID
	 * @return
	 */
	public boolean readyConnect(String cookieUserSession, String broadCastID) {
		// 番組IDを解析
		String parseBroadCastID = null;
		for (String s : broadCastID.split("[/?]"))
			if (s.startsWith("lv"))
				parseBroadCastID = s;
		OutputStreamWriter w = null;
		XMLEventReader r = null;
		try {
			URLConnection con = new URL("http://live.nicovideo.jp/api/getplayerstatus").openConnection();
			con.setRequestProperty("Cookie", "user_session=" + cookieUserSession);
			String data = "v=" + parseBroadCastID;
			con.setDoOutput(true);
			w = new OutputStreamWriter(con.getOutputStream());
			w.write(data);
			w.flush();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			r = factory.createXMLEventReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			int open_time = 0;
			int start_time = 0;
			this.addr = this.port = this.thread = null;
			while (r.hasNext()) {
				XMLEvent e = r.nextEvent();
				switch (e.toString()) {
				case "<user_id>":
					this.user_id = r.getElementText();
					break;
				case "<nickname>":
					this.nickname = r.getElementText();
					break;
				case "<thread>":
					this.thread = r.getElementText();
					break;
				case "<addr>":
					this.addr = r.getElementText();
					break;
				case "<port>":
					this.port = r.getElementText();
					break;
				case "<open_time>":
					open_time = Integer.parseInt(r.getElementText());
					break;
				case "<start_time>":
					start_time = Integer.parseInt(r.getElementText());
					break;
				}
			}
			ChatAbsolute.offsetTime = open_time - start_time;
			if ((this.addr == null) || (this.port == null) || (this.thread == null))
				return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (w != null)
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (r != null)
				try {
					r.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
		}
	}

	/*
	 * 主の情報取得系
	 */
	public String getUserID() {
		return this.user_id;
	}

	public String getNickName() {
		return this.nickname;
	}

	/**
	 * 接続を開始
	 * 
	 * @return
	 */
	public boolean startConnect() {
		try {
			this.socket = new Socket();
			this.socket.connect(new InetSocketAddress(this.addr, Integer.parseInt(this.port)));

			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.printWriter.write(
					"<thread thread=\"" + this.thread + "\" version=\"20061206\" res_from=\"-0\" scores=\"1\" />\0");
			this.printWriter.flush();

			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			int c;
			while ((c = this.bufferedReader.read()) != -1) {
				if ((char) c == 0)
					break;
				sb.append((char) c);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 接続を終了
	 */
	public void endConnect() {
		if (this.printWriter != null)
			this.printWriter.close();
		if (this.bufferedReader != null)
			try {
				this.bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (this.socket != null)
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 取得したチャット文字列を１つ抽出
	 * 
	 * @return
	 */
	public String getChat() {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			int c;
			while ((c = this.bufferedReader.read()) != -1) {
				if ((char) c != 0) {
					stringBuilder.append((char) c);
					if (((char) c == '>') && (stringBuilder.toString().endsWith("</chat>")))
						return stringBuilder.toString();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * チャット文字列から属性値をプロパティに持つチャットクラスに変換
	 * 
	 * @return
	 */
	public ChatAbsolute getChatAbsolute() {
		String s = getChat();
		return new ChatAbsolute(s);
	}

	/**
	 * チャットをクラス化
	 */
	static class ChatAbsolute {
		static long offsetTime;
		boolean isHidden;
		boolean is184;
		boolean isPremium;
		String user_id;
		String anonymity;
		String locale;
		String content;// comment
		String time;
		int sec;
		int min;
		int hour;

		public String toString() {
//			return "time=" + this.time + " 184=" + this.is184 + " user_id=" + this.user_id + " premium="
//					+ this.isPremium + " anonymity=" + this.anonymity + " locale=" + this.locale + " " + this.content;
			return this.content;
		}

		public ChatAbsolute(String chat) {
			int first = chat.indexOf(">") + 1;
			int last = chat.lastIndexOf("<");
			this.content = chat.substring(first, last);
			for (String s : chat.split(" |>"))
				if (!s.startsWith("no")) {
					if (s.startsWith("mail")) {
						if (parseValue(s).equals("184")) {
							this.is184 = true;
						} else {
							this.isHidden = true;
							break;
						}
					} else if (s.startsWith("user_id")) {
						this.user_id = parseValue(s);
					} else if (s.startsWith("premium")) {
						if (parseValue(s).equals("1")) {
							this.isPremium = true;
						}
					} else if (s.startsWith("date=")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis((Long
								.parseLong(parseValue(s)) /* + offsetTime */) * 1000);
						this.sec = calendar.get(Calendar.SECOND);
						this.min = calendar.get(Calendar.MINUTE);
						this.hour = calendar.get(Calendar.HOUR_OF_DAY);
						this.time = this.hour + ":" + this.min + ":" + this.sec;
					} else if (s.startsWith("anonymity")) {
						this.anonymity = parseValue(s);
					} else if (s.startsWith("locale"))
						this.locale = parseValue(s);
				}
		}

		private static final String parseValue(String absoluteString) {
			int first = absoluteString.indexOf("\"") + 1;
			int last = absoluteString.lastIndexOf("\"");
			if (last < first)
				return absoluteString.substring(first);
			return absoluteString.substring(first, last);
		}
	}

	/**************************************
	 * おまけ
	 **************************************/
	private String getAbsoluteValue(String absolute) {
		String[] absoluteValue = absolute.split("[=|\"]+");
		if (absoluteValue.length != 2) {
			System.out.println(absolute + "は正しく属性値を取得することができません");
			return null;
		}
		return absoluteValue[1];
	}

	public boolean connectMyPageAndGetUserInfo(String user_session) {
		this.userName = (this.userID = this.profURL = this.profImageMiniURL = null);
		if (user_session == null)
			return false;
		String[] split = user_session.split("_");
		if (split.length < 2)
			return false;
		this.userID = split[2];
		BufferedReader bufferedReader = null;
		try {
			URLConnection con = new URL("http://www.nicovideo.jp/user/" + this.userID).openConnection();
			con.setRequestProperty("Cookie", "user_session=" + user_session);
			con.setDoOutput(true);
			con.connect();

			bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String s;
			while ((s = bufferedReader.readLine()) != null) {
				if (s.matches(".*http://usericon\\.nimg\\.jp/usericon/s/.*")) {
					for (String absolute : s.split(">|<| ")) {
						if (absolute.startsWith("data-nico-userIconUrl")) {
							this.profImageMiniURL = getAbsoluteValue(absolute);
							this.profURL = "";
							for (String ss : this.profImageMiniURL.split("/s"))
								this.profURL += ss;
						}
					}
				}
				if (s.matches(".*siteHeaderUserNickNameContainer.*")) {
					int first = s.indexOf(">") + 1;
					int last = s.lastIndexOf("<");
					this.userName = s.substring(first, last - 3);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return (this.userName != null) && (this.userID != null) && (this.profURL != null)
				&& (this.profImageMiniURL != null);
	}

	public void printMyPageInfo() {
		System.out.println("マイページ取得");
		System.out.println("ID: " + this.userID);
		System.out.println("名前: " + this.userName);
		System.out.println("プロフ画像URL: " + this.profURL);
		System.out.println("プロフ画像リサイズURL: " + this.profImageMiniURL);
	}

}