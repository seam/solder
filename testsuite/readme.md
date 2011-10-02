#Seam Solder Test Suite

##Running the testsuite on the default container (Embedded Weld)

    mvn clean verify 

##Running the testsuite on JBoss AS 7

    export JBOSS_HOME=/path/to/jboss-as-7.x
    mvn clean verify -Darquillian=jbossas-managed-7


