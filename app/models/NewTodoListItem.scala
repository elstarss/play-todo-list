package models
import play.api.libs.json.{Json, OFormat}

case class NewTodoListItem(description: String, completed: Option[Boolean])

object NewTodoListItem {
  implicit val format: OFormat[NewTodoListItem] = Json.format[NewTodoListItem]
}
