package com.example.mybleapp

import android.util.Log
import kotlinx.coroutines.*
import kotlin.random.Random

class BLEScanner(
    private val onDevicesUpdated: (List<DeviceModel>, Boolean) -> Unit,
    private val onScanStatusChanged: (Boolean) -> Unit,
    private val onMessage: (String) -> Unit  // UI 메시지 전달용 콜백
) {
    private var isScanning = false
    private var scanJob: Job? = null
    private var USE_SIMULATOR_MODE = true

    fun setSimulatorMode(isSimulated: Boolean) {
        USE_SIMULATOR_MODE = isSimulated
    }

    fun startScan() {
        Log.d("BLE_SCAN", "Starting single BLE scan...")
        onMessage("SCANNING\n")

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            val scannedDevices = listOf(
                DeviceModel("Device_X", "AA:BB:CC:DD:EE:FF", -60)
            )
            onDevicesUpdated(scannedDevices, false)
        }
    }

    fun startScanSimul() {
        Log.d("BLE_SCAN", "Starting simulated BLE scan...")
        onMessage("SIMULATED SCANNING\n")

        val possibleDevices = listOf(
            DeviceModel("Mi Band 6", "00:11:22:33:44:55", 0),
            DeviceModel("Apple AirTag", "66:77:88:99:AA:BB", 0),
            DeviceModel("Galaxy Watch 5", "CC:DD:EE:FF:00:11", 0)
        )

        val deviceCount = Random.nextInt(2, 8)
        val simulatedDevices = List(deviceCount) {
            val device = possibleDevices[Random.nextInt(possibleDevices.size)]
            DeviceModel(device.name, device.address, Random.nextInt(-100, -50))
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            onDevicesUpdated(simulatedDevices, false)
        }
    }

    fun startScanLoop() {
        isScanning = true
        onScanStatusChanged(true)
        Log.d("BLE_SCAN", "Starting continuous scan...")

        scanJob = CoroutineScope(Dispatchers.IO).launch {
            while (isScanning) {
                if (USE_SIMULATOR_MODE) {
                    startScanSimul()
                } else {
                    startScan()
                }
                delay(5000)
            }
        }
    }

    fun stopScanLoop() {
        isScanning = false
        onScanStatusChanged(false)
        scanJob?.cancel()
        onMessage("Scan Stop")
        Log.d("BLE_SCAN", "Scan stopped.")
    }
}
