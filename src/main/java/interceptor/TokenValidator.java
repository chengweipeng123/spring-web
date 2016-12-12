package interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by chengseas on 2016/12/12.
 */
public class TokenValidator implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // POST, PUT, DELETE 请求都有可能是表单提交
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            String clientToken = request.getParameter("token");
            String serverToken = (String) request.getSession().getAttribute(clientToken);

            if (clientToken == null || clientToken.isEmpty() || !clientToken.equals(serverToken)) {
                throw new RuntimeException("重复提交表单");
            }

            // 正常提交表单，删除 token
            request.getSession().removeAttribute(clientToken);
        }

        return true;
    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        // GET 请求访问表单页面
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        // 生成 token 存储到 session 里，并且保存到 form 的 input 域
        String token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        modelAndView.addObject("token", token);
        request.getSession().setAttribute(token, token);
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

    }
}
