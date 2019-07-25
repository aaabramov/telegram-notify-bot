package com.github.aaabramov.telegram.notify

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.models.Message
import com.typesafe.config.Config

import scala.concurrent.Future


class NotifyBot(
                 val client: RequestHandler[Future],
                 val config: Config,
                 val usersRepo: UserRepo,
                 val groupsRepo: GroupsRepo
               ) extends BaseBot {

  override val webhookUrl: String = config.getString("services.telegram.webhookUrl")
  override val port: Int = config.getInt("services.telegram.port")

  onCommand('create) { implicit msg =>
    withArgs { args =>

      if (args.isEmpty) {
        howto()
      }
      else if (args.length < 2) {
        howto()
      }
      else {

        val groupName = args.head
        val users = args.tail
        val user = msg.userId

        groupsRepo
          .findOne(user, groupName)
          .flatMap {
            case Some(_) =>
              reply("Group with such name already exists. Try to choose new one.")
            case _       =>
              usersRepo
                .findOne(user)
                .flatMap { u =>
                  groupsRepo.insert(Group(u.id, groupName, users.toList))
                }
                .flatMap { group =>
                  reply(s"Awesome! I have created group named $groupName with the following members: ${users.mkString(" ")}")
                }
          }
      }

    }
  }

  onCommand('mygroups) { implicit msg =>
    groupsRepo
      .findAll(msg.userId)
      .flatMap { userGroups =>
        if (userGroups.isEmpty) {
          reply {
            s"""|Currently you have no group.
                |In order to create one use the following command:
                |/create group_name @username1 @username2""".stripMargin
          }
        } else {
          replyMd {
            s"""|You have the following groups:
                |${userGroups.map(_.info).map("- " + _).mkString("\n")}""".stripMargin
          }
        }
      }
  }

  onCommand('delete) { implicit msg =>
    withArgs { args =>
      if (args.size != 1) {
        reply {
          """|In order to delete groups use the following command:
             |/delete group_name""".stripMargin
        }
      } else {
        val groupName = args.head
        groupsRepo
          .delete(msg.userId, groupName)
          .flatMap {
            case Some(_) =>
              reply {
                s"""Okay, I have deleted $groupName group.""".stripMargin
              }
            case _       =>
              reply {
                s"""|You have no group named $groupName.
                    |Use /mygroups to list your groupds""".stripMargin
              }
          }
      }
    }
  }

  onCommand('members) { implicit msg =>
    withArgs { args =>
      if (args.size != 1) {
        reply {
          """|In order to list group members use the following command:
             |/members group_name""".stripMargin
        }
      } else {
        val groupName = args.head
        groupsRepo
          .findOne(msg.userId, groupName)
          .flatMap {
            case Some(group) if group.users.isEmpty =>
              reply {
                s"""|Well, this group seems to be empty. This may happen if you have removed all users from group.
                    |You can delete this group using the following command:
                    |
                    |/delete $groupName""".stripMargin
              }
            case Some(group)                        =>
              reply {
                s"""|Here are $groupName members:
                    |${group.users.map("- " + _).mkString("\n")}""".stripMargin
              }
            case _                                  =>
              reply {
                s"""|You have no group named $groupName.
                    |Use /mygroups to list your groups""".stripMargin
              }
          }
      }
    }
  }

  onCommand('add) { implicit msg =>
    withArgs { args =>
      if (args.size < 2) {
        replyMd {
          """|To add a member to existing groups use the following command:
             |/add group_name @username1""".stripMargin
        }
      } else {
        val groupName = args.head
        val userToAdd = args(1)

        if (!userToAdd.startsWith("@")) {
          replyMd {
            """|To add a member to existing groups use the following command:
               |/add group_name @username1""".stripMargin
          }
        } else {
          groupsRepo
            .findOne(msg.userId, groupName)
            .flatMap {
              case Some(group) if group.users.contains(userToAdd) =>
                replyMd {
                  s"""User $userToAdd is already in group $groupName""".stripMargin
                }
              case Some(_)                                        =>

                groupsRepo
                  .update(msg.userId, groupName) { group =>
                    group.copy(users = userToAdd :: group.users)
                  }
                  .flatMap { _ =>
                    replyMd {
                      s"""Done! I have added $userToAdd to group $groupName""".stripMargin
                    }
                  }

              case _ =>
                replyMd {
                  s"""|You have no group named $groupName.
                      |In order to create one use the following command:
                      |/create $groupName @username1 @username2""".stripMargin
                }
            }
        }

      }
    }
  }

  private def howto()(implicit msg: Message) = {
    reply("To create a new notify group use /create group_name @username1 @username2")
  }

  onCommand('help) { implicit msg =>
    howto()
  }

  onUpdate { implicit upd =>
    logger.debug(upd.toString)
    Future.unit
  }

}
