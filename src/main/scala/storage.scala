package storage

import collection.JavaConverters._

trait Entity[I] {
  def getId: I
}

sealed trait CreateError
final case object DuplicatedId extends CreateError

sealed trait UpdateError
final case object NotFound extends UpdateError

trait Storage[I, E <: Entity[I]] {
  def create(entity: E): Either[CreateError, E]
  def update(entity: E): Either[UpdateError, Unit]
  def delete(id: I): Unit
  def read(id: I): Option[E]
  def getAll: List[E]
}

class InMemoryStorage[I, E <: Entity[I]] extends Storage[I, E] {
  private val entityMap: java.util.Map[I, E] = new java.util.concurrent.ConcurrentSkipListMap()

  override def create(e: E): Either[CreateError, E] = {
    entityMap.putIfAbsent(e.getId, e) match {
      case null => Right(e)
      case _    => Left(DuplicatedId)
    }
  }

  override def update(e: E): Either[UpdateError, Unit] = {
    entityMap.replace(e.getId, e) match {
      case null => Left(NotFound)
      case _    => Right(())
    }
  }

  override def delete(id: I): Unit = entityMap.remove(id)

  override def read(id: I): Option[E] = Option(entityMap.get(id))

  override def getAll: List[E] = entityMap.values().asScala.toList
}
