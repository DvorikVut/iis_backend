package iis.project.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {

    private final MyLogger requestLogger;

    public RequestLoggerInterceptor(MyLogger requestLogger) {
        this.requestLogger = requestLogger;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String params = request.getQueryString() != null ? request.getQueryString() : "No params";
        String ip = request.getRemoteAddr();
        request.setAttribute("startTime", System.currentTimeMillis());
        requestLogger.logRequest(method, url, params, ip);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURL().toString();
        int status = response.getStatus();
        long startTime = (Long) request.getAttribute("startTime");
        long executionTime = System.currentTimeMillis() - startTime;

        requestLogger.logResponse(url, status, executionTime);
    }
}
