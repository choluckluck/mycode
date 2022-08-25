
/*
create or replace procedure pcd_tbl_member_test_insert
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
        to_char(sysdate, 'hh24') < '14' OR to_char(sysdate, 'hh24') > '16'
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
             raise_application_error(-20004, '영업시간(월~금 14:00 ~ 16:59:59 까지) 아니므로 입력불가함!!');
        when error_insert then
             raise_application_error(-20003, '암호는 최소 8글자 이상이면서 대문자, 소문자, 숫자, 특수문자가 혼합되어져야 합니다.');
     
end pcd_tbl_member_test_insert;
 */
package jdbc.day02;
import java.sql.*;
import java.util.*;
/*
 	
      === tbl_member_test 테이블에 insert 할 수 있는 요일명과 시간을 제한해 두겠습니다. ===
        
      tbl_member_test 테이블에 insert 할 수 있는 요일명은 월,화,수,목,금 만 가능하며
      또한 월,화,수,목,금 중에 오후 4시 부터 오후 6시 이전까지만(오후 5시 정각은 안돼요) insert 가 가능하도록 하고자 한다.
      만약에 insert 가 불가한 요일명(토,일)이거나 불가한 시간대에 insert 를 시도하면 
      '영업시간(월~금 16:00 ~ 17:59:59 까지) 아니므로 입력불가함!!' 이라는 오류메시지가 뜨도록 한다. 
*/
/*
	create or replace procedure pcd_tbl_member_test_insert
	(p_userid IN tbl_member_test.userid%type -- IN 만큼은 생략이 가능하다. (default값이 IN이기때문에 생략하면 IN으로 본다.)
	,p_passwd    tbl_member_test.passwd%type -- OUT은 생략이 불가능하다.
	,p_name      tbl_member_test.name%type
	)
	is
	    v_length       number(2);  --     - 99 ~ 99
	    error_insert   exception;  -- EXCEPTION error_insert 선언
	    v_ch           varchar2(1);
	    v_flag_upper   number(1) := 0; -- 대문자 표식
	    v_flag_lower   number(1) := 0; -- 소문자 표식
	    v_flag_num     number(1) := 0; -- 숫자 표식
	    v_flag_special number(1) := 0; -- 특수문자 표식 
	    error_dayTime  exception;
	begin
	
	    -- 입력(insert) 이 불가한 요일명과 시간대를 알아본다. -- 
	    if( to_char(sysdate, 'd')in (1,7) OR  -- to_char(sysdate, 'd') ==> '1'(일), '2'(월), '3'(화), '4'(수), '5'(목), '6'(금), '7'(토) 
	        to_char(sysdate,'hh24') < '16' OR to_char(sysdate,'hh24') > '17' -- DB에서 범위는 숫자, 문자, 날짜, 시간 다 상관없이 쓰인다.
	    )then raise error_dayTime;
	    else -- 입력 가능한 시간이라면 암호 검사로 넘어간다
	        v_length := length(p_passwd);
	        
	        if( v_length < 8 OR v_length > 20 ) then  -- 글자수가 안맞는 경우 
	            raise error_insert; -- error_insert는 사용자가 정의하는 예외절(Exception)을 구동시켜라 
	        else -- 글자수는 올바르게 입력한 경우
	            for i in 1..v_length loop 
	               v_ch := substr(p_passwd,i,1);
	               
	               if( v_ch between 'A' and 'Z' ) then v_flag_upper := 1;
	               elsif( v_ch between 'a' and 'z' ) then v_flag_lower := 1;
	               elsif( v_ch between '0' and '9' ) then v_flag_num := 1;
	               else v_flag_special := 1;
	               end if;
	               
	            end loop;
	            
	            if(v_flag_upper * v_flag_lower * v_flag_num * v_flag_special = 1) then 
	                insert into tbl_member_test(userid, passwd, name) values(p_userid, p_passwd, p_name);
	            else 
	                raise error_insert; -- 사용자가 정의하는 예외절(EXCEPTION)을 구동시켜라.
	            end if;
	            
	        end if;
	        
	    end if;
	    
	    exception 
	    
	        when error_dayTime then -- 만약 error_dayTime 발생하면 rase_application_error();을 일으킨다.
	            raise_application_error(-20004 ,'영업시간(월~금 16:00 ~ 17:59:59 까지) 아니므로 입력불가함!!');
	        when error_insert then -- 만약 error_insert가 발생하면 rase_application_error();을 일으킨다.
	            raise_application_error(-20003 ,'암호는 최소 8글자 이상이면서 대문자,소문자, 숫자, 특수문자가 혼합되어져야 합니다.'); 
	end pcd_tbl_member_test_insert;
 */
	
public class Procedure_Insert_exception_CallableStatement_03 {
		
	public static void main(String[] args) {
			
			Connection conn = null;
			// Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바객체이다.
			
			CallableStatement cstmt = null;
			// CallableStatement cstmt 은 Connection conn(DB서버)에 존재하는 Procedure를 호출 해주는 자바 객체이다.
			String userid ="";
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				// oracle.jdbc.driver 패키지의 OracleDriver.class 파일 
	
				// >>> 2. 어떤 오라클 서버에연결을 할래? <<
				conn = DriverManager.getConnection("jdbc:oracle:thin:@211.238.142.28:1521:xe", "JDBC_USER", "aclass");
				
				// >>>> 3. Connection conn 객체를 사용하여 prepareCall() 메소드를 호출함으로써 
		        //         CallableStatement cstmt 객체를 생성한다.
		        //         즉, 우편배달부(택배기사) 객체 만들기 
				cstmt = conn.prepareCall("{call pcd_tbl_member_test_insert(?,?,?)}");
				/*
		            오라클 서버에 생성한 프로시저  pcd_tbl_member_test_insert 의 
		            매개변수 갯수가 3개 이므로 ? 를 3개 준다.
		                   
		            프로시저의 IN mode 로 되어진 파라미터에 값을 넣어줄때는 
		            cstmt.setXXX() 메소드를 사용한다.
		         */   
				
				Scanner sc = new Scanner(System.in);
				System.out.print("# 아이디 : ");
				userid = sc.nextLine();
				
				System.out.print("# 비밀번호 : ");
				String passwd = sc.nextLine();
				
				System.out.print("# 성명 : ");
				String name = sc.nextLine();
				
				cstmt.setString(1, userid); // 숫자 1은 프로시저 파라미터중 첫번째 파라미터인 IN 모드의 ? 를 말한다.
				cstmt.setString(2, passwd); // 숫자 2은 프로시저 파라미터중 두번째 파라미터인 IN 모드의 ? 를 말한다.
				cstmt.setString(3, name);  // 숫자 3은 프로시저 파라미터중 세번째 파라미터인 IN 모드의 ? 를 말한다.
				
				// >>> CallableStatement cstmt 객체를 사용하여 오라클의 프로시저 실행하기.. <<<//
				int n = cstmt.executeUpdate(); // 오라클 서버에게 해당 프로시저를 실행해라는 것이다.
				// 프로시의 실행은 cstmt.executeUpdate(); 또는 cstmt.execute(); 이다.
				if(n == 1) {
					System.out.println(">>> 데이터 입력 성공!! <<<");
				}
				
				sc.close();
			} catch (ClassNotFoundException e) {
				System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			} catch (SQLException e) { // SQL 관련해서 예외가 발생할 경우 여기로 떨어지게 된다.
				//e.printStackTrace();
				// 오라클에서 발생한 에러코드를 갖고온다.
				if(e.getErrorCode() == 20004 || e.getErrorCode() == 20003 ) {
					System.out.println(e.getMessage());
//					System.out.println("영업시간(월~금 16:00 ~ 17:59:59 까지) 아니므로 입력불가함!!");
				}
				else if(e.getErrorCode() == 1) {
					System.out.println("입력하신 아이디 "+userid+"는 이미 사용중입니다. 다른 아이디를 입력하세요.");
				}
				
			} finally {
				try { 
					if(cstmt != null)
						cstmt.close();
					
					if(conn != null)
						conn.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("~~~~ 프로그램 종료 ~~~~");
			
		}// end of main()——————————————————————————
	
}
