package jdbc.day04.c.board;

import java.sql.*;
import java.util.*;

import jdbc.day04.b.dbconnection.MyDBConnection;

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
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	} // end of private void close() *********************************************************************************
	
	
	// ********************* 회원가입처리 메소드 ***************************************************************************
	@Override
	public int memberRegister(MemberDTO member) {
		
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			// getConn 은 public static 으로 구성되어 있음! (실제 conn 은 private으로 묶여있겠지만...)
			
			String sql = " insert into tbl_member(userseq, userid, passwd, name, mobile)" // 아... 시퀀스가 있어서 시퀀스 값이 들어가니까 있던거엿음.
					   + " values (userseq.nextval, ?,?,?,?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, member.getPasswd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMobile());
			
			result = pstmt.executeUpdate();			
			
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
			conn = MyDBConnection.getConn();
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "
					   + " from tbl_member" 
					   + " where status = 1 and userid = ? and passwd = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, map.get("userid"));
			pstmt.setString(2, map.get("passwd"));

			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setUserid(rs.getString(2));
				member.setName(rs.getString(3));
				member.setMobile(rs.getString(4));
				member.setPoint(rs.getInt(5));
				member.setRegisterday(rs.getString(6));
			}
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}
		
		return member;
	}// end of public MemberDTO login(Map<String, String> map) *******************************************************

	// ********************* 회원 삭제 메소드 ****************************************************************************
	@Override
	public int memberDelete(int userseq) {
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
		
			String sql = " update tbl_member set status = 0"
						+ " where userseq = ? " ;
		
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,userseq);
			result = pstmt.executeUpdate();
		
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	} // end of memberDelete(int userseq) **************************************************************


	
	// ********************* 모든회원정보보기 메소드 ****************************************************************************
	@Override
	public List<MemberDTO> showAllMemeber(String sortchoice) {
		List<MemberDTO> memberList = new ArrayList<>();
		
		try {
			conn = MyDBConnection.getConn();
			
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS registerday, status "
					   // + " case status when 1 then '가입중' else '탈퇴' end AS status " 이러면 아래 while 문에서 타입이 안맞을 수 있기때문에 부르는건 그대로 부르고 바꾼다!
					   + " from tbl_member ";
			
			switch (sortchoice) {
			case "1": // 회원명의 오름차순
				sql += " order by name asc ";
				break;
				
			case "2": // 회원명의 내림차순
				sql += " order by name desc ";
				break;
				
			case "3": // 가입일자의 오름차순
				sql += " order by userseq asc ";
				break;
				
			case "4": // 가입일자의 내림차순
				sql += " order by userseq desc ";
				break;
			}
			
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

		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}
		
		return memberList;
	} // end of public List<MemberDTO> showAllMemeber() **************************************************************

	
	
	
	
	// ********************* 나의정보조회(select) 메소드  *******************************************************************
	@Override
	public MemberDTO select_myinfo(int userseq) {
		MemberDTO member = null;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd') AS registerday "
					   + " from tbl_member" 
					   + " where status = 1 and userseq= ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userseq);


			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt(1));
				member.setUserid(rs.getString(2));
				member.setName(rs.getString(3));
				member.setMobile(rs.getString(4));
				member.setPoint(rs.getInt(5));
				member.setRegisterday(rs.getString(6));
			}
		} catch (SQLException e) {		// 이건 걍~ 모든 오류를 포함해버림. 그래서 위처럼 따로 좁힌 오류 코드도 집어 넣는다.
			e.printStackTrace();		// 나중에는 유효성 검사도 해봅시다~ 플젝할 때는~ .trim() 같은 것~
		} finally {
			close();
		}
		
		return member;
	} // end of public MemberDTO select_myinfo(int userseq) ************************************************************





}
