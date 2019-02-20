las2peer-ActivityTracker
===================

Idea
-------------------
las2peer-ActivityTracker is a microservice to provide activity information for las2peer services. The service works together with all kinds of microservices.

The activity tracker response to HTTP or (las2peer) p2p requests and can fetch activity objects from the origin microservice which created an activity.
This service has also build-in MQTT support, so that all new activities will get publish to an MQTT broker of your choice. To enable MQTT publish please provide the MQTT information in the configuration file.

We also provide a modern webcomponent frontend for this service which you can find also on **[<i class="icon-link "></i>Github](https://github.com/rwth-acis/activity-tracker)**.

The service is under development. You can participate by creating pull requests or by discussing ideas and requirements inside **[<i class="icon-link "></i>Requirements-Bazaar](https://requirements-bazaar.org/projects/2/categories/169)**.

More information about our microservice ecosystem is explained in our  **[<i class="icon-link "></i>Requirements-Bazaar wiki on Github](https://github.com/rwth-acis/RequirementsBazaar/wiki)**.

----------

Service Documentation
-------------------
We use **[<i class="icon-link "></i>Swagger](http://swagger.io/specification/)** for documenting this microservice. You can use **[<i class="icon-link "></i>Swagger UI](http://swagger.io/swagger-ui/)** to inspect the API.
You can inspect the API documentation of our live (master branch) instance an our beta (develo branch) with our **[<i class="icon-link "></i>Swagger UI instance](http://requirements-bazaar.org/docs/)**.

API documentation endpoint:

 - `baseURL/activities/swagger.json`

----------

Technology
-------------------
The activity tracker is built on Java technologies. As a service framework we use our in-house developed **[<i class="icon-link "></i>las2peer](https://github.com/rwth-acis/LAS2peer)** project. 
For persisting our data we use MySQL database and jOOQ to access it. User input validation is done using Jodd Vtor library and for serializing our data into JSON format, we use the Jackson library. As MQTT client we use Eclipse Paho.

Dependencies
-------------------
In order to be able to run this service project the following components should be installed on your system:

 - JDK (min v1.8) + Java Cryptography Extension (JCE) 
 - MySQL 5.7 
 - Apache Ant to build
  
----------
  
How to set up the database
-------------------
 1. `git clone` this repo
 2. To configure your database access look at the [Configuration](#configuration) section
 3. Compile the project with `ant`
 4. Create a new database called `reqbaztrack`, possibly with UTF-8 collation
 5. Run `ant migrate-db` to create your db schema or migrate to a newer version while updating your service
 6. If you need sample data run the file `\etc\add_activitytracker_demo_data.sql`
  
Configuration
-------------------
You need to configure the the service to your own specific environment. Here is the list of configuration variables:

`\etc\de.rwth.dbis.acis.activity.service.ActivityService.properties`:
 - `dbUserName`:	Database username, which will be used to access the database
 - `dbPassword`:	Database user password, which will be used to access the database
 - `dbUrl`:			JDBC Connection string to access the database
 - `baseURL`:       Base URL this service runs on
 - `mqttBroker`:    MQTT Broker, if this field is set it enables MQTT publish of new activities
 - `mqttUserName`:  MQTT username to publish to broker, if this field is set MQTT use username and password. If not it MQTT doe not use authorize to broker.
 - `mqttPassword`:  MQTT password to publish to broker
 - `mqttOrganization`: Your organisation name, used as first channel description in MQTT

For other configuration settings, check the **[<i class="icon-link "></i>las2peer](https://github.com/rwth-acis/LAS2peer)** project.

Build
-------------------
For build management we use Ant. To build the cloned code, please using a console/terminal navigate to the `home` directory, where the `build.xml` file is located and run the following command:

 - `ant`
 
You can also generate a bundled jar with all the dependencies with the command

 - `ant jar-big`

How to run
-------------------
 1. First please make sure you have already [set up the database](#how-to-set-up-the-database)
 2. Make sure your [config settings](#configuration) are properly set.
 3. [Build](#build)
 4. Open a console/terminal window and navigate to the `\bin` directory
 5. Run the `start_network.bat` or `start_network.sh` script

How to run using Docker
-------------------

First build the image:
```bash
docker build . -t activity-tracker
```

Then you can run the image like this:

```bash
docker run -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypasswd -p 8080:8080 -p 9011:9011 activity-tracker
```

Replace *myuser* and *mypasswd* with the username and password of a MySQL user with access to a database named *reqbaztrack*.
By default the database host is *mysql* and the port is *3306*.
The REST-API will be available via *http://localhost:8080/activities* and the las2peer node is available via port 9011.

In order to customize your setup you can set further environment variables.

### Node Launcher Variables

Set [las2peer node launcher options](https://github.com/rwth-acis/las2peer-Template-Project/wiki/L2pNodeLauncher-Commands#at-start-up) with these variables.
The las2peer port is fixed at *9011*.

| Variable | Default | Description |
|----------|---------|-------------|
| BOOTSTRAP | unset | Set the --bootstrap option to bootrap with existing nodes. The container will wait for any bootstrap node to be available before continuing. |
| SERVICE_PASSPHRASE | Passphrase | Set the second argument in *startService('<service@version>', '<SERVICE_PASSPHRASE>')*. |
| SERVICE_EXTRA_ARGS | unset | Set additional launcher arguments. Example: ```--observer``` to enable monitoring. |

### Service Variables

See [configuration](#configuration) for a description of the settings.

| Variable | Default |
|----------|---------|
| MYSQL_USER | *mandatory* |
| MYSQL_PASSWORD | *mandatory* |
| MYSQL_HOST | mysql |
| MYSQL_PORT | 3306 |
| BASE_URL | http://localhost:8080/activities/ |
| MQTT_BROKER | "" |
| MQTT_USER | "" |
| MQTT_PASSWORD | "" |
| MQTT_ORGANIZATION | "" |

### Web Connector Variables

Set [WebConnector properties](https://github.com/rwth-acis/las2peer-Template-Project/wiki/WebConnector-Configuration) with these variables.
*httpPort* and *httpsPort* are fixed at *8080* and *8443*.

| Variable | Default |
|----------|---------|
| START_HTTP | TRUE |
| START_HTTPS | FALSE |
| SSL_KEYSTORE | "" |
| SSL_KEY_PASSWORD | "" |
| CROSS_ORIGIN_RESOURCE_DOMAIN | * |
| CROSS_ORIGIN_RESOURCE_MAX_AGE | 60 |
| ENABLE_CROSS_ORIGIN_RESOURCE_SHARING | TRUE |
| OIDC_PROVIDERS | https://api.learning-layers.eu/o/oauth2,https://accounts.google.com |

### Other Variables

| Variable | Default | Description |
|----------|---------|-------------|
| DEBUG  | unset | Set to any value to get verbose output in the container entrypoint script. |
| INSERT_DEMO_DATA | unset | Set to any value to insert demo data into the database at startup. |

### Custom Node Startup

If the variables are not sufficient for your setup you can customize how the node is started via arguments after the image name.
In this example we start the node in interactive mode:
```bash
docker run -it -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypasswd activity-tracker startService\(\'de.rwth.dbis.acis.activitytracker.service.ActivityTrackerService@0.6.0\', \'Passphrase\'\) startWebConnector interactive
```
Inside the container arguments are placed right behind the launch node command:
```bash
java -cp lib/* i5.las2peer.tools.L2pNodeLauncher -s service -p ${LAS2PEER_PORT} <your args>
```

### Volumes

The following places should be persisted in volumes in productive scenarios:

| Path | Description |
|------|-------------|
| /src/node-storage | Pastry P2P storage. |
| /src/etc/startup | Service agent key pair and passphrase. |
| /src/log | Log files. |

*Do not forget to persist you database data*

----------

Troubleshooting & FAQ
-------------------
 - I get Java encryption errors: Did you install Java Cryptography Extension?
 - I can not run the start script: Check if you have OS permission to run the file.
 - The service does not start: Check if all jars in the lib and service folder are readable.
 - The start script seems broken: Check if the start script has the correct encoding. If you ran the service on Unix use `dos2unix` to change the encoding.
 - To enable MQTT publish of new activities set `mqttBroker` and `mqttOrganization` in property file.