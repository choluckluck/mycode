package jdbc.day04.c.board;

public class BoardDTO { // BoardDTO가 오라클의 tbl_board 에 insert 하기 위한 용이었다면...

	private int boardno;              // 글번호
	private String fk_userid;         // 작성자
	private String subject;           // 글제목
	private String contents;          // 내용
	private String wirteday;          // 작성일자
	private int viewcount;             // 조회수
	private String boardpasswd;       // 글암호
	
	/////////////////////////////////////////////////////////////
	// 아래 내용은 join 을 통해서 확인되어지는 데이터 값이다. select 용도
	private MemberDTO member; // tbl_board 테이블과 tbl_member 테이블을 JOIN. 글쓴이에 대한 모든 정보
							  // MemberDTO member 는 오라클의 tbl_member 테이블(부모테이블)에 해당함 
							  // 부모테이블에다가 변수 선언하면 안되고, 자식테이블이 향하는 곳에 부모를 불러와서 변수를 선언해야한다.
	private int commentcnt;	  // 원글에 딸린 댓글 개수

	/////////////////////////////////////////////////////////////
	public int getBoardno() {
		return boardno;
	}
	public void setBoardno(int boardno) {
		this.boardno = boardno;
	}
	public String getFk_userid() {
		return fk_userid;
	}
	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getWirteday() {
		return wirteday;
	}
	public void setWirteday(String wirteday) {
		this.wirteday = wirteday;
	}
	public int getViewcount() {
		return viewcount;
	}
	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}
	public String getBoardpasswd() {
		return boardpasswd;
	}
	public void setBoardpasswd(String boardpasswd) {
		this.boardpasswd = boardpasswd;
	}
	
	
	
	public MemberDTO getMember() {
		return member;
	}
	public void setMember(MemberDTO member) {
		this.member = member;
	}
	
	
	public int getCommentcnt() {
		return commentcnt;
	}
	public void setCommentcnt(int commentcnt) {
		this.commentcnt = commentcnt;
	}
	
	
	public String showBoardList() {
		// 글번호 \t 글제목 \t\t 작성자명 \t 작성일자 \t\t 조회수
		if(subject != null && subject.length() > 10 ) { // 8글자만 보여주고 뒤에 ... 찍을래용
			subject = subject.substring(0,8) + "...";
		}
		
		if(commentcnt > 0 ) {
			subject += "["+ commentcnt + "]";
		}
		
		String BoardTitle = boardno + "\t" + subject + "\t\t" + member.getName() + "\t" + wirteday + "\t" + viewcount;
		return BoardTitle;
	}
	
}
