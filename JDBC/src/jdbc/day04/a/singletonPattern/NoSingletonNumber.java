package jdbc.day04.a.singletonPattern;

public class NoSingletonNumber {
	
	private int cnt = 0; // 인스턴스 변수
	
	// == 기본생성자 ==
	// public NoSingletonNumber() {} 은 생략되어짐
	
	public int getNextNumber() { // 인스턴스 메소드
		return ++cnt;	// 인스턴스 변수
	}
}
