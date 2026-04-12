package com.goodskill.ai.service;

import com.goodskill.ai.dto.response.KnowledgeDocumentDto;
import com.goodskill.ai.entity.KnowledgeDocumentEntity;
import com.goodskill.ai.repository.KnowledgeDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class KnowledgeBaseService {

    private final KnowledgeDocumentRepository documentRepository;
    private final TextChunker textChunker;
    private final VectorStoreService vectorStoreService;

    public KnowledgeBaseService(KnowledgeDocumentRepository documentRepository,
                                TextChunker textChunker,
                                VectorStoreService vectorStoreService) {
        this.documentRepository = documentRepository;
        this.textChunker = textChunker;
        this.vectorStoreService = vectorStoreService;
    }

    @Transactional
    public KnowledgeDocumentDto addTextDocument(String title, String content) {
        KnowledgeDocumentEntity document = new KnowledgeDocumentEntity();
        document.setTitle(title.trim());
        document.setContent(content.trim());
        document.setCreatedAt(Instant.now());
        KnowledgeDocumentEntity saved = documentRepository.save(document);

        List<String> chunks = textChunker.split(saved.getContent());
        List<VectorStoreService.ChunkDocument> esChunks = new ArrayList<>(chunks.size());
        int idx = 0;
        for (String chunk : chunks) {
            esChunks.add(new VectorStoreService.ChunkDocument(
                    saved.getId(),
                    saved.getTitle(),
                    idx++,
                    chunk
            ));
        }

        if (!esChunks.isEmpty()) {
            vectorStoreService.saveChunks(esChunks);
        }

        return toDto(saved, esChunks.size());
    }

    @Transactional(readOnly = true)
    public List<KnowledgeDocumentDto> listDocuments() {
        List<KnowledgeDocumentEntity> docs = documentRepository.findAll();
        return docs.stream()
                .sorted(Comparator.comparing(KnowledgeDocumentEntity::getCreatedAt).reversed())
                .map(doc -> toDto(doc, vectorStoreService.countByDocumentId(doc.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResponseStatusException(NOT_FOUND, "文档不存在");
        }
        vectorStoreService.deleteByDocumentId(documentId);
        documentRepository.deleteById(documentId);
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunk> search(String query, int topK) {
        return vectorStoreService.search(query, topK).stream()
                .map(hit -> new RetrievedChunk(
                        hit.documentId(),
                        hit.documentTitle(),
                        hit.chunkIndex(),
                        hit.content(),
                        hit.score()
                ))
                .toList();
    }

    private KnowledgeDocumentDto toDto(KnowledgeDocumentEntity doc, long chunkCount) {
        return new KnowledgeDocumentDto(doc.getId(), doc.getTitle(), doc.getCreatedAt(), chunkCount);
    }

    public record RetrievedChunk(
            Long documentId,
            String documentTitle,
            Integer chunkIndex,
            String content,
            Double score
    ) {
    }
}
