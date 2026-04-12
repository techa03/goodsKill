package com.goodskill.ai.service;

import com.goodskill.ai.config.KnowledgeBaseProperties;
import com.goodskill.ai.dto.response.AskResponse;
import com.goodskill.ai.dto.response.SourceDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class QaService {

    private static final String SYSTEM_PROMPT = """
            你是企业知识库问答助手。必须优先基于给定知识片段回答；
            如果信息不足，明确说"知识库中没有足够信息"。
            回答要中文、准确、简洁，并在结尾附上引用标记，例如 [D12-C3]。
            """;

    private static final String USER_PROMPT_TEMPLATE = """
            {query}

            Context information is below.
            ---------------------
            {context}
            ---------------------

            Given the context information and no prior knowledge, answer the query.
            Follow these rules:
            1. If the answer is not in the context, just say that you don't know.
            2. Avoid statements like "Based on the context...".
            3. Answer in Chinese.
            """;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final KnowledgeBaseProperties properties;

    public QaService(ChatModel chatModel,
                     VectorStore vectorStore,
                     KnowledgeBaseProperties properties) {
        this.vectorStore = vectorStore;
        this.properties = properties;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public AskResponse ask(String question, Integer topK) {
        int finalTopK = topK == null || topK <= 0 ? properties.getDefaultTopK() : topK;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(finalTopK)
                .build());

        List<SourceDto> sources = documents.stream()
                .map(this::toSourceDto)
                .toList();

        if (sources.isEmpty()) {
            return new AskResponse("当前知识库没有可用内容，请先上传文档或录入知识。", List.of());
        }

        String context = buildContext(documents);
        Prompt prompt = new PromptTemplate(USER_PROMPT_TEMPLATE)
                .create(Map.of("query", question, "context", context));

        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        String answer = chatResponse.getResult().getOutput().getText();

        return new AskResponse(
                answer == null || answer.isBlank() ? "模型没有返回内容，请重试。" : answer,
                sources
        );
    }

    public StreamResult streamAnswer(String question, Integer topK) {
        int finalTopK = topK == null || topK <= 0 ? properties.getDefaultTopK() : topK;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(finalTopK)
                .build());

        List<SourceDto> sources = documents.stream()
                .map(this::toSourceDto)
                .toList();

        if (sources.isEmpty()) {
            return new StreamResult(Flux.just("当前知识库没有可用内容，请先上传文档或录入知识。"), sources);
        }

        String context = buildContext(documents);
        Prompt prompt = new PromptTemplate(USER_PROMPT_TEMPLATE)
                .create(Map.of("query", question, "context", context));

        Flux<String> contentFlux = chatClient.prompt(prompt).stream().content();

        return new StreamResult(contentFlux, sources);
    }

    private String buildContext(List<Document> documents) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            sb.append("[").append(i + 1).append("] ");
            sb.append(doc.getText());
            sb.append("\n");
        }
        return sb.toString();
    }

    private SourceDto toSourceDto(Document doc) {
        Long documentId = parseLong(doc.getMetadata().get("documentId"));
        String documentTitle = (String) doc.getMetadata().get("documentTitle");
        Integer chunkIndex = parseInt(doc.getMetadata().get("chunkIndex"));
        Double score = doc.getScore();

        return new SourceDto(
                documentId,
                documentTitle,
                chunkIndex,
                score,
                shorten(doc.getText(), 180)
        );
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer i) {
            return i;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String shorten(String text, int max) {
        if (text == null) {
            return "";
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max) + "...";
    }

    public record StreamResult(Flux<String> content, List<SourceDto> sources) {
    }
}
