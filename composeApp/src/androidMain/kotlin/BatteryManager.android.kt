import android.content.Context

actual class BatteryManager(private val context: Context) {
    actual fun getBatteryLevel(): Int {
        return 10
    }
}