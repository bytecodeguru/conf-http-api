package controller

import storage._

case class Config(id: String, name: String, value: String) extends Entity[String] {
  override def getId: String = id
}

sealed trait DeleteError
final case object DeleteNotFound extends DeleteError

sealed trait UpdateError
final case object UpdateNotFound extends UpdateError
final case object IdConflict extends UpdateError

object Controller {
  private val storage = new InMemoryStorage[String, Config]()

  def getAll: List[Config] = storage.getAll

  def create(c: Config): Either[CreateError, Config] = storage.create(c)

  def read(id: String): Option[Config] = storage.read(id)

  def delete(id: String): Either[DeleteError, Unit] = {
    storage.read(id) match {
      case Some(_) => Right(storage.delete(id))
      case None    => Left(DeleteNotFound)
    }
  }

  def update(id: String, c: Config): Either[UpdateError, Unit] = {
    if (c.id == id) {
      storage.update(c) match {
        case Left(_) => Left(UpdateNotFound)
        case Right(_) => Right(())
      }
    } else {
      Controller.read(id) match {
        case None    => Left(UpdateNotFound)
        case Some(c) => Left(IdConflict)
      }
    }
  }
}
