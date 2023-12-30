package fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.service_engineer.MainActivity
import com.example.service_engineer.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class profileFragment : Fragment() {

    private lateinit var infoButton: Button
    private lateinit var infoCard: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        infoButton = fragmentView.findViewById(R.id.personalInfoButton)
        infoCard = fragmentView.findViewById(R.id.infoCard)
        val name1 = fragmentView.findViewById<TextView>(R.id.Name)
        val Editname = fragmentView.findViewById<EditText>(R.id.editName)
        val edit =  fragmentView.findViewById<ImageView>(R.id.edit)
        val Editphone_no = fragmentView.findViewById<EditText>(R.id.editphone_no)
        val phone_no =  fragmentView.findViewById<TextView>(R.id.phone_no)
        val Editemail = fragmentView.findViewById<EditText>(R.id.editemail)
        val email =  fragmentView.findViewById<TextView>(R.id.email)
        val Editaddress = fragmentView.findViewById<EditText>(R.id.editaddress)
        val Address =  fragmentView.findViewById<TextView>(R.id.Address)
        val Editcity = fragmentView.findViewById<EditText>(R.id.editcity)
        val City =  fragmentView.findViewById<TextView>(R.id.city)
        val Editstate = fragmentView.findViewById<EditText>(R.id.editstate)
        val State =  fragmentView.findViewById<TextView>(R.id.state)
        val savebutton =  fragmentView.findViewById<Button>(R.id.saveButton)



        val sharedPrefs = activity?.getSharedPreferences("login-data", Context.MODE_PRIVATE)
        val name = sharedPrefs?.getString("name", "")
        val phonenumber1 = sharedPrefs?.getString("phoneNumber", "")
        Log.d("variable", "Name: $name  PhoneNumber: $phonenumber1")




        val serviceEngineerCollection = FirebaseFirestore.getInstance().collection("ServiceEngineer")
        val query = serviceEngineerCollection.whereEqualTo("phoneNumber", phonenumber1)

        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                // Get the data from the Firestore document
                val email1 = document.getString("email")
                val address = document.getString("address")
                val city = document.getString("city")
                val state = document.getString("state")


                // Now, you have the data for the ServiceEngineer with the specified phone number
                // You can use this data as needed
                if (email != null) {
                    // Handle email
                }
                if (address != null) {
                    // Handle address
                }
                if (city != null) {
                    // Handle city
                }
                if (state != null) {
                    // Handle state
                }


                name1.text = name
                phone_no.text = phonenumber1
                email.text = email1
                Address.text = address
                City.text = city
                State.text = state


            }
        }.addOnFailureListener { e ->
            val errorMessage = e.message
            if (errorMessage != null) {
                // Handle the error, e.g., display an error message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }





        // Initialize the EditText variable
        var profileName = fragmentView.findViewById<TextView>(R.id.profileName)
        var phonenumber = fragmentView.findViewById<TextView>(R.id.ph_no)
        profileName.text = name
        phonenumber.text = phonenumber1


        var isEditing = false
        edit.setOnClickListener{
            if(isEditing){
                name1.visibility = View.VISIBLE
                phone_no.visibility = View.VISIBLE
                email.visibility = View.VISIBLE
                Address.visibility = View.VISIBLE
                City.visibility = View.VISIBLE
                State.visibility = View.VISIBLE

                Editname.visibility = View.GONE
                Editphone_no.visibility = View.GONE
                Editemail.visibility = View.GONE
                Editaddress.visibility = View.GONE
                Editcity.visibility = View.GONE
                Editstate.visibility = View.GONE
                savebutton.visibility = View.GONE
            }
            else{
            name1.visibility = View.GONE
            phone_no.visibility = View.GONE
            email.visibility = View.GONE
            Address.visibility = View.GONE
            City.visibility = View.GONE
            State.visibility = View.GONE


            Editname.visibility = View.VISIBLE
            Editphone_no.visibility = View.VISIBLE
            Editemail.visibility = View.VISIBLE
            Editaddress.visibility = View.VISIBLE
            Editcity.visibility = View.VISIBLE
            Editstate.visibility = View.VISIBLE
            savebutton.visibility = View.VISIBLE
        }
            isEditing = !isEditing
        }

        savebutton.setOnClickListener {
            val newName = Editname.text.toString().trim()
            val newEmail = Editemail.text.toString().trim()
            val newAddress = Editaddress.text.toString().trim()
            val newCity = Editcity.text.toString().trim()
            val newState = Editstate.text.toString().trim()


            if (newName.isNotEmpty() && newEmail.isNotEmpty() && newAddress.isNotEmpty()
                && newCity.isNotEmpty() && newState.isNotEmpty() ) {

                updateEngineerDetails(newName, newEmail, newAddress, newCity, newState)

                name1.visibility = View.VISIBLE
                phone_no.visibility = View.VISIBLE
                email.visibility = View.VISIBLE
                Address.visibility = View.VISIBLE
                City.visibility = View.VISIBLE
                State.visibility = View.VISIBLE

                Editname.visibility = View.GONE
                Editphone_no.visibility = View.GONE
                Editemail.visibility = View.GONE
                Editaddress.visibility = View.GONE
                Editcity.visibility = View.GONE
                Editstate.visibility = View.GONE
                savebutton.visibility = View.GONE
            } else {
                // Handle the case where any field is empty
                // You can show a Toast or an error message to the user
                Toast.makeText(requireContext(), "Fill all the Details!", Toast.LENGTH_LONG).show()
            }

        }






        infoButton.setOnClickListener {

            val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            val slideDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
            if (infoCard.visibility == View.VISIBLE) {
                infoCard.startAnimation(slideUp)
                infoCard.visibility = View.GONE
            } else {
                infoCard.startAnimation(slideDown)
                infoCard.visibility = View.VISIBLE
            }

        }


        val logoutImage = fragmentView.findViewById<ImageView>(R.id.logout)
        if (logoutImage == null) {
            Log.d("logout", "image is not inflated")
        }
        logoutImage?.setOnClickListener {
            logout()
        }

        return fragmentView
    }

    private fun updateEngineerDetails(newName: String, newEmail: String, newAddress: String, newCity: String, newState: String) {
        val sharedPrefs = activity?.getSharedPreferences("login-data", Context.MODE_PRIVATE)
        val phonenumber1 = sharedPrefs?.getString("phoneNumber", "")

        // Access the Firestore collection for Service Engineers
        val serviceEngineerCollection = FirebaseFirestore.getInstance().collection("ServiceEngineer")

        // Query to find the document with the corresponding phone number
        val query = serviceEngineerCollection.whereEqualTo("phoneNumber", phonenumber1)

        // Execute the query
        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                // Update the document with new values
                document.reference.update(
                    "name", newName,
                    "email", newEmail,
                    "address", newAddress,
                    "city", newCity,
                    "state", newState
                ).addOnSuccessListener {
                    // Document updated successfully
                    // Show a Toast or perform any other action to indicate success
                    Toast.makeText(requireContext(), "Details updated successfully!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    // Handle failure while updating the document
                    // Show an error message or perform necessary actions
                    Toast.makeText(requireContext(), "Failed to update details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            // Handle failure while executing the query
            // Show an error message or perform necessary actions
            Toast.makeText(requireContext(), "Failed to retrieve data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



}
