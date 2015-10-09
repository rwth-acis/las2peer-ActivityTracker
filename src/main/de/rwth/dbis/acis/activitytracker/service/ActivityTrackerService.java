package de.rwth.dbis.acis.activitytracker.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.*;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.rwth.dbis.acis.activitytracker.service.dal.DALFacade;
import de.rwth.dbis.acis.activitytracker.service.dal.DALFacadeImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PageInfo;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.security.Context;
import i5.las2peer.security.UserAgent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import net.minidev.json.JSONObject;
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
        String returnString = "";
        //returnString += "You are " + ((UserAgent) getActiveAgent()).getLoginName() + " and your login is valid!";

        DALFacade dalFacade = null;
        try {
            dalFacade = createConnection();

            List<Activity> activities = dalFacade.findActivitiesForUser(new PageInfo(1, 10));
            System.out.println(activities.size());

        } catch (Exception ex) {
            returnString = "Error";
        } finally {
            closeConnection(dalFacade);
        }


        return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
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
