package `in`.sendildevar.kavish.dgdashboard

import android.content.ContentValues
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import `in`.sendildevar.kavish.dgdashboard.databinding.FragmentFirstBinding


class FirstFragment : Fragment(R.layout.fragment_first) {

    private var _binding: FragmentFirstBinding? = null
    private var loggedIn = true
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!requireActivity().findViewById<BottomNavigationItemView>(R.id.PESButton).isActivated)
            requireActivity().findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.FirstFragment)
        }
        callback.isEnabled = true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * Main FireBase Code Start
         */

        val imagepes = binding.PESImage
        val database =
            Firebase.database("https://sare.asia-southeast1.firebasedatabase.app/").reference
        val database_pes = database.child("/SAREPH3/devel/live/status")
        val database_pes_status = database_pes.child("/DG")
        val postListener_pes = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d("dgdasboard.fb", dataSnapshot.value.toString())
                if (dataSnapshot.value.toString() == "true") {
                    //Log.d("dgdasboard.fb", "[PES] DG on")
                    imagepes.setImageResource(R.drawable.on)
                } else if (dataSnapshot.value.toString() == "false") {
                    //Log.d("dgdasboard.fb", "[PES] DG off")
                    imagepes.setImageResource(R.drawable.off)
                }
                if (loggedIn) {
                    val ot = database_pes.child("/lastOFFtime").get()
                    val Ot = database_pes.child("/lastONtime").get()
                    val ut = database_pes.child("/updatedtime").get()

                    while (!(ot.isComplete and Ot.isComplete and ut.isComplete)) {
                    }
                    val ots = convertEpoch(ot.result.value.toString())
                    val Ots = convertEpoch(Ot.result.value.toString())
                    val uts = convertMillis(ut.result.value.toString())
                    var text: String = if (dataSnapshot.value.toString() == "True") {
                        "On since $Ots.\nUpdated - $uts"
                    } else {
                        "Last turned on - $Ots\nTurned off - $ots. \nUpdated - $uts"
                    }
                    binding.PESDetails.text = text

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        database_pes_status.addValueEventListener(postListener_pes)


        /**
         * Main FireBase Code End
         */
        super.onViewCreated(view, savedInstanceState)
    }
}