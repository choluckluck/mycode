package jdbc.day01;

import java.sql.*;
import java.util.Scanner;

public class DML_delete_PreparedStatement_05 {

	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Scanner sc = new Scanner(System.in);
		
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			System.out.print("▷ 연결할 오라클 서버의 IP 주소 : ");
			String ip = sc.nextLine();
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe", "JDBC_USER", "aclass");
			
			String sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					   + " from tbl_memo "
					   + " order by no desc "; 
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
		     
			System.out.println("-------------------------------------------------------");
			System.out.println("글번호\t글쓴이\t글내용\t작성일자");
			System.out.println("-------------------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			
			while(rs.next()) {
				 
			    int no = rs.getInt(1);             //  1 은 select 해온 컬럼의 위치값으로서 1번째 컬럼을 가리키는 것이다.
			    String name = rs.getString(2);     //  2 는 select 해온 컬럼의 위치값으로서 2번째 컬럼을 가리키는 것이다. 
			    String msg = rs.getString(3);      //  3 은 select 해온 컬럼의 위치값으로서 3번째 컬럼을 가리키는 것이다. 
				String writeday = rs.getString(4); //  4 은 select 해온 컬럼의 위치값으로서 4번째 컬럼을 가리키는 것이다. 
				
				sb.append(no);
				sb.append("\t"+name);
				sb.append("\t"+msg);
				sb.append("\t"+writeday+"\n");
				
			}// end of while(rs.next())
			
			System.out.println(sb.toString());
			
			////////////////////////////////////////////////////////////////////
			
			// --------------------------------------------------- //
			String no = "";
			
			do {
				System.out.print("▷ 삭제할 글번호 : ");
				no = sc.nextLine();  // "15"  "똘똘이"   "234234"
				
				try {
					Integer.parseInt(no); // 입력받은 값을 정수로 변환시켜본다.
					break;
				} catch(NumberFormatException e) {
					System.out.println("[경고] 삭제할 글번호는 정수로만 입력하세요!! \n");
				}
				
			} while(true);
			// --------------------------------------------------- //
			
			sql = " select name, msg "
				+ " from tbl_memo "
				+ " where no = ? ";
			
			pstmt.close();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, no);
			
			rs.close();
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 삭제할 글번호가 존재하는 경우
				
				String name = rs.getString(1);
				String msg = rs.getString(2);
				
				System.out.println("\n=== 삭제할 글내용 ===");
				System.out.println("\n□ 글쓴이 : " + name);
				System.out.println("□ 글내용 : " + msg + "\n");
				
				String yn = "";
				do {
					////////////////////////////////////////////////////////////////////
					System.out.print("▷ 정말로 삭제하시겠습니까?[Y/N] : ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						
						sql = " delete from tbl_memo "
							+ " where no = ? ";
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, no);
						
						int n = pstmt.executeUpdate();
						
						if(n==1) {
							System.out.println(">> 글번호 "+no+" 글은 삭제되었습니다. << \n");
						}
						
					}
					else if("n".equalsIgnoreCase(yn)) {
						System.out.println(">> 데이터 삭제 취소!! <<");
					}
					else {
						System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
					}
	                ////////////////////////////////////////////////////////////////////
				} while( !("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)) );
				
			}// end of if(rs.next())-----------------------------
			
			else { // 삭제해야할 글번호가 DB에 존재하지 않는 경우
				
				System.out.println(">>> 글번호 "+no+" 은 존재하지 않습니다. <<< \n");
			}
			
		} catch (ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				if(rs != null)
				   rs.close();
					
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
		
	}// end of main()-------------------------------------

}
