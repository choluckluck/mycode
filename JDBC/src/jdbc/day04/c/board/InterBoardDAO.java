package jdbc.day04.c.board;

import java.util.*;

public interface InterBoardDAO {
	
	int write(BoardDTO bdto);  //게시판 글쓰기( Transaction 처리: tbl_board에 insert + tbl_member 의 point 컬럼에 10 update)

	List<BoardDTO> boardList(); // 글목록보기

	BoardDTO viewContents(Map<String, String> paraMap); // 글 내용보기
	BoardDTO viewContents(String boardno); // 글 내용보기

	void updateViewCount(String boardno); // 현재 로그인되어진 사용자가 자신의 글이 아닌 다른 사용자가 쓴 글을 조회했을 때만 조회수를 1 증가 시킨다.

	int writeComment(CommentDTO cmdto);	// 댓글 작성

	List<CommentDTO> commentList(String boardno);	// 댓글 보기

	int updateBoard(Map<String, String> paraMap); // 글 수정하기

	int deleteBoard(String boardno); // 글 삭제하기

	Map<String, Integer> statisticsByWeek(); // 최근 일주일 내 작성된 글 보기

	List<Map<String, String>> statisticsByCurrentMonth(); 
	
}
