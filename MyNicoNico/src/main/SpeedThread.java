package main;


public class SpeedThread extends Thread{
	
	MainFrame frame;
	public static boolean endSW = false;

	public SpeedThread(MainFrame frame) {
		this.frame = frame;
	}

	@Override
	public void run() {
		while(true){
			if(endSW) break;
			try{
				
				
				String s = frame.dis.readUTF();
				System.out.println(s);
				String[] sp = s.split(",");
				
				if(s.equals("GOAL")){
					frame.Goal();
				}else if(sp.length == 2){
					frame.NotGoal();
					frame.left_s.setText(sp[0]);
					frame.right_s.setText(sp[1]);
				}
				
			}catch(Exception e){
				
			}
			
		}
	
	}
	
}
