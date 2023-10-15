package fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.service_engineer.CloudMessaging
import com.example.service_engineer.R
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [notificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class notificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val notifDbRef = Firebase.firestore.collection("engineer-notifications")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        val selectedStatus = arguments?.getString("selectedStatus")
        Log.d("notificationselected","$selectedStatus")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sharedPref = activity?.getSharedPreferences("login-data", FirebaseMessagingService.MODE_PRIVATE)

        val loggedInId = sharedPref?.getString("loggedInServiceEngineerId", "") ?: return null

        val fragmentView =  inflater.inflate(R.layout.fragment_notification, container, false)

        val linearLayout = fragmentView.findViewById<LinearLayout>(R.id.notificationLinearLayout) ?: return null

        notifDbRef.whereEqualTo("engineerId", loggedInId).get().addOnSuccessListener {
            Log.d("notifications", "Displaying for $loggedInId, size: ${it.documents.size}")
            it.documents.forEach{ doc ->
                val cardView = layoutInflater.inflate(
                    R.layout.notification_card,
                    linearLayout,
                    false
                )
                val desc = cardView.findViewById<TextView>(R.id.notificationDescription)
                desc.text = doc.getString("description")
                linearLayout.addView(cardView)
            }
        }
        return fragmentView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment notificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            notificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }
}