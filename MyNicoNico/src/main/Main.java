package main;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import lejos.pc.comm.NXTConnector;

public class Main {
	
	
	
	public static void main(String[] args){
		
		
		
		ArrayList<WakuInformation> wakus = new ArrayList<WakuInformation>();
		
		while(true){
			String addr = JOptionPane.showInputDialog("アドレス");
			if(addr.equals(""))break;
			String port = JOptionPane.showInputDialog("ポート");
			if(port.equals(""))break;
			String thread = JOptionPane.showInputDialog("スレッド");
			if(thread.equals(""))break;
			wakus.add(new WakuInformation(addr, port, thread));
			
		}
		
		
		
		NXTConnector conn = new NXTConnector();
		
		boolean connected = conn.connectTo("btspp://");
    
        
        if (!connected) {
            System.err.println("Failed to connect to any NXT");
            System.exit(1);
        }
		
		MainFrame frame = new MainFrame(conn);
		Thread speedThread = new SpeedThread(frame);
		
		speedThread.start();
		for(int i = 0; i < wakus.size(); i++){
			new CommentThread(conn, wakus.get(i)).start();
		}
		
		
		
		
	}
	
}
