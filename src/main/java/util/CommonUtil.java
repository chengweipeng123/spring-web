package util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chengseas on 2016/12/10.
 */
public class CommonUtil {

    // 判断是否 AJAX 请求
    public static boolean useAjax(HttpServletRequest request){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}
