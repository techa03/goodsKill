package com.goodskill.ai.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunker {

    private static final int CHUNK_SIZE = 800;
    private static final int OVERLAP = 120;

    public List<String> split(String text) {
        String normalized = text == null ? "" : text.replace("\r\n", "\n").trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = normalized.length();

        while (start < length) {
            int end = Math.min(start + CHUNK_SIZE, length);
            if (end < length) {
                int candidate = findSplitPoint(normalized, start, end);
                if (candidate > start + CHUNK_SIZE / 2) {
                    end = candidate;
                }
            }
            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= length) {
                break;
            }
            start = Math.max(end - OVERLAP, start + 1);
        }
        return chunks;
    }

    private int findSplitPoint(String text, int start, int end) {
        for (int i = end - 1; i > start; i--) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c) || c == '。' || c == '，' || c == '.' || c == ',' || c == ';' || c == '；') {
                return i + 1;
            }
        }
        return end;
    }
}
