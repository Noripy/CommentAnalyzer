package main;


public class SpeedThread extends Thread{
	
	Keyevent ke;

	public SpeedThread(Keyevent ke) {
		this.ke = ke;
	}

	@Override
	public void run() {
		while(true){
			
			try{
				
				
				String s = ke.dis.readUTF();
				System.out.println(s);
				String[] sp = s.split(",");
				
				if(s.equals("GOAL")){
					ke.Goal();
				}else if(sp.length == 2){
					ke.NotGoal();
					ke.left_s.setText(sp[0]);
					ke.right_s.setText(sp[1]);
				}
				
			}catch(Exception e){
				
			}
			
		}
	
	}
	
}
