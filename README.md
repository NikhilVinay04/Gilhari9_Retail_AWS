## PROJECT DESCRIPTION

This project involves configuring and running a (Kafka) streaming server, developing a standalone program (Producer) to keep generating data for JSON objects programmatically and sending that data (of one or multiple JSON objects at a time) to the (Kafka) streaming service. A consumer program is developed to retrieve that data and transfer it to an RDBMS hosted on AWS using the Gilahri microservice.

This project aims to show that the Gilhari microservice is an easy to use, low code service to perform the task of transferring JSON data to an RDBMS. Gilhari helps eliminate the need to write any code in Java to create the RDBMS table with the help of the ORM JDX for which we provide a mapping file gilhari_simple_example.jdx.



## INSTALLING AND STARTING A KAFKA SERVER ON MACOS

  We can install kafka by using the homebrew formula: ```brew install kafka```
	We also need to install zookeeper which is used for metadata management in Kafka through the formula: ```brew install zookeeper```

  To start a Kafka server, first we must start zookeeper on one terminal with the command: ```brew services start zookeeper``` and then on a separate terminal start kafka with: ```brew services start kafka```.

## GILHARI SETUP AND CONFIGURATION
	
  In ```src/org/emp/```, create a class file ```JSON_Employee.java``` as shown to create a ```JDX_JSONObject``` (derived from Software Tree's JDX).
	We then compile the class in Java 8 with : ```javac -cp lib/json-20240303.jar:lib/jxclasses.jar -d bin -source 1.8 -target 1.8 src/main/java/org/emp/JSON_Employee.java```
	where ```json-20240303.jar``` and ```jxclasses.jar``` are present in a lib directory. This creates the required bin directory.

  In ```config/```, create a file named ```gilhari_simple_example.jdx``` as shown. To the config directory we also add postgresql’s(as that is what I have used) JDBC driver jar and a file called classNameMapping.js which maps “Employees” to the defined Employee container class.
	Following this create a ```gilhari_service.config file``` and the ```Dockerfile``` by following instructions from the Gilhari readme.

  Run the command ```docker build -t my_app_gilhari -f ./Dockerfile . --progress=plain --no-cache``` to build the docker image. To run the docker image we do: ```docker run -p 80:8081 my_app_gilhari```

## POSTGRESQL installation and running
  
  Install ```Postgres.app``` from the website and then execute the command : ```psql postgres://username@localhost:5432```. Username refers to your local username and the default password is postgres which can be changed.5432 is the default port for POSTGRESQL. These details are required to be filled in the .jdx file and the .config file. A new database can be created with the command: ```CREATE DATABASE db1;``` on the command line where db1 is the name of the database. Do note that the semi colon is a part of Postgresql syntax. To connect to database db1 run ```\c db1``` on the command line or open the Postgres app and click on the database db1.

Postgresql should be running in a separate terminal at the same time as the docker image of Gilhari,kafka and zookeeper.

## RUNNING PRODUCER AND CONSUMER
 The producer(```Prod.java```) and consumer(```Consumer.java```) are a part of a Maven project on  IntelliJ IDE. Fill in the required dependencies in the ```pom.xml``` file as and when needed. Run the producer and consumer using the run button on IntelliJ. The Producer takes in user input to enter 10 names which gets allotted to respective partitions in a topic and can be retrieved by the consumer. The Consumer also features the REST API POST HTTP request where the JSON object as specified by the Entity class(which in turn uses an object of class User to fit the required format to send data to Gilhari) is sent to Gilhari which fills in the required PostgreSQL database.
