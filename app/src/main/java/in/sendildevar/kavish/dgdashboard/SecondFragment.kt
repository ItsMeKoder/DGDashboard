package `in`.sendildevar.kavish.dgdashboard

import android.content.ContentValues
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import `in`.sendildevar.kavish.dgdashboard.databinding.FragmentSecondBinding


class SecondFragment : Fragment(R.layout.fragment_second) {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var auth: FirebaseAuth
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var loggedIn=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.slide_right)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated)
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId=R.id.PESButton
                requireActivity().findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
        }
//        if(requireActivity().getSharedPreferences("in.sendildevar.kavish.DGDashboard.PREFERENCE_FILE_KEY",
//                AppCompatActivity.MODE_PRIVATE
//            ).getBoolean("darkMode",false)){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        callback.isEnabled = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
              return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * Main FireBase Code Start
         */
        val imagegenerator = binding.GeneratorImage

        val database = Firebase.database("https://sare.asia-southeast1.firebasedatabase.app/").reference
        val database_generator = database.child("/SAREPH3/dg1/live/status")
        val database_generator_status = database_generator.child("/DG")
        val postListener_generator = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d("dgdasboard.fb",dataSnapshot.value.toString())
                if (dataSnapshot.value.toString()=="true"){
                    //Log.d("dgdasboard.fb","[generator] DG on")
                    imagegenerator.setImageResource(R.drawable.on)
                }
                else if (dataSnapshot.value.toString()=="false"){
                    //Log.d("dgdasboard.fb","[generator] DG off")
                    imagegenerator.setImageResource(R.drawable.off)
                }
                if (loggedIn){
                    val ot = database_generator.child("/lastOFFtime").get()
                    val Ot = database_generator.child("/lastONtime").get()
                    val ut = database_generator.child("/updatedtime").get()

                    while (!(ot.isComplete and Ot.isComplete and ut.isComplete)){}
                    val ots=convertEpoch(ot.result.value.toString())
                    val Ots= convertEpoch(Ot.result.value.toString())
                    val uts= convertMillis(ut.result.value.toString())
                    var text: String = if (dataSnapshot.value.toString() == "True") {
                        "On since $Ots.\nUpdated - $uts"
                    } else {
                        "Last turned on - $Ots\nTurned off - $ots. \nUpdated - $uts"
                    }
                    binding.GeneratorDetails.text=text
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database_generator_status.addValueEventListener(postListener_generator)
        /**
         * Main FireBase Code End
         */
        super.onViewCreated(view, savedInstanceState)
    }

}