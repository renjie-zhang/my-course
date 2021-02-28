/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.renjiezhang.web.mvc;

import com.renjiezhang.web.mvc.controller.Controller;
import com.renjiezhang.web.mvc.controller.PageController;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

public class FrontControllerServlet extends HttpServlet {

    /**
     * 请求路径和 Controller 的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>(16);

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>(16);

    public void init(ServletConfig servletConfig) {
        initHandleMethods();
    }


    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // hello/world
        String requestURI = request.getRequestURI();
        String prefixPath = request.getContextPath();
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));

        // find controller
        Controller controller = controllersMapping.get(requestMappingPath);

        if(controller != null){
            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try{
                if(handlerMethodInfo!= null){
                    String method = request.getMethod();
                    if(!handlerMethodInfo.getSupportedHttpMethods().contains(method)){
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    if(controller instanceof PageController){
                        PageController cast = PageController.class.cast(controller);
                        String execute = cast.execute(request, response);
                        ServletContext servletContext = request.getServletContext();

                        // 页面请求 forward
                        // request -> RequestDispatcher forward
                        // RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
                        // ServletContext -> RequestDispatcher forward
                        // ServletContext -> RequestDispatcher 必须以 "/" 开头
                        if(!execute.startsWith("/")){
                            execute = "/"+execute;
                        }
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(execute);
                        requestDispatcher.forward(request,response);
                    }
                }
            } catch (Throwable throwable) {
                if(throwable.getCause() instanceof IOException){
                    throw  (IOException) throwable.getCause();
                }else {
                    throw new ServletException(throwable.getCause());
                }
            }
        }
    }

    /**
     * use SPI read all path
     */
    private void initHandleMethods() {
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<? extends Controller> controllerClass = controller.getClass();
            Path p = controllerClass.getAnnotation(Path.class);
            StringBuilder requestPath = new StringBuilder(p.value());
            Method[] methods = controllerClass.getMethods();
            for (Method m : methods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(m);
                Path annotation = m.getAnnotation(Path.class);
                if (annotation != null) {
                    requestPath.append(annotation.value());
                }
                handleMethodInfoMapping.put(requestPath.toString(),
                        new HandlerMethodInfo(requestPath.toString(), m, supportedHttpMethods));
            }
            controllersMapping.put(requestPath.toString(), controller);
        }
    }

    /***
     * find method support Http Method Collection
     * @return
     *
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethod = new LinkedHashSet<>();
        for (Annotation a : method.getAnnotations()) {
            HttpMethod annotation = a.annotationType().getAnnotation(HttpMethod.class);
            if (annotation != null) {
                supportedHttpMethod.add(annotation.value());
            }
        }
        if (supportedHttpMethod.isEmpty()) {
            supportedHttpMethod.addAll(asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }
        return supportedHttpMethod;
    }
}
