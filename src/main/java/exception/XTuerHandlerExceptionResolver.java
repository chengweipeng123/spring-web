package exception;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chengseas on 2016/12/12.
 */
public class XTuerHandlerExceptionResolver implements HandlerExceptionResolver{

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex){
        String errorViewName = "error.htm";
        ModelMap modelMap = new ModelMap();

        // 自己定义的异常
        if (ex instanceof ApplicationException){
            ApplicationException appEx = (ApplicationException) ex;
            errorViewName = (appEx.getErrorViewName() == null)? errorViewName: appEx.getErrorViewName();
            modelMap = appEx.getModel();
        }
        modelMap.addAttribute("error", ex.getMessage()); // 异常消息

        return new ModelAndView(errorViewName, modelMap);
    }
}
