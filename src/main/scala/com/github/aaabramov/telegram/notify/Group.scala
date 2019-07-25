package com.github.aaabramov.telegram.notify

case class Group(userId: UserId, name: String, users: List[String]) {

  def info: String =
    users.size match {
      case 0    => s"`$name` _(1 member)_"
      case more => s"`$name` _($more members)_"
    }

}
