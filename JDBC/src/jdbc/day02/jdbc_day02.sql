set hidden param parseThreshold = 150000;

show user;


------------------------------------------------------------------------------
-- 1) 학급테이블 생성
create table tbl_class
(classno        number(3)
,classname      varchar2(100)
,teachername    varchar2(20)
,constraint PK_tbl_class_classno primary key(classno)
);

create sequence seq_classno
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍A', '김샘'); 

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍B', '이샘');

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍C', '서샘');

commit;

select *
from tbl_class;


-- 2) 학생테이블 생성 
create table tbl_student
(stno           number(8)               -- 학번
,name           varchar2(20) not null   -- 학생명
,tel            varchar2(15) not null   -- 연락처
,addr           varchar2(100)           -- 주소
,registerdate   date default sysdate    -- 입학일자
,fk_classno     number(3) not null      -- 학급번호
,constraint PK_tbl_student_stno primary key(stno)
,constraint FK_tbl_student_classno foreign key(fk_classno) references tbl_class(classno)
);    

-- 학번에 사용할 시퀀스 생성
create sequence seq_stno
start with 9001
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '이순신', '02-234-5678', '서울시 강남구 역삼동', default, 1);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '김유신', '031-345-8876', '경기도 군포시', default, 2);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '안중근', '02-567-1234', '서울시 강서구 화곡동', default, 2);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '엄정화', '032-777-7878', '인천시 송도구', default, 3);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '박순신', '02-888-9999', '서울시 마포구 서교동', default, 3);

commit;

select *
from tbl_student;

/*
  >>>> Stored Procedure 란? <<<<<
  Query 문을 하나의 파일형태로 만들거나 데이터베이스에 저장해 놓고 함수처럼 호출해서 사용하는 것임.
  Stored Procedure 를 사용하면 연속되는 query 문에 대해서 매우 빠른 성능을 보이며, 
  코드의 독립성과 함께 보안적인 장점도 가지게 된다.
*/
-- drop procedure pcd_student_select_one;
create or replace procedure pcd_student_select_one
(p_stno             IN tbl_student.stno%type
,o_name             OUT tbl_student.name%type          -- 'o' 는 out 모드라는 뜻 
,o_tel              OUT tbl_student.tel%type
,o_addr             OUT tbl_student.addr%type
,o_registerdate     OUT varchar2
,o_classname        OUT tbl_class.classname%type
,o_teachername      OUT tbl_class.teachername%type
)

is
    v_cnt number(1);
begin
    select count(*) INTO v_cnt
    from tbl_student
    where stno = p_stno;
    
    if v_cnt = 0 then
       o_name := null;
       o_tel := null;
       o_addr := null;
       o_registerdate := null;
       o_classname := null;
       o_teachername:= null;
    else 
       select S.name, S.tel, S.addr, to_char(S.registerdate, 'yyyy-mm-dd'),
              C.classname, C.teachername 
              INTO
              o_name, o_tel, o_addr, o_registerdate, o_classname, o_teachername
       from tbl_student S join tbl_class C
       on S.fk_classno = C.classno
       where S.stno = p_stno;
    end if;
end pcd_student_select_one;

create or replace procedure pcd_student_select_many
(p_addr         IN      tbl_student.addr%type
, o_data        OUT     SYS_REFCURSOR
)

is 

begin
    open o_data for
    select S.stno, S.name, S.tel, S.addr, to_char(S.registerdate, 'yyyy-mm-dd') AS registerdate,
           C.classname, C.teachername 
    from ( select *
           from tbl_student
           where addr like '%' || p_addr || '%') S JOIN tbl_class C
    ON S.fk_classno = C.classno;
    
end pcd_student_select_many;

create table tbl_member_test
(userid      varchar2(20)
,passwd      varchar2(20) not null
,name        varchar2(30) not null
,constraint  PK_tbl_member_test_userid  primary key(userid)
);



/*
      === tbl_member_test 테이블에 insert 할 수 있는 요일명과 시간을 제한해 두겠습니다. ===
        
      tbl_member_test 테이블에 insert 할 수 있는 요일명은 월,화,수,목,금 만 가능하며
      또한 월,화,수,목,금 중에 오후 3시 부터 오후 6시 이전까지만(오후 6시 정각은 안돼요) insert 가 가능하도록 하고자 한다.
      만약에 insert 가 불가한 요일명(토,일)이거나 불가한 시간대에 insert 를 시도하면 
      '영업시간(월~금 15:00 ~ 17:59:59 까지) 아니므로 입력불가함!!' 이라는 오류메시지가 뜨도록 한다. 
*/
-- drop procedure pcd_tbl_member_test_insert ;
create or replace procedure pcd_tbl_member_test
  (p_userid IN tbl_member_test.userid%type
  ,p_passwd    tbl_member_test.passwd%type   -- IN 만큼은 생략가능한데 생략하면 IN 으로 본다.
  ,p_name      tbl_member_test.name%type
  )
  is
     v_length        number(2);     --  -99 ~ 99 
     error_insert    exception;
     v_ch            varchar2(1);
     v_flag_upper    number(1) := 0; -- 대문자 표식
     v_flag_lower    number(1) := 0; -- 소문자 표식
     v_flag_num      number(1) := 0; -- 숫자 표식
     v_flag_special  number(1) := 0; -- 특수문자 표식
     error_dayTime   exception;
     
  begin
       
       -- 입력(insert)이 불가한 요일명과 시간대를 알아봅니다. --
       if ( to_char(sysdate, 'd') in('1','7') OR    -- to_char(sysdate, 'd') ==> '1'(일), '2'(월), '3'(화), '4'(수), '5'(목), '6'(금), '7'(토) 
            to_char(sysdate, 'hh24') < '15' OR to_char(sysdate, 'hh24') > '16'
           ) then raise error_dayTime;
       
       else -- 암호검사를 하겠다.
           v_length := length(p_passwd);
            
           if( v_length < 8 OR v_length > 20 ) then
               raise  error_insert;  -- 사용자가 정의하는 예외절(EXCEPTION)을 구동시켜라.
           else
               for i in 1..v_length loop
                  v_ch := substr(p_passwd, i, 1);
                  
                  if( v_ch between 'A' and 'Z' )    then v_flag_upper := 1;
                  elsif( v_ch between 'a' and 'z' ) then v_flag_lower := 1;
                  elsif( v_ch between '0' and '9' ) then v_flag_num := 1;
                  else v_flag_special := 1;
                  end if;
                   
               end loop;
               
               if (v_flag_upper * v_flag_lower * v_flag_num * v_flag_special = 1) then
                   insert into tbl_member_test(userid, passwd, name) values(p_userid, p_passwd, p_name);
                  
               else
                   raise error_insert;  -- 사용자가 정의하는 예외절(EXCEPTION)을 구동시켜라.
               end if;    
               
           end if;
           
       end if;    
       
       exception
            when error_dayTime then
                 raise_application_error(-20004, '영업시간(월~금 16:00 ~ 17:59:59 까지) 아니므로 입력불가함!!');
            when error_insert then
                 raise_application_error(-20003, '암호는 최소 8글자 이상이면서 대문자, 소문자, 숫자, 특수문자가 혼합되어져야 합니다.');
         
  end pcd_tbl_member_test;
  -- Procedure PCD_TBL_MEMBER_TEST이(가) 컴파일되었습니다.
  
  select * from tbl_member_test;
  
  
  
  -----------------------------------------------------------
  -- [퀴즈 풀기 위해서 오류 코드 확인했어야함]
  insert into tbl_student(stno, name, tel,addr,fk_classno)
  values(9007,'조하솔','010-2222-2222','서울',46);
    -- 에러코드 2291