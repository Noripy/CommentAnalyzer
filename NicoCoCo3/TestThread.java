package main;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;
import main.NicoLiveManager.Proc;

public class TestThread extends Thread{

	DataOutputStream dos;
	NXTConnector conn;
	Keyevent ke;
	
	public TestThread(DataOutputStream dos, NXTConnector conn, Keyevent ke) {
		this.dos = dos;
		this.conn = conn;
		this.ke = ke;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		 
		while(true){
			ke.checkFLAG();
			try {
				
				if (NicoLiveManager.controlMachine(ke.keyData) == Proc.STOP_MACHINE.ordinal()) {
					ke.keyData = ' ';
					ke.ki.key = ' ';
            		System.out.println("stop");
                    dos.writeInt(Proc.STOP_MACHINE.ordinal());//車をstop
                    dos.flush(); 
                    NicoLiveManager.ExitTool();
                    break;
                    
				}else if(NicoLiveManager.controlMachine(ke.keyData) == Proc.RESTART_MACHINE.ordinal()){
            		ke.keyData = ' ';
            		ke.ki.key = ' ';
					System.out.println("restart");
					System.out.println(Proc.RESTART_MACHINE.ordinal());
                    dos.writeInt(Proc.RESTART_MACHINE.ordinal());//車をrestart
                    dos.flush(); 
				}
                            
                
            } catch (IOException ioe) {
                System.out.println("IO Exception writing bytes:");
                System.out.println(ioe.getMessage());
            }    
		}
		
		if(ke.FLAG){//収束条件[qボタン押したら終了.]
			try {
	            dos.close();
	            conn.close();    
	            System.out.println("System finished.");
	           
	        } catch (IOException ioe) {
	            System.out.println("IOException closing connection:");
	            System.out.println(ioe.getMessage());
	            
	        }
		}
	}
	
}
