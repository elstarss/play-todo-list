package models

import play.api.libs.json._


//partials allowed, so using Optional as they may not exist
case class TodoUpdate(description: Option[String], completed: Option[Boolean])
object TodoUpdate {
  implicit val format: OFormat[TodoUpdate] = Json.format[TodoUpdate]
}