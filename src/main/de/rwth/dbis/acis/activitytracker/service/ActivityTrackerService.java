package de.rwth.dbis.acis.activitytracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacade;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacadeImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.ActivityEx;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PageInfo;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import de.rwth.dbis.acis.activitytracker.service.network.HttpRequestCallable;
import i5.las2peer.api.Context;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import io.swagger.annotations.*;
import org.apache.commons.dbcp2.*;
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
public class ActivityTrackerService extends RESTService {

    //CONFIG PROPERTIES
    protected String dbUserName;
    protected String dbPassword;
    protected String dbUrl;
    protected String baseURL;

    private DataSource dataSource;

    private final L2pLogger logger = L2pLogger.getInstance(ActivityTrackerService.class.getName());

    public ActivityTrackerService() throws Exception {
        setFieldValues();
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        dataSource = setupDataSource(dbUrl, dbUserName, dbPassword);
    }

    @Override
    protected void initResources() {
        getResourceConfig().register(Resource.class);

    }

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
                    ExceptionHandler.getInstance().throwException(ExceptionLocation.ACTIVITIESERVICE, ErrorCode.WRONG_PARAMETER, "both: before and after parameter not possible");
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

                int getObjectCount = 0;
                PaginationResult<Activity> activities;
                List<ActivityEx> activitiesEx = new ArrayList<>();
                Pageable pageInfo = new PageInfo(cursor, limit, "", sortDirection);
                while (activitiesEx.size() < limit && getObjectCount < 5) {
                    pageInfo = new PageInfo(cursor, limit, "", sortDirection);
                    activities = dalFacade.findActivities(pageInfo);
                    getObjectCount++;
                    cursor = sortDirection == Pageable.SortDirection.ASC ? cursor + limit : cursor - limit;
                    if (cursor < 0) {
                        cursor = 0;
                    }
                    activitiesEx.addAll(service.getObjectBodies(httpclient, executor, authorizationToken, activities.getElements()));
                }

                executor.shutdown();
                if (activitiesEx.size() > limit) {
                    activitiesEx = activitiesEx.subList(0, limit);
                }

                PaginationResult<ActivityEx> activitiesExResult = new PaginationResult<>(pageInfo, activitiesEx);

                Map<String, String> parameter = new HashMap<>();
                parameter.put("limit", String.valueOf(limit));

                Response.ResponseBuilder responseBuilder = Response.ok();
                responseBuilder = responseBuilder.entity(gson.toJson(activitiesExResult.getElements()));
                responseBuilder = service.paginationLinks(responseBuilder, activitiesExResult, "", parameter);
                responseBuilder = service.xHeaderFields(responseBuilder, activitiesExResult);
                Response response = responseBuilder.build();

                return response;

            } catch (ActivityTrackerException atException) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionHandler.getInstance().toJSON(atException)).build();
            } catch (Exception ex) {
                ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, ex.getMessage());
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
                " entity as JSON", required = true) String activity) {
            Gson gson = new Gson();
            DALFacade dalFacade = null;
            Activity activityToCreate = gson.fromJson(activity, Activity.class);
            //TODO validate activity
            try {
                dalFacade = service.getDBConnection();
                Activity createdActivity = dalFacade.createActivity(activityToCreate);
                return Response.status(Response.Status.CREATED).entity(gson.toJson(createdActivity)).build();
            } catch (Exception ex) {
                ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, "");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionHandler.getInstance().toJSON(atException)).build();
            } finally {
                service.closeDBConnection(dalFacade);
            }
        }

    }

    private List<ActivityEx> getObjectBodies(CloseableHttpClient httpclient, ExecutorService executor, String authorizationToken,
                                             List<Activity> activities) throws Exception {
        List<ActivityEx> activitiesEx = new ArrayList<>();
        Map<Integer, Future<String>> dataFutures = new HashMap<>();
        Map<Integer, Future<String>> parentDataFutures = new HashMap<>();
        Map<Integer, Future<String>> userFutures = new HashMap<>();
        JsonParser parser = new JsonParser();

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getDataUrl() != null && !activity.getDataUrl().isEmpty()) {
                URIBuilder uriBuilder = new URIBuilder(activity.getDataUrl());
                URI uri = uriBuilder.build();
                HttpGet httpget = new HttpGet(uri);
                if (!authorizationToken.isEmpty()) {
                    httpget.addHeader("authorization", authorizationToken);
                }
                dataFutures.put(activity.getId(), executor.submit(new HttpRequestCallable(httpclient, httpget)));
            }
            if (activity.getParentDataUrl() != null && !activity.getParentDataUrl().isEmpty()) {
                URIBuilder uriBuilder = new URIBuilder(activity.getParentDataUrl());
                URI uri = uriBuilder.build();
                HttpGet httpget = new HttpGet(uri);
                if (!authorizationToken.isEmpty()) {
                    httpget.addHeader("authorization", authorizationToken);
                }
                parentDataFutures.put(activity.getId(), executor.submit(new HttpRequestCallable(httpclient, httpget)));
            }
            if (activity.getUserUrl() != null && !activity.getUserUrl().isEmpty()) {
                URIBuilder uriBuilder = new URIBuilder(activity.getUserUrl());
                URI uri = uriBuilder.build();
                HttpGet httpget = new HttpGet(uri);
                if (!authorizationToken.isEmpty()) {
                    httpget.addHeader("authorization", authorizationToken);
                }
                userFutures.put(activity.getId(), executor.submit(new HttpRequestCallable(httpclient, httpget)));
            }
        }

        for (int i = 0; i < activities.size(); i++) {
            try {
                Activity activity = activities.get(i);
                ActivityEx activityEx = ActivityEx.getBuilderEx().activity(activity).build();
                Future<String> dataFuture = dataFutures.get(activity.getId());
                if (dataFuture != null) {
                    activityEx.setData(parser.parse(dataFuture.get()));
                }
                Future<String> parentDataFuture = parentDataFutures.get(activity.getId());
                if (parentDataFuture != null) {
                    activityEx.setParentData(parser.parse(parentDataFuture.get()));
                }
                Future<String> userFuture = userFutures.get(activity.getId());
                if (userFuture != null) {
                    activityEx.setUser(parser.parse(userFuture.get()));
                }
                activitiesEx.add(activityEx);
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
        return activitiesEx;
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
                links.add(Link.valueOf("<" + uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"prev\","));
            }
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.valueOf("<" + uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"next\""));
            }
        } else {
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.valueOf("<" + uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"prev\","));
            }
            if (paginationResult.getPrevCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links.add(Link.valueOf("<" + uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"next\""));
            }
        }

        responseBuilder = responseBuilder.links((Link[]) links.toArray());
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


    public DALFacade getDBConnection() throws Exception {
        return new DALFacadeImpl(dataSource, SQLDialect.MYSQL);
    }

    public void closeDBConnection(DALFacade dalFacade) {
        if (dalFacade == null) return;
        dalFacade.close();
    }

}
