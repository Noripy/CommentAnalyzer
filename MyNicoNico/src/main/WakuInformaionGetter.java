package main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



public class WakuInformaionGetter {
	public static void main(String[] args){
		String mail = JOptionPane.showInputDialog("mail");
		String pass = JOptionPane.showInputDialog("password");
		
		String lv = JOptionPane.showInputDialog("放送URL");
		
		
		
		String userSession = login(mail, pass);
		
		WakuInformation waku = getWakuInfromation(userSession, lv);
		
		
		if(waku.room != null && waku.addr != null && waku.port != null && waku.thread != null){
			System.out.println(waku.room + "\n" + waku.addr + "\n" + waku.port + "\n" + waku.thread);
		}
	}
	
	
	
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
	
	public static WakuInformation getWakuInfromation(String cookieUserSession, String broadCastID) {
		WakuInformation waku = new WakuInformation();
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
			
			
			while (r.hasNext()) {
				XMLEvent e = r.nextEvent();
				switch (e.toString()) {
				
				case "<thread>":
					waku.thread = r.getElementText();
					break;
				case "<addr>":
					waku.addr = r.getElementText();
					break;
				case "<port>":
					waku.port = r.getElementText();
					break;
				case "<room_label>":
					waku.room = r.getElementText();
					break;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
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
		
		return waku;
	}
}
