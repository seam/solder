To run the example with embedded jetty:

mvn jetty:run

Then navigate to:

http://localhost:9090/princess-rescue/home.jsf

To deploy the example to jbossas 6:

export JBOSS_HOME=/path/to/jboss
mvn clean install jboss:hard-deploy -Pjavaee

To deploy the example to Glassfish:

mvn clean package -Pjavaee 

and then copy the resulting war to the Glassfish deploy directory.

