package com.goodskill.ai.controller;

import com.goodskill.ai.dto.request.AskRequest;
import com.goodskill.ai.dto.response.AskResponse;
import com.goodskill.ai.service.QaService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return qaService.ask(request.question(), request.topK());
    }

    @PostMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askStream(@Valid @RequestBody AskRequest request) {
        SseEmitter emitter = new SseEmitter();
        QaService.StreamResult result = qaService.streamAnswer(request.question(), request.topK());

        result.content()
                .filter(t -> t != null && !t.isEmpty())
                .map(t -> SseEmitter.event().name("token").data(Map.of("token", t)))
                .concatWithValues(SseEmitter.event().name("done").data(Map.of("sources", result.sources())))
                .subscribe(
                        event -> {
                            try {
                                emitter.send(event);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );

        return emitter;
    }
}
