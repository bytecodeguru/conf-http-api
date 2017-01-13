package model

import collection.JavaConverters._

trait Entity[I] {
  def getId: I
}

trait Configuration extends Entity[String]

// id duplicato
class EntityConflictException extends Exception

class EntityNotFoundException extends Exception

trait CrudRepository[I, E <: Entity[I]] {
  @throws[EntityConflictException]
  def create(entity: E): Unit
  @throws[EntityNotFoundException]
  def update(entity: E): Unit
  def delete(id: I): Unit
  def read(id: I): Option[E]
  def getAll: List[E]
}

class InMemoryCrudRepository[I, E <: Entity[I]] extends CrudRepository[I, E] {
  private val entityMap: java.util.Map[I, E] = new java.util.concurrent.ConcurrentSkipListMap()

  override def create(entity: E): Unit = {
    if (entityMap.putIfAbsent(entity.getId, entity) != null) {
      throw new EntityConflictException()
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
