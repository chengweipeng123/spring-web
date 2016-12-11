package interceptor;

import org.springframework.web.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by chengseas on 2016/12/11.
 */
public class MyInterceptor implements HandlerInterceptor{


    /**
     *  Intercept the execution of a handler.
     Called after HandlerMapping determined an appropriate handler object,
     but before HandlerAdapter invokes the handler.
     DispatcherServlet processes a handler in an execution chain,
     consisting of any number of interceptors,
     with the handler itself at the end. With this method,
     each interceptor can decide to abort the execution chain,
     typically sending a HTTP error or writing a custom response.
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{
        System.out.println("my interceptor");
        return true;
    }

    /**
     * Intercept the execution of a handler.
     * Called after HandlerAdapter actually invoked the handler,
     * but before the DispatcherServlet renders the view.
     * Can expose additional model objects to the view via the given ModelAndView.
     DispatcherServlet processes a handler in an execution chain,
     consisting of any number of interceptors,with the handler itself at the end.
     With this method, each interceptor can post-process an execution,
     getting applied in inverse order of the execution chain.
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception{
        String token = UUID.randomUUID().toString().replace("-","").toUpperCase();
        System.out.println(token);
        modelAndView.addObject("token", token);
    }

    /**
     * Callback after completion of request processing, that is, after rendering the view.
     * Will be called on any outcome of handler execution, thus allows for proper resource cleanup.
     Note: Will only be called if this interceptor's preHandle method has successfully completed and returned true!
     As with the postHandle method, the method will be invoked on each interceptor in the chain in reverse order,
     so the first interceptor will be the last to be invoked.
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception{

    }
}
