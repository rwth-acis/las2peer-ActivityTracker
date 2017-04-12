package de.rwth.dbis.acis.activitytracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacade;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacadeImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PageInfo;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import de.rwth.dbis.acis.activitytracker.service.network.HttpRequestCallable;
import i5.las2peer.api.Context;
import i5.las2peer.execution.NoSuchServiceMethodException;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.L2pSecurityException;
import io.swagger.annotations.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jooq.SQLDialect;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * LAS2peer Activity Service
 */
@ServicePath("activities")
public class ActivityTrackerService extends RESTService {

    private final L2pLogger logger = L2pLogger.getInstance(ActivityTrackerService.class.getName());
    //CONFIG PROPERTIES
    protected String dbUserName;
    protected String dbPassword;
    protected String dbUrl;
    protected String baseURL;
    private DataSource dataSource;

    public ActivityTrackerService() throws Exception {
        setFieldValues();
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        dataSource = setupDataSource(dbUrl, dbUserName, dbPassword);
    }

    private static DataSource setupDataSource(String dbUrl, String dbUserName, String dbPassword) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setValidationQuery("SELECT 1;");
        dataSource.setTestOnBorrow(true); // test each connection when borrowing from the pool with the validation query
        dataSource.setMaxConnLifetimeMillis(1000 * 60 * 60); // max connection life time 1h. mysql drops connection after 8h.
        return dataSource;
    }

    @Override
    protected void initResources() {
        getResourceConfig().register(Resource.class);
    }

    /**
     * Create Activity over RMI
     *
     * @param activity as JSON string
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws L2pSecurityException
     * @throws NoSuchServiceMethodException
     */
    public String createActivity(String activity) throws InvocationTargetException, IllegalAccessException, L2pSecurityException, NoSuchServiceMethodException {
        try {
            Gson gson = new Gson();
            Activity activityToCreate = gson.fromJson(activity, Activity.class);
            this.storeActivity(activityToCreate);
            return new Integer(Response.Status.CREATED.getStatusCode()).toString();
        } catch (ActivityTrackerException atException) {
            return new Integer(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).toString();
        }
    }

    private Activity storeActivity(Activity activity) throws ActivityTrackerException {
        //TODO validate activity
        DALFacade dalFacade = null;
        try {
            dalFacade = this.getDBConnection();
            Activity createdActivity = dalFacade.createActivity(activity);
            return createdActivity;
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITYTRACKERSERVICE, ErrorCode.UNKNOWN, "");
            throw atException;
        } finally {
            this.closeDBConnection(dalFacade);
        }
    }

    private List<Activity> getObjectBodies(CloseableHttpClient httpclient, ExecutorService executor, String authorizationToken,
                                             List<Activity> activities, Map<String, Object> tempObjectStorage) throws Exception {
        List<Activity> activitiesWithObjectBodies = new ArrayList<>();
        JsonParser parser = new JsonParser();

        for (Activity activity : activities) {
            Activity.Builder builder = Activity.getBuilder().activity(activity);

            try {
                if (activity.getDataUrl() != null && !activity.getDataUrl().isEmpty()) {
                    if (tempObjectStorage.containsKey(activity.getDataUrl())) {
                        builder.data(tempObjectStorage.get(activity.getDataUrl()));
                    } else {
                        URI uri = new URIBuilder(activity.getDataUrl()).build();
                        HttpGet httpget = new HttpGet(uri);
                        if (!authorizationToken.isEmpty()) {
                            httpget.addHeader("authorization", authorizationToken);
                        }
                        Future<String> dataFuture = executor.submit(new HttpRequestCallable(httpclient, httpget));
                        if (dataFuture != null) {
                            builder.data(parser.parse(dataFuture.get()));
                            tempObjectStorage.put(activity.getDataUrl(), parser.parse(dataFuture.get()));
                        }
                    }
                }
                if (activity.getParentDataUrl() != null && !activity.getParentDataUrl().isEmpty()) {
                    if (tempObjectStorage.containsKey(activity.getParentDataUrl())) {
                        builder.parentData(tempObjectStorage.get(activity.getParentDataUrl()));
                    } else {

                        URI uri = new URIBuilder(activity.getParentDataUrl()).build();
                        HttpGet httpget = new HttpGet(uri);
                        if (!authorizationToken.isEmpty()) {
                            httpget.addHeader("authorization", authorizationToken);
                        }
                        Future<String> parentDataFuture = executor.submit(new HttpRequestCallable(httpclient, httpget));
                        if (parentDataFuture != null) {
                            builder.parentData(parser.parse(parentDataFuture.get()));
                            tempObjectStorage.put(activity.getParentDataUrl(), parser.parse(parentDataFuture.get()));
                        }
                    }
                }
                if (activity.getUserUrl() != null && !activity.getUserUrl().isEmpty()) {
                    if (tempObjectStorage.containsKey(activity.getUserUrl())) {
                        builder.user(tempObjectStorage.get(activity.getUserUrl()));
                    } else {
                        URI uri = new URIBuilder(activity.getUserUrl()).build();
                        HttpGet httpget = new HttpGet(uri);
                        if (!authorizationToken.isEmpty()) {
                            httpget.addHeader("authorization", authorizationToken);
                        }
                        Future<String> userFuture = executor.submit(new HttpRequestCallable(httpclient, httpget));
                        if (userFuture != null) {
                            builder.user(parser.parse(userFuture.get()));
                            tempObjectStorage.put(activity.getUserUrl(), userFuture.get());
                        }
                    }
                }
                activitiesWithObjectBodies.add(builder.build());
            } catch (Exception ex) {
                Throwable exCause = ex.getCause();
                if (exCause instanceof ActivityTrackerException &&
                        ((ActivityTrackerException) exCause).getErrorCode() == ErrorCode.AUTHORIZATION) {
                    logger.log(L2pLogger.DEFAULT_CONSOLE_LEVEL, "Object not visible for user token or anonymous. Skip object.");
                } else if (exCause instanceof ActivityTrackerException &&
                        ((ActivityTrackerException) exCause).getErrorCode() == ErrorCode.NOT_FOUND) {
                    logger.log(L2pLogger.DEFAULT_CONSOLE_LEVEL, "Resource not found. Skip object.");
                } else {
                    throw ex;
                }
            }
        }
        return activitiesWithObjectBodies;
    }

    public Response.ResponseBuilder paginationLinks(Response.ResponseBuilder responseBuilder, PaginationResult paginationResult, String path, Map<String, String> httpParameter) throws URISyntaxException {
        List<Link> links = new ArrayList<>();

        URIBuilder uriBuilder = new URIBuilder(baseURL + path);
        for (Map.Entry<String, String> entry : httpParameter.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue());
        }
        if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
            if (paginationResult.getPrevCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.fromUri(uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getPrevCursor())).build()).rel("prev").build());
            }
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.fromUri(uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getNextCursor())).build()).rel("next").build());
            }
        } else {
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.fromUri(uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getNextCursor())).build()).rel("prev").build());
            }
            if (paginationResult.getPrevCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.fromUri(uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getPrevCursor())).build()).rel("next").build());
            }
        }
        responseBuilder = responseBuilder.links(links.toArray(new Link[links.size()]));
        return responseBuilder;
    }

    public Response.ResponseBuilder xHeaderFields(Response.ResponseBuilder responseBuilder, PaginationResult paginationResult) {
        responseBuilder = responseBuilder.header("X-Limit", String.valueOf(paginationResult.getPageable().getLimit()));
        if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
            if (paginationResult.getPrevCursor() != -1) {
                responseBuilder = responseBuilder.header("X-Cursor-Before", String.valueOf(paginationResult.getPrevCursor()));
            }
            if (paginationResult.getNextCursor() != -1) {
                responseBuilder = responseBuilder.header("X-Cursor-After", String.valueOf(paginationResult.getNextCursor()));
            }
        } else {
            if (paginationResult.getNextCursor() != -1) {
                responseBuilder = responseBuilder.header("X-Cursor-Before", String.valueOf(paginationResult.getNextCursor()));
            }
            if (paginationResult.getPrevCursor() != -1) {
                responseBuilder = responseBuilder.header("X-Cursor-After", String.valueOf(paginationResult.getPrevCursor()));
            }
        }
        return responseBuilder;
    }

    public DALFacade getDBConnection() throws Exception {
        return new DALFacadeImpl(dataSource, SQLDialect.MYSQL);
    }

    public void closeDBConnection(DALFacade dalFacade) {
        if (dalFacade == null) return;
        dalFacade.close();
    }

    @Api(value = "/activities", description = "Activities resource")
    @SwaggerDefinition(
            info = @Info(
                    title = "las2peer Activity Service",
                    version = "0.2",
                    description = "An activity tracker for las2peer and other web services.",
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

    @Path("/")
    public static class Resource {

        private final ActivityTrackerService service = (ActivityTrackerService) Context.getCurrent().getService();

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
        public Response getActivities(
                @ApiParam(value = "Before cursor pagination", required = false) @DefaultValue("-1") @QueryParam("before") int before,
                @ApiParam(value = "After cursor pagination", required = false) @DefaultValue("-1") @QueryParam("after") int after,
                @ApiParam(value = "Limit of elements of components", required = false) @DefaultValue("10") @QueryParam("limit") int limit,
                @ApiParam(value = "User authorization token", required = false) @DefaultValue("") @HeaderParam("authorization") String authorizationToken) {

            DALFacade dalFacade = null;
            try {
                if (before != -1 && after != -1) {
                    ExceptionHandler.getInstance().throwException(ExceptionLocation.ACTIVITYTRACKERSERVICE, ErrorCode.WRONG_PARAMETER, "both: before and after parameter not possible");
                }
                int cursor = before != -1 ? before : after;
                Pageable.SortDirection sortDirection = after != -1 ? Pageable.SortDirection.ASC : Pageable.SortDirection.DESC;

                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                cm.setMaxTotal(20);
                CloseableHttpClient httpclient = HttpClients.custom()
                        .setConnectionManager(cm)
                        .build();

                dalFacade = service.getDBConnection();
                Gson gson = new Gson();
                ExecutorService executor = Executors.newCachedThreadPool();

                Map<String, Object> tempObjectStorage = new HashMap<>();

                int getObjectCount = 0;
                PaginationResult<Activity> activitiesPaginationResult = null;
                List<Activity> activities = new ArrayList<>();
                Pageable pageInfo = null;
                while (activities.size() < limit && getObjectCount < 5) {
                    pageInfo = new PageInfo(cursor, limit, "", sortDirection);
                    activitiesPaginationResult = dalFacade.findActivities(pageInfo);
                    getObjectCount++;
                    cursor = sortDirection == Pageable.SortDirection.ASC ? cursor + limit : cursor - limit;
                    if (cursor < 0) {
                        cursor = 0;
                    }
                    activities.addAll(service.getObjectBodies(httpclient, executor, authorizationToken, activitiesPaginationResult.getElements(), tempObjectStorage));
                }

                executor.shutdown();
                if (activities.size() > limit) {
                    activities = activities.subList(0, limit);
                }

                activitiesPaginationResult = new PaginationResult<>(pageInfo, activities);

                Map<String, String> parameter = new HashMap<>();
                parameter.put("limit", String.valueOf(limit));

                Response.ResponseBuilder responseBuilder = Response.ok();
                responseBuilder = responseBuilder.entity(gson.toJson(activitiesPaginationResult.getElements()));
                responseBuilder = service.paginationLinks(responseBuilder, activitiesPaginationResult, "", parameter);
                responseBuilder = service.xHeaderFields(responseBuilder, activitiesPaginationResult);
                Response response = responseBuilder.build();

                return response;

            } catch (ActivityTrackerException atException) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionHandler.getInstance().toJSON(atException)).build();
            } catch (Exception ex) {
                ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITYTRACKERSERVICE, ErrorCode.UNKNOWN, ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionHandler.getInstance().toJSON(atException)).build();
            } finally {
                service.closeDBConnection(dalFacade);
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
        public Response createActivity(@ApiParam(value = "Activity" +
                " entity to create", required = true) Activity activity) {
            try {
                Activity createdActivity = service.storeActivity(activity);
                return Response.status(Response.Status.CREATED).entity(createdActivity.toJSON()).build();
            } catch (ActivityTrackerException atException) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionHandler.getInstance().toJSON(atException)).build();
            }
        }

    }

}
