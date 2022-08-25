set hidden param parseThreshold = 150000;

----- **** ===== 오라클 계정 생성하기 시작 ===== **** -----
   
--- 오라클 계정 생성을 위해서 SYS 또는 SYSTEM 으로 연결하여 작업을 해야 합니다. [SYS 시작]  ---
show user;
-- USER이(가) "SYS"입니다.

-- 이제 부터 오라클 계정생성시 계정명앞에 c## 붙이지 않고 생성하도록 하겠다.
alter session set "_ORACLE_SCRIPT"=true;
-- Session이(가) 변경되었습니다.
   
create user jdbc_user identified by aclass default tablespace users;
-- User JDBC_USER이(가) 생성되었습니다.

-- 위에서 생성되어진 jdbc_user 이라는 오라클 일반사용자 계정에게 오라클서버에 접속이 되어지고, 테이블 등등을 생성할 수 있도록 여러가지 권한을 부여해주겠습니다. 
grant connect, resource, create view, unlimited tablespace to jdbc_user;
-- Grant을(를) 성공했습니다.

----- **** ===== 오라클 계정 생성하기 끝 ===== **** -----

show user;
-- USER이(가) "JDBC_USER"입니다.

-- drop table tbl_memo purge;

create table tbl_memo
(no        number(4)
,name      varchar2(20) not null
,msg       varchar2(200) not null
,writeday  date default sysdate
,constraint PK_tbl_memo_no primary key(no)
);
-- Table TBL_MEMO이(가) 생성되었습니다.

-- drop sequence seq_memo;

create sequence seq_memo
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_MEMO이(가) 생성되었습니다.

select *
from tbl_memo
order by no desc;


select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
order by no desc;


select *
from tbl_memo
where no = 1
order by no desc;

select *
from tbl_memo
where no = '1'
order by no desc;

select *
from tbl_memo
where no = to_number('1')
order by no desc;


desc tbl_memo;

select *
from tbl_memo
where no = to_number('하하호호')
order by no desc;
/*
ORA-01722: 수치가 부적합합니다
01722. 00000 -  "invalid number"
*/


select *
from tbl_memo
where to_char(no) = '하하호호'
order by no desc;


select *
from tbl_memo
where to_char(no) = '1'
order by no desc;


select *
from tbl_memo
where name = '서영학'
order by no desc;

select *
from tbl_memo
where name = '아이유'
order by no desc;

select *
from tbl_memo
where no = '3423'
order by no desc;

select *
from tbl_memo
where msg like '%'|| '몰라' ||'%'
order by no desc;

select *
from tbl_memo
where msg like '%'|| '안녕' ||'%'
order by no desc;



----------------------------------------------------------------

select* from tbl_memo
order by no asc;

update tbl_memo set name = '서영학', msg = '밖밖'
where no = 1;

commit;

select *
from user_tables
where table_name = 'TBL_EXAM_TEST';

drop table tbl_parent_dept purge;
create table tbl_parent_dept
(deptno         number(2) not null
, dname         varchar2(40) not null
, constraint PK_tbl_parent_dept_deptno primary key (deptno)
);

create table tbl_child_emp
(empno          number(4) not null
, ename         varchar2(20) not null
, fk_deptno     number(2)
, constraint PK_tbl_child_emp_empno primary key(empno)
, constraint fk_tbl_child_emp_deptno foreign key(fk_deptno) references tbl_parent_dept
);

select *
from user_constraints
where table_name = 'TBL_CHILD_EMP';

drop table tbl_parent_dept cascade constraints purge;
-- tbl_parent_dept 테이블을 참조하고 있는 모든 자식테이블의 foreign key(외래 키)를 먼저 삭제해 주고 나서 tbl_parent_dept 테이블을 drop 시켜주는 것이다.
-- cascade constraints 를 넣어주면 부모 자식 관계 때문에 삭제하지 못하는 부분을 해결해 준다.

select *
from user_sequences
where sequence_name = 'SEQ_MEMO';

create sequence seq_exam_test
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache

insert into tbl_exam_test (no, name, msg)
values (seq_exam_test.nextval, '이순신', '안녕하세요? 이순신입니다.');