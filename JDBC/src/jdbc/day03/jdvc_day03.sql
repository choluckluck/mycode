set hidden param parseThreshold = 150000;

show user;

----- *** 회원 테이블 생성하기 *** -----
select *
from user_tables
where table_name = 'TBL_MEMBER';

select * from tab;

-- 어떤 경우에는,,, userid 를 unique 값과 primary key(&sequence) 값을 만들어서 사용한다. userid 가 남용되어 사용되면 안되니까~
create table TBL_MEMBER
( userseq       number  not null        -- 회원번호
, userid        varchar2(30) not null   -- 회원ID
, passwd        varchar2(30) not null   -- 비밀번호
, name          Nvarchar2(10) not null  -- 회원명
, mobile        varchar2(20)            -- 연락처
, point         number(10) default 0    -- 포인트
, registerday   date default sysdate    -- 가입일자
, status        number(1) default 1     -- status 값이 1이면 가입, 0이면 탈퇴 상태
, constraint PK_tbl_member_userseq primary key(userseq)
, constraint UQ_tbl_member_userid unique(userid)
, constraint CK_tbl_member_status check( status in (0,1) )
);


-- 회원번호로 쓸 시퀀스 생성
create sequence userseq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select * from tbl_member
order by userseq desc;


insert into tbl_member(userseq, userid, passwd, name, mobile)
values (userseq.nextval, 'eomjh' , 'qWer1234$' , '엄정화', '010-4444-6789');

insert into tbl_member(userseq, userid, passwd, name, mobile)
values (userseq.nextval, 'kimjh' , 'qWer1234$' , '김정화', '010-2345-6789');

insert into tbl_member(userseq, userid, passwd, name, mobile)
values (userseq.nextval, 'leejh' , 'qWer1234$' , '이정화', '010-3333-6789');

insert into tbl_member(userseq, userid, passwd, name, mobile)
values (userseq.nextval, 'jungjh' , 'qWer1234$' , '정정화', '010-1111-6789');