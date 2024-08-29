package murraco.interceptor;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Component
public class UriValidationInterceptor implements HandlerInterceptor {

    private AntPathMatcher pathMatcher = new AntPathMatcher();


    private List<String> whiteListUris = Lists.newArrayList(
            "/tasks/user/api/v1/users/{userId}/reset/pwd/put/{orderId}",
            "/tasks/user/api/v1/users/{userId}/reset/pwd/put");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
//        String whiteListUri = "/tasks/user/api/v1/users/{userId}/reset/pwd/put/{orderId}";

        for(String whiteListUri : whiteListUris){
            if (pathMatcher.match(whiteListUri, requestUri)) {
                // 匹配成功，可以继续处理请求
                return true;
            }
        }

        // 匹配失败，返回错误响应或进行其他处理
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Invalid URI");
        return false;
    }
}
