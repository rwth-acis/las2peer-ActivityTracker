:: this script starts a las2peer node providing the example service of this project
:: pls execute it from the bin folder of your deployment by double-clicking on it

%~d0
cd %~p0
cd ..
set BASE=%CD%
set CLASSPATH="%BASE%/lib/*;"

java -cp %CLASSPATH% i5.las2peer.tools.L2pNodeLauncher -w -p 9012 uploadStartupDirectory('etc/startup') startService('de.rwth.dbis.acis.activitytracker.service.ActivityTrackerService','TemplateServicePass') startWebConnector interactive
pause
