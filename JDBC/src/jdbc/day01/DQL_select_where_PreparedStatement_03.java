package jdbc.day01;

import java.sql.*;
import java.util.Scanner;

public class DQL_select_where_PreparedStatement_03 {

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
			
			// === StringBuilder sb 을 초기화 하기 === //
			sb = new StringBuilder();
			// 또는
			// sb.setLength(0);
			
			sb.append("------------- >>> 조회할 대상 <<< --------------\n");
			sb.append("1.글번호   2.글쓴이   3.글내용   4.종료\n");
			sb.append("----------------------------------------------\n");
			String menu = sb.toString();
			
			String menuNo = "";
			
			do {
				///////////////////////////////////////////////////////////////////////////
				System.out.println(menu);
				System.out.print("▷ 번호선택 : ");
				menuNo = sc.nextLine();
				
				String colName = "";    // where 절에 들어올 컬럼명
				String searchType = "";
				
				switch (menuNo) {
					case "1":   // 글번호
						colName = "no";
						searchType = "글번호";
						break;
						
					case "2":   // 글쓴이
						colName = "name";
						searchType = "글쓴이중";
						break;	
						
					case "3":   // 글내용
						colName = "msg";
						searchType = "글내용에는";
						break;			
						
					case "4":   // 종료
						
						break;					
		
					default:
						System.out.println("~~~ 메뉴에 없는 번호 입니다. ~~~\n");
						break;
				}// end of switch (menuNo)---------------------------- 
				
				
				if("1".equals(menuNo) || "2".equals(menuNo) || "3".equals(menuNo)) {
					
					System.out.print("▷ 검색어 : ");
					String search = sc.nextLine();
					
					sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					    + " from tbl_memo ";
					
					if( !"3".equals(menuNo) ) { // 글번호 또는 글쓴이로 검색시
						sql += " where to_char("+colName+") = ? ";
						// !!!! 컬럼명 또는 테이블명은 위치홀더인 ? 를 쓰면 안되고 반드시 변수로 처리 해야한다. !!!!
						// !!!! 데이터값만 위치홀더인 ? 를 써야 한다. !!!!
					}
					else { // 글내용으로 검색시 
						sql += " where "+colName+" like '%'|| ? ||'%' ";
					}
					
					sql += " order by no desc "; 
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, search);
					
					rs = pstmt.executeQuery();
					
					// === StringBuilder sb 을 초기화 하기 === //
					sb = new StringBuilder();
					// 또는
					// sb.setLength(0);
					
					int cnt = 0;
					
					while(rs.next()) {
						cnt++;
						
						if(cnt == 1) {
							System.out.println("-------------------------------------------------------");
							System.out.println("글번호\t글쓴이\t글내용\t작성일자");
							System.out.println("-------------------------------------------------------");
						}
						
						int no = rs.getInt(1);             //  1 은 select 해온 컬럼의 위치값으로서 1번째 컬럼을 가리키는 것이다.
					    String name = rs.getString(2);     //  2 는 select 해온 컬럼의 위치값으로서 2번째 컬럼을 가리키는 것이다. 
					    String msg = rs.getString(3);      //  3 은 select 해온 컬럼의 위치값으로서 3번째 컬럼을 가리키는 것이다. 
						String writeday = rs.getString(4); //  4 은 select 해온 컬럼의 위치값으로서 4번째 컬럼을 가리키는 것이다. 
						
						sb.append(no);
						sb.append("\t"+name);
						sb.append("\t"+msg);
						sb.append("\t"+writeday+"\n");
						
					}// end of while(rs.next())------------------------
					
					if(cnt > 0) { // 검색되어진 결과물이 존재하는 경우  
						System.out.println(sb.toString());
					}
					else {  // 검색되어진 결과물이 없는 경우
						
						System.out.println(">>> "+searchType+" "+search+"에 해당하는 데이터가 없습니다. <<< \n");
					}
					
				}// end of if-------------------------
	            ///////////////////////////////////////////////////////////////////////////
			
			} while( !("4".equals(menuNo) ) );
			// end of do ~ while----------------------------
			
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
