package com.github.aaabramov.telegram.notify

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport}

/**
  * @author Andrii Abramov on 2019-07-25.
  */
trait MyPostgresProfile extends ExPostgresProfile
                        with PgArraySupport {

  override val api = MyAPI

  object MyAPI extends API
               with ArrayImplicits {

    implicit val userIdMapper: BaseColumnType[UserId] = MappedColumnType.base[UserId, Long](
      _.value, UserId
    )

  }

}

object MyPostgresProfile extends MyPostgresProfile
