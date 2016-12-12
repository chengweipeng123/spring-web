package controller;

import domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by chengseas on 2016/12/4.
 */
@Controller
@SessionAttributes({"result"})
public class ParameterController {

    /** ============== @PathVariable:
     *
     * 取得 URL 中的匹配的内容，适合 REST 的风格。
     *
     * ============== **/


    // {userId} 是 placeholder，里面的内容可以用 @PathVariable 取到
    @RequestMapping("/user/{userId}")  // http://localhost:8080/user/1234
    @ResponseBody
    public String one(@PathVariable Integer userId){
        return userId+"";
    }

    // 如果变量名和 {} 中的名字不一致，
    // 需要用 @PathVariable("nameInPath") 来指定变量要使用路径的哪一个变量
    @RequestMapping("/category/{categoryName}/product/{productId}") // http://localhost:8080/category/xbox/product/1234
    @ResponseBody
    public String two(@PathVariable String categoryName, @PathVariable("productId") Integer pId){
        return "categoryName: " + categoryName + ", productId: " + pId;
    }

    // 还支持正则表达式的方式
    @RequestMapping("/regex/{text:[a-z]+}-{number:\\d+}")  // http://localhost:8080/regex/part-1234
    @ResponseBody
    public String three(@PathVariable String text, @PathVariable Integer number) {
        return "Text: " + text + ", Number: " + number;
    }


    /** ============== @RequestParam:
     *
     * 取得 HTTP 请求中的参数。
     *
     * ============== **/

    // 取得名字为 id 的参数的值
    @RequestMapping("/user")  // http://localhost:8080/user?id=1234
    @ResponseBody
    public String findUser(@RequestParam Integer id){
        return "ID: " + id;
    }

    // required 默认是 true，参数是必要的，如果没有提供需要的参数，则报错
    // required 为 false 表示参数是可选的
    @RequestMapping("/product") // http://localhost:8080/product?productId=1234&productName=PS4
    @ResponseBody
    public String findProduct(@RequestParam(value="productId", required=true) Integer id,
                              @RequestParam(value="productName", required=false) String name) {
        return "ID: " + id + ", Name: " + name;
    }


    /** ============== @ModelAttribute:
     *
     * 把 HTTP 请求的参数映射到对象，参数名和对象中的属性名匹配的就做映射，不匹配的就不管。
     *
     * ============== **/

    // 表单中的参数被映射到对象 user
    @RequestMapping("/userModel")  // http://localhost:8080/userModel?id=1234&username=biao&password=Secret&age=12
    @ResponseBody
    public String findUser(@ModelAttribute User user,
                           @RequestParam(required=false) Integer age) {
        System.out.println("Age: " + age);
        return user.toString();
    }


    /** ============== Forward and Redirect:Forward：
     *
     * forward:/URI Redirect：redirect:/URI
     *
     * ============== **/

    @RequestMapping("/forward-test") // http://localhost:8080/forward-test
    public String forward() {
        return "forward:/helloworld-springmvc";
    }

    @RequestMapping("/redirect-test") // http://localhost:8080/redirect-test
    public String redirect() {
        return "redirect:/helloworld-springmvc";
    }


    /** ============== RedirectAttributes:
     *
     * 表单提交后一般都会 redirect 到另一个页面，防止表单重复提交,
     * RedirectAttributes 的作用就是把处理 PageA 的结果存储起来，当 redirect 到 PageB 的时候显示 PageA 的结果。
     Request 中的参数不能被传递给 redirect 的页面，因为 redirect 是从浏览器端发起一个新的请求。
     *
     * ============== **/

    // 显示表单
    @RequestMapping(value = "/user-form", method = RequestMethod.GET) // http://localhost:8080/user-form
    public String showUserForm(ModelMap model, HttpSession session){
        String token = UUID.randomUUID().toString().toUpperCase().replaceAll("-","");
        model.addAttribute("token", token);
        session.setAttribute(token,token);
        return "user-form.htm";
    }

    /**
     * 可以使用 token 防止后退的情况下重复提交表单，访问表单页面的时候生成一个 token 在 form 里并且在 Server 端存储这个 token，
     * 提交表单的时候先检查 Server 端有没有这个 token，如果有则说明是第一次提交表单，
     * 然后把 token 从 Server 端删除，处理表单，redirect 到 result 页面，
     * 如果 Server 端没有这个 token，则说明是重复提交的表单，不处理表单的提交。
     *
     * 注：发现这种方式并不能解决重复提交的问题，因为chrome浏览器中发现网页的后退依然执行了上面的get方法，导致重新生成了新的token
     * @param username
     * @param password
     * @param token
     * @param session
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/user-form", method = RequestMethod.POST)
    public String handleUserForm(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String token,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes){
        // 处理表单前， 查看 token 是否有效
        if (token == null || token.isEmpty() || !token.equals(session.getAttribute(token))){
            throw new RuntimeException("重复提交表单");
        }

        // 正常提交表单
        session.removeAttribute(token);

        // Update user in database...
        System.out.println("Username:" + username + ",Password:" + password);

        // 操作结果显示给用户
        redirectAttributes.addFlashAttribute("result", "The user is already successfully updated");

        return "redirect:result";
    }

    // 更新 User，把结果保存到 RedirectAttributes
    @RequestMapping("/update-user")
    public String updateUser(@RequestParam String username,
                             @RequestParam String password,
                             final RedirectAttributes redirectAttributes){
        // Update user in database...
        System.out.println("Username: " + username + ", Password: " + password);
        // 操作结果显示给用户
        redirectAttributes.addFlashAttribute("result", "The user is already successfully updated");
        return "redirect:/result";
    }

    // 显示表单处理结果
    @RequestMapping("/result")
    public String result() {
        return "result.htm";
    }

     //    Redirect 到另一个 Controller 时获取 RedirectAttributes 里的属性使用 @ModelAttribute
    @RequestMapping("flash")
    public String flash(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("username", "Biao");
        return "redirect:flash2";
    }

    @RequestMapping("flash2")
    @ResponseBody
    public String flash2(@ModelAttribute("username") String username) {
        return "username: " + username;
    }


    /** ============== Request and Response:
     *
     * 想取得 HttpServletRequest 和 HttpServletResponse 很容易，
     * 只要在方法的参数里定义后，SpringMVC 会自动的注入它们。
     *
     * ============== **/

    @RequestMapping("/request-response")
    public String requestAndResponse(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request);
        return "done";
    }


    /** ============== @RequestHeader:
     *
     * Read and write header. Header直接写到HttpServletResponse
     *
     * ============== **/


    @RequestMapping("/read-header")
    @ResponseBody
    public String readHeader(@RequestHeader("Host") String host,
                             @RequestHeader(value="Accept-Encoding", required=false) String encoding,
                             @RequestHeader(value="Accept-Charset", required=false) String charset) {
        return "Host: " + host + ", Encoding: " + encoding + ", Charset: " + charset;
    }

    @RequestMapping("/read-header-error")
    @ResponseBody
    public String readHeaderError(@RequestHeader("Host") String host,
                                  @RequestHeader("Accept-Charset") String charset) {
        return "Host: " + host + ", Charset: " + charset;
    }

    @RequestMapping("/write-header")
    @ResponseBody
    public String writeHeader(HttpServletResponse response) {
        response.setHeader("token", "D4BFCEC2-89E6-40CB-AF9A-B5513CB30FED");
        return "Header is wrote.";
    }


    /** ============== @CookieValue:
     *
     * Read and write Cookie. Cookie直接写到HttpServletResponse
     *
     * ============== **/

    @RequestMapping("/write-cookie")
    @ResponseBody
    public String writeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("username", "Don't tell you");
        cookie.setMaxAge(1000);
        response.addCookie(cookie); // Put cookie in response.

        return "Cookie is wrote.";
    }

    @RequestMapping("/read-cookie")
    @ResponseBody
    public String readCookie(@CookieValue("username") String cookie) {
        return "Cookie for username: " + cookie;
    }


    /** ============== @SessionAttributes and HttpSession:
     *
     * 可以使用 HttpSession 来直接操作 session，同时 SpringMVC 也提供了操作 session 的注解 @SessionAttributes。

     HttpSession 的方式什么时候都生效，但是 @SessionAttributes 有时候就不行，
     如返回 AJAX 请求时设置的 session 无效。所以推荐使用注入 HttpSession 来读写 session。
     *
     * ============== **/

    @RequestMapping("/write-session")
    public String writeSession(HttpSession session){
        session.setAttribute("username", "seas");
        session.setAttribute("password", "123456");
        return "Session write";
    }

    @RequestMapping("/read-session")
    public String readSession() {
        return "helloworld-freemarker.htm";
    }

    @RequestMapping("/write-session2")
    public ModelAndView writeSession2() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("result", "Save session");
        mav.setViewName("user-form.htm");
        return mav;
    }

    @RequestMapping("/read-session2")
    public String readSession2() {
        return "result.htm";
    }

    /** ============== JSON交互:
     *
     * ============== **/

    //前台键值对：h5页面直接传js对象，content-type默认值
    @RequestMapping("/submitKeyValue")
    public void submitKeyValue(HttpServletRequest request, HttpServletResponse response){
        // 获取页面参数
        String username = request.getParameter("username");
        // 页面赋值
        request.setAttribute("username", username == null ? "" : username);
    }
    //或直接转换为对象
    @RequestMapping("/submitObject")
    public void submitObject(HttpServletRequest request, HttpServletResponse response, User user){
        System.out.println(user.getUsername());
    }

    //前台json raw：app端或ajax传jsson字符串（JSON.stringify(data)），contentType: "application/json"
    @RequestMapping("/submitJsonStr")
    public void submitJsonStr(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, Object> map){
        for (Object obj : map.keySet()){
            Object value = map.get(obj);
            System.out.println(value);
        }
    }
    // 或，直接转换为对象
    @RequestMapping("/submitJsonObject")
    public void submitJsonObject(HttpServletRequest request, HttpServletResponse response, @RequestBody User user){
        System.out.println(user.getUsername());
    }
}
