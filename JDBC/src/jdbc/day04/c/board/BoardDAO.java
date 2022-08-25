package jdbc.day04.c.board;

import java.sql.*;
import java.util.*;

public class BoardDAO implements InterBoardDAO {

	
	// field, attribute, property, 속성
		Connection conn = null;
		PreparedStatement pstmt;
		ResultSet rs;
		
	// ********************* 자원반납을 해주는 메소드 ***********************************************************************
	private void close() {
		try {
			if( rs != null) rs.close();
			if(pstmt != null) pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}	
	} // end of private void close() *********************************************************************************
		
	
	//******* 게시판 글쓰기( Transaction 처리: tbl_board에 insert + tbl_member 의 point 컬럼에 10 update) ******************************
	// 글쓰기 성공하면 1, 실패하면 -1 을 리턴.
	@Override
	public int write(BoardDTO bdto) { 
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			// Transaction 처리를 위해서 수동 commit 으로 전환
			conn.setAutoCommit(false);
			
			String sql = " insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd) "
					   + " values(seq_board.nextval,?,?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bdto.getFk_userid());
			pstmt.setString(2, bdto.getSubject());
			pstmt.setString(3, bdto.getContents());
			pstmt.setString(4, bdto.getBoardpasswd());
			
			int n1 = pstmt.executeUpdate();
			
			if (n1==1) {
				sql = " update tbl_member set point = point + 10 "
					+ " where userid = ? ";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, bdto.getFk_userid());
				
				int n2 = pstmt.executeUpdate();
			
				if(n2 == 1) {
					conn.commit(); // 커밋한다.
					result = 1;
				}
			
			}
		
		} catch(SQLException e) {
			if(e.getErrorCode() == 2290) {
				System.out.println(">> " + bdto.getFk_userid() +"님의 포인트는 30을 초과할 수 없습니다.<<\n");
			}
			else {
				e.printStackTrace();
			}
			try {
				conn.rollback(); // 롤백해준다.
				result = -1;
			} catch (SQLException e1) {
		} finally {
			try {
				conn.setAutoCommit(true);	// 자동 commit으로 복원 시킴.	
			} catch (SQLException e1) { }
			
			close();	// 자원 반납하기
		}
	   }
		return result;
	} // end of public int write(BoardDTO bdto) ***************************************************************************


	// ******* 글목록보기를 해주는 메소드 *****************************************************************************************
	@Override
	public List<BoardDTO> boardList() {
		
		List<BoardDTO> boardList = new ArrayList<>();
		
		try {
			conn = MyDBConnection.getConn();
		
			String sql = "   select A.boardno, A.subject, A.name, A.writeday, NVL(CMT.commentcnt,0) AS commentcnt, A.viewcount "+
					"   from "+
					"   ("+
					"    select B.boardno, B.subject, M.name, to_char( B.wirteday, 'yyyy-mm-dd') AS writeday, B.viewcount "+
					"    from tbl_board B join tbl_member M\n"+
					"    on B.fk_userid = M.userid\n"+
					"    ) A "+
					"   LEFT JOIN "+
					"   ( "+
					"   select fk_boardno "+
					"         , count(*) AS commentcnt "+
					"    from tbl_comment "+
					"    group by fk_boardno "+
					"    ) CMT "+
					"    ON A.boardno = CMT.fk_boardno "+
					"    order by 1 desc";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				BoardDTO bdto = new BoardDTO();
				bdto.setBoardno(rs.getInt("BOARDNO"));
				bdto.setSubject(rs.getString("SUBJECT"));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString("NAME"));
				
				bdto.setMember(member);
				// 이렇게 JOIN 해 본다...
				// JOIN 하는 다른테이블(MemberDTO) 을 리턴값 설정하여 BoardDTO에서 변수를 선언하고, getter/setter 해서 불러올 수 있게 한 다음에
				// JOIN 시 select 할 값을 불러온 후, 위처럼 기존 DTO 값에 집어 넣는다.
				
				bdto.setWirteday(rs.getString("WRITEDAY"));
				bdto.setCommentcnt(rs.getInt("COMMENTCNT"));
				bdto.setViewcount(rs.getInt("VIEWCOUNT"));
				
				boardList.add(bdto);
			} // end of while -------------------------------------------------
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return boardList;

	} // end of public List<BoardDTO> boardList() *************************************************************************

	
	// 글 내용보기 메소드 
	@Override
	public BoardDTO viewContents(Map<String, String> paraMap) {
		
		BoardDTO bdto = null;
		
		try {
			conn = MyDBConnection.getConn();
		
			String sql = " select subject, contents, fk_userid, boardpasswd"
					   + " from tbl_board"
					   + " where to_char(boardno) = ?"; // 왜냐면 사용자가 숫자 외에 문자를 칠 수도 있으니까 string 으로 받을 수 있게 전환한다.
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("boardno"));
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				bdto = new BoardDTO();
				bdto.setSubject(rs.getString("SUBJECT"));
				bdto.setContents(rs.getString("CONTENTS"));
				bdto.setFk_userid(rs.getString("FK_USERID"));
				bdto.setBoardpasswd(rs.getString("BOARDPASSWD"));
				
			} // end of while -------------------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return bdto;
	}


	// ******** 조회수 1 증가시키는 메소드 ********************************************************************
	@Override
	public void updateViewCount(String boardno) {
		
		BoardDTO bdto = new BoardDTO();
		
		try {
			conn = MyDBConnection.getConn();
		
			String sql = " update tbl_board set viewcount = viewcount + 1"
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	} // end of public void updateViewCount(String boardno) ********************************************************************


	
	// -_- 댓글쓰기 메소드 -_- ***************************************************************************************************
	@Override
	public int writeComment(CommentDTO cmdto) {
		
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = " insert into tbl_comment( commentno, fk_boardno, fk_userid, contents ) "
					   + " values(seq_comment.nextval ,? ,? ,? ) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1,cmdto.getFk_boardno()); // 넘어온 원글의 글번호가 존재하지 않는 12312313 이 올 수 있다.
			pstmt.setString(2,cmdto.getFk_userid());
			pstmt.setString(3,cmdto.getContents());
			
			result = pstmt.executeUpdate();
			// insert 가 성공되어지면 result 에는 1 이 들어온다.
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 2291) {
				// ORA-02291: 무결성 제약조건(JDBC_USER.FK_TBL_COMMENT_FK_BOARDNO)이 위배되었습니다- 부모 키가 없습니다
				System.out.println(">> [경고] 댓글을 작성할 원글의 번호가 존재하지 않습니다.  << \n");
				result = -1;
			}else {
				e.printStackTrace();
			}
		}finally {
			close(); // 자원 반납하기 
		}// end of try catch finally————————————————————————
		
		return result;

	} // end of public int writeComment(CommentDTO cmdto) ********************************************************************


	// 원글에 대한 댓글을 가져오는 것. (특정 게시글 글번호에 대한 tbl_Comment 테이블과 tbl_member 테이블에 JOIN 해서 보여준다.) **********************
	@Override
	public List<CommentDTO> commentList(String boardno) {
		List<CommentDTO> commentList = null;
	
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "select contents, name, writeday\n"+
	                  "from \n"+
	                  "(   \n"+
	                  "    select commentno, contents, to_char(writeday,'yyyy-mm-dd hh24:mi:ss') as writeday, fk_userid\n"+
	                  "    from tbl_comment \n"+
	                  "    where fk_boardno = ?\n"+
	                  ") C JOIN tbl_member M\n"+
	                  "ON C.fk_userid = M.userid\n"+
	                  "order by C.commentno desc";
			/*
			 * "select contents, name, to_char(C.writeday, 'yyyy-mm-dd hh24:mi:ss') writeday "+
                   "from  "+
                   "(   select commentno, contents, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') writeday, fk_userid "+
                   "    from tbl_comment "+
                   "    where fk_boardno = ? "+
                   ") C JOIN tbl_member M "+
                   "on C.fk_userid = M.userid "+
                   "order by C.commentno desc";
			 */
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			while(rs.next()) {
				
				cnt++;
				
				CommentDTO cdto = new CommentDTO();
				cdto.setContents(rs.getString("CONTENTS"));
				
				MemberDTO mbdto  = new MemberDTO();
				mbdto.setName(rs.getString("NAME"));
				
				cdto.setMember(mbdto);
				
				cdto.setWriteday(rs.getString("WRITEDAY"));
				
				if (cnt == 1) {
					commentList = new ArrayList<>();
				}
				
				commentList.add(cdto);
			} // end of while ----------------------------------------------

		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return commentList;
	}// end of public List<CommentDTO> commentList(String boardno) *******************************************************

	
	
	
	// 정보를 좀 더 빨리 읽어오려고 인덱스 만들어서 불러옴
	// ******* 글 내용보기 메소드 **********************************************************************************************
	@Override
	public BoardDTO viewContents(String boardno) {
		
		BoardDTO bdto = null;
		
		try {
			conn = MyDBConnection.getConn();
		
			String sql = " select subject, contents, fk_userid, boardpasswd"
					   + " from tbl_board"
					   + " where boardno = ?"; // 왜냐면 사용자가 숫자 외에 문자를 칠 수도 있으니까 string 으로 받을 수 있게 전환한다.
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				bdto = new BoardDTO();
				bdto.setSubject(rs.getString("SUBJECT"));
				bdto.setContents(rs.getString("CONTENTS"));
				bdto.setFk_userid(rs.getString("FK_USERID"));
				bdto.setBoardpasswd(rs.getString("BOARDPASSWD"));
				
			} // end of while -------------------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return bdto;
	} // end of public BoardDTO viewContents(String boardno) ************************************************************


	
	// ********** 글 수정하기 메소드 *****************************************************************************************
	@Override
	public int updateBoard(Map<String, String> paraMap) {
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = " update tbl_board set subject = ?, contents =? "
						+ " where boardno = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("subject"));
			pstmt.setString(1, paraMap.get("contents"));
			pstmt.setString(1, paraMap.get("boardno"));
			
			result = pstmt.executeUpdate();
		
		} catch(SQLException e) {
			
		} finally {
			close();	// 자원 반납하기
		}
		
		return result;
	} // public int updateBoard(Map<String, String> paraMap) ------------------------------------------------------


	
	// ******** 글 삭제하기 메소드 ************************************************************************************************
	@Override
	public int deleteBoard(String boardno) {
		
			int result = 0;
			
			try {
				conn = MyDBConnection.getConn();
				
				String sql = " delte from tbl_board "
							+ " where boardno = ?";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, boardno);
				
				result = pstmt.executeUpdate();
			
			} catch(SQLException e) {
				
			} finally {
				close();	// 자원 반납하기
			}
			
			return result;

	} // end of public int deleteBoard(String boardno) *****************************************************************


	// 최근 1주일 내 작성된 게시글 ***************************
	@Override
	public Map<String, Integer> statisticsByWeek() {
		
		Map<String, Integer> resultMap = new HashMap<>();

			try {
				conn = MyDBConnection.getConn();
			
				String sql = "select count(wirteday)\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 6, 1, 0) ) AS PREVIOUS6\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 5, 1, 0) ) AS PREVIOUS5\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 4, 1, 0) ) AS PREVIOUS4\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 3, 1, 0) ) AS PREVIOUS3\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 2, 1, 0) ) AS PREVIOUS2\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 1, 1, 0) ) AS PREVIOUS1\n"+
						"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 0, 1, 0) ) AS TODAY\n"+
						"from tbl_board\n"+
						"where to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7";
				
				pstmt = conn.prepareStatement(sql);

				rs = pstmt.executeQuery();
				
				rs.next();
				
				resultMap.put("TOTAL", rs.getInt(1));
				resultMap.put("PREVIOUS6", rs.getInt(2));
				resultMap.put("PREVIOUS5", rs.getInt(3));
				resultMap.put("PREVIOUS4", rs.getInt(4));
				resultMap.put("PREVIOUS3", rs.getInt(5));
				resultMap.put("PREVIOUS2", rs.getInt(6));
				resultMap.put("PREVIOUS1", rs.getInt(7));
				resultMap.put("TODAY", rs.getInt(8));
				

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
		
		return resultMap;
	} // end of public Map<String, Integer> statisticsByWeek() *********************************************************


	
	
	
	// 이번달 일자별 게시글 작성건수
	@Override
	public List<Map<String, String>> statisticsByCurrentMonth() {
		
		List<Map<String,String>> mapList = new ArrayList<>();
		try {
			conn = MyDBConnection.getConn();
		
			String sql = "\n"+
					"select decode( grouping (to_char(wirteday, 'yyyy-mm-dd') ), 0, to_char(wirteday, 'yyyy-mm-dd'),'전체') AS writeday\n"+
					"     , count(*) AS CNT \n"+
					"from tbl_board\n"+
					"where to_char(wirteday, 'yyyy-mm') = to_char(sysdate,'yyyy-mm')\n"+
					"group by rollup(to_char(wirteday, 'yyyy-mm-dd'))";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				Map<String,String> map = new HashMap<>();
				map.put("WRITEDAY", rs.getString(1));
				map.put("CNT", rs.getString(2));
				
				mapList.add(map);
				
			} // end of while -----------------------------------------------------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}				
		
		return mapList;
	} // end of public List<Map<String, String>> statisticsByCurrentMonth() --------------------------------------------
}