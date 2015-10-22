package de.rwth.dbis.acis.activitytracker.service;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacade;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacadeImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.ActivityEx;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PageInfo;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import de.rwth.dbis.acis.activitytracker.service.network.HttpRequestCallable;
import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import io.swagger.annotations.*;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jooq.SQLDialect;

/**
 * LAS2peer Activity Service
 */
@Path("/activitytracker")
@Version("0.1")
@Api
@SwaggerDefinition(
        info = @Info(
                title = "LAS2peer Activity Service",
                version = "0.1",
                description = "An activity tracker for LAS2peer services.",
                termsOfService = "http://requirements-bazaar.org",
                contact = @Contact(
                        name = "Requirements Bazaar Dev Team",
                        url = "http://requirements-bazaar.org",
                        email = "info@requirements-bazaar.org"
                ),
                license = @License(
                        name = "Apache2",
                        url = "http://requirements-bazaar.org/license"
                )
        ))
public class ActivityTrackerService extends Service {

    //CONFIG PROPERTIES
    protected String dbUserName;
    protected String dbPassword;
    protected String dbUrl;
    protected String lang;
    protected String country;

    public ActivityTrackerService() {
        setFieldValues();
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // Service methods.
    // //////////////////////////////////////////////////////////////////////////////////////

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "User Validation",
            notes = "Simple function to validate a user login.")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Validation Confirmation"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
    })
    public HttpResponse validateLogin() {
        String returnString = new String();
        //returnString += "You are " + ((UserAgent) getActiveAgent()).getLoginName() + " and your login is valid!";

        DALFacade dalFacade = null;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet;
        CloseableHttpResponse response = null;
        try {
            dalFacade = createConnection();

            List<Activity> activities = dalFacade.findActivities(new PageInfo(1, 10));
            System.out.println(activities.size());

            if (activities.size() > 0) {
                httpGet = new HttpGet(activities.get(0).getDataUrl());
                response = httpclient.execute(httpGet);
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();

                StringWriter writer = new StringWriter();
                IOUtils.copy(entity.getContent(), writer);
                String entityString = writer.toString();
                System.out.println(entityString);

                EntityUtils.consume(entity);

                returnString = entityString;
            }
        } catch (Exception ex) {
            returnString = "Error";
        } finally {
            closeConnection(dalFacade);
            try {
                response.close();
            } catch (Exception ex) {
                // Could not close the resource?
            }
        }

        return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "This method returns a list of activities",
            notes = "Default the latest ten activities will be returned")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns a list of activities"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not found"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems")
    })
    //TODO add filter
    public HttpResponse getActivities(
            @ApiParam(value = "Page number", required = false) @DefaultValue("0") @QueryParam("page") int page,
            @ApiParam(value = "Elements of components by page", required = false) @DefaultValue("10") @QueryParam("per_page") int perPage) {
        List<Activity> activities = new ArrayList<Activity>();
        List<ActivityEx> activitiesEx = new ArrayList<ActivityEx>();
        DALFacade dalFacade = null;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        try {
            dalFacade = createConnection();
            Gson gson = new Gson();
            Pageable pageInfo = new PageInfo(page, perPage);

            activities = dalFacade.findActivities(pageInfo);


//           List<Thread> httpRequestThreads = new ArrayList<Thread>();
//            for (Activity activity : activities) {
//                ActivityEx activityEx = ActivityEx.getBuilderEx().activity(activity).build();
//                activitiesEx.add(activityEx);
//                if (activity.getDataUrl().length() > 0) {
//                    HttpGet httpget = new HttpGet(activity.getDataUrl());
//                    httpRequestThreads.add(new HttpRequestCallable(httpclient, httpget, activityEx.getData()));
//                }
//                if (activity.getUserUrl().length() > 0) {
//                    HttpGet httpget = new HttpGet(activity.getUserUrl());
//                    httpRequestThreads.add(new HttpRequestCallable(httpclient, httpget, activityEx.getUser()));
//                }
//            }
//
//            for (Thread httpRequestThread : httpRequestThreads) {
//                httpRequestThread.start();
//            }
//
//            for (Thread httpRequestThread : httpRequestThreads) {
//                httpRequestThread.join(1000);
//            }

            ExecutorService executor = Executors.newCachedThreadPool();
            Activity activity = activities.get(0);
            HttpGet httpget = new HttpGet(activity.getDataUrl());
            Future<String> future = executor.submit(new HttpRequestCallable(httpclient, httpget));
            ActivityEx activityEx = ActivityEx.getBuilderEx().activity(activity).build();
            JsonParser parser = new JsonParser();
            activityEx.setData(parser.parse(future.get()));
            executor.shutdown();
            return new HttpResponse(gson.toJson(activityEx), HttpURLConnection.HTTP_OK);
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.BAZAARSERVICE, ErrorCode.UNKNOWN, "");
            return new HttpResponse(ExceptionHandler.getInstance().toJSON(atException), HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            closeConnection(dalFacade);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // Methods required by the LAS2peer framework.
    // //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method for debugging purposes.
     * Here the concept of restMapping validation is shown.
     * It is important to check, if all annotations are correct and consistent.
     * Otherwise the service will not be accessible by the WebConnector.
     * Best to do it in the unit tests.
     * To avoid being overlooked/ignored the method is implemented here and not in the test section.
     *
     * @return true, if mapping correct
     */
    public boolean debugMapping() {
        String XML_LOCATION = "./restMapping.xml";
        String xml = getRESTMapping();

        try {
            RESTMapper.writeFile(XML_LOCATION, xml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XMLCheck validator = new XMLCheck();
        ValidationResult result = validator.validate(xml);

        if (result.isValid()) {
            return true;
        }
        return false;
    }

    /**
     * This method is needed for every RESTful application in LAS2peer. There is no need to change!
     *
     * @return the mapping
     */
    public String getRESTMapping() {
        String result = "";
        try {
            result = RESTMapper.getMethodsAsXML(this.getClass());
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // Methods providing a Swagger documentation of the service API.
    // //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the API documentation of all annotated resources
     * for purposes of Swagger documentation.
     * <p/>
     * Note:
     * If you do not intend to use Swagger for the documentation
     * of your service API, this method may be removed.
     *
     * @return The resource's documentation.
     */
    @GET
    @Path("/swagger.json")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getSwaggerJSON() {
        Swagger swagger = new Reader(new Swagger()).read(this.getClass());
        if (swagger == null) {
            return new HttpResponse("Swagger API declaration not available!", HttpURLConnection.HTTP_NOT_FOUND);
        }
        swagger.getDefinitions().clear();
        try {
            return new HttpResponse(Json.mapper().writeValueAsString(swagger), HttpURLConnection.HTTP_OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new HttpResponse(e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    //TODO use connection pool
    public DALFacade createConnection() throws Exception {
        Connection dbConnection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
        return new DALFacadeImpl(dbConnection, SQLDialect.MYSQL);
    }

    public void closeConnection(DALFacade dalFacade) {
        if (dalFacade == null) return;
        Connection dbConnection = dalFacade.getConnection();
        if (dbConnection != null) {
            try {
                dbConnection.close();
                System.out.println("Database connection closed!");
            } catch (SQLException ignore) {
                System.out.println("Could not close db connection!");
            }
        }
    }

}
