package `in`.sendildevar.kavish.dgdashboard

import android.app.Application
import com.google.android.material.color.DynamicColors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DGDashboardApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
//    val executorService: ExecutorService = Executors.newFixedThreadPool(4)

}