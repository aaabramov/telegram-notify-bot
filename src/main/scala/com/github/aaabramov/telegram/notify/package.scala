package com.github.aaabramov.telegram

import com.bot4s.telegram.models.Message

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
  * @author Andrii Abramov on 2019-07-25.
  */
package object notify {

  implicit class MessageExt(val self: Message) extends AnyVal {
    def userId: UserId = UserId(self.from.map(_.id).get)
  }

  implicit def f2u[T](f: Future[T])(implicit ec: ExecutionContext): Future[Unit] =
    f.map(_ => ())

}
