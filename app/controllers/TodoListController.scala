package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import repositories.TodoRepository

import scala.collection.mutable
import models.TodoListItem
import models.NewTodoListItem
import models.TodoUpdate

import scala.concurrent.{ExecutionContext, Future}


class TodoListController @Inject()(
                                    val controllerComponents: ControllerComponents,
                                    todoRepository: TodoRepository
                                  )(implicit ec: ExecutionContext) extends BaseController {
  // GET
  def getAll(): Action[AnyContent] = Action.async {
    todoRepository.findAll().map { items =>
      if (items.isEmpty) NoContent
      else Ok(Json.toJson(items))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async { // aync as findById returns a Future
    todoRepository.findById(id).map {
      case Some(item) =>
        Ok(Json.toJson(item))
      case None =>
        NotFound
    }
  }

  //  POST
  def addNewItem(): Action[JsValue] = Action(parse.json).async { implicit request =>
    request.body.validate[NewTodoListItem].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      newItem => todoRepository.insert(newItem).map { created =>
        Created(Json.toJson(created))
      }
    )
  }
// PATCH
  def updateById(itemId: Long): Action[JsValue] =
    Action(parse.json).async { implicit request =>
      request.body.validate[TodoUpdate].fold(
        errors => Future.successful(BadRequest(JsError.toJson(errors))), // just validating for correct json in body

        updateFields =>
          todoRepository.findById(itemId).flatMap { // flatMap waits and returns an Option
            case None =>
              Future.successful(NotFound)
            case Some(oldItem) =>
              val updatedItem = oldItem.copy(
                description = updateFields.description.getOrElse(oldItem.description),
                completed = updateFields.completed.getOrElse(oldItem.completed)
              )
              todoRepository.update(updatedItem).map { _ =>
                Ok(Json.toJson(updatedItem))
              }
          }
      )
    }
}


////  delete
//  def deleteById(itemId: Long): Action[AnyContent] = Action {
//    val itemIndex = todoList.indexWhere(_.id == itemId)
//    if(itemIndex == -1) NotFound
//    else {
//      todoList.remove(itemIndex)
//      NoContent
//    }
//  }
//  def deleteAll(): Action[AnyContent] = Action{
//    if (todoList.isEmpty) {
//      NoContent
//    } else {
//      todoList.clear()
//      NoContent
//    }
//  }
//}