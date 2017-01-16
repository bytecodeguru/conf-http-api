package model

import org.scalatest._

object ModelSpec {
  def newStorage = new InMemoryCrudRepository[String, Config]()
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

  it should "throw EntityConflictException when creating a config with a duplicated id" in {
    val storage = newStorage
    val id = "id"
    a [EntityConflictException] should be thrownBy {
      storage.create(Config(id, "name", "value"))
      storage.create(Config(id, "name", "value"))
    }
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
    a [EntityNotFoundException] should be thrownBy {
      storage.update(Config("id", "name", "value"))
    }
  }

}
