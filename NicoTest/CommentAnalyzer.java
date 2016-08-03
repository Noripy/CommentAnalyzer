package main;

public class CommentAnalyzer {
	public static void main(String[] args){
		System.out.println(AnalyzeComment("[左,下]"));
	}
	
	public static int AnalyzeComment(String com){
		if(com == null || com.length() < 5) return -1;
		
		char[] comArray = com.toCharArray();
		
		if(comArray[0] != '[') return -1;
		char wheel = comArray[1];
		if(comArray[2] != ',') return -1;
		char speed = comArray[3];
		if(comArray[4] != ']') return -1;
		
		if(wheel == '左' && speed == '上') return 1;
		else if(wheel == '左' && speed == '下') return 2;
		else if(wheel == '右' && speed == '上') return 3;
		else if(wheel == '右' && speed == '下') return 4;
		else{
			return -1;
		}
	}
}
