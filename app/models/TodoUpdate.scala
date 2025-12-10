package models

import play.api.libs.json._


//partials allowed, so using Optional as they may not exist
case class TodoUpdate(title: Option[String], description: Option[String], completed: Option[Boolean])
Object TodoUpdate {
  implicit val jsonFormat = Json.format[TodoUpdate]
}