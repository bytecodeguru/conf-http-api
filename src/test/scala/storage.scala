package storage

import org.scalatest._
import storage._
import controller._

object ModelSpec {
  def newStorage = new InMemoryStorage[Config]()
}

class ModelSpec extends FlatSpec with Matchers {
  import ModelSpec._

  "A storage" should "be empty" in {
    val storage = newStorage
    storage.getAll.isEmpty should be (true)
  }

  it should "contain the specified config after creating" in {
    val storage = newStorage
    val id = "id"
    storage.create(Config(id, "name", "value"))
    storage.read(id) should be (Some(Config(id, "name", "value")))
  }

  it should "return a Left when creating a config with a duplicated id" in {
    val storage = newStorage
    val id = "id"
    storage.create(Config(id, "name", "value")).isRight should be (true)
    val e = storage.create(Config(id, "name", "value"))
    e.isLeft should be (true)
    e.left.get should be (DuplicatedId)
  }

  it should "be empty after creating and deleting a config" in {
    val storage = newStorage
    val id = "id"
    storage.create(Config(id, "name", "value"))
    storage.delete(id)
    storage.getAll.isEmpty should be (true)
  }

  it should "be empty after deleting a non existing config" in {
    val storage = newStorage
    storage.delete("id")
    storage.getAll.isEmpty should be (true)
  }

  it should "throw EntityNotFoundException if a non existing config is updated" in {
    val storage = newStorage
    val e = storage.update(Config("id", "name", "value"))
    e.isLeft should be (true)
    e.left.get should be (NotFound)
  }

}
