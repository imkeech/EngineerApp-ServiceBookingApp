package com.example.service_engineer

import Adapter.AdapterViewPager
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import fragment.notificationFragment
import fragment.profileFragment
import fragment.wrk_Fragment
import java.util.ArrayList

class MainActivity2 : AppCompatActivity() {
    private lateinit var pager: ViewPager2
    private lateinit var fragmentArrayList: ArrayList<Fragment>
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var email: String
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        pager = findViewById(R.id.pager)
        bottomNav = findViewById(R.id.botnav)

        // Retrieve the email value from the intent
        val auth = FirebaseAuth.getInstance()

        sharedPref = getSharedPreferences("login-data", MODE_PRIVATE)

        email = sharedPref.getString("loggedInServiceEngineerId", "") ?: return

        Log.d("MainActivity2 ServiceID", email)

        fragmentArrayList = ArrayList()
        val workFragment = wrk_Fragment()

        // Pass the email value to the Work fragment
        val bundle = Bundle()
        bundle.putString("email", email)
        workFragment.arguments = bundle

        fragmentArrayList.add(workFragment)
        fragmentArrayList.add(notificationFragment())
        fragmentArrayList.add(profileFragment())

        val adapterViewPager = AdapterViewPager(this, fragmentArrayList)
        pager.adapter = adapterViewPager

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.Work
                    1 -> bottomNav.selectedItemId = R.id.Notification
                    2 -> bottomNav.selectedItemId = R.id.Profile
                }
                super.onPageSelected(position)
            }
        })

        intent.let {
            val isNotification = it.getStringExtra("is-notification")
            Log.d("fcm", "Is it a notification: $isNotification")
            if (isNotification != null) {
                pager.currentItem = 1
            }
        }

        bottomNav.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.Work -> pager.setCurrentItem(0)
                    R.id.Notification -> pager.setCurrentItem(1)
                    R.id.Profile -> pager.setCurrentItem(2)
                }
                return true
            }
        })
    }
}
