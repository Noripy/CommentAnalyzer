package main;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lejos.pc.comm.NXTConnector;
import main.CommentThread.Proc;

public class MainFrame implements KeyListener{
	JLabel left;
	JLabel right;
	JLabel left_s;
	JLabel right_s;
	JLabel goal;
	DataInputStream dis;
	DataOutputStream dos;

	JFrame frame;
	JPanel panel;

	char keyData;
	

	public MainFrame(NXTConnector conn) {

		frame = new JFrame();
		frame.setSize(500, 500);
		frame.setTitle("Keyevent");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();

		left = new JLabel("左");
		right = new JLabel("右");
		left_s = new JLabel("25");
		right_s = new JLabel("25");
		goal = new JLabel("GOAL!!!!!");

		left.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		right.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		left_s.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		right_s.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		goal.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 30));
		panel.setLayout(null);

		left.setBounds(100, 100, 50, 30);
		right.setBounds(200, 100, 50, 30);
		left_s.setBounds(100, 150, 50, 30);
		right_s.setBounds(200, 150, 50, 30);
		goal.setBounds(100, 100, 500, 500);
		left.setVisible(true);
		right.setVisible(true);
		left_s.setVisible(true);
		right_s.setVisible(true);
		goal.setVisible(false);

		panel.add(left);
		panel.add(right);
		panel.add(left_s);
		panel.add(right_s);
		panel.add(goal);

		frame.add(panel);
		panel.setVisible(true);

		frame.setVisible(true);

		dis = new DataInputStream(conn.getInputStream());
		dos = new DataOutputStream(conn.getOutputStream());
		
		frame.addKeyListener(this);
	}

	public void Goal() {
		left.setVisible(false);
		right.setVisible(false);
		left_s.setVisible(false);
		right_s.setVisible(false);
		goal.setVisible(true);
	}

	public void NotGoal() {
		left.setVisible(true);
		right.setVisible(true);
		left_s.setVisible(true);
		right_s.setVisible(true);
		goal.setVisible(false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		try{
		if (e.getKeyChar() == 'q') {
			System.out.println("stop");
            dos.writeInt(Proc.STOP_MACHINE.ordinal());//車をstop
            dos.flush(); 
            CommentThread.endSW = true;
            SpeedThread.endSW = true;
            System.exit(0);
		}else if(e.getKeyChar() == 'r'){
    		System.out.println("restart");
			System.out.println(Proc.RESTART_MACHINE.ordinal());
            dos.writeInt(Proc.RESTART_MACHINE.ordinal());//車をrestart
            dos.flush(); 
		}
		}catch(Exception ex){}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
