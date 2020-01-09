package tech.simter.operation.impl.dao.mongo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.operation.core.OperationDao
import tech.simter.operation.impl.dao.mongo.TestHelper.randomOperation
import tech.simter.util.RandomUtils.randomString
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

/**
 * Test [OperationDaoImpl.findByTarget]
 *
 * @author zh
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataMongoTest
class FindByTargetMethodImplTest @Autowired constructor(
  private val repository: OperationReactiveRepository,
  private val dao: OperationDao
) {
  @Test
  fun `find something`() {
    // init data
    val targetType = randomString()
    val targetId = randomString()
    val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
    val operation1 = randomOperation(targetType = targetType, targetId = targetId, ts = now) // without items
    val operation2 = randomOperation(targetType = targetType, targetId = targetId, ts = now.plusHours(1),
      items = setOf(TestHelper.randomOperationItem(id = "field1"), TestHelper.randomOperationItem(id = "field2"))
    ) // with items

    val operation3 = randomOperation(targetType = targetType, targetId = randomString()) // another targetId
    val operation4 = randomOperation(targetType = randomString(), targetId = targetId) // another targetType
    val operation5 = randomOperation(targetType = randomString(), targetId = randomString()) // another target
    repository
      .saveAll(listOf(operation1, operation2, operation3, operation4, operation5))
      .then().test().verifyComplete()

    // invoke and verify
    dao.findByTarget(targetType, targetId).test().expectNext(operation2).expectNext(operation1).verifyComplete()
  }

  @Test
  fun `find nothing 1`() {
    val targetType = randomString()
    val targetId = randomString()
    dao.findByTarget(targetType, targetId).test().verifyComplete()
  }

  @Test
  fun `find nothing 2`() {
    // init data
    repository
      .saveAll(listOf(randomOperation(batch = randomString()), randomOperation()))
      .then().test().verifyComplete()

    // invoke and verify
    val targetType = randomString()
    val targetId = randomString()
    dao.findByTarget(targetType, targetId).test().verifyComplete()
  }
}