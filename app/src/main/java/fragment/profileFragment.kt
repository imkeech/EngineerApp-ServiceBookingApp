package fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.service_engineer.MainActivity
import com.example.service_engineer.R
import com.google.firebase.messaging.FirebaseMessagingService

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class profileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    // Declare EditText variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()




    }

    private fun logout() {
        val sharedPref = activity?.getSharedPreferences("login-data", Context.MODE_PRIVATE)
        Log.d("logout", "Logging out the user")
        if (sharedPref == null) {
            Log.d("logout", "sharedPref is null")
            return
        }
        val editor = sharedPref.edit()
        editor.putString("loggedInServiceEngineerId", "")
        editor.commit()
        val n = Intent(activity, MainActivity::class.java)
        startActivity(n)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve data from SharedPreferences
        val sharedPrefs = activity?.getSharedPreferences("login-data", Context.MODE_PRIVATE)
        val name = sharedPrefs?.getString("name", "")
        val phonenumber1 = sharedPrefs?.getString("phoneNumber", "")
        Log.d("variable", "Name: $name  PhoneNumber: $phonenumber1")
        // Initialize the EditText variable
        var profileName = fragmentView.findViewById<TextView>(R.id.profileName)
        var phonenumber = fragmentView.findViewById<TextView>(R.id.ph_no)
        profileName.text = name
        phonenumber.text = phonenumber1



        val logoutImage = fragmentView.findViewById<ImageView>(R.id.logout)
        if (logoutImage == null) {
            Log.d("logout", "image is not inflated")
        }
        logoutImage?.setOnClickListener {
            logout()
        }

        return fragmentView
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
