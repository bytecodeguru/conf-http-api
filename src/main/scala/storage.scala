package storage

import collection.JavaConverters._

trait Entity[I] {
  def getId: I
}

class DuplicatedIdException extends Exception

class EntityNotFoundException extends Exception

trait Storage[I, E <: Entity[I]] {
  @throws[DuplicatedIdException]
  def create(entity: E): Unit
  @throws[EntityNotFoundException]
  def update(entity: E): Unit
  def delete(id: I): Unit
  def read(id: I): Option[E]
  def getAll: List[E]
}

class InMemoryStorage[I, E <: Entity[I]] extends Storage[I, E] {
  private val entityMap: java.util.Map[I, E] = new java.util.concurrent.ConcurrentSkipListMap()

  override def create(entity: E): Unit = {
    if (entityMap.putIfAbsent(entity.getId, entity) != null) {
      throw new DuplicatedIdException()
    }
  }

  override def update(entity: E): Unit = {
    if (entityMap.replace(entity.getId, entity) == null) {
      throw new EntityNotFoundException()
    }
  }

  override def delete(id: I): Unit = entityMap.remove(id)

  override def read(id: I): Option[E] = Option(entityMap.get(id))

  override def getAll: List[E] = entityMap.values().asScala.toList
}
