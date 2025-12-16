# Notes and useful references on play as I learn! 

## Scenarios, response codes, ScalaTest assertions
Scenario	Controller Response	HTTP Code	Test Assertion
GET success	Ok(Json)	200	status(result) mustBe OK
POST success	Created(Json)	201	status(result) mustBe CREATED
DELETE success	NoContent	204	status(result) mustBe NO_CONTENT
Missing fields	BadRequest	400	status(result) mustBe BAD_REQUEST
ID not found	NotFound	404	status(result) mustBe NOT_FOUND
Invalid JSON shape	BadRequest(JsonError)	400	status(result) mustBe BAD_REQUEST

---

## Overview on how play projects are typically setup 
### Example project setup
/app
/controllers
/models
/services       (optional, common in larger apps)
/views          (optional, mostly for HTML rendering)
/utils          (optional helpers)

/conf
/routes          <- defines all the HTTP endpoints
/application.conf <- main app configuration
/logback.xml      <- logging config

/test
/controllers     <- tests for controllers
/services        <- tests for service layer
/models          <- tests for model logic

/build.sbt         <- sbt project configuration

## Key components 
#### Controllers
- handle HTTP requests
- co-ordinate between request data, services/models and responses
  - Parse request (forms like JSON, query params, form data)
  - call a service/ repository to do the work
  - return a result (JSON, HTML, redirect etc)
- can be synchronous (Action) or asynchronous (Action.async)
#### Models
- represent the data in the app 
- often use case classes in Scala for immutability and pattern matching
- can include domain logic (e.g. validation functions), JSON formatters
#### Services
- business logic lives here, separate from controllers
- controllers call services instead of directly manipulating data 
- makes it easier to test business logic separately, without HTTP
- keeps controllers thin and focussed 
- works well with dependency injection
#### Views
- only needed if your app renders html, not for API-only apps
- uses twirl templates- files with .scala.html
#### Routes
- map http method + url path -> controller method 
- play uses reverse routing to generate URLs safely in code 
#### Persistence/ database layer
- typically slick (scala's db library) or JDBC
- example workflow:
  - controller receives request
  - controller calls service
  - service calls repository / DAO
  - repository performs query and returns data 
  - service returns processed data to the controller 
  - controller returns JSON response 
#### Dependency injection
- play uses Guice for dependency injection by default
- controllers and services are injected automatically 
- promotes separation of concerns 
- makes testing easier (mock services)
- makes it easy to swap implementations 
#### Overview flow
HTTP request -> Router -> Controller -> Service -> Repository/ DB <- Response (JSON/ HTTP status)
- Controllers: translate HTTP -> business logic + JSON response
- Services: business rules, data manipulation
- Repository/ database: persistent storage
- Views (optional): HTML rendering


## Adding persistence
Action: part of the play framework. An object that has the method apply(block: => Result).
Most of the requests handled by Play are handled by an Action. An Action returns a play.api.mcv.Result, representing the HTTP response 
to send to the web client. 

### Responding to async queries
In Play we are regularly doing async requests, meaning we have to handle data potentially not returning the expected result. This is why
we return Futures from the repository. Clients may also make requests of the API that cannot be fulfilled, meaning we have to anticipate
null results, for this we use Options- which may contain Some or None. A common pattern to pattern match on Options in Scala is using 
map {case Some => Ok(Json.toJson(Item)) case None => NotFound}. This handles both cases and ties to REST semantics.

Values returned from the repository are Futures. You cannot pattern match on a Future. You must wait asynchronously, using map. Then, 
pattern match the Option inside.
Pattern to patch by id:
JSON → validate → findById (Future) → map → Option match → update → save → Result

map -> transform a value
flatMap -> chain async operations 
