package jdbc.day04.c.board;

public class CommentDTO { // CommentDTO가 오라클의 tbl_comment 테이블(tbl_member)의 자식테이블)에 해당

	private int commentno;				// 댓글번호
	private int fk_boardno;				// 원 글번호
	private String fk_userid;			//  작성자 아이디
	private String contents;			// 댓글내용
	private String writeday;			// 작성일자
	
	
	private MemberDTO member; // tbl_comment 테이블과 tbl_member 테이블을 JOIN. 글쓴이에 대한 모든 정보
	  // MemberDTO member 는 오라클의 tbl_member 테이블(부모테이블)에 해당함 

	
	public int getCommentno() {
		return commentno;
	}

	public void setCommentno(int commentno) {
		this.commentno = commentno;
	}
	public int getFk_boardno() {
		return fk_boardno;
	}
	public void setFk_boardno(int fk_boardno) {
		this.fk_boardno = fk_boardno;
	}
	public String getFk_userid() {
		return fk_userid;
	}
	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getWriteday() {
		return writeday;
	}
	public void setWriteday(String writeday) {
		this.writeday = writeday;
	}
	
	public MemberDTO getMember() {
		return member;
	}
	public void setMember(MemberDTO member) {
		this.member = member;
	}

	public String viewCommentInfo() {
		return contents + "\t\t" + member.getName()+"\t" +writeday;
	}
	
}
