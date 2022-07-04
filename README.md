# TaskPlanner for AWS
A simple task planner API for listing tasks grouped by lists and managing them

## Stack
- Kotlin, Java with GraalVM (for native compilation)
- Quarkus, the Supersonic Subatomic Java Framework.
- TestContainers, RESTEasy, RESTAssured
- AWS Lambda, AWS API Gateway, AWS Dynamodb
- AWS SAM CLI (for local testing and AWS Deploy): SAM = Serverless Application Model

## Project status - Sonar analysis
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=reschoene_taskplanneraws)](https://sonarcloud.io/summary/new_code?id=reschoene_taskplanneraws)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=reschoene_taskplanneraws&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=reschoene_taskplanneraws)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=reschoene_taskplanneraws&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=reschoene_taskplanneraws)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=reschoene_taskplanneraws&metric=bugs)](https://sonarcloud.io/summary/new_code?id=reschoene_taskplanneraws)

[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=reschoene_taskplanneraws&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=reschoene_taskplanneraws)

## Build and Deploy 
There are two deploy options available: JVM and native executables. Bellow are the instructions for each one

### Option 1 - Building and deploying native executable

#### Creating a native executable
You can create a native executable using:
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

#### Deploying a native executable
 - You can then test the executable locally with sam local
`sam local start-api --template build/sam.native.yaml`

 - To deploy to AWS Lambda:
`sam deploy -t build/sam.native.yaml -g`

### Option 2 - Building and deploying JVM executable

#### Creating a JVM executable
You can create a native executable using:
```shell script
./gradlew build
```

#### Deploying a JVM executable
- You can then test the executable locally with sam local
  `sam local start-api --template build/sam.jvm.yaml`

- To deploy to AWS Lambda:
  `sam deploy -t build/sam.jvm.yaml -g`


### Deploy error tips
If you get an error similar to this one: "An error occurred (SignatureDoesNotMatch) when calling the CreateChangeSet operation: Signature expired: 20220619T193119Z is now earlier than 20220619T193604Z (20220619T194104Z - 5 min.)"
run following command to sync your PC date and time: 
```shell script
sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"
```


## Running the application in dev mode
As this is a quarkus application, you can run it using its developer mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

or in debug mode
```shell script
./gradlew quarkusDev --debug
```

## Running dynamoDb locally in dev mode
```shell script
docker run --publish 8000:8000 amazon/dynamodb-local:1.11.477 -jar DynamoDBLocal.jar -inMemory -sharedDb
```
This starts a DynamoDB instance that is accessible on port 8000. You can check it’s running by accessing the web shell on http://localhost:8000/shell.
Open "http://localhost:8000/shell" in your browser.

Copy and paste the following code to the shell and run it:

```shell script 
var params = {
TableName: 'TaskLists',
KeySchema: [{ AttributeName: 'id', KeyType: 'HASH' }],
AttributeDefinitions: [{  AttributeName: 'id', AttributeType: 'S', }],
ProvisionedThroughput: { ReadCapacityUnits: 1, WriteCapacityUnits: 1, }
};

dynamodb.createTable(params, function(err, data) {
if (err) ppJson(err);
else ppJson(data);

});

params = {
TableName: 'Tasks',
KeySchema: [{ AttributeName: 'id', KeyType: 'HASH' }],
AttributeDefinitions: [{  AttributeName: 'id', AttributeType: 'S', }],
ProvisionedThroughput: { ReadCapacityUnits: 1, WriteCapacityUnits: 1, }
};

dynamodb.createTable(params, function(err, data) {
if (err) ppJson(err);
else ppJson(data);

});

params = {
TableName: 'Quotations',
KeySchema: [{ AttributeName: 'id', KeyType: 'HASH' }],
AttributeDefinitions: [{  AttributeName: 'id', AttributeType: 'S', }],
ProvisionedThroughput: { ReadCapacityUnits: 1, WriteCapacityUnits: 1, }
};

dynamodb.createTable(params, function(err, data) {
if (err) ppJson(err);
else ppJson(data);

});
```

## accessing swagger UI
http://localhost:8080/q/swagger-ui

## accessing OpenAPI definition
http://localhost:8080/q/openapi

## creating dynamodb table on your aws:
```shell script 
aws dynamodb create-table --table-name TaskLists \
--attribute-definitions AttributeName=id,AttributeType=S \
--key-schema AttributeName=id,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

```shell script 
aws dynamodb create-table --table-name Tasks \
--attribute-definitions AttributeName=id,AttributeType=S \
--key-schema AttributeName=id,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

```shell script 
aws dynamodb create-table --table-name Quotations \
--attribute-definitions AttributeName=id,AttributeType=S \
--key-schema AttributeName=id,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

### SonarCloud Integration:
Change sonarqube properties on build.gradle file to your SonarCloud configs

#### To generate JaCoCo coverage reports, run:
```shell script
./gradlew test
```

#### To upload coverage data to SonarCloud:
```shell script 
export SONAR_TOKEN=YOUR_SONAR_KEY
```

```shell script 
./gradlew sonarqube
```