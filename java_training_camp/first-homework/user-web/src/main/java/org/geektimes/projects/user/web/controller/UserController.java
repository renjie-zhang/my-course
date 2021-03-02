package org.geektimes.projects.user.web.controller;

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

    @GET
    @Path("/singIn") // /hello/world -> HelloWorldController
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if(email != null){
            request.setAttribute("email",email);
        }
        if(password != null){
            request.setAttribute("password",password);
        }
        return "index.jsp";
    }
}
