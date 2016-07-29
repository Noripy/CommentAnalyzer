package a;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class a {
	public static void main(String[] args){
		String mail = "tarachantakutaku@yahoo.co.jp";
		String password = "262421262421";
		login(mail, password);
	}
	 private final static String LOGINPAGE_URL = "https://secure.nicovideo.jp/secure/login?site=niconico";
     private final static String MYPAGE_URL = "http://www.nicovideo.jp/my";
     private final static String MYPAGE_SYMBOL = "さんのマイページ";
	public static boolean login(String mail, String password)
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
    			System.out.println(userSession);
    			System.out.println(analyze(userSession));
    			
                    
            } catch (MalformedURLException e) {
            	System.out.println("aaa");
                    Logger.writeException(e);
            } catch (IOException e) {
                    System.out.println("ioe");
            } finally {
                    if(http != null) http.disconnect();
            }
            
            return false;
    }
	
	public static String analyze(String mes){
		char[] array = mes.toCharArray();
		int i = 1;
		String buf = "";
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
}
