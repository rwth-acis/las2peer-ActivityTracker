#!/bin/bash

            # this script is autogenerated by 'ant startscripts'
            # it starts a LAS2peer node providing the service 'de.rwth.dbis.acis.activitytracker.service.ActivityTrackerService' of this project
            # pls execute it from the root folder of your deployment, e. g. ./bin/start_network.sh

            java -cp "lib/*" i5.las2peer.tools.L2pNodeLauncher -p 9012 uploadStartupDirectory startService\(\'de.rwth.dbis.acis.activitytracker.service.ActivityTrackerService@0.1\',\'${service.passphrase}\'\) startWebConnector interactive

        