package org.example.savethewas.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    // 로그 폭발 방지
    private static final int MAX_BODY_LOG_BYTES = 4_096;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 바디 캐싱 wrapper
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request);

        // 요청 시작 로그(URI/헤더)
        logRequestLineAndHeaders(wrapped);

        try {
            // 요청 처리 끝난 뒤(컨트롤러가 바디를 읽은 뒤) 바디 로그
            filterChain.doFilter(wrapped, response);
        } finally {
            logRequestBody(wrapped);
        }
    }

    private void logRequestLineAndHeaders(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        log.info("[REQ] {} {}{}", method, uri, (query != null ? "?" + query : ""));

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) return;

        for (String name : Collections.list(headerNames)) {
            String value = request.getHeader(name);

            // 민감 헤더 마스킹
            if ("authorization".equalsIgnoreCase(name) || "cookie".equalsIgnoreCase(name)) {
                value = "***";
            }

            log.info("[HDR] {}={}", name, value);
        }
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf == null || buf.length == 0) {
            log.info("[BODY] (empty)");
            return;
        }

        int len = Math.min(buf.length, MAX_BODY_LOG_BYTES);

        Charset charset = StandardCharsets.UTF_8;
        if (request.getCharacterEncoding() != null) {
            try {
                charset = Charset.forName(request.getCharacterEncoding());
            } catch (Exception ignored) {
            }
        }

        String body = new String(buf, 0, len, charset);
        if (buf.length > MAX_BODY_LOG_BYTES) {
            body += "...(truncated)";
        }

        log.info("[BODY] {}", body);
    }
}