package repositories

import slick.jdbc.MySQLProfile.api._
import models.TodoListItem
// mapping table -> scala object via slick.
class TodoTable(tag: Tag) extends Table[TodoListItem](tag, "todos") { // tells slick this table maps to TodoListItem case class
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def description = column[String]("description")
  def completed = column[Boolean]("completed")
  def * = (id, description, completed) <> (TodoListItem.tupled, TodoListItem.unapply)
}
// <> projection- maps tuple (id, description, completed) to TodoListItem
