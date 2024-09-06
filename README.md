## Tech stack

* SpringBoot
* Java
* Mockito
* Feign
* Gradle
* Swagger
* Wiremock

## How to build

gradle clean build

## Tests

* There are unit test for each service or components
* There is integration test starting context with wiremock
* Code coverage os 88% on methods and 89% on lines

## API

Api is exposed by rest API by url: /user/{username}/repos

# Important!

* In application.yml there is property for token. It should be override with value from VAULT, ConfigServer or github
  actions secrets!! I could not find developer api for creating jwt token in github docs. If it exist we could create
  token programmatically.