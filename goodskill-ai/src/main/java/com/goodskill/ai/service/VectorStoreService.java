package com.goodskill.ai.service;

import com.goodskill.ai.config.VectorStoreProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Vector Store Service using PostgreSQL pgvector
 */
@Service
public class VectorStoreService {

    private static final int EMBEDDING_BATCH_SIZE = 10;

    private final VectorStore vectorStore;
    private final VectorStoreProperties properties;
    private final JdbcTemplate jdbcTemplate;

    public VectorStoreService(VectorStore vectorStore,
                              VectorStoreProperties properties,
                              JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Save text chunks to vector store
     */
    public void saveChunks(List<ChunkDocument> chunks) {
        if (chunks.isEmpty()) {
            return;
        }

        List<Document> documents = chunks.stream()
                .map(chunk -> {
                    String chunkId = chunk.documentId() + "_" + chunk.chunkIndex();
                    String id = UUID.randomUUID().toString();
                    return new Document(
                            id,
                            chunk.content(),
                            Map.of(
                                    "chunkId", chunkId,
                                    "documentId", chunk.documentId().toString(),
                                    "documentTitle", chunk.documentTitle(),
                                    "chunkIndex", chunk.chunkIndex().toString()
                            )
                    );
                })
                .toList();

        for (int i = 0; i < documents.size(); i += EMBEDDING_BATCH_SIZE) {
            int end = Math.min(i + EMBEDDING_BATCH_SIZE, documents.size());
            List<Document> batch = documents.subList(i, end);
            vectorStore.add(batch);
        }
    }

    /**
     * Search similar documents
     */
    public List<SearchChunkResult> search(String query, int topK) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(Math.max(topK, 1))
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        return results.stream()
                .map(doc -> {
                    Map<String, Object> metadata = doc.getMetadata();
                    Long docId = Long.parseLong(metadata.get("documentId").toString());
                    String title = metadata.getOrDefault("documentTitle", "未知文档").toString();
                    Integer chunkIndex = Integer.parseInt(metadata.get("chunkIndex").toString());
                    double score = doc.getScore() != null ? doc.getScore() : 0.0;
                    return new SearchChunkResult(docId, title, chunkIndex, doc.getText(), score);
                })
                .toList();
    }

    /**
     * Count chunks by document ID
     */
    public long countByDocumentId(Long documentId) {
        String tableName = properties.getTableName();
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE metadata->>'documentId' = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, documentId.toString());
        return count != null ? count : 0L;
    }

    /**
     * Delete chunks by document ID
     */
    public void deleteByDocumentId(Long documentId) {
        String tableName = properties.getTableName();
        String sql = "DELETE FROM " + tableName + " WHERE metadata->>'documentId' = ?";
        jdbcTemplate.update(sql, documentId.toString());
    }

    public record ChunkDocument(
            Long documentId,
            String documentTitle,
            Integer chunkIndex,
            String content
    ) {
    }

    public record SearchChunkResult(
            Long documentId,
            String documentTitle,
            Integer chunkIndex,
            String content,
            Double score
    ) {
    }
}
