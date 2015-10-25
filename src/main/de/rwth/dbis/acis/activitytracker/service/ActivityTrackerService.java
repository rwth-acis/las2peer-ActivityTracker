package de.rwth.dbis.acis.activitytracker.service;

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
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import io.swagger.annotations.*;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jooq.SQLDialect;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * LAS2peer Activity Service
 */
@Path("/activities")
@Version("0.1")
@Api(value = "/activities", description = "Activities resource")
@SwaggerDefinition(
        info = @Info(
                title = "LAS2peer Activity Service",
                version = "0.1",
                description = "An activity tracker for LAS2peer and other web services.",
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
        //@ApiParam(value = "User access token", required = false) @HeaderParam("access_token") String access_token) {
        //TODO Use access_token from header, at the moment WebConnector does not work with this
        String accessToken = "";

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

            ExecutorService executor = Executors.newCachedThreadPool();
            HttpGet httpget;
            Future<String> future;
            JsonParser parser = new JsonParser();
            for (Activity activity : activities) {
                ActivityEx activityEx = ActivityEx.getBuilderEx().activity(activity).build();
                //TODO check if this runs paralel or if I need to create Threads
                if (!activity.getDataUrl().isEmpty()) {
                    URIBuilder uriBuilder = new URIBuilder(activity.getDataUrl());
                    if (!accessToken.isEmpty()) {
                        uriBuilder.setParameter("access_token", accessToken);
                    }
                    URI uri = uriBuilder.build();
                    httpget = new HttpGet(uri);
                    future = executor.submit(new HttpRequestCallable(httpclient, httpget));
                    activityEx.setData(parser.parse(future.get()));
                }
                if (!activity.getUserUrl().isEmpty()) {
                    URIBuilder uriBuilder = new URIBuilder(activity.getUserUrl());
                    if (!accessToken.isEmpty()) {
                        uriBuilder.setParameter("access_token", accessToken);
                    }
                    URI uri = uriBuilder.build();
                    httpget = new HttpGet(uri);
                    future = executor.submit(new HttpRequestCallable(httpclient, httpget));
                    activityEx.setUser(parser.parse(future.get()));
                }
                activitiesEx.add(activityEx);
            }
            executor.shutdown();
            return new HttpResponse(gson.toJson(activitiesEx), HttpURLConnection.HTTP_OK);
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, "");
            return new HttpResponse(ExceptionHandler.getInstance().toJSON(atException), HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            closeConnection(dalFacade);
        }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "This method allows to create an activity",
            notes = "Returns the created activity")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Activity created"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal server problems")
    })
    public HttpResponse createActivity(@ApiParam(value = "Activity" +
            " entity as JSON", required = true) @ContentParam String activity) {
        Gson gson = new Gson();
        DALFacade dalFacade = null;
        Activity activityToCreate = gson.fromJson(activity, Activity.class);
        //TODO validate activity
        try {
            dalFacade = createConnection();
            Activity createdActivity = dalFacade.createActivity(activityToCreate);
            return new HttpResponse(gson.toJson(createdActivity), HttpURLConnection.HTTP_CREATED);
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, "");
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
     * <p>
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
