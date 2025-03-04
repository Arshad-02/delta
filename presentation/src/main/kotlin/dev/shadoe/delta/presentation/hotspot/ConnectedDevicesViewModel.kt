package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ConnectedDevicesViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val connectedClients =
    softApRepository.status.mapLatest { it.tetheredClients }

  fun blockDevice(device: ACLDevice) {
    softApRepository.apply {
      val d = config.value.blockedDevices
      config.value = config.value.copy(blockedDevices = d + device)
    }
  }
}
