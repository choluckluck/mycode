package jdbc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyUtil {
	public static String addDay(int n) {
		
		// begin 에서 day13.c 를 참고한다.
		Calendar currentDate = Calendar.getInstance();
		// 현재 날짜와 시간을 얻어온다.
		
		currentDate.add(Calendar.DATE, n); // 날짜를 더해줌
		// currentDate.add(Calendar.DATE, 1); 
	    // ==> currentDate(현재 날짜)에서 두번째 파라미터에 입력해준 숫자(그 단위는 첫번째 파라미터인 것이다. 지금은 Calendar.DATE 이므로 날짜수이다)만큼 더한다.
	    // ==> 위의 결과는  currentDate 값이 1일 더한 값으로 변한다.   
		
		SimpleDateFormat dateft = new SimpleDateFormat("yyyy-MM-dd");
		
		return dateft.format(currentDate.getTime());
	} // end of public static String addDay(int n) --------------------------------------------------------------
}
