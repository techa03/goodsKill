//package org.seckill.web.controller;
//
//import io.swagger.annotations.Api;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Api(tags = "公共控制")
//@Controller
//public class CommonController {
//    @GetMapping("/error")
//    public String handleError(HttpServletRequest request){
//        String errorCode = String.valueOf(request.getAttribute("javax.servlet.error.status_code"));
//        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
//        if("404".equals(errorCode)){
//            return "redirect:/html/404.html";
//        }else if("500".equals(errorCode)){
//            return "redirect:/html/500.html";
//        }else{
//            return "redirect:/seckill/list";
//        }
//    }
//}
