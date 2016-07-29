package a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public enum NicoLiveManager {
	I;
	
	private final static String LOGINPAGE_URL = "https://secure.nicovideo.jp/secure/login?site=niconico";
    private final static String MYPAGE_URL = "http://www.nicovideo.jp/my";
    private final static String MYPAGE_SYMBOL = "さんのマイページ";
	public static String login(String mail, String password)
   {

           String param = String.format("mail_tel=%s&password=%s&next_url=", mail, password);
           HttpsURLConnection http = null;
           try {

   			http = (HttpsURLConnection) Http.postConnection(LOGINPAGE_URL,
   					param, null);

   			String userSession = "";

   			Map headers = http.getHeaderFields();
   			Iterator it = headers.keySet().iterator();
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
//		//繝九さ繝九さ蜍慕判縺ｫ繝ｭ繧ｰ繧､繝ｳ縺励◆迥ｶ諷九〒Cookie縺九ｉuser_session繧偵→縺｣縺ｦ縺上ｋ
		
		String mail = JOptionPane.showInputDialog("mail");
		String pass = JOptionPane.showInputDialog("pass");
		
		String userSession = login(mail, pass);
		String lv = JOptionPane.showInputDialog("放送URL");
		
		
		// 謖�螳壹＠縺溘け繝�繧ｭ繝ｼ縺九ｉ繝槭う繝壹�ｼ繧ｸ縺ｮ諠�蝣ｱ繧貞叙蠕�
		if (NicoLiveManager.I.connectMyPageAndGetUserInfo(userSession))
			NicoLiveManager.I.printMyPageInfo();
		// 竭�繧ｳ繝｡繝ｳ繝亥叙蠕玲ｺ門ｙ
		NicoLiveManager.I.readyConnect(userSession, lv);
		// 竭｡繧ｳ繝｡繝ｳ繝亥叙蠕鈴幕蟋�
		NicoLiveManager.I.startConnect();
		while (true) {
			// System.out.println(NicoLiveManager.INST.getChat());
			// 竭｢繧ｳ繝｡繝ｳ繝亥叙蠕�
			NicoLiveManager.ChatAbsolute chatAbsolute = NicoLiveManager.I.getChatAbsolute();
			System.out.println(chatAbsolute);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 繝｡繝�繧ｻ繝ｼ繧ｸ繧ｵ繝ｼ繝舌↓謗･邯壹☆繧九◆繧√↓蠢�隕√↑諠�蝣ｱ縺ｪ縺ｩ
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
	 * 繝槭う繝壹�ｼ繧ｸ縺ｮ諠�蝣ｱ
	 */
	private String profURL;
	private String profImageMiniURL;
	private String userID;
	private String userName;

	/**
	 * 繝ｦ繝ｼ繧ｶ繝ｼ繧ｻ繝�繧ｷ繝ｧ繝ｳID縺ｨ逡ｪ邨ИD繧剃ｽｿ縺｣縺ｦ繧ｳ繝｡繝ｳ繝医し繝ｼ繝舌↓謗･邯壹☆繧九◆繧√�ｮ貅門ｙ繧偵☆繧�
	 * 
	 * @param cookieUserSession
	 * @param broadCastID
	 * @return
	 */
	public boolean readyConnect(String cookieUserSession, String broadCastID) {
		// 逡ｪ邨ИD繧定ｧ｣譫�
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
	 * 荳ｻ縺ｮ諠�蝣ｱ蜿門ｾ礼ｳｻ
	 */
	public String getUserID() {
		return this.user_id;
	}

	public String getNickName() {
		return this.nickname;
	}

	/**
	 * 謗･邯壹ｒ髢句ｧ�
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
	 * 謗･邯壹ｒ邨ゆｺ�
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
	 * 蜿門ｾ励＠縺溘メ繝｣繝�繝域枚蟄怜�励ｒ�ｼ代▽謚ｽ蜃ｺ
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
	 * 繝√Ε繝�繝域枚蟄怜�励°繧牙ｱ樊�ｧ蛟､繧偵�励Ο繝代ユ繧｣縺ｫ謖√▽繝√Ε繝�繝医け繝ｩ繧ｹ縺ｫ螟画鋤
	 * 
	 * @return
	 */
	public ChatAbsolute getChatAbsolute() {
		String s = getChat();
		return new ChatAbsolute(s);
	}

	/**
	 * 繝√Ε繝�繝医ｒ繧ｯ繝ｩ繧ｹ蛹�
	 */
	static class ChatAbsolute {
		static long offsetTime;
		boolean isHidden;
		boolean is184;
		boolean isPremium;
		String user_id;
		String anonymity;
		String locale;
		String content;
		String time;
		int sec;
		int min;
		int hour;

		public String toString() {
			return "time=" + this.time + " 184=" + this.is184 + " user_id=" + this.user_id + " premium="
					+ this.isPremium + " anonymity=" + this.anonymity + " locale=" + this.locale + " " + this.content;
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
	 * 縺翫∪縺�
	 **************************************/
	private String getAbsoluteValue(String absolute) {
		String[] absoluteValue = absolute.split("[=|\"]+");
		if (absoluteValue.length != 2) {
			System.out.println(absolute + "縺ｯ豁｣縺励￥螻樊�ｧ蛟､繧貞叙蠕励☆繧九％縺ｨ縺後〒縺阪∪縺帙ｓ");
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
		System.out.println("繝槭う繝壹�ｼ繧ｸ蜿門ｾ�");
		System.out.println("ID: " + this.userID);
		System.out.println("蜷榊燕: " + this.userName);
		System.out.println("繝励Ο繝慕判蜒酋RL: " + this.profURL);
		System.out.println("繝励Ο繝慕判蜒上Μ繧ｵ繧､繧ｺURL: " + this.profImageMiniURL);
	}

}