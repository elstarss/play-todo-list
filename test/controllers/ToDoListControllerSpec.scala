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
  "TodoListController GET /todo" should {
    "return all tasks when they exist" in {
      route(app,
        FakeRequest(POST, "/todo")
          .withJsonBody(Json.parse("""{"description":"one"}"""))
      )
      route(app,
        FakeRequest(POST, "/todo")
          .withJsonBody(Json.parse("""{"description":"two"}"""))
      )
      val result = route(app, FakeRequest(GET, "/todo")).get
      status(result) mustBe OK
      val json = contentAsJson(result)
      json.as[Seq[JsValue]].size mustBe 2
    }
    "return 204 no content when no todos exist" in {
      val request = FakeRequest(GET, "/todo")
      val result = route(app, request).get
      status(result) mustBe NO_CONTENT
    }
  }

  "TodoListController POST /todo" should {
  "create a new todo item and return 201" in {
    val request = FakeRequest(POST, "/todo")
      .withJsonBody(Json.parse("""{"description":"learn play"}"""))
    val result = route(app, request).get
    status(result) mustBe CREATED
    val json = contentAsJson(result)
    (json \ "id").as[Long] must be > 0L
    (json \ "description").as[String] mustBe "learn play"
    (json \ "completed").as[Boolean] mustBe false
  }
    "return 400 for empty JSON" in {
      val request = FakeRequest(POST, "/todo")
        .withJsonBody(Json.parse("""{}"""))
      val result = route(app, request).get
      status(result) mustBe BAD_REQUEST
    }
    "return 400 for invalid JSON" in {
      val request = FakeRequest(POST, "/todo")
        .withJsonBody(Json.parse("""{"title":"new task"}"""))
      val result = route(app, request).get
      status(result) mustBe BAD_REQUEST
    }
}

  "TodoListController PATCH /todo" should {
    "update only the completed field" in {
      val postResult = route(app, FakeRequest(POST, "/todo")
      .withJsonBody(Json.parse("""{"description}":"learn patch"""))
      ).get
      val id = (contentAsJson(postResult) \ "id").as[Long]
//      just patching the completed field
      val patchResult = route(app,
        FakeRequest(PATCH, s"/todo/$id")
      .withJsonBody(Json.parse("""{"completed":"true"""))
      ).get
      status(patchResult) mustBe OK
      val json = contentAsJson(patchResult)
      (json \ "description").as[String] mustBe "learn patch"
      (json \ "completed").as[Boolean] mustBe true
    }
    "return 404 when updating an absent todo" in {
      val patchResult = route(app,
        FakeRequest(PATCH, "/todo/9999")
          .withJsonBody(Json.parse("""{"completed": true}"""))
      ).get
      status(patchResult) mustBe NOT_FOUND
    }
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