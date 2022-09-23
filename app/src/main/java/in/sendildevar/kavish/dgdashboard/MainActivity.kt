package `in`.sendildevar.kavish.dgdashboard

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import `in`.sendildevar.kavish.dgdashboard.databinding.ActivityMainBinding

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * Main FireBase Code Start
     */
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        auth.currentUser
        auth.signInWithEmailAndPassword("", "")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /**
     * Main FireBase Code End
     */

    @SuppressLint("CutPasteId", "UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        auth.currentUser
        auth.signInWithEmailAndPassword("", "")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
//        if(getSharedPreferences("in.sendildevar.kavish.DGDashboard.PREFERENCE_FILE_KEY", MODE_PRIVATE).getBoolean("darkMode",false)){
//            setDefaultNightMode(MODE_NIGHT_YES)
//        }else{
//            setDefaultNightMode(MODE_NIGHT_NO)
//        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        val sharedPref = getSharedPreferences(
            "in.sendildevar.kavish.DGDashboard.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
        val defaultValue = false

        val bottomNavigation = findViewById<NavigationBarView>(R.id.bottom_navigation)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            task.result
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = "PESchannel"
            val channelName = "DG turned on/off notification for PES"
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channelId2 = "DGchannel"
            val channelName2 = "DG turned on/off notification for Generator"
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH
                )
            )
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId2,
                    channelName2, NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

//        intent.extras?.let {
//            for (key in it.keySet()) {
//                val value = intent.extras?.get(key)
//                //Log.d(TAG, "Key: $key Value: $value")
//            }
//        }
        bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.PESButton -> {
                    findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated = true
                    if (findViewById<ActionMenuItemView>(R.id.action_settings).isActivated) {
                        findViewById<ActionMenuItemView>(R.id.action_settings).isActivated = false
                        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
                    }
                }

                R.id.GeneratorButton -> {
                    findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated = true
                    if (findViewById<ActionMenuItemView>(R.id.action_settings).isActivated) {
                        findViewById<ActionMenuItemView>(R.id.action_settings).isActivated = false
                        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.SecondFragment)
                    }
                }
            }
            findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated = true
            findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated = false
        }
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                android.R.id.home -> {
                    //Log.v("DEBUG", "HOME PRESSED")
                    findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId=R.id.PESButton
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
                true
                }

                R.id.PESButton -> {
                    findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated = true
                    if (findViewById<ActionMenuItemView>(R.id.action_settings).isActivated) {
                        if (findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                            findViewById<ActionMenuItemView>(R.id.action_settings).isActivated = false
                            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
                        }
                    } else {
                        findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated = false
                        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
                    }
                    true
                }

                R.id.GeneratorButton -> {
                    findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated = true
                    if (findViewById<ActionMenuItemView>(R.id.action_settings).isActivated) {
                        if (findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated) {
                            findViewById<ActionMenuItemView>(R.id.action_settings).isActivated = false
                            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.SecondFragment)
                            //Log.d(TAG,"NAVIGATING TO SECOND FRAGMENT")
                        }
                    } else {
                        findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated = false
                        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.SecondFragment)
                        //Log.d(TAG,"NAVIGATING TO SECOND FRAGMENT")

                    }
                    true
                }

                else -> false
            }
        }

        auth = Firebase.auth
        val firstTimeDone = sharedPref!!.getBoolean("FirstTimeDone", defaultValue)
        if (!firstTimeDone) {
            MaterialAlertDialogBuilder(
                this@MainActivity,
                    com.google.android.material.R.attr.materialAlertDialogTheme
            )
                .setTitle("Are you are resident of SARE Crescent ParC - Phase 3")
                .setMessage("This app is only for sole use by the residents of SARE Crescent ParC - Phase 3 only.")
                .setNegativeButton("No") { _, _ ->
                    finish()
                }
                .setPositiveButton("Yes") { _, _ ->
                    with(sharedPref.edit()) {
                        putBoolean("FirstTimeDone", true)
                        apply()
                    }
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.settingsfragment)
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.settingsfragment)
                findViewById<ActionMenuItemView>(R.id.action_settings).isActivated = true
                return true
            }

            R.id.action_login -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.loginActivity)
                findViewById<ActionMenuItemView>(R.id.action_login).isActivated = true
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
