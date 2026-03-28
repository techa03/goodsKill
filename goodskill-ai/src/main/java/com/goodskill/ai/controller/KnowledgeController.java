package com.goodskill.ai.controller;

import com.goodskill.ai.dto.request.AddTextRequest;
import com.goodskill.ai.dto.response.KnowledgeDocumentDto;
import com.goodskill.ai.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping("/text")
    public KnowledgeDocumentDto addText(@Valid @RequestBody AddTextRequest request) {
        return knowledgeBaseService.addTextDocument(request.title(), request.content());
    }

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<KnowledgeDocumentDto> uploadFiles(@RequestPart("files") List<MultipartFile> files) throws IOException {
        List<KnowledgeDocumentDto> created = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String title = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "未命名文件";
            String content = new String(file.getBytes(), StandardCharsets.UTF_8).trim();
            if (content.isEmpty()) {
                continue;
            }
            created.add(knowledgeBaseService.addTextDocument(title, content));
        }
        return created;
    }

    @GetMapping
    public List<KnowledgeDocumentDto> list() {
        return knowledgeBaseService.listDocuments();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        knowledgeBaseService.deleteDocument(id);
    }
}
