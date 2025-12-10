package models

import play.api.libs.json._



case class TodoListItem(id: Long, description: String, isItDone: Boolean)
