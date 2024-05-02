package com.project.core.utils.gzip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@Component
public class CompressionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CompressionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("CompressionFilter: Incoming request");
        if (request.getHeader("Accept-Encoding").contains("gzip")) {
            logger.debug("CompressionFilter: Gzip encoding supported");
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
            GzipResponseWrapper wrappedResponse = new GzipResponseWrapper(response);
            filterChain.doFilter(request, wrappedResponse);
            gzipOutputStream.close();
        } else {
            logger.debug("CompressionFilter: Gzip encoding not supported");
            filterChain.doFilter(request, response);
        }
    }
}
