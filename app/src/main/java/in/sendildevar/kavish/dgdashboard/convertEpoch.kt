package `in`.sendildevar.kavish.dgdashboard

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun convertEpoch(s: String): String?{
    val sdf = SimpleDateFormat("hh:mm:ss MM/dd/yyyy")
    val netDate = Date(s.toLong() * 1000)
    return sdf.format(netDate)
}
fun convertMillis(s: String): String? {
    val sdf = SimpleDateFormat("hh:mm:ss MM/dd/yyyy")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = s.toLong()
    return sdf.format(calendar.time)
}