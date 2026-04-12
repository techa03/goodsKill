package com.goodskill.ai.repository;


import com.goodskill.ai.entity.KnowledgeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, Long> {
}
