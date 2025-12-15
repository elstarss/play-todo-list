package models

import play.api.libs.json.{Json, OFormat}

case class TodoListItem(
                         id: Long,
                         description: String,
                         completed: Boolean
                       )
object TodoListItem {
  implicit val todoListItemFormat: OFormat[TodoListItem] = Json.format[TodoListItem]
}
// implicit JSON formatter required for the object