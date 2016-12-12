package exception;

import org.springframework.ui.ModelMap;

/**
 * SpringMVC 提供了 3 种异常处理方法：
 1. 使用 @ExceptionHandler 注解实现异常处理
 2. 简单异常处理器 SimpleMappingExceptionResolver
 3. 实现异常处理接口 HandlerExceptionResolver，自定义异常处理器
 通过比较，实现异常处理接口 HandlerExceptionResolver 是最合适的，
 可以给定更多的异常信息，可一自定义异常显示页面等，下面介绍使用这种方式处理异常。

 自定义异常：
 实现自定义异常是为了能更灵活的添加一些默认异常没有的功能，如果需要更多功能，自行扩展。
 * Created by chengseas on 2016/12/12.
 */
public class ApplicationException extends RuntimeException {
    private String errorViewName = null;
    private ModelMap model = new ModelMap();

    public ApplicationException(String message){
        this(message, null);
    }

    public ApplicationException(String message, String errorViewName){
        super(message);
        this.errorViewName = errorViewName;
    }

    public String getErrorViewName(){
        return errorViewName;
    }

    public ModelMap getModel(){
        return model;
    }

    public void addAttribute(String name, Object value){
        model.addAttribute(name, value);
    }
}
