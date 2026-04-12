package com.goodskill.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由控制器
 */
@Controller
public class PageController {

    /**
     * AI 对话页面
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 企业问答知识库页面（RAG）
     */
    @GetMapping("/rag")
    public String rag() {
        return "index_rag";
    }
}
