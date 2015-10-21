package de.rwth.dbis.acis.activitytracker.service.network;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.StringWriter;
import java.util.concurrent.Callable;

public class HttpRequestCallable implements Callable {

    private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;

    public HttpRequestCallable(CloseableHttpClient httpClient, HttpGet httpget) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpget = httpget;
    }

    @Override
    public Object call() throws Exception {
        String responseBody = new String();
        try {
            CloseableHttpResponse response = httpClient.execute(httpget, context);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(entity.getContent(), writer);
                    responseBody = writer.toString();
                }
            } finally {
                response.close();
                return responseBody;
            }
        } catch (Exception e) {
            //TODO Exception handling
        }
        return responseBody;
    }
}
