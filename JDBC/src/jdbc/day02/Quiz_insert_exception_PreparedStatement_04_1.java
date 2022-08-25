package jdbc.day02;

import java.sql.*;
import java.util.Scanner;

/*
    ▶ 학번 : 9003
    ▶ 성명 : 홍길동
    ▶ 연락처 : 010-2345-5234
    ▶ 주 소 : 서울시 마포구 월드컵북로 21
    ▶ 학급번호 : 3
    
    >>> 학번 9003 은 이미 사용중이므로 다른 학번을 입력하세요!! <<<
    
    
    ▶ 학번 : 9006
    ▶ 성명 : 홍길동
    ▶ 연락처 : 010-2345-5234
    ▶ 주 소 : 서울시 마포구 월드컵북로 21
    ▶ 학급번호 : 20
    
    >>> 학급번호 20 은 존재하지 않습니다. <<<
    >>> 사용가능한 학급번호는 1,2,3 입니다. <<<
    
    
    ▶ 학번 : 9006
    ▶ 성명 : 홍길동
    ▶ 연락처 : 010-2345-5234
    ▶ 주 소 : 서울시 마포구 월드컵북로 21
    ▶ 학급번호 : 3
    
    >>> 데이터 입력성공 !! <<<

*/

public class Quiz_insert_exception_PreparedStatement_04_1 {

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
			
			// >>> 3. SQL문(편지)을 작성한다. <<< //
			System.out.print("▶ 학번 : ");
			stno = sc.nextLine();
						
			System.out.print("▶ 성명 : ");
			String name = sc.nextLine();
			
			System.out.print("▶ 연락처 : ");
			String tel = sc.nextLine();
			
			System.out.print("▶ 주 소 : ");
			String addr = sc.nextLine();
			
			System.out.print("▶ 학급번호 : ");
			fk_classno = sc.nextLine();
			
			String sql = "insert into tbl_student(stno, name, tel, addr, fk_classno) "
					   + "values(?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stno);   
			pstmt.setString(2, name); 
			pstmt.setString(3, tel); 
			pstmt.setString(4, addr); 
			pstmt.setString(5, fk_classno); 
			
			int n = pstmt.executeUpdate();
			
			if(n == 1) {
				System.out.println(">>> 데이터 입력성공 !! <<<");
			}
			
		} catch (ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			
			if(e.getErrorCode() == 1) {
				System.out.println(">>> 학번 "+stno+" 은 이미 사용중이므로 다른 학번을 입력하세요!! <<<");
			}
			else if(e.getErrorCode() == 2291) {
				System.out.println(">>> 학급번호 "+fk_classno+" 은 존재하지 않습니다. <<<");
				
				String sql = " select classno "
						   + " from tbl_class "
						   + " order by classno asc ";
				
				try {
					pstmt.close();
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					
					StringBuilder sb = new StringBuilder();
					while(rs.next()) {
						int classno = rs.getInt("CLASSNO");
						sb.append(classno+", ");
					}
					
					String str_classno = sb.toString();
					System.out.println(">>> 사용가능한 학급번호는 "+ str_classno.substring(0, str_classno.length()-2) + " 입니다. <<<"); 
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
			
			else {
				e.printStackTrace();
			}
			
		} finally {
			
			try {
				if(pstmt != null)
					pstmt.close();
				
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
        sc.close();
        System.out.println("~~~~ 프로그램 종료 ~~~~");		

	}// end of main()-----------------------------------------

}
