package de.rwth.dbis.acis.activitytracker.service.network;

import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.StringWriter;
import java.net.HttpURLConnection;
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
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpget, context);

            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                ExceptionHandler.getInstance().throwException(ExceptionLocation.NETWORK, ErrorCode.AUTHORIZATION,
                        "User is not authorized for this request");
            }
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                ExceptionHandler.getInstance().throwException(ExceptionLocation.NETWORK, ErrorCode.NOT_FOUND,
                        "Resource not found");
            }
            if (statusLine.getStatusCode() != HttpURLConnection.HTTP_OK) {
                ExceptionHandler.getInstance().throwException(ExceptionLocation.NETWORK, ErrorCode.NETWORK_PROBLEM,
                        "Error while trying to receive activity content");
            }
            if (entity != null) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(entity.getContent(), writer);
                responseBody = writer.toString();
            }
        } catch (ActivityTrackerException ate) {
            throw ate;
        } catch (Exception e) {
            throw ExceptionHandler.getInstance().convert(e, ExceptionLocation.NETWORK, ErrorCode.UNKNOWN, "");
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }
}
