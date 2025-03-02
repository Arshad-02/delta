package dev.shadoe.delta.domain

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.api.SoftApStatus

object Stubs {
  fun getSoftApConfiguration() =
    SoftApConfiguration(
      ssid = "wifi",
      passphrase = "passphrase123",
      securityType = SoftApSecurityType.SECURITY_TYPE_WPA2_PSK,
      bssid = null,
      isHidden = false,
      speedType = SoftApSpeedType.BAND_5GHZ,
      blockedDevices =
        listOf(
          ACLDevice(
            hostname = "device 1",
            macAddress = MacAddress("ff:aa:ff:bb:ff:cc"),
          ),
          ACLDevice(
            hostname = null,
            macAddress = MacAddress("ff:aa:ff:bb:ff:dd"),
          ),
          ACLDevice(
            hostname = "device 3",
            macAddress = MacAddress("ff:aa:ff:bb:ff:ee"),
          ),
        ),
      allowedClients = emptyList(),
      isAutoShutdownEnabled = true,
      maxClientLimit = 32,
    )

  fun getSoftApStatus() =
    SoftApStatus(
      enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED,
      tetheredClients = emptyList(),
      supportedSpeedTypes = emptyList(),
      maxSupportedClients = 0,
    )
}
