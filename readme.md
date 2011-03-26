#Seam Solder

A portable CDI extension library for applications, frameworks and other
extensions based on CDI.

Seam Solder is independent of CDI implementation and fully portable between
Java EE 6 and Servlet environments enhanced with CDI.

For more information, see the [Seam Solder project page](http://seamframework.org/Seam3/Solder).

## Building

   mvn clean install

## Running tests in-container (managed JBoss AS 6)

   mvn clean install -Dincontainer

## Running tests in-container (remote JBoss AS 6)

   mvn clean install -Dincontainer-remote

## Running tests in-container (remote GlassFish 3.1)

   mvn clean install -Dincontainer-glassfish-remote
