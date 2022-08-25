show user;

select *
from tbl_member;

desc tbl_member;
/*

이름          널?       유형            
----------- -------- ------------- 
USERSEQ     NOT NULL NUMBER        
USERID      NOT NULL VARCHAR2(30)  
PASSWD      NOT NULL VARCHAR2(30)  
NAME        NOT NULL NVARCHAR2(10) 
MOBILE               VARCHAR2(20)  
POINT                NUMBER(10)    
REGISTERDAY          DATE          
STATUS               NUMBER(1) 

*/

--- *** 게시판 테이블 생성하기 *** ---
create table tbl_board
(boardno        number                not null           -- 글번호
, fk_userid     varchar2(30)         not null            -- 작성자
, subject       Nvarchar2(100)       not null            -- 글제목
, contents      Nvarchar2(200)       not null            -- 글내용
, wirteday      date default sysdate not null            -- 작성일자
, viewcount     number default 0     not null            -- 조회수
, boardpasswd   varchar2(20)         not null            -- 글암호
,constraint PK_tbl_board_boardno primary key(boardno)
,constraint fk_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
);

-- drop sequence board_seq;
create sequence seq_board
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

desc tbl_board;

select *
from tbl_board
order by boardno desc;

--- *** 댓글 테이블 생성하기 *** ---
create table tbl_comment
(commentno        number          not null   -- 댓글번호
, fk_boardno     number          not null   -- 원 글번호
, fk_userid      varchar2(3)     not null   -- 작성자 아이디
, contents       Nvarchar2(100)  not null   -- 댓글내용
, writeday       date default sysdate       -- 작성일자
, constraint PK_tbl_comment_commento primary key(commentno)
, constraint FK_tbl_comment_fk_boardno foreign key(fk_boardno) references tbl_board(boardno) on delete cascade
, constraint FK_tbl_comment_fk_userid foreign key(fk_userid) references tbl_member(userid) 
);

-- drop sequence comment_seq;
create sequence seq_comment
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select *
from tbl_comment;


----------------------------------------------------------------------------------------------
/*
    Transaction(트랜잭션) 처리 실습을 위해서 tbl_member 테이블의 point 컬럼의 값은 최대 30을 넘지 못하도록 check 제약을 건다.
*/
----------------------------------------------------------------------------------------------

alter table tbl_member
add constraint CK_tbl_member_point check(point between 0 and 30);

select *
from tbl_member
order by userseq desc;

update tbl_member set point = point + 10
where userid = 'chohs77';

update tbl_member set point = point + 10
where userid = 'chohs77';

update tbl_member set point = point + 10
where userid = 'chohs77';

update tbl_member set point = point + 10
where userid = 'chohs77';
/*
오류 보고 -
ORA-02290: 체크 제약조건(JDBC_USER.CK_TBL_MEMBER_POINT)이 위배되었습니다
*/

rollback;

-- 최근 1주일 내에 작성된 게시글만 출력하기
select *
from tbl_board;

insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd)
values (seq_board.nextval, 'eomjh', '나는용 오빠가 좋은걸!', '으아앙아! 으아아앙ㄱ1아아아!!', '1234');

insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd)
values (seq_board.nextval, 'eomjh', '하솔씨...할 말이 있습니다.', '저랑 사궈주시궜어요?', '1234');

insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd)
values (seq_board.nextval, 'chohs77', '아니요 하솔씨!!', '제가 더 사랑합니다.', '1234');

insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd)
values (seq_board.nextval, 'chohs77', '저는 아직 데뷔하지 않았지만', '이미 좋아하고 있었습니다.', '1234');

insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd)
values (seq_board.nextval, 'leess', '하솔씨 저는 잊으셨나요?', '사내맞선을 기억해 주세요.', '1234');


commit;

update tbl_board set wirteday = wirteday -1
where boardno between 14 and 16;

rollback;

update tbl_board set wirteday = wirteday -10
where boardno between 19 and 20;


-- [퀴즈] 최근 1주일 내에 작성된 게시글만 출력하기

select *
from tbl_board
where to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7;

select wirteday
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 6, 1) AS PREVIOUS6
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 5, 1) AS PREVIOUS5
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 4, 1) AS PREVIOUS4
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 3, 1) AS PREVIOUS3
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 2, 1) AS PREVIOUS2
     , decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 1, 1) AS PREVIOUS1
from tbl_board
where to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7;


String sql = "select count(wirteday)\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 6, 1, 0) ) AS PREVIOUS6\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 5, 1, 0) ) AS PREVIOUS5\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 4, 1, 0) ) AS PREVIOUS4\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 3, 1, 0) ) AS PREVIOUS3\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 2, 1, 0) ) AS PREVIOUS2\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 1, 1, 0) ) AS PREVIOUS1\n"+
"     , sum (decode( to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') , 0, 1, 0) ) AS TODAY\n"+
"from tbl_board\n"+
"where to_date(to_char( sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(wirteday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7;";


-- 이번달 일자별 게시글 작성건수

update tbl_board set wirteday = add_months(wirteday,-1)
where boardno = 4;

commit;

select decode( grouping (to_char(wirteday, 'yyyy-mm-dd') ), 0, to_char(wirteday, 'yyyy-mm-dd'),'전체') AS Writeday
     , count(*) CNT
from tbl_board
where to_char(wirteday, 'yyyy-mm') = to_char(sysdate,'yyyy-mm')
group by rollup(to_char(wirteday, 'yyyy-mm-dd'));