package io.github.viet_anh_it.book_selling_website.custom;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DelegatingAccessDeniedHandler implements AccessDeniedHandler {

    final LinkedHashMap<RequestMatcher, AccessDeniedHandler> handlerMap;
    AccessDeniedHandler defaultHandler;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        for (Map.Entry<RequestMatcher, AccessDeniedHandler> entry : handlerMap.entrySet()) {
            RequestMatcher matcher = entry.getKey();
            if (matcher.matches(request)) {
                // Nếu RequestMatcher khớp, gọi handler tương ứng và return
                entry.getValue().handle(request, response, accessDeniedException);
                return;
            }
        }

        // Nếu không có matcher nào khớp, dùng defaultHandler (nếu đã set):
        if (this.defaultHandler != null) {
            this.defaultHandler.handle(request, response, accessDeniedException);
        } else {
            // Nếu không set default, ta đành trả 403 thuần:
            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
        }
    }

}
