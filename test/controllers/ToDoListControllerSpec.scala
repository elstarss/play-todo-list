package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class ToDoListControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  "TodoListController GET /todo" should {
    "return all items" in {
      val request = FakeRequest(GET, "/todo")
      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val json = contentAsJson(result)
      json.as[Seq[JsValue]].size must be >= 1
    }
    "return item by id" in {
      val request = FakeRequest(GET, "/todo/2")
      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val json = contentAsJson(result)
      (json \ "description").as[String] mustBe "go swimming"
      (json \ "completed").as[Boolean] mustBe false
    }
  }
  "TodoListController POST /todo" should {
    "create a new item" in {
      val newItemJson = Json.obj(
        "description" -> "Write tests",
        "completed" -> true
      )
      val request = FakeRequest(POST, "/todo")
        .withJsonBody(newItemJson)
      val result = route(app, request).get
      status(result) mustBe CREATED

      val json = contentAsJson(result)
      (json \ "description").as[String] mustBe "Write tests"
      (json \ "completed").as[Boolean] mustBe true
    }
    "create a new item without including completed status" in {
      val newItemJson = Json.obj(
        "description" -> "Write tests",
      )
      val request = FakeRequest(POST, "/todo")
        .withJsonBody(newItemJson)
      val result = route(app, request).get
      status(result) mustBe CREATED

      val json = contentAsJson(result)
      (json \ "description").as[String] mustBe "Write tests"
      (json \ "completed").as[Boolean] mustBe false
    }
  }
    "TodoListController PATCH /todo/:id" should {
    "update an existing item" in {
      val updateJson = Json.obj(
        "completed" -> true
      )
      val request = FakeRequest(PATCH, "/todo/1")
        .withJsonBody(updateJson)
      val result = route(app, request).get
      status(result) mustBe OK
      (contentAsJson(result) \ "completed").as[Boolean] mustBe true
    }
  }
  "TodoListController DELETE /todo/deleteAll" should {
    "delete all items" in {
      val request = FakeRequest(DELETE, "/todo/deleteAll")
      val result = route(app, request).get
      status(result) mustBe NO_CONTENT

      // list should now be empty
      val getRequest = FakeRequest(GET, "/todo")
      val getResponse = route(app, getRequest).get
      status(getResponse) mustBe NO_CONTENT
    }
      "delete item by id" in {
        val request = FakeRequest(DELETE, "/todo/deleteById/1")
        val result = route(app, request).get
        status(result) mustBe NO_CONTENT

        // item should be removed from list
        val getRequest = FakeRequest(GET, "/todo/1")
        val getResponse = route(app, getRequest).get

        status(getResponse) mustBe NOT_FOUND
      }
    }
}