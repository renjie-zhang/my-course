package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.impl.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 输出 “Hello,World” Controller
 */
@Path("/user")
public class UserController implements PageController {

    private UserService userService = new UserServiceImpl();
    @GET
    @Path("/singIn") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("username");
        String phoneNumber = request.getParameter("phoneNumber");
        User u = new User();
        if(email != null){
            u.setEmail(email);
        }
        if(password != null){
            u.setPassword(password);
        }
        if(name != null){
            u.setName(name);
        }
        if(phoneNumber != null){
            u.setPhoneNumber(phoneNumber);
        }
        boolean register = userService.register(u);
        if(register){
            User user = userService.queryUserByNameAndPassword(name, password);
            request.setAttribute("user",user);
            return "index.jsp";
        }
        return "login-form.jsp";
    }
}
