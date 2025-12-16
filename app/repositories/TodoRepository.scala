package repositories

import javax.inject._
import models.{TodoListItem, NewTodoListItem}
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class TodoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val db = dbConfig.db
  import dbConfig.profile.api._

  private val todos = TableQuery[TodoTable]

  def findAll(): Future[Seq[TodoListItem]] =
    db.run(todos.result)

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

  def update(item: TodoListItem): Future[Int] =
    db.run(
      todos.filter(_.id === item.id).update(item)
    )

  def deleteAll(): Future[Int] =
    db.run(todos.delete)

  def deleteById(id: Long): Future[Int] =
    db.run(todos.filter(_.id === id).delete)
}