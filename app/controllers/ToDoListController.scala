package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.collection.mutable
import models.TodoListItem
import models.NewTodoListItem
import models.TodoUpdate

@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val todoList = new mutable.ListBuffer[TodoListItem]()
  implicit val todoListJson = Json.format[TodoListItem]
  implicit val newTodoListJson = Json.format[NewTodoListItem]
//  default items on list
  todoList += TodoListItem(1, "clean dishes", true)
  todoList += TodoListItem(2, "go swimming", false)
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
//      .validate tries to turn incoming json into a TodoUpdate object -> JsResult[TodoUpdate], either a success or error
      val update: JsResult[TodoUpdate] = request.body.validate[TodoUpdate]


//      at this point update is either a jsSuccess- update object or JsError - errors
//      .fold lets us handle either of these outcomes in a similar way to match. fold is often used with json validation as it is binary pass fail
    update.fold(
      errors => BadRequest(JsError.toJson(errors)), // 400 bad request
      updateFields => {
        todoList.find(_.id == itemId) match {
        case None => NotFound
        case Some(oldItem) =>
//          merge old and new fields
          val updatedItem = oldItem.copy(
            description = updateFields.description.getOrElse(oldItem.description),
            completed = updateFields.completed.getOrElse(oldItem.completed)
          )
//          replace item inside ListBuffer entire list
          val itemIndex = todoList.indexWhere(_.id == itemId)
          todoList.update(itemIndex, updatedItem)
          Ok(Json.toJson(updatedItem))
      }
        }
    )

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