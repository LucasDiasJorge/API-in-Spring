package com.project.core.utils.gzip;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipResponseWrapper extends HttpServletResponseWrapper {

    private static final Logger logger = LoggerFactory.getLogger(GzipResponseWrapper.class);

    private GzipServletOutputStream gzipOutputStream;

    public GzipResponseWrapper(HttpServletResponse response, GZIPOutputStream gzipOutputStream) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (gzipOutputStream == null) {
            logger.debug("Initializing GzipServletOutputStream");
            gzipOutputStream = new GzipServletOutputStream(getResponse().getOutputStream());
        }
        return gzipOutputStream;
    }

    @Override
    public void flushBuffer() throws IOException {
        logger.debug("Flushing buffer");
        if (gzipOutputStream != null) {
            gzipOutputStream.finish();
        }
        super.flushBuffer();
    }

    private static class GzipServletOutputStream extends ServletOutputStream {

        private final GZIPOutputStream gzipStream;

        public GzipServletOutputStream(ServletOutputStream outputStream) throws IOException {
            this.gzipStream = new GZIPOutputStream(outputStream);
        }

        @Override
        public void write(int b) throws IOException {
            gzipStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            gzipStream.flush();
        }

        public void finish() throws IOException {
            gzipStream.finish();
        }

        @Override
        public void close() throws IOException {
            gzipStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}
