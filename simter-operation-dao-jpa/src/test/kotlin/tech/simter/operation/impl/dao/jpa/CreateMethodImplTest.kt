package tech.simter.operation.impl.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.operation.core.OperationDao
import tech.simter.operation.impl.dao.jpa.TestHelper.randomOperation
import tech.simter.operation.impl.dao.jpa.TestHelper.randomOperationItem
import tech.simter.operation.impl.dao.jpa.po.OperationPo
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import tech.simter.reactive.test.jpa.TestEntityManager

/**
 * Test [OperationDaoImpl.create].
 *
 * @author zh
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class CreateMethodImplTest @Autowired constructor(
  val rem: TestEntityManager,
  private val dao: OperationDao
) {
  @Test
  fun `success without items`() {
    // do create
    val po = randomOperation()
    dao.create(po).test().verifyComplete()

    // verify created
    assertEquals(po, rem.find(OperationPo::class.java, po.id).get())
  }

  @Test
  fun `success with items`() {
    // do create
    val po = randomOperation().apply {
      addItem(randomOperationItem(id = "field1"))
      addItem(randomOperationItem(id = "field2"))
    }
    dao.create(po).test().verifyComplete()

    // verify created
    assertEquals(po, rem.find(OperationPo::class.java, po.id).get())
  }
}