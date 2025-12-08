package com.example.demo.common.lee.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userInfoHeader = request.getHeader("X-User-Info");
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(userInfoHeader);
            String decodedString = new String(decodedBytes, "UTF-8");

            Map<String, String> userInfo = objectMapper.readValue(decodedString, Map.class);

            String userId = userInfo.get("id");
            String userName = userInfo.get("name");

            request.setAttribute("userId", userId);
            request.setAttribute("userName", userName);


        } catch (IllegalArgumentException e) {
            System.out.println("无效的 Base64 编码在 X-User-Info 请求头");
        } catch (Exception e) {
            System.out.println("无效的 JSON 格式在 X-User-Info 请求头");
        }
        return true;
    }
}
