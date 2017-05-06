las2peer-ActivityTracker
===================

Idea
-------------------
las2peer-ActivityTracker is a microservice to provide activity information for las2peer services. The service is generically to work with all kinds of microservices.

The activity tracker response to HTTP or p2p requests and can fetch activity objects from the origin microservice which created an activity.

----------

Service Documentation
-------------------
We use **[<i class="icon-link "></i>Swagger](http://swagger.io/specification/)** for documenting this microservice. You can use **[<i class="icon-link "></i>Swagger UI](http://swagger.io/swagger-ui/)** to inspect the API.
In the future we want to deploy our own Swagger UI instance which you can use to check and use the API. Until then please use the **[<i class="icon-link "></i>Swagger petstore instance](http://petstore.swagger.io/)**. 
Open the Swagger UI petstore demo, enter the APi endpoint you want to inspect and press explore. Of course the microservice instance you want to inspect needs to run. At the moment you can not authorize yourself, but we are working on this feature.

API documentation endpoint:

 - `baseURL/activities/swagger.json`

----------

Technology
-------------------
The activity tracker is built on Java technologies. As a service framework we use our in-house developed **[<i class="icon-link "></i>las2peer](https://github.com/rwth-acis/LAS2peer)** project. For persisting our data we use MySQL database and jOOQ to access it. User input validation is done using Jodd Vtor library and for serializing our data into JSON format, we use GSON library.

----------


Dependencies
-------------------
In order to be able to run this service project the following components should be installed on your system:

 - JDK (min v1.8) + Java Cryptography Extension (JCE) 
 - MySQL 5.7 
 - Apache Ant to build
 
How to set up the database
-------------------
 1. `git clone` this repo
 2. create a new database called `activitytracker`, possibly with UTF-8 collation
 3. Run the SQL commands in the file `\etc\activitytracker_create.sql`
     This will create the tables and relations between them.
 4.  If you need sample data run the file `\etc\activitytracker_demo_data.sql`
 5. To configure your database access look at the [Configuration](#configuration) section

 
Configuration
-------------------
Of course there are way to configure this service to your own specific environment. Here are the list of configuration files and their short description:

`\etc\de.rwth.dbis.acis.activity.service.ActivityService.properties`:
 - `dbUserName`:	A database user's name, which will be used to access the database
 - `dbPassword`:	The database user's password
 - `dbUrl`:			JDBC Connection string to access the database. Modify it if you have changed the name of your database
 - `land`:          Default language setting
 - `country`:       Default country setting
 - `baseURL`:       Base URL this service runs on

For other configuration settings, check the **[<i class="icon-link "></i>las2peer](https://github.com/rwth-acis/LAS2peer)** project.


Build
-------------------
For build management we use Ant. To build the cloned code, please using a console/terminal navigate to the `home` directory, where the `build.xml` file is located and run the following command:

 - `ant`

How to run
-------------------
 1. First please make sure you have already [set up the database](#how-to-set-up-the-database)
 2. Make sure your [config settings](#configuration) are properly set.
 3. [Build](#build)
 4. Open a console/terminal window and navigate to the `\bin` directory
 5. Run the `start_network.bat` or `start_network.sh` script

How to run using Docker
-------------------
Docker will be provided at a later point.