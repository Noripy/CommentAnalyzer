package CommentAnalyzer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetCommentData {

	public static String http(String url) throws Exception {

	    	// リクエスト送信
			URL requestUrl = new URL(url);
			HttpURLConnection connection =
					(HttpURLConnection) requestUrl.openConnection();
			InputStream input = connection.getInputStream();

	    	// 結果取得
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "UTF-8"));

			String line;
			StringBuilder tmpResult = new StringBuilder();
			while ( (line = reader.readLine() ) != null) {
				tmpResult.append(line + "\n");
			}
			reader.close();
			return tmpResult.toString();
		}
	
	public static void main(String[] args) {
		
		try {
			System.out.println(http("http://ext.nicovideo.jp/api/getthumbinfo/sm29192994"));			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}
}
