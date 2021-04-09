=======
# Hearing Recording Service - Ingestor


#Local Dev

##First Time Build (If you wish to use sonarqube)

You'll need to get sonarqube docker image if you do not have it already, and initialise it and change the password to adminnew

to fetch the latest image, run it and open the browser
run:
make sonarqube-fetch-sonarqube-latest
make report-sonarqube

in the browser, log in as admin (password=admin), go to http://localhost:9000/account/security/ and change password to adminnew


##Subsequent Builds (these must all pass before raising a PR)

checks:
- make check-all

sonarqube:
- make sonarqube-run-local-sonarqube-server
- sonarqube-run-tests-with-password-as-adminnew

smoketest:

for first time use you will need to be logged into the Azure Container repo's using these commands:

az login
az acr login --name hmctspublic && az acr login --name hmctsprivate


the get hrs-api running, from root of hrs-api project, run:
./docker/dependencies/start-local-environment.sh
./gradlew bootRun

then get the ingestor dependencies running, from the root of this project run:

docker/dependencies/start-local-environment.sh
./gradlew bootRun


then smoke test with
./gradlew smokeTest

#Idea Setup

Increase import star to 200 to avoid conflicts with checkstyle
https://intellij-support.jetbrains.com/hc/en-us/community/posts/206203659-Turn-off-Wildcard-imports-

Auto import of non ambiguous imports
https://mkyong.com/intellij/eclipse-ctrl-shift-o-in-intellij-idea/#:~:text=In%20Eclipse%2C%20you%20press%20CTRL,imports%2C%20never%20imports%20any%20package.

Import the checkstyle code scheme into the java code settings

Reverse the import layout settings / modify until the checkstyle passes
Uncheck "Comment at first column"







NOTE THE BELOW IS NOT YET TESTED!!!
NOTE THE BELOW IS NOT YET TESTED!!!
NOTE THE BELOW IS NOT YET TESTED!!!
NOTE THE BELOW IS NOT YET TESTED!!!
NOTE THE BELOW IS NOT YET TESTED!!!
NOTE THE BELOW IS NOT YET TESTED!!!








## Setup

Simply run the following script to start all application dependencies.

```bash
  ./docker/dependencies/start-local-environment.sh
```

## Building and deploying the application

### Building the application

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew assemble
```

Create docker image:

```bash
  docker-compose build
```

Run the applicaiton in docker by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port [8090]

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:8090/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
