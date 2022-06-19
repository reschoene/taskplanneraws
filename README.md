# TaskPlanner for AWS
A simple task planner API for listing tasks grouped by lists and managing them

## Stack
- Kotlin, Java with GraalVM (for native compilation)
- Quarkus, the Supersonic Subatomic Java Framework.
- RESTEasy, RESTAssured
- AWS Lambda, AWS API Gateway, AWS Dynamodb
- AWS SAM CLI (for local testing and AWS Deploy): SAM = Serverless Application Model

## Building and deploying native executable

### Creating a native executable
You can create a native executable using:
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

### Deploying a native executable
 - You can then test the executable locally with sam local
`sam local start-api --template build/sam.native.yaml`

 - To deploy to AWS Lambda:
`sam deploy -t build/sam.native.yaml -g`

## Building and deploying JVM executable

### Creating a JVM executable
You can create a native executable using:
```shell script
./gradlew build
```

### Deploying a native executable
- You can then test the executable locally with sam local
  `sam local start-api --template build/sam.jvm.yaml`

- To deploy to AWS Lambda:
  `sam deploy -t build/sam.jvm.yaml -g`

### deploy throubleshooting
If you get an error similar to this one: "An error occurred (SignatureDoesNotMatch) when calling the CreateChangeSet operation: Signature expired: 20220619T193119Z is now earlier than 20220619T193604Z (20220619T194104Z - 5 min.)"
run following command to sync your PC date and time: 
```shell script
sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"
```


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```
