package controllers

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
// integration tests - controllers + db
class ToDoListControllerSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    super.beforeEach()
    route(app, FakeRequest(DELETE, "/todo"))
  }
  "TodoListController DELETE /todo" should {
    "delete all items" in {
      route(app, FakeRequest(POST, "/todo")
        .withJsonBody(Json.parse("""{"description":"test"}""")) // seed data
      )
      val deleteRequest = FakeRequest(DELETE, "/todo")
      val deleteResult = route(app, deleteRequest).get
      status(deleteResult) mustBe NO_CONTENT
      // verify empty
      val getResult = route(app, FakeRequest(GET, "/todo")).get
      status(getResult) mustBe NO_CONTENT
    }
      "delete a single item by id" in {
        // create item
        val postResult = route(app,
          FakeRequest(POST, "/todo")
            .withJsonBody(Json.parse("""{"description":"delete me"}"""))
        ).get
        val id = (contentAsJson(postResult) \ "id").as[Long]
        // delete it
        val deleteResult =
          route(app, FakeRequest(DELETE, s"/todo/$id")).get
        status(deleteResult) mustBe NO_CONTENT
        // verify it’s gone
        val getResult =
          route(app, FakeRequest(GET, s"/todo/$id")).get
        status(getResult) mustBe NOT_FOUND
      }
    }
}

//"TodoListController GET /todo" should {
//    "return all items" in {
//      val request = FakeRequest(GET, "/todo")
//      val result = route(app, request).get
//      status(result) mustBe OK
//      contentType(result) mustBe Some("application/json")
//      val json = contentAsJson(result)
//      json.as[Seq[JsValue]].size must be >= 1
//    }
//    "return item by id" in {
//      val request = FakeRequest(GET, "/todo/2")
//      val result = route(app, request).get
//      status(result) mustBe OK
//      contentType(result) mustBe Some("application/json")
//      val json = contentAsJson(result)
//      (json \ "description").as[String] mustBe "go swimming"
//      (json \ "completed").as[Boolean] mustBe false
//    }
//  }
//  "TodoListController POST /todo" should {
//    "create a new item" in {
//      val newItemJson = Json.obj(
//        "description" -> "Write tests",
//        "completed" -> true
//      )
//      val request = FakeRequest(POST, "/todo")
//        .withJsonBody(newItemJson)
//      val result = route(app, request).get
//      status(result) mustBe CREATED
//
//      val json = contentAsJson(result)
//      (json \ "description").as[String] mustBe "Write tests"
//      (json \ "completed").as[Boolean] mustBe true
//    }
//    "create a new item without including completed status" in {
//      val newItemJson = Json.obj(
//        "description" -> "Write tests",
//      )
//      val request = FakeRequest(POST, "/todo")
//        .withJsonBody(newItemJson)
//      val result = route(app, request).get
//      status(result) mustBe CREATED
//
//      val json = contentAsJson(result)
//      (json \ "description").as[String] mustBe "Write tests"
//      (json \ "completed").as[Boolean] mustBe false
//    }
//  }
//    "TodoListController PATCH /todo/:id" should {
//    "update an existing item" in {
//      val updateJson = Json.obj(
//        "completed" -> true
//      )
//      val request = FakeRequest(PATCH, "/todo/1")
//        .withJsonBody(updateJson)
//      val result = route(app, request).get
//      status(result) mustBe OK
//      (contentAsJson(result) \ "completed").as[Boolean] mustBe true
//    }
//  }

//    }