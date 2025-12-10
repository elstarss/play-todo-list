package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.collection.mutable
import models.TodoListItem
import models.NewTodoListItem

@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val todoList = new mutable.ListBuffer[TodoListItem]()
  implicit val todoListJson = Json.format[TodoListItem]
  implicit val newTodoListJson = Json.format[NewTodoListItem]
  todoList += TodoListItem(1, "test", true)
  todoList += TodoListItem(2, "some other value", false)
//  get
  def getAll(): Action[AnyContent] = Action {
    if (todoList.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(todoList)) // 200 ok
    }
  }
  def getById(itemId: Long): Action[AnyContent] = Action {
    val foundItem = todoList.find(_.id == itemId)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None       => NotFound // 404 not found
    }
  }
//  post
  def addNewItem() = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val todoListItem: Option[NewTodoListItem] =
      jsonObject.flatMap(
        Json.fromJson[NewTodoListItem](_).asOpt
      )

    todoListItem match {
      case Some(newItem) =>
        val nextId = todoList.map(_.id).max + 1
        val toBeAdded = TodoListItem(nextId, newItem.description, false)
        todoList += toBeAdded
        Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
  }
//  patch
//  Action[JsValue] is a request handler that expects JSON in the body
  def updateById(itemId: Long): Action[JsValue] =
//  if the body is not raw json will produce a 400 response
    Action(parse.json) { request =>
    val update = request.body.validate[TodoUpdate]

    update.fold()

  }
//  delete
  def deleteById(itemId: Long): Action[AnyContent] = Action {
    val itemIndex = todoList.indexWhere(_.id == itemId)
    if(itemIndex == -1) NotFound
    else {
      todoList.remove(itemIndex)
      NoContent
    }
  }
  def deleteAll(): Action[AnyContent] = Action{
    if (todoList.isEmpty) {
      NoContent
    } else {
      todoList.clear()
      NoContent
    }
  }
}