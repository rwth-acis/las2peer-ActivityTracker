package de.rwth.dbis.acis.activitytracker.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import i5.las2peer.api.p2p.ServiceNameVersion;
import i5.las2peer.connectors.webConnector.WebConnector;
import i5.las2peer.connectors.webConnector.client.ClientResponse;
import i5.las2peer.connectors.webConnector.client.MiniClient;
import i5.las2peer.p2p.LocalNode;
import i5.las2peer.p2p.LocalNodeManager;
import i5.las2peer.security.UserAgentImpl;
import i5.las2peer.testing.MockAgentFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Example Test Class demonstrating a basic JUnit test structure.
 */
public class ActivityTrackerTest {

    private static final String testPass = "adamspass";
    private static final String mainPath = "activities/";
    private static final String testVersion = "1.0.0";
    private static LocalNode node;
    private static WebConnector connector;
    private static ByteArrayOutputStream logStream;
    private static UserAgentImpl testAgent;

    /**
     * Called before a test starts.
     * <p>
     * Sets up the node, initializes connector and adds user agent that can be used throughout the test.
     *
     * @throws Exception
     */
    @Before
    public void startServer() throws Exception {
        // start node
        node = new LocalNodeManager().newNode();
        node.launch();

        // add agent to node
        testAgent = MockAgentFactory.getAdam();
        testAgent.unlock(testPass); // agents must be unlocked in order to be stored
        node.storeAgent(testAgent);

        // start service
        // during testing, the specified service version does not matter
        node.startService(new ServiceNameVersion(ActivityTrackerService.class.getName(), testVersion), "a pass");

        // start connector
        connector = new WebConnector(true, 0, false, 0); // port 0 means use system defined port
        logStream = new ByteArrayOutputStream();
        connector.setLogStream(new PrintStream(logStream));
        connector.start(node);
    }

    /**
     * Called after the test has finished. Shuts down the server and prints out the connector log file for reference.
     *
     * @throws Exception
     */
    @After
    public void shutDownServer() throws Exception {
        if (connector != null) {
            connector.stop();
            connector = null;
        }
        if (node != null) {
            node.shutDown();
            node = null;
        }
        if (logStream != null) {
            System.out.println("Connector-Log:");
            System.out.println("--------------");
            System.out.println(logStream.toString());
            logStream = null;
        }
    }

    private MiniClient getClient() {
        MiniClient client = new MiniClient();
        client.setConnectorEndpoint(connector.getHttpEndpoint());

        client.setLogin(testAgent.getIdentifier(), testPass);
        return client;
    }

    /**
     * Test to get the version from the version endpoint
     */
    @Test
    public void testGetVersion() {
        try {
            MiniClient client = getClient();

            ClientResponse result = client.sendRequest("GET", mainPath + "version", "");
            JsonObject response = JsonParser.parseString(result.getResponse()).getAsJsonObject();

            Assert.assertTrue(response.isJsonObject());
            Assert.assertEquals(response.get("version").getAsString(), ActivityTrackerService.class.getName() + "@" + testVersion);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * Test to create a new activity
     */
    @Test
    public void testCreateActivity() {
        try {
            MiniClient client = getClient();

            String testActivity = "{ \"id\": 8524, \"creationDate\": \"2021-08-08T01:56:58.057+0200\", \"activityAction\": \"UPDATE\", \"origin\": \"reqbaz\", \"dataUrl\": \"https://requirements-bazaar.org/bazaar/requirements/1234\", \"dataType\": \"REQUIREMENT\", \"dataFrontendUrl\": \"https://requirements-bazaar.org/projects/426/categories/1234/requirements/1234\", \"parentDataUrl\": \"https://requirements-bazaar.org/bazaar/categories/1234\", \"parentDataType\": \"CATEGORY\", \"userUrl\": \"https://requirements-bazaar.org/bazaar/users/1234\", \"additionalObject\": {  \"user\": {   \"id\": 1234  },  \"project\": {   \"id\": 1234,   \"name\": \"Project\"  },  \"category\": {   \"id\": 1234,   \"name\": \"Group\"  },  \"requirement\": {   \"id\": 1234,   \"name\": \"User Stories\"  } }, \"data\": {  \"id\": 1234,  \"name\": \"User Stories\",  \"description\": \"...\",  \"projectId\": 1234,  \"creator\": {   \"id\": 1234,   \"userName\": \"...\",   \"firstName\": \"...\",   \"lastName\": \"...\",   \"admin\": false,   \"las2peerId\": 1234,   \"profileImage\": \"https://api.learning-layers.eu/profile.png\",   \"emailLeadSubscription\": true,   \"emailFollowSubscription\": true  },  \"categories\": [   {    \"id\": 1234,    \"name\": \"Group\",    \"description\": \"Requirements for Group\",    \"projectId\": 1234   }  ],  \"creationDate\": \"2021-08-08T01:56:58.057+0200\",  \"lastUpdatedDate\": \"2021-01-05T16:30:34.057+0200\",  \"numberOfComments\": 0,  \"numberOfAttachments\": 0,  \"numberOfFollowers\": 2,  \"upVotes\": 0,  \"downVotes\": 0,  \"userVoted\": \"NO_VOTE\" }, \"parentData\": {  \"id\": 1234,  \"name\": \"Group\",  \"description\": \"Requirements for Group\",  \"projectId\": 1234,  \"leader\": {   \"id\": 1234,   \"userName\": \"...\",   \"firstName\": \"...\",   \"lastName\": \"...\",   \"admin\": false,   \"las2peerId\": 1234,   \"profileImage\": \"https://api.learning-layers.eu/profile.png\",   \"emailLeadSubscription\": true,   \"emailFollowSubscription\": true  },  \"creationDate\": \"2020-11-03T15:23:12.057+0200\",  \"lastUpdatedDate\": \"2021-01-05T14:18:31.057+0200\",  \"numberOfRequirements\": 5,  \"numberOfFollowers\": 2 }, \"user\": {  \"id\": 1234,  \"userName\": \"...\",  \"firstName\": \"...\",  \"lastName\": \"...\",  \"admin\": false,  \"las2peerId\": 1234,  \"profileImage\": \"https://api.learning-layers.eu/profile.png\",  \"emailLeadSubscription\": true,  \"emailFollowSubscription\": true,  \"creationDate\": \"2020-11-02T10:37:13.057+0200\",  \"lastLoginDate\": \"2021-01-05T16:34:35.057+0200\" }}";

            // testInput is the pathParam
            ClientResponse result = client.sendRequest("POST", mainPath, testActivity,
                    MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, new HashMap<>());
            System.out.println("Result of 'testPost': " + result.getResponse().trim());
            Assert.assertEquals(201, result.getHttpCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * Test to get the activities stored in the test db.
     */
    @Test
    public void testGetActivities() {
        try {
            MiniClient client = getClient();

            ClientResponse result = client.sendRequest("GET", mainPath, "");
            System.out.println(result.toString());

            Assert.assertEquals(200, result.getHttpCode());
            JsonElement response = JsonParser.parseString(result.getResponse());
            Assert.assertTrue(response.isJsonArray());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
}
