package cn.ariven.openaimpbackend.config;

import cn.ariven.openaimpbackend.repository.CorsWhitelistRepository;
import cn.ariven.openaimpbackend.entity.CorsWhitelist;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CORSFilter implements Filter {

    private final CorsWhitelistRepository whitelistRepository;
    private List<String> allowedOrigins;

    public CORSFilter(CorsWhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;

        // 正确地将 Iterable 转换为 Stream
        this.allowedOrigins = StreamSupport.stream(whitelistRepository.findAll().spliterator(), false)
                .map(entry -> entry.getDomain().toLowerCase())
                .collect(Collectors.toList());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        String origin = req.getHeader("Origin");

        if (origin != null && allowedOrigins.contains(origin.toLowerCase())) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN");
            res.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
        }

        // 对于预检请求，直接返回
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.allowedOrigins = StreamSupport.stream(whitelistRepository.findAll().spliterator(), false)
                .map(entry -> entry.getDomain().toLowerCase())
                .collect(Collectors.toList());
    }

    @Override
    public void destroy() {
    }
}
