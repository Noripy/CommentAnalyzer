package main;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;
import main.NicoLiveManager.Proc;

public class TestThread implements Runnable{

	DataOutputStream dos;
	NXTConnector conn;
	
	public TestThread(DataOutputStream dos, NXTConnector conn) {
		this.dos = dos;
		this.conn = conn;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Keyevent ke = new Keyevent();
		while(true){
			ke.checkFLAG();
			try {
				
				if (NicoLiveManager.controlMachine(ke.keyData) == Proc.STOP_MACHINE.ordinal()) {
					ke.keyData = ' ';
					ke.ki.key = ' ';
            		System.out.println("stop");
                    dos.writeInt(Proc.STOP_MACHINE.ordinal() + 4);//5 + 4;//車では9でstop
                    dos.flush(); 
                    NicoLiveManager.ExitTool();
                    break;
                    
				}else if(NicoLiveManager.controlMachine(ke.keyData) == Proc.RESTART_MACHINE.ordinal()){
            		ke.keyData = ' ';
            		ke.ki.key = ' ';
					System.out.println("restart");
                    dos.writeInt(Proc.RESTART_MACHINE.ordinal() + 2);//6 + 2;//車では8でrestart
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
