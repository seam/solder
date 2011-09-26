To run the example with embedded jetty:

mvn jetty:run

Then navigate to:

http://localhost:8080/config-quiz/home.jsf

To deploy the example to jbossas:

export JBOSS_HOME=/path/to/jboss
mvn clean package jboss:hard-deploy -Pjavaee

To deploy the example to Glassfish:

mvn clean package -Pjavaee 

and then copy the resulting war to the Glassfish deploy directory.

