package com.github.aaabramov.telegram.notify

import com.bot4s.telegram.api.declarative.{Commands, Declarative}
import com.bot4s.telegram.api.{AkkaTelegramBot, ChatActions, Webhook}

import scala.concurrent.Future

/**
  * @author Andrii Abramov on 2019-07-25.
  */
trait BaseBot
  extends AkkaTelegramBot
  with Webhook
  with Declarative[Future]
  with Commands[Future]
  with ChatActions[Future] {

}
