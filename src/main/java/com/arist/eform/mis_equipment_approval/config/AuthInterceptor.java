package com.arist.eform.mis_equipment_approval.config;

import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 瀏覽器的「預檢請求」(CORS preflight),一律放行,不檢查 Token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 登入這支 API 本身不需要檢查 Token(因為user這時候還沒有 Token)
        if (request.getRequestURI().equals("/api/auth/login")) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {
            response.setStatus(401);
            response.getWriter().write("請先登入");
            return false;
        }

        User user = tokenService.getUserByToken(token);

        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("登入已失效,請重新登入");
            return false;
        }

        // 把查到的使用者,存放進這次請求裡,讓後面的 Controller 可以直接取用
        request.setAttribute("currentUser", user);

        return true;
    }
}