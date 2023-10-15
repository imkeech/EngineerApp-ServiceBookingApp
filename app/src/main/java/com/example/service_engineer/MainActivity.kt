package com.example.service_engineer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import fragment.profileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var serviceid: EditText
    private lateinit var servicepw: EditText
    private lateinit var loginButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()
        serviceid = findViewById(R.id.service_id1)
        servicepw = findViewById(R.id.service_pw1)
        loginButton = findViewById(R.id.loginbut)
        sharedPref = getSharedPreferences("login-data", MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = serviceid.text.toString()
            val password = servicepw.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkServiceCredentials(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAuth()
        registerFCM()
    }

    private fun checkServiceCredentials(email: String, password: String) {
        val serviceEngineerRef = firestore.collection("ServiceEngineer")
        val query = serviceEngineerRef.whereEqualTo("id", email)

        query.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val document = documents.documents[0]
                val storedPassword = document.getString("password")

                if (password == storedPassword) {
                    // Successful login
                    val name = document.getString("name")
                    val phoneNumber = document.getString("phoneNumber")

                    // Now 'name' and 'phoneNumber' contain the service engineer's information
                    Log.d("ServiceEngineer", "Name: $name, PhoneNumber: $phoneNumber")

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val editor = sharedPref.edit()
                    editor.putString("loggedInServiceEngineerId", email)
                    editor.putString("name", name)
                    editor.putString("phoneNumber", phoneNumber)
                    editor.apply()

                    // Start MainActivity2
                    checkAuth()
                } else {
                    // Invalid password
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
                }
            } else {
                // User not found in Firestore
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Error occurred while querying Firestore
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAuth() {
        val loggedInEmail = sharedPref.getString("loggedInServiceEngineerId", "")
        if (loggedInEmail != "") {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun registerFCM() {
        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener {
                Log.d("fcm", "Token: $it")
                FirebaseMessaging.getInstance().subscribeToTopic("booking").addOnSuccessListener {
                    Log.d("fcm", "Subscribed to topic: booking")
                }
            }
    }

    override fun onStart() {
        super.onStart()
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        Log.d("fcm", "Asking notification permission")

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("fcm", "Successfully added notifications")
            } else {
                Log.d("fcm", "Failed to get permission for notifications")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("fcm", "Permission already granted")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                Log.d("fcm", "Requesting permission since it's a higher API level")
            }
        }
    }
}
