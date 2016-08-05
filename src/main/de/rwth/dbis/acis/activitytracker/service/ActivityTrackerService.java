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
import i5.las2peer.api.Service;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.security.Context;
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
import java.io.IOException;
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
    protected String baseURL;

    private DataSource dataSource;

    private final L2pLogger logger = L2pLogger.getInstance(ActivityTrackerService.class.getName());

    public ActivityTrackerService() throws Exception {
        setFieldValues();
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        dataSource = setupDataSource(dbUrl, dbUserName, dbPassword);
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

            dalFacade = getDBConnection();
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
                activitiesEx.addAll(getObjectBodies(httpclient, executor, authorizationToken, activities.getElements()));
            }

            executor.shutdown();
            if (activitiesEx.size() > limit) {
                activitiesEx = activitiesEx.subList(0, limit);
            }
            PaginationResult<ActivityEx> activitiesExResult = new PaginationResult<>(pageInfo, activitiesEx);

            HttpResponse response = new HttpResponse(gson.toJson(activitiesExResult.getElements()), HttpURLConnection.HTTP_OK);
            Map<String, String> parameter = new HashMap<>();
            parameter.put("limit", String.valueOf(limit));
            response = this.addPaginationToHtppResponse(activitiesExResult, "activities", parameter, response);

            return response;

        } catch (ActivityTrackerException atException) {
            return new HttpResponse(ExceptionHandler.getInstance().toJSON(atException), HttpURLConnection.HTTP_INTERNAL_ERROR);
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, ex.getMessage());
            return new HttpResponse(ExceptionHandler.getInstance().toJSON(atException), HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            closeDBConnection(dalFacade);
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
                    Context.logMessage(this, "Object not visible for user token or anonymous. Skip object.");
                } else if (exCause instanceof ActivityTrackerException &&
                        ((ActivityTrackerException) exCause).getErrorCode() == ErrorCode.NOT_FOUND) {
                    Context.logMessage(this, "Resource not found. Skip object.");
                } else {
                    throw ex;
                }
            }
        }
        return activitiesEx;
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
            dalFacade = getDBConnection();
            Activity createdActivity = dalFacade.createActivity(activityToCreate);
            return new HttpResponse(gson.toJson(createdActivity), HttpURLConnection.HTTP_CREATED);
        } catch (Exception ex) {
            ActivityTrackerException atException = ExceptionHandler.getInstance().convert(ex, ExceptionLocation.ACTIVITIESERVICE, ErrorCode.UNKNOWN, "");
            return new HttpResponse(ExceptionHandler.getInstance().toJSON(atException), HttpURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            closeDBConnection(dalFacade);
        }
    }

    public HttpResponse addPaginationToHtppResponse(PaginationResult paginationResult, String path, Map<String, String> httpParameter,
                                                    HttpResponse httpResponse) throws URISyntaxException {
        httpResponse.setHeader("X-Limit", String.valueOf(paginationResult.getPageable().getLimit()));

        if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
            if (paginationResult.getPrevCursor() != -1) {
                httpResponse.setHeader("X-Cursor-Before", String.valueOf(paginationResult.getPrevCursor()));
            }
            if (paginationResult.getNextCursor() != -1) {
                httpResponse.setHeader("X-Cursor-After", String.valueOf(paginationResult.getNextCursor()));
            }
        } else {
            if (paginationResult.getNextCursor() != -1) {
                httpResponse.setHeader("X-Cursor-Before", String.valueOf(paginationResult.getNextCursor()));
            }
            if (paginationResult.getPrevCursor() != -1) {
                httpResponse.setHeader("X-Cursor-After", String.valueOf(paginationResult.getPrevCursor()));
            }
        }

        /*
        if (paginationResult.getPrevCursor() != -1) {
            if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
                httpResponse.setHeader("X-Cursor-Before", String.valueOf(paginationResult.getPrevCursor()));
            } else {
                httpResponse.setHeader("X-Cursor-Before", String.valueOf(paginationResult.getNextCursor()));
            }
        }
        if (paginationResult.getNextCursor() != -1) {
            if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
                httpResponse.setHeader("X-Cursor-After", String.valueOf(paginationResult.getNextCursor()));
            } else {
                httpResponse.setHeader("X-Cursor-After", String.valueOf(paginationResult.getPrevCursor()));
            }
        }
        */

        URIBuilder uriBuilder = new URIBuilder(baseURL + path);
        for (Map.Entry<String, String> entry : httpParameter.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue());
        }
        String links = new String();
        if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
            if (paginationResult.getPrevCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links = links.concat("<" + uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"prev\",");
            }
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links = links.concat("<" + uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"next\"");
            }
        } else {
            if (paginationResult.getNextCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links = links.concat("<" + uriBuilderTemp.addParameter("before", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"prev\",");
            }
            if (paginationResult.getPrevCursor() != -1) {
                URIBuilder uriBuilderTemp = new URIBuilder(uriBuilder.build());
                links = links.concat("<" + uriBuilderTemp.addParameter("after", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"next\"");
            }
        }
        /*
        if (paginationResult.getPrevCursor() != -1) {
            URIBuilder uriBuilder1 = new URIBuilder(uriBuilder.build());
            if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
                links = links.concat("<" + uriBuilder1.addParameter("before", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"prev\",");
            } else {
                links = links.concat("<" + uriBuilder1.addParameter("before", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"prev\",");
            }
        }
        if (paginationResult.getNextCursor() != -1) {
            URIBuilder uriBuilder2 = new URIBuilder(uriBuilder.build());
            if (paginationResult.getPageable().getSortDirection() == Pageable.SortDirection.ASC) {
                links = links.concat("<" + uriBuilder2.addParameter("after", String.valueOf(paginationResult.getNextCursor())).build() + ">; rel=\"next\"");
            } else {
                links = links.concat("<" + uriBuilder2.addParameter("after", String.valueOf(paginationResult.getPrevCursor())).build() + ">; rel=\"next\"");
            }
        }
        */
        httpResponse.setHeader("Link", links);
        return httpResponse;
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
