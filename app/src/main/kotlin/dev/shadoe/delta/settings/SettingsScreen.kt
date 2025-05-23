package dev.shadoe.delta.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.SettingsPower
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSecurityType.getResOfSecurityType
import dev.shadoe.delta.api.SoftApSpeedType.getResOfSpeedType
import dev.shadoe.delta.navigation.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  vm: SettingsViewModel = viewModel(),
) {
  val navController = LocalNavController.current
  val focusManager = LocalFocusManager.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val config by vm.config.collectAsState()
  val status by vm.status.collectAsState()

  val passphraseEmptyWarningText =
    stringResource(R.string.passphrase_empty_warning)

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(
        title = { Text(text = stringResource(R.string.settings)) },
        navigationIcon = {
          IconButton(onClick = { navController?.navigateUp() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back_button),
            )
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { scaffoldPadding ->
    LazyColumn(
      modifier =
        Modifier.fillMaxSize()
          .padding(scaffoldPadding)
          .padding(horizontal = 16.dp)
          .pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }
          .then(modifier)
    ) {
      item {
        Text(
          text = stringResource(R.string.settings_desc),
          modifier = Modifier.padding(8.dp),
        )
      }
      item {
        SSIDField(
          ssid = config.ssid ?: "",
          onSSIDChange = { vm.updateSsid(it) },
        )
      }
      item {
        SecurityTypeField(
          securityType = config.securityType,
          supportedSecurityTypes = status.capabilities.supportedSecurityTypes,
          onSecurityTypeChange = { vm.updateSecurityType(it) },
        )
      }
      if (config.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN) {
        item {
          PassphraseField(
            passphrase = config.passphrase,
            onPassphraseChange = { vm.updatePassphrase(it) },
          )
        }
      }
      item {
        AutoShutdownField(
          isAutoShutdownEnabled = config.isAutoShutdownEnabled,
          onAutoShutdownChange = { vm.updateAutoShutdown(it) },
        )
      }
      item {
        SpeedTypeField(
          speedType = config.speedType,
          supportedSpeedTypes = status.capabilities.supportedFrequencyBands,
          onSpeedTypeChange = { vm.updateSpeedType(it) },
        )
      }
      item {
        Button(
          onClick = onClick@{
              if (
                config.passphrase.isEmpty() &&
                  config.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
              ) {
                scope.launch {
                  snackbarHostState.showSnackbar(
                    message = passphraseEmptyWarningText,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short,
                  )
                }
                return@onClick
              }
              vm.commit()
              navController?.navigateUp()
            }
        ) {
          Text(text = stringResource(R.string.save_button))
        }
      }
    }
  }
}

@Composable
private fun SSIDField(ssid: String, onSSIDChange: (String) -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Wifi,
      contentDescription = stringResource(R.string.ssid_field_icon),
    )
    OutlinedTextField(
      value = ssid,
      onValueChange = { onSSIDChange(it) },
      singleLine = true,
      keyboardOptions =
        KeyboardOptions(
          capitalization = KeyboardCapitalization.None,
          autoCorrectEnabled = false,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next,
        ),
      modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
      label = { Text(text = stringResource(R.string.ssid_field_label)) },
    )
  }
}

@Composable
private fun SecurityTypeField(
  securityType: Int,
  supportedSecurityTypes: List<Int>,
  onSecurityTypeChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.WifiPassword,
      contentDescription = stringResource(R.string.security_proto_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.security_proto_field_label),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedSecurityTypes.size) {
          FilterChip(
            selected = securityType == supportedSecurityTypes[it],
            onClick = { onSecurityTypeChange(supportedSecurityTypes[it]) },
            label = {
              Text(
                text =
                  stringResource(
                    getResOfSecurityType(supportedSecurityTypes[it])
                  )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun PassphraseField(
  passphrase: String,
  onPassphraseChange: (String) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Password,
      contentDescription = stringResource(R.string.passphrase_field_icon),
    )
    OutlinedTextField(
      value = passphrase,
      onValueChange = { onPassphraseChange(it) },
      singleLine = true,
      keyboardOptions =
        KeyboardOptions(
          capitalization = KeyboardCapitalization.None,
          autoCorrectEnabled = false,
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done,
        ),
      modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
      label = { Text(text = stringResource(R.string.passphrase_field_label)) },
    )
  }
}

@Composable
private fun AutoShutdownField(
  isAutoShutdownEnabled: Boolean,
  onAutoShutdownChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.SettingsPower,
      contentDescription = stringResource(R.string.auto_shutdown_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.auto_shutdown_field_title),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.auto_shutdown_field_desc),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(
      checked = isAutoShutdownEnabled,
      onCheckedChange = { onAutoShutdownChange(it) },
    )
  }
}

@Composable
private fun SpeedTypeField(
  speedType: Int,
  supportedSpeedTypes: List<Int>,
  onSpeedTypeChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.NetworkWifi,
      contentDescription = stringResource(R.string.freq_band_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.freq_band_field_label),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedSpeedTypes.size) {
          FilterChip(
            selected = speedType == supportedSpeedTypes[it],
            onClick = { onSpeedTypeChange(supportedSpeedTypes[it]) },
            label = {
              Text(
                text =
                  stringResource(getResOfSpeedType(supportedSpeedTypes[it]))
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}
