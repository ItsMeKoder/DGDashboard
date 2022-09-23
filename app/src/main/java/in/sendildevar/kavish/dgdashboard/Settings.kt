package `in`.sendildevar.kavish.dgdashboard

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@Suppress("NAME_SHADOWING")
class Settings : Fragment(R.layout.settingsfragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.slide_right)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                    R.id.PESButton
                requireActivity().findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.FirstFragment)
            }
            else if (requireActivity().findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated){
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                    R.id.GeneratorButton
                requireActivity().findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.SecondFragment)
            }
        }
        callback.isEnabled = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (requireActivity().findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated) {
                    findNavController().navigate(R.id.SecondFragment)
                }
                else if (requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                    findNavController().navigate(R.id.FirstFragment)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBackPressedCallback
        )
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.settingsfragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPref = requireActivity().getSharedPreferences("in.sendildevar.kavish.DGDashboard.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val defaultValue = false
        val notifications = sharedPref!!.getBoolean("notifications", defaultValue)
        val PES = sharedPref.getBoolean("PES", defaultValue)
        val Generator = sharedPref.getBoolean("Generator", defaultValue)
        var darkMode = sharedPref.getBoolean("darkMode", defaultValue)
        val pesSwtich = requireView().findViewById<CheckBox>(R.id.pesnotificationswitch)
        val generatorSwitch= requireView().findViewById<CheckBox>(R.id.generatornotificationswitch)
        val notificationSwitch = view.findViewById<MaterialSwitch>(R.id.notificationSwitch)
        val darkmodeswitch = view.findViewById<MaterialSwitch>(R.id.darkSwitch)
        var saved=true
        notificationSwitch.isChecked = notifications
        pesSwtich?.isChecked = PES
        generatorSwitch?.isChecked=Generator
        darkmodeswitch?.isChecked=darkMode
        if (!notifications) {
            pesSwtich.isEnabled = false
            generatorSwitch.isEnabled = false
        }
        val discardButton=view.findViewById<MaterialButton>(R.id.discard)
        val saveButton=view.findViewById<MaterialButton>(R.id.save)
        notificationSwitch.setOnClickListener {
            if (notificationSwitch.isChecked) {
                pesSwtich.isEnabled = true
                generatorSwitch.isEnabled = true
                discardButton.isEnabled=true
                saveButton.isEnabled=true
                saved=false
            } else {
                pesSwtich.isEnabled = false
                generatorSwitch.isEnabled = false
                discardButton.isEnabled=true
                saveButton.isEnabled=true
                saved=false
            }
        }
        darkmodeswitch.setOnClickListener {
            darkMode = sharedPref.getBoolean("darkMode", defaultValue)
            if (darkMode != darkmodeswitch.isChecked) {
                discardButton.isEnabled = true
                saveButton.isEnabled = true
                saved = false
            }
            else {
                discardButton.isEnabled = false
                saveButton.isEnabled = false
                saved = false
            }
            if (darkmodeswitch.isChecked) {
                setDefaultNightMode(MODE_NIGHT_YES)
            }
        }
        pesSwtich.setOnClickListener {discardButton.isEnabled=true;saveButton.isEnabled=true;if(pesSwtich.isChecked!=PES) {saved = false}}
        generatorSwitch.setOnClickListener {discardButton.isEnabled=true;saveButton.isEnabled=true;if(generatorSwitch.isChecked!=Generator) {saved = false}}
        val notSavedDialog = MaterialAlertDialogBuilder(requireActivity(), com.google.android.material.R.attr.materialAlertDialogTheme)
            .setTitle("Not Saved!")
            .setMessage("You have not saved the preferences. Would you like to save the preferences?")
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss()}
            .setNegativeButton("No") { _, _ ->
                pesSwtich?.isChecked = PES
                generatorSwitch?.isChecked=Generator
                if (requireActivity().findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated) {
                    findNavController().navigate(R.id.SecondFragment)
                }
                else if (requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                    findNavController().navigate(R.id.FirstFragment)
                }                   }
            .setPositiveButton("Yes") { _, _ ->
                if (pesSwtich.isChecked){
                    Firebase.messaging.subscribeToTopic("PES")
                    with (sharedPref.edit()) {
                        putBoolean("PES", true)
                        apply()
                    }
                } else{Firebase.messaging.unsubscribeFromTopic("PES")
                    with (sharedPref.edit()) {
                        putBoolean("PES", false)
                        apply()
                    }
                }
                if (generatorSwitch.isChecked) {
                    Firebase.messaging.subscribeToTopic("Generator")
                    with (sharedPref.edit()) {
                        putBoolean("Generator", true)
                        apply()
                    }
                } else{
                    Firebase.messaging.unsubscribeFromTopic("Generator")
                    with (sharedPref.edit())
                    { putBoolean("Generator", false); apply()}
                }
                if (requireActivity().findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated) {
                    findNavController().navigate(R.id.SecondFragment)
                }
                else if (requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                    findNavController().navigate(R.id.FirstFragment)
                }
            }
        view.findViewById<FloatingActionButton>(R.id.exit).setOnClickListener {
            if(!saved) {
                notSavedDialog.show()
            }
            else {
                if (requireActivity().findViewById<BottomNavigationItemView>(R.id.GeneratorButton).isActivated) {
                    findNavController().navigate(R.id.SecondFragment)
                }
                else if (requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated) {
                    findNavController().navigate(R.id.FirstFragment)
                }
            }
        }
        discardButton.setOnClickListener {
            val PES = sharedPref.getBoolean("PES", defaultValue)
            val Generator = sharedPref.getBoolean("Generator", defaultValue)
            pesSwtich?.isChecked = PES
            generatorSwitch?.isChecked=Generator
            darkmodeswitch?.isChecked=darkMode
            it.isEnabled=false
            saveButton.isEnabled=false
        }
        saveButton.setOnClickListener()
        {
            discardButton.isEnabled=false
            it.isEnabled=false
            saved=true

            with (sharedPref.edit()) {
                putBoolean("notifications", notificationSwitch.isChecked)
                apply()
            }

            with (sharedPref.edit()) {
                putBoolean("darkMode", darkmodeswitch.isChecked)
                apply()
            }

            Firebase.messaging.subscribeToTopic("PES")
            with (sharedPref.edit()) {
                putBoolean("PES", pesSwtich.isChecked)
                apply()
            }

            Firebase.messaging.subscribeToTopic("Generator")
            with (sharedPref.edit()) {
                putBoolean("Generator", generatorSwitch.isChecked)
                apply()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}