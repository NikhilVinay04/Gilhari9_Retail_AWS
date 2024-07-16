## PROJECT DESCRIPTION

This project involves configuring and running a (Kafka) streaming server, developing a standalone program (Producer) to keep generating data for JSON objects programmatically and sending that data (of one or multiple JSON objects at a time) to the (Kafka) streaming service. A consumer program is developed to retrieve that data and transfer it to an RDBMS hosted on AWS using the Gilahri microservice.

This project aims to show that the Gilhari microservice is an easy to use, low code service to perform the task of transferring JSON data to an RDBMS. Gilhari helps eliminate the need to write any code in Java to create the RDBMS table with the help of the ORM JDX(a product of Software Tree) for which we provide a mapping file ```gilhari9_retail_postgres_aws.jdx```.



## INSTALLING AND STARTING A KAFKA SERVER ON MACOS

  We can install kafka by using the homebrew formula: ```brew install kafka```. We also need to install zookeeper which is used for metadata management in Kafka through the formula: ```brew install zookeeper```

  To start a Kafka server, first we must start zookeeper on one terminal with the command: ```brew services start zookeeper``` and then on a separate terminal start kafka with: ```brew services start kafka```.

## GILHARI SETUP AND CONFIGURATION
	
  In ```src/org/emp/```, create class files ```JSON_Employee.java, JSON_InventoryItem.java, JSON_Sale.java, JSON_Shipment.java``` as shown to create a ```JDX_JSONObject``` (derived from Software Tree's JDX).We then compile the class in Java 8 with:```javac -cp lib/json-20240303.jar:lib/jxclasses.jar -d bin -source 1.8 -target 1.8 src/main/java/org/emp/JSON_ClassName.java```
 ```json-20240303.jar``` and ```jxclasses.jar``` are present in a lib directory and ClassName is a placeholder for the name of the class we are compiling namely JSON_Employee, Shipment, Sale or InventoryItem. This creates the required bin directory.

  In ```config/```, create a file named ```gilhari9_retail_postgres_aws.jdx``` as shown. To the config directory we also add postgresql’s(as that is what I have used) JDBC driver jar and a file called classNameMapping.js which maps “Employees”,"Sales","Shipment" and "Inventory" to their defined container classes.
  
  Following this create a ```gilhari9_retail_postgres_aws_service.config``` file and the ```Dockerfile``` by following instructions from the Gilhari readme.

  Run the command ```docker build -t my_app_gilhari -f ./Dockerfile . --progress = plain --no-cache``` to build the docker image. To run the docker image we do: ```docker run -p 80:8081 my_app_gilhari```.

## POSTGRESQL INSTALLATION AND RUNNING
  
  Install ```Postgres.app``` from the website and then execute the command : ```psql postgres://username@localhost:5432```. Username refers to your local username and the default password is postgres which can be changed.5432 is the default port for POSTGRESQL. These details are required to be filled in the .jdx file and the .config file. A new database can be created with the command: ```CREATE DATABASE db1;``` on the command line where db1 is the name of the database. Do note that the semi colon is a part of Postgresql syntax. To connect to database db1 run ```\c db1``` on the command line or open the Postgres app and click on the database db1.

Postgresql should be running in a separate terminal at the same time as the docker image of Gilhari, kafka and zookeeper.

## HOSTING A POSTGRESQL DATABASE ON AWS
   To do this, a Postgresql database db1 is created using Amazon RDS(Relational Database Service) with public access to its IP. To avoid any issues the security group must be configured with inbound and outbound rules such that network traffic from one's system is allowed to access the database and bypass the firewall else Gilhari would be unable to connect to the database. Following this, in the ORM file we provide the link in the format ``` my_aws_endpoint:5432/db1? ``` where ``` my_aws_endpoint ``` refers to the endpoint that is provided by AWS and can be copied from the webpage showing the details of the database being hosted. Username and password used must also be mentioned in the ORM file to allow Gilhari access to the database. One must also ensure to delete the database instance once usage of it is completed as merely stopping the instance does not prevent getting charged as it incurs storage costs.

## RUNNING PRODUCER AND CONSUMER
 The 4 producers(```Prod.java, Prod_Inventory.java, Prod_Sales.java, Prod_Shipment```) and consumer(```Consumer.java```) are a part of a Maven project on  IntelliJ IDE. Fill in the required dependencies in the ```pom.xml``` file as and when needed. 

```Producer_Employee.java``` is a producer which takes in employee data from ```employee_data.json``` and sends it to the kafka cluster.

```Producer_Inventory.java``` takes in Inventory data from ```inventory_data.json``` and the Consumer is then run to extract Inventory data from the cluster and send it to the backend PostgreSQL database by using the POST API call of Gilhari. 

```Producer_Sales.java``` takes sales data from ```sales_data.json``` and updates the Inventory data(as selling an item will lead to a decrease in its quantity in Inventory) stored in the PostgreSQL database.The consumer then stores the Sales data to the Sales table in the database with the POST API call of Gilhari. 

```Producer_Shipment.java``` takes sales data from ```shipment_data.json``` and updates the Inventory data(as shipment of an item will lead to an increase in its quantity in Inventory) stored in the PostgreSQL database.The consumer then stores the Shipment data in the Shipment table in the database with the POST API call of Gilhari. 

The single Consumer serves all 4 topics(Employees, Inventory, Shipment and Sales) and takes care of sending data obtained to the database through POST API calls.

Data is filled in the JSON files by means of JSON generator programs ```EmployeeGen.java, SalesGen.java, ShipmentGen.java and InventoryGen.java``` which create data following the template specified by the Employee, Sales, Shipment and Inventory classes respectively. This is done with the help of Gson which is a Java library that helps converting Java Objects into a string in a JSON format.

