//package com.goodskill.chat.controller;
//
//import com.goodskill.chat.service.TongYiService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AiController {
//
//    @Autowired
//    @Qualifier("tongYiSimpleServiceImpl")
//    private TongYiService tongYiSimpleService;
//
//    @GetMapping("/example")
//    public String completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        return tongYiSimpleService.completion(message);
//    }
//
//}
