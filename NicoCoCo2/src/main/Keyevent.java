package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Keyevent {

	/*
	public static void main(String[] args) {
		Keyevent t = new Keyevent();
	}
	*/
	char keyData;
	KeyboardInput ki;
	boolean FLAG;
	
	public Keyevent(){
		
		JFrame frame = new JFrame();
		frame.setSize(250,250);
		frame.setTitle("Keyevent");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		ki = new KeyboardInput(); 
		frame.addKeyListener(ki);
		FLAG = false;
	}
	
	public char getKeyData(){
		this.keyData = ki.getKey();
		return this.keyData; 
	}
	
	public void checkFLAG(){
		if(this.keyData == 'q'){//収束条件[qボタン押したら終了.]
			FLAG = true;
		}
		FLAG = false;		
	}
}

class KeyboardInput implements KeyListener{

	char key;
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char key = e.getKeyChar();
		setKey(key);
		System.out.println(key);
		
		
	}
	
	public void setKey(char k){
		this.key = k;
	}
	public char getKey(){
		return this.key;
	}
}
