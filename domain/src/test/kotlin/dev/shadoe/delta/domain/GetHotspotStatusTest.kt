package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class GetHotspotStatusTest {
  @Test
  fun `Retrieve status`() {
    val statusStub = Stubs.getSoftApStatus()
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn MutableStateFlow(statusStub)
      }
    val getHotspotStatus = GetHotspotStatus(softApRepository)
    val result = getHotspotStatus().value
    assertEquals(statusStub, result)
  }
}
