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
        // Логирование информации о запросе перед обработкой контроллером
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String params = request.getQueryString() != null ? request.getQueryString() : "No params";
        String ip = request.getRemoteAddr();

        // Сохранение времени начала выполнения
        request.setAttribute("startTime", System.currentTimeMillis());

        requestLogger.logRequest(method, url, params, ip);

        // Продолжить выполнение
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Логирование информации о завершении запроса
        String url = request.getRequestURL().toString();
        int status = response.getStatus();
        long startTime = (Long) request.getAttribute("startTime");
        long executionTime = System.currentTimeMillis() - startTime;

        requestLogger.logResponse(url, status, executionTime);
    }
}
