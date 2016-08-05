package main;

import lejos.pc.comm.NXTConnector;

public class Main {
	
	
	
	public static void main(String[] args){
		
		String addr = "omsg101.live.nicovideo.jp";
		String port = "2812";
		String thread = "1539190246";
		WakuInformation waku = new WakuInformation();
		waku.addr = addr;
		waku.port = port;
		waku.thread = thread;
		
		NXTConnector conn = new NXTConnector();
		
		boolean connected = conn.connectTo("btspp://");
    
        
        if (!connected) {
            System.err.println("Failed to connect to any NXT");
            System.exit(1);
        }
		
		MainFrame frame = new MainFrame(conn);
		Thread speedThread = new SpeedThread(frame);
		Thread commentThread = new CommentThread(conn, waku);
		speedThread.start();
		commentThread.start();
		
	}
	
}
