package com.github.aaabramov.telegram.notify

import com.github.aaabramov.telegram.notify.MyPostgresProfile.api._
import com.tranzzo.util.declarative.Mutation

import scala.concurrent.{ExecutionContext, Future}


trait Tables {

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def userId = column[UserId]("user_id")

    override def * = userId.mapTo[User]
  }

  class GroupsTable(tag: Tag) extends Table[Group](tag, "groups") {
    def userId = column[UserId]("user_id")

    def name = column[String]("name")

    def users = column[List[String]]("users")

    override def * = (userId, name, users).mapTo[Group]
  }

}

class UserRepo(val db: Database)
              (implicit ec: ExecutionContext) extends Tables {

  private val users = TableQuery[UsersTable]

  def findOne(userId: UserId): Future[User] = {
    val action =
      users
        .filter(_.userId === userId)
        .result
        .headOption
        .flatMap {
          case Some(existing) =>
            DBIO.successful(existing)
          case _              =>
            val inserted = User(userId)
            (users += inserted).map(_ => User(userId))
        }

    db run action.transactionally

  }

}

class GroupsRepo(val db: Database)
                (implicit ec: ExecutionContext) extends Tables {

  private val groups = TableQuery[GroupsTable]

  def insert(group: Group): Future[Group] = {
    val action = (groups += group).map(_ => group)

    db run action
  }

  def findOne(userId: UserId, groupName: String): Future[Option[Group]] = {
    val query =
      groups
        .filter(_.userId === userId)
        .filter(_.name === groupName)

    db run query.result.headOption
  }

  def delete(userId: UserId, groupName: String): Future[Option[Group]] = {
    val action =
      groups
        .filter(_.userId === userId)
        .filter(_.name === groupName)
        .result
        .headOption
        .flatMap {
          case Some(found) =>
            groups
              .filter(_.userId === userId)
              .filter(_.name === groupName)
              .delete
              .map(_ => Some(found))
          case _           => DBIO.successful(None)

        }

    db run action
  }

  def findAll(userId: UserId): Future[Seq[Group]] = {
    val query =
      groups
        .filter(_.userId === userId)

    db run query.result
  }

  def update(userId: UserId, groupName: String)(mutation: Mutation[Group]): Future[Group] = {
    val action =
      groups
        .filter(_.userId === userId)
        .filter(_.name === groupName)
        .result
        .head
        .flatMap { existing =>
          val mutated = mutation(existing)
          groups
            .filter(_.userId === userId)
            .filter(_.name === groupName)
            .update(mutated)
            .map(_ => mutated)
        }

    db run action.transactionally

  }

}
