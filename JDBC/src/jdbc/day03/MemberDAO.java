package jdbc.day03;

import java.sql.*;
import java.util.*;

// DAO(DataBase Access Object) ==> 특정 데이터베이스에 연결하여 SQL구문(DDL,DML,DQL)을 실행시켜주는 객체.
public class MemberDAO implements InterMemberDAO {

	// field, attribute, property, 속성
	Connection conn = null;
	PreparedStatement pstmt;
	ResultSet rs;
	
	// method, operation, 기능
	
	// ********************* 자원반납을 해주는 메소드 ***********************************************************************
	private void close() {
		
		try {
			if( rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	} // end of private void close() *********************************************************************************
	
	
	// ********************* 회원가입처리 메소드 ***************************************************************************
	@Override
	public int memberRegister(MemberDTO member) {
		
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe","jdbc_user","aclass");
			
			
			String sql = " insert into tbl_member(userseq, userid, passwd, name, mobile)" // 아... 시퀀스가 있어서 시퀀스 값이 들어가니까 있던거엿음.
					   + " values (userseq.nextval, ?,?,?,?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, member.getPasswd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMobile());
			
			result = pstmt.executeUpdate();			
			
		} catch (ClassNotFoundException e) {
			System.out.println(" >> ojdbc8.jar 파일이 없습니다. << ");
		} catch (SQLIntegrityConstraintViolationException e) {			// 제약 조건에 위배되었을 때만 뜨든 것.
			
			if( e.getErrorCode() == 1 ) {// Unique 는 primary key 와 마찬가지로 오류코드 1 임.
				System.out.println(" >> 아이디가 중복되었습니다.새로운 아니디를 입력하세요 !! << ");
			}else {
				System.out.println("에러메세지: " + e.getMessage());
			}
			
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}

		return result;
	
	} // end of public int memberRegister(MemberDTO member) **********************************************************


	// ********************* 로그인처리 메소드 ****************************************************************************
	@Override
	public MemberDTO login(Map<String, String> map) {
		
		MemberDTO member = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe","jdbc_user","aclass");
			
			
			String sql = " select userseq, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "
					   + " from tbl_member" 
					   + " where status = 1 and userid = ? and passwd = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, map.get("userid"));
			pstmt.setString(2, map.get("passwd"));

			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setName(rs.getString(2));
				member.setMobile(rs.getString(3));
				member.setPoint(rs.getInt(4));
				member.setRegisterday(rs.getString(5));
			}
		} catch (ClassNotFoundException e) {
			System.out.println(" >> ojdbc8.jar 파일이 없습니다. << ");
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}
		
		return member;
	}// end of public MemberDTO login(Map<String, String> map) *******************************************************


	@Override
	public int memberDelete(int userseq) {
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe","jdbc_user","aclass");
		
			String sql = " update tbl_member set status = 0"
						+ " where userseq = ? " ;
		
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,userseq);
			result = pstmt.executeUpdate();
		
		} catch (ClassNotFoundException e) {
			System.out.println(" >> ojdbc8.jar 파일이 없습니다. << ");
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	} // end of memberDelete(int userseq) **************************************************************


	@Override
	public List<MemberDTO> showAllMemeber() {
		List<MemberDTO> memberList = new ArrayList<>();
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe","jdbc_user","aclass");
			
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS registerday, status"
					   // + " case status when 1 then '가입중' else '탈퇴' end AS status " 이러면 아래 while 문에서 타입이 안맞을 수 있기때문에 부르는건 그대로 부르고 바꾼다!
					   + " from tbl_member "
					   + " order by userseq asc ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MemberDTO member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setUserid(rs.getString(2));
				member.setName(rs.getString(3));
				member.setMobile(rs.getString(4));
				member.setPoint(rs.getInt(5));
				member.setRegisterday(rs.getString(6));
				member.setStatus(rs.getInt(7));
				
				memberList.add(member);
			}

		} catch (ClassNotFoundException e) {
			System.out.println(" >> ojdbc8.jar 파일이 없습니다. << ");
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}
		
		return null;
	} // end of public List<MemberDTO> showAllMemeber() **************************************************************





}
