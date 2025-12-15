package repositories

import javax.inject._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import models.{TodoListItem, NewTodoListItem}

@Singleton
class TodoRepository @Inject()(db: Database)(implicit ec: ExecutionContext) {
  private val todos = TableQuery[TodoTable] // give query object to interact with the table

  def findAll(): Future[Seq[TodoListItem]] = db.run(todos.result)  // runs slick query asynchronously, returns a Future

  def findById(id: Long): Future[Option[TodoListItem]] =
    db.run(todos.filter(_.id === id).result.headOption)

  def insert(newItem: NewTodoListItem): Future[TodoListItem] = {
    val completed = newItem.completed.getOrElse(false)
    val insertQuery =
      (todos returning todos.map(_.id)
        into ((todo, id) => todo.copy(id = id))
        )
    db.run(insertQuery += TodoListItem(0, newItem.description, completed))
  }

  def deleteAll(): Future[Int] = db.run(todos.delete) // returns a Future for aync- all DB calls are async in play
}
