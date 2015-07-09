LAS2peer-ActivityTracker
===================

Table of contents
-------------

- [LAS2peer-ActivityTracker](#)
	- [Table of contents](#)
	- [Idea](#)
	- [Technology](#)
	- [Dependencies](#)
	- [How to run using Docker](#)
	- [How to set up the database](#)
	- [Configuration](#)
	- [Build](#)
	- [How to run](#)

----------

Idea
-------------

LAS2peer-ActivityTracker is a microservice to provide activity information for LAS2peer services. The service is generically to work with different other microservices.

The activity tracker answers long-polling HTTP requests to push new activities to a client. Later we will support additional websocket connections.

----------


Technology
-------------------

The activity tracker is built on Java technologies. As a service framework we use our in-house developed **[<i class="icon-link "></i>LAS2Peer](https://github.com/rwth-acis/LAS2peer)** project. For persisting our data we use MySQL database and JOOQ to access it. User input validation is done using Jodd Vtor library and for serializing our data into JSON format, we use GSON library.

----------


Dependencies
-------------------

In order to be able to run this service project the following components should be installed on your system:

 - JDK (min v1.7) + Java Cryptography Extension (JCE) 
 - MySQL 5 
 - (Apache Ant)
 

How to run using Docker
-------------------

Docker will be provided at a later point.


How to set up the database
-------------------

 1. `git clone this repo`
 2. create a new database called `activitytracker`, possibly with UTF-8 collation
 3. Run the SQL commands in the file `\etc\activitytracker_create.sql`
     This will create the tables and relations between them.
 4.  If you need sample data run the file `\etc\activitytracker_demo_data.sql`
 5. To configure your database access look at the [Configuration](#configuration) section

 
Configuration
-------------------

Of course there are way to configure this service to your own specific environment. Here are the list of configuration files and their short description:

\etc\de.rwth.dbis.acis.activity.service.ActivityService.properties
:   *dbUserName*:	A database user's name, which will be used to access the database
:   *dbPassword*:		The database user's password
:   *dbUrl*:			JDBC Connection string to access the database. Modify it if you have changed the name of your database

For other configuration settings, check the **[<i class="icon-link "></i>LAS2Peer](https://github.com/rwth-acis/LAS2peer)** project.


Build
-------------------

For build management we use Ant. To build the cloned code, please using a console/terminal navigate to the home directory, where the build.xml file is located and run the following commands:

 1. `ant install-ivy`
 2. `ant clean_all`
 3. `ant get_deps`
 4. `ant generate_configs`
 5. `ant jar`

How to run
-------------------

 1. First please make sure you have already [set up the database](#how-to-set-up-the-database)
 2. Make sure your [config settings](#configuration) are properly set.
 3. [Build](#build)
 4. Open a console/terminal window and navigate to the `\bin` directory
 5. Run the `start_network.bat` or `start_network.sh`


