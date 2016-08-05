package main;

public class WakuInformation {
	String room = null;
	String addr = null;
	String port = null;
	String thread = null;
	
	public WakuInformation(){}
	
	public WakuInformation(String room, String addr, String port, String thread){
		this.room = room;
		this.addr = addr;
		this.port = port;
		this.thread = thread;
	}
	
	public WakuInformation(String addr, String port, String thread){
		this.addr = addr;
		this.port = port;
		this.thread = thread;
	}
}
