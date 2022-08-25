package jdbc.day01;


import java.sql.*;
import java.util.Scanner;

public class DML_insert_PreparedStatement_01 {

	public static void main(String[] args) {
		
		Connection conn = null;
		// Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바 객체이다.
		
		PreparedStatement pstmt = null;
		// PreparedStatement pstmt 은 Connection conn(연결한 DB 서버)에 전송할 SQL문(편지)을 전송(전달) 해주는 객체(우편배달부)이다.
		
		Scanner sc = new Scanner(System.in);
		
		
		
		
		try {
			
			// >>> 1. 오라클 드라이버 로딩 <<<
			
			/*
            === OracleDriver(오라클 드라이버)의 역할 ===
            1). OracleDriver 를 메모리에 로딩시켜준다.
            2). OracleDriver 객체를 생성해준다.
            3). OracleDriver 객체를 DriverManager에 등록시켜준다.
                --> DriverManager 는 여러 드라이버들을 Vector 에 저장하여 관리해주는 클래스이다.
			*/ 
			
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			// >>> 2. 어떤 오라클 서버에 연결을 할 것인가? <<<
			System.out.print("▷ 연결할 오라클 서버의 IP 주소 : ");
			String ip = sc.nextLine();
			
	
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe ", "JDBC_USER", "aclass");
			// 방화벽 포트가 열려있어야 한다... 
			
			// === Connection conn 기본값은 auto commit 이다. ====
			// === Connection conn의 기본값인 auto commit을 수동 commit으로 전환
			conn.setAutoCommit(false); 		// 수동 commit으로 전환됨
			
			// >>> 3. SQL문(편지)을 작성한다. <<< 
			System.out.print("▷ 글쓴이: ");
			String name = sc.nextLine();
			
			System.out.print("▷ 글내용: ");
			String msg = sc.nextLine();
			
			/* String sql = "insert into tbl_memo(no, name, msg)"
					  + "values(seq_memo.nextval, '"+name+"', '"+msg+"')";
			*/
			// SQL문 맨 뒤에 ;을 넣으면 오류!!!!!!!!
			// !!중요!! 위처럼 하면 보안상 위험 . 그래서 아래처럼 진행한다.
			
			String sql = "insert into tbl_memo(no, name, msg)"
					  + "values(seq_memo.nextval, ?, ?)";
			// ? 를 "위치홀더"라고 부른다.
			
			
			// >>> 4. 연결한 오라클서버(conn)에 SQL문(편지)를 전달할 PreparedStatement 객체(우편배달부) 생성하기 <<<
			pstmt = conn.prepareStatement(sql);	
			pstmt.setString(1, name);	// 1은 String sql에서 첫번째 위치홀더(?)를 말한다.
			pstmt.setString(2, msg);	// 2은 String sql에서 두번째 위치홀더(?)를 말한다.
			
			System.out.println("sql => "+ sql);
			
			// >>> 5. PreparedSatament 객체(우편배달부)는 작성된 SQL문(편지)를 오라클 서버에 보내서 실행이
			int n = pstmt.executeUpdate();
			
			/*  
			 * .executeUpdate(); 은 SQL문이 DML문(insert, update, delete, merge) 이거나 
			 * SQL문이 DDL문(create, drop, alter, truncate) 일 경우에 사용된다.
			 * SQL문이 DML문이라면 return 되어지는 값은 적용되어진 행의 개수를 리턴시켜준다.
			 * 예를 들어, insert into ... 하면 1 개행이 입력되므로 리턴값은 1 이 나온다. 
			 			update ... 할 경우에 update 할 대상의 행의 개수가 5 이라면 리턴값은 5 가 나온다. 
			 			delete ... 할 경우에 delete 되어질 대상의 행의 개수가 3 이라면 리턴값은 3 가 나온다.
			 
			 * SQL문이 DDL문이라면 return 되어지는 값은 무조건 0 이 리턴된다.       
			 * .executeQuery(); 은 SQL문이 DQL문(select) 일 경우에 사용된다.
			*/
			
			System.out.println(" n => " + n);
			
			if(n == 1) {
				String yn = "";
				
				do {
					//////////////////////////////////////////////////////////////////////////////////////
					System.out.print("▷ 정말로 입력하시겠습니까? [Y/N] : ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						conn.commit();
						System.out.println(">> 데이터 입력 성공 !!<<");
					}
					else if("n".equalsIgnoreCase(yn)) {
						conn.rollback();
						System.out.println(">> 데이터 입력 취소!! <<");
					}
					else {
						System.out.println(">> Y 도는 N만 입력하세요!! << \n");
					}
					/////////////////////////////////////////////////////////////////////////////////////	
				} while (!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
			}
		} catch (ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			// >>> 6. 사용하였던 자원을 반납하기 <<<
			// 반납의 순서는 생성순서의 역순으로 한다.
			
			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();				// null 포인트 뜨는 것을 방지하기 위해서
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		sc.close();
		System.out.println("~~~~~~~~~~~ 프로그램 종료 ~~~~~~~~~~~~~");
		
	} // end of main() ----------------------------------------------------------------------------------------------------------------------------

}
