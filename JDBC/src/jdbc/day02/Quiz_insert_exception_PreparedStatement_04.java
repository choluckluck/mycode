package jdbc.day02;

import java.sql.*;
import java.util.Scanner;

/*
 * [1번케이스]
 * @ 학번: 9003
 * @ 성명: 홍길동
 * @ 연락처: 010-2345-6789
 * @ 주소: 서울시 마포구 서교동 123-123
 * @ 학급번호: 3
 * 
 * >>> 학번 9003은 이미 사용중이므로 다른 학번을 입력하세요!! <<<
 * 
 * [2번케이스]
 * @ 학번: 9003
 * @ 성명: 홍길동
 * @ 연락처: 010-2345-6789
 * @ 주소: 서울시 마포구 월드컵북로 123
 * @ 학급번호: 20
 * 
 * >>> 학급번호 20은 존재하지 않습니다!! <<<
 * >>> 사용 가능한 학번번호는 1,2,3 입니다. <<<
 * 
 * [3번케이스]
 * @ 학번: 9006
 * @ 성명: 홍길동
 * @ 연락처: 010-2345-6789
 * @ 주소: 서울시 마포구 월드컵북로 123
 * @ 학급번호: 1
 * 
 * >>> 데이터 입력 성공 !! <<<
 */


public class Quiz_insert_exception_PreparedStatement_04 {

	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Scanner sc = new Scanner(System.in);
		
		String stno = "";
		String fk_classno = "";
		
		try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "aclass");
		
				System.out.print("@ 학번: ");
				stno = sc.nextLine();
				
				System.out.print("@ 성명: ");
				String name = sc.nextLine();
				
				System.out.print("@ 연락처: ");
				String tel = sc.nextLine();
				
				System.out.print("@ 주소: ");
				String addr = sc.nextLine();
				
				System.out.print("@ 학급번호: ");
				fk_classno = sc.nextLine();
				
				String sql = "insert into tbl_student(stno, name, tel, addr, fk_classno)"
						   + " values(?,?,?,?,?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, stno);
				pstmt.setString(2, name);
				pstmt.setString(3, tel);
				pstmt.setString(4, addr);
				pstmt.setString(5, fk_classno);

				int n = pstmt.executeUpdate();
				rs = pstmt.executeQuery();
				
				if (n==1) {
					System.out.println(">>> 데이터 입력 성공 !! <<<");
				}
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		}catch (SQLException e) {
			// e.printStackTrace();
			if(e.getErrorCode() ==1 ) {			// 에러코드 == 1 primary key 위반의 뜻.
				System.out.println(">>> 학번 "+stno+" 은 이미 사용중이므로 다른 학번을 입력하세요!! <<<");
			}
			else if(e.getErrorCode() == 2291 ) {
				System.out.println(">>> 학급번호 "+fk_classno+" 은 존재하지 않습니다. <<<");
				
				String sql = " select classno "
						   + " from tbl_class "
						   + " order by classno asc";
				
				try {
					pstmt = conn.prepareStatement(sql);
					
				} catch
				
			}
		}
	}
}


