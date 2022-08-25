package jdbc.day01;

import java.sql.*;

public class DDL_create_PreparedStatement_06 {

	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "aclass");
			// 127.0.0.1 을 "loop back address" 라고 부른다. 자기자신의 IP 를 말하는 것이다.
			
			String sql_1 = " select * "
					     + " from user_tables "
					     + " where table_name = 'TBL_EXAM_TEST' "; 
			
			String sql_2 = " drop table TBL_EXAM_TEST cascade constraints purge ";
			
			String sql_3 = " create table tbl_exam_test "
					     + " (no    number(4) "
					     + " ,name  Nvarchar2(10) "
					     + " ,msg   Nvarchar2(200) "
					     + " ) ";
			
			String sql_4 = " select * "
					     + " from user_sequences "
					     + " where sequence_name = 'SEQ_EXAM_TEST' ";
			
			String sql_5 = " drop sequence SEQ_EXAM_TEST ";
			
			String sql_6 = " create sequence seq_exam_test "
					     + " start with 1 "
					     + " increment by 1 "
					     + " nomaxvalue "
					     + " nominvalue "
					     + " nocycle "
					     + " nocache ";
			
			String sql_7 = " insert into tbl_exam_test(no, name, msg) "
					     + " values(seq_exam_test.nextval, '이순신', '안녕하세요? 이순신 인사드립니다' ) ";
			
			String sql_8 = " select * "
					     + " from tbl_exam_test "
					     + " order by no desc ";
			
			
			pstmt = conn.prepareStatement(sql_1);
			
			rs = pstmt.executeQuery();
			
			int n = 0;
			if(rs.next()) {
				// 'TBL_EXAM_TEST' 테이블이 존재하는 경우 
				
				// 'TBL_EXAM_TEST' 테이블을 drop 한다.
				pstmt.close();
				pstmt = conn.prepareStatement(sql_2);
				
				n = pstmt.executeUpdate();
				/*  .executeUpdate(); 은 SQL문이 DML문(insert, update, delete, merge) 이거나 
						                SQL문이 DDL문(create, drop, alter, truncate) 일 경우에 사용된다. 
						
					SQL문이 DML문이라면 return 되어지는 값은 적용되어진 행의 개수를 리턴시켜준다.
					예를 들어, insert into ... 하면 1 개행이 입력되므로 리턴값은 1 이 나온다. 
					     update ... 할 경우에 update 할 대상의 행의 개수가 5 이라면 리턴값은 5 가 나온다. 
					     delete ... 할 경우에 delete 되어질 대상의 행의 개수가 3 이라면 리턴값은 3 이 나온다.
					
					SQL문이 DDL문이라면 return 되어지는 값은 무조건 0 이 리턴된다.       
				*/
				
				System.out.println("확인용 drop table : " + n);
				// 확인용 drop table : 0
			}
			
			
			pstmt = conn.prepareStatement(sql_3);
			n = pstmt.executeUpdate();
			System.out.println("확인용 create table : " + n);
			// 확인용 create table : 0
			
			
			pstmt = conn.prepareStatement(sql_4);
			rs.close();
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 'SEQ_EXAM_TEST' 시퀀스가 존재하는 경우
				// 'SEQ_EXAM_TEST' 시퀀스를 drop 한다.
				pstmt.close();
				pstmt = conn.prepareStatement(sql_5);
				n = pstmt.executeUpdate();
				System.out.println("확인용 drop table : " + n);
				// 확인용 drop table : 0
			}
			
			pstmt = conn.prepareStatement(sql_6);
			n = pstmt.executeUpdate();
			System.out.println("확인용 create table : " + n);
			// 확인용 create sequence : 0
			
			pstmt = conn.prepareStatement(sql_7);
			n = pstmt.executeUpdate();
			System.out.println("확인용 insert into tbl_exam_test : " + n);
			// 확인용 insert into tbl_exam_test : 1
			
			pstmt = conn.prepareStatement(sql_8);
			rs = pstmt.executeQuery();
			
			
			int cnt = 0;
			StringBuilder sb = new StringBuilder();
			while(rs.next()) {
				cnt++;
				
				if (cnt==1) {
					sb.append("--------------------------------------------------------------\n");
					sb.append("일련번호 \t 성명 \t 글내용 \t \n");
					sb.append("--------------------------------------------------------------\n");
				}
				sb.append(rs.getInt("NO") + "\t" + rs.getString("NAME") + "\t" + rs.getString("MSG") + "\n");
			} // end of while --------------------------------------------------------------------

			if(cnt>0) {
				System.out.println(sb.toString());
			}
			else {
				System.out.println(" >> 입력된 데이터가 없습니다. << ");
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

        System.out.println("~~~~ 프로그램 종료 ~~~~");		

	}// end of main()----------------------------

}
