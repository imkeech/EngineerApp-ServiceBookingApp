package fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.service_engineer.CloudMessaging
import com.example.service_engineer.Communicator
import com.example.service_engineer.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class wrk_Fragment : Fragment() {
    private lateinit var communicator: Communicator
    private lateinit var cardContainer: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var email: String = ""
    private lateinit var firestore: FirebaseFirestore
    private val cloudMessaging = CloudMessaging()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wrk_, container, false)

        // Retrieve the email value from the arguments
        email = arguments?.getString("email") ?: ""

        // Get references to the SwipeRefreshLayout and the LinearLayout container
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        cardContainer = view.findViewById(R.id.cardContainer)

        // Configure the SwipeRefreshLayout colors
        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.prjtred),
            ContextCompat.getColor(requireContext(), R.color.prjtred3)
        )

        // Set a listener for the pull-to-refresh action
        swipeRefreshLayout.setOnRefreshListener {
            // Implement your refresh logic here
            // This is where you can fetch new data or reload your fragment's content
            // After completing the refresh, make sure to call swipeRefreshLayout.isRefreshing = false
            // to stop the refreshing animation.
            refreshData()
        }

        // Check if email matches assignedServiceEngineerId
        if (email.isNotEmpty()) {
            checkAssignedServiceEngineer(email)
        }

        return view
    }

    private fun refreshData() {
        // Implement your data refreshing logic here
        // This method will be called when the user pulls to refresh
        // Once the refresh is complete, set swipeRefreshLayout.isRefreshing = false
        // to stop the refreshing animation.
        // You can update your UI or fetch new data here.

        // For example, you can clear the existing cardContainer and reload the data
        cardContainer.removeAllViews()
        checkAssignedServiceEngineer(email)

        // After refreshing, make sure to stop the refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    private fun checkAssignedServiceEngineer(email: String) {
        val firestore = FirebaseFirestore.getInstance()
        val serviceBookingRef = firestore.collection("Service_Booking")

        // Query Firestore to find documents where assignedServiceEngineerId matches the email
        serviceBookingRef.whereEqualTo("assignedServiceEngineerId", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Fetch the customer details based on ph_no
                    val ph_no = document.getString("ph_no")
                    if (ph_no != null) {
                        fetchCustomerDetails(ph_no) { customerDetails ->
                            // Create a CardView for each data item including customer details
                            val cardView = createCardView(document, customerDetails, ph_no)

                            // Create a LinearLayout to wrap the CardView and add margin
                            val cardContainerItem = createCardContainerItem(cardView)

                            // Add the CardView with margin to the container
                            cardContainer.addView(cardContainerItem)
                        }
                    }
                }
            }
    }

    private fun fetchCustomerDetails(
        ph_no: String,
        callback: (customerDetails: DocumentSnapshot?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val customerDetailsRef = firestore.collection("customerDetails")

        // Query Firestore to find the customer details based on ph_no
        customerDetailsRef.whereEqualTo("ph_no", ph_no)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming ph_no is unique in customerDetails collection, get the first document
                    val customerDetails = documents.documents[0]
                    callback(customerDetails)
                } else {
                    // If no matching customer details found, pass null to the callback
                    callback(null)
                }
            }
    }

    private fun createCardView(
        document: DocumentSnapshot,
        customerDetails: DocumentSnapshot?,
        customerPhone: String
    ): View {
        // Inflate the card_service_booking.xml layout
        val cardView = layoutInflater.inflate(R.layout.card_service_booking, null) as CardView

        // Find and set data to TextViews in card_service_booking.xml
        val textViewServiceId = cardView.findViewById<TextView>(R.id.textViewServiceId)
        val textViewModelId = cardView.findViewById<TextView>(R.id.textViewModelId)
        val textViewPhNo = cardView.findViewById<TextView>(R.id.textViewPhNo)
        val textViewProblems = cardView.findViewById<TextView>(R.id.textViewProblems)
        val textViewOthers = cardView.findViewById<TextView>(R.id.textViewOthers)

        val callClosedButton = cardView.findViewById<Button>(R.id.callClosedButton)
        val status = cardView.findViewById<Spinner>(R.id.status)
        val drop = cardView.findViewById<LinearLayout>(R.id.drop)

        val observation = cardView.findViewById<CardView>(R.id.dropdownCardView2)
        val spareNeeded = cardView.findViewById<CardView>(R.id.spareNeeded)
        val BILL = cardView.findViewById<CardView>(R.id.BILL)
        val reason = cardView.findViewById<CardView>(R.id.dropdownReason)
        val workdone = cardView.findViewById<CardView>(R.id.dropdownWorkdone)


        val serviceId =document.getString("serviceId").orEmpty();
        textViewServiceId.text = "Service ID: ${serviceId}"
        textViewModelId.text = "Model ID: ${document.getString("modelId")}"
        textViewPhNo.text = "Phone Number: ${document.getString("ph_no")}"
        textViewProblems.text = "Problems: ${document.getString("problems")}"
        textViewOthers.text = "Others: ${document.getString("others")}"

        // Retrieve serviceId and customer_phno from the document


        // Optionally, you can also set customer details if available
        if (customerDetails != null) {
            val customerName = cardView.findViewById<TextView>(R.id.customerName)
            val customerAddress = cardView.findViewById<TextView>(R.id.customerAddress)
            val customerAMD = cardView.findViewById<TextView>(R.id.customerAMD)

            customerName.text = "Customer Name: ${customerDetails.getString("name")}"
            customerAddress.text = "Customer Address: ${customerDetails.getString("address")}"
            customerAMD.text = "Customer AMD: ${customerDetails.getString("AMD")}"
        }



        callClosedButton.setOnClickListener {
            // Implement your logic here for the "Call Closed" button click
            val statusdrop = cardView.findViewById<CardView>(R.id.statusdrop)
            statusdrop.visibility = View.VISIBLE


            // Set the default selection of the Spinner
            val statusArray = resources.getStringArray(R.array.Status)
            val statusValue = document.getString("status")
            val selectedIndex = statusArray.indexOf(statusValue)
            if (selectedIndex >= 0) {
                status.setSelection(selectedIndex)
            }

            // Set an item selection listener for the Spinner
            status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedStatus = status.selectedItem.toString()

                    Log.d("selectedStatus", selectedStatus)
                    var shouldSendNotification = false;
                    if (selectedStatus=="On Hold") {
                        drop.visibility= View.VISIBLE
                        observation.visibility = View.VISIBLE
                        spareNeeded.visibility =View.VISIBLE
                        BILL.visibility =View.VISIBLE
                        reason.visibility =View.GONE
                        workdone.visibility= View.GONE
                        shouldSendNotification = true;
                    }else if (selectedStatus == "Cancelled"){
                        drop.visibility= View.VISIBLE
                        observation.visibility = View.VISIBLE
                        reason.visibility =View.VISIBLE
                        BILL.visibility =View.VISIBLE
                        spareNeeded.visibility =View.GONE
                        workdone.visibility= View.GONE
                        shouldSendNotification = true;
                    }else if (selectedStatus =="Completed"){
                        drop.visibility= View.VISIBLE
                        observation.visibility = View.VISIBLE
                        workdone.visibility= View.VISIBLE
                        BILL.visibility =View.VISIBLE
                        spareNeeded.visibility =View.GONE
                        reason.visibility =View.GONE
                    }else{
                        drop.visibility= View.GONE
                        observation.visibility = View.GONE
                        spareNeeded.visibility =View.GONE
                        BILL.visibility =View.GONE
                        reason.visibility =View.GONE
                        workdone.visibility= View.GONE
                    }


                    if (shouldSendNotification) {
                        cloudMessaging.sendCallUpdateNotification(selectedStatus, serviceId, customerPhone)
                    }

                    observation.setOnClickListener{
                        val o = cardView.findViewById<LinearLayout>(R.id.observation)
                        val dropdownArrow1 = cardView.findViewById<ImageView>(R.id.dropdownArrow2)
                        if (o.visibility == View.GONE) {
                            o.visibility = View.VISIBLE
                            dropdownArrow1.setImageResource(R.drawable.baseline_arrow_drop_up_24) // Replace with your arrow up icon
                        } else {
                            o.visibility = View.GONE
                            dropdownArrow1.setImageResource(R.drawable.baseline_arrow_drop_down_24) // Replace with your dropdown icon
                        }
                    }

                    workdone.setOnClickListener{
                        val w = cardView.findViewById<LinearLayout>(R.id.workdoneLinearLayout)
                        val dropdownArrow2 = cardView.findViewById<ImageView>(R.id.workdownArrow)
                        if (w.visibility == View.GONE) {
                            w.visibility = View.VISIBLE
                            dropdownArrow2.setImageResource(R.drawable.baseline_arrow_drop_up_24) // Replace with your arrow up icon
                        } else {
                            w.visibility = View.GONE
                            dropdownArrow2.setImageResource(R.drawable.baseline_arrow_drop_down_24) // Replace with your dropdown icon
                        }
                    }

                    reason.setOnClickListener{
                        val r = cardView.findViewById<LinearLayout>(R.id.reasonLinearLayout)
                        val dropdownArrow3 = cardView.findViewById<ImageView>(R.id.ReasonArrow)
                        if (r.visibility == View.GONE) {
                            r.visibility = View.VISIBLE
                            dropdownArrow3.setImageResource(R.drawable.baseline_arrow_drop_up_24) // Replace with your arrow up icon
                        } else {
                            r.visibility = View.GONE
                            dropdownArrow3.setImageResource(R.drawable.baseline_arrow_drop_down_24) // Replace with your dropdown icon
                        }
                    }

                    BILL.setOnClickListener{
                        val b = cardView.findViewById<LinearLayout>(R.id.drop3)
                        val dropdownArrow4 = cardView.findViewById<ImageView>(R.id.dropdownArrow3)
                        if (b.visibility == View.GONE) {
                            b.visibility = View.VISIBLE
                            dropdownArrow4.setImageResource(R.drawable.baseline_arrow_drop_up_24) // Replace with your arrow up icon
                        } else {
                            b.visibility = View.GONE
                            dropdownArrow4.setImageResource(R.drawable.baseline_arrow_drop_down_24) // Replace with your dropdown icon
                        }
                    }

                    val spare = cardView.findViewById<Spinner>(R.id.spare)
                    val StextView = cardView.findViewById<EditText>(R.id.spareNeededOthers)

                    spare.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedItem = parent.getItemAtPosition(position).toString()
                            if (selectedItem == "Others") {
                                StextView.visibility = View.VISIBLE
                            } else {
                                StextView.visibility = View.GONE
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle nothing selected, if needed.
                        }
                    }


                    firestore = FirebaseFirestore.getInstance()

                    // Find the submit button and add a click listener
                    val submitButton = cardView.findViewById<Button>(R.id.submit)
                    submitButton.setOnClickListener {
                        // Gather the values from UI elements
                        val statusValue1 = cardView.findViewById<Spinner>(R.id.status)
                        val noOfCoppiesValue1 = cardView.findViewById<EditText>(R.id.noOfCoppies)
                        val observationValue1 = cardView.findViewById<EditText>(R.id.Observation)
                        val workdoneValue1 = cardView.findViewById<EditText>(R.id.wrokdone)
                        val reasonValue1 = cardView.findViewById<EditText>(R.id.Reason)
                        val spareValue1 = cardView.findViewById<Spinner>(R.id.spare)
                        val serviceChargeValue1 = cardView.findViewById<Spinner>(R.id.serviceCharge)
                        val invoiceNoValue1 = cardView.findViewById<EditText>(R.id.invoiceNo)
                        val invoiceBillAmountValue1 = cardView.findViewById<EditText>(R.id.invoiceBillAmount)

                        val serviceId = document.getString("serviceId") ?: ""
                        val customer_phno = document.getString("ph_no")
                        val currentEngineerId = email
                        val selectedStatus = status.selectedItem.toString()
                        Log.d("selectedStatus", selectedStatus)

                        // Update the status in the Service_Booking collection
                        val serviceBookingRef = firestore.collection("Service_Booking")

                        if (serviceId != null) {
                            serviceBookingRef.document(serviceId)
                                .update("callStatus", selectedStatus)
                                .addOnSuccessListener {
                                    // Status updated successfully
                                    Log.d("Firestore", "Status updated for service ID: $serviceId")
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error
                                    Log.w("Firestore", "Error updating status for service ID: $serviceId", e)
                                }
                        }

                        val statusValue = statusValue1?.selectedItem?.toString() ?: ""
                        val noOfCoppiesValue = noOfCoppiesValue1?.text?.toString() ?: ""

                        if (customer_phno != null && noOfCoppiesValue.isNotEmpty()) {
                            val customerDetailsRef = firestore.collection("customerDetails")

                            // Query Firestore to find the customer details based on ph_no
                            customerDetailsRef.whereEqualTo("ph_no", customer_phno)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        // Assuming ph_no is unique in customerDetails collection, get the first document
                                        val customerDetails = documents.documents[0]

                                        // Update the "NoOfCoppies" field in the customerDetails document
                                        customerDetailsRef.document(customerDetails.id)
                                            .update("no_of_coppies", noOfCoppiesValue)
                                            .addOnSuccessListener {
                                                // NoOfCoppies updated successfully in customerDetails
                                                Log.d(
                                                    "Firestore",
                                                    "NoOfCoppies updated for customer: $customer_phno"
                                                )
                                            }
                                            .addOnFailureListener { e ->
                                                // Handle the error
                                                Log.w(
                                                    "Firestore",
                                                    "Error updating NoOfCoppies for customer: $customer_phno",
                                                    e
                                                )
                                            }
                                    }
                                }
                        }



                        val observationValue = observationValue1?.text?.toString() ?: ""
                        val workdoneValue = workdoneValue1?.text?.toString() ?: ""
                        val reasonValue = reasonValue1?.text?.toString() ?: ""
                        val spareValue = spareValue1?.selectedItem?.toString() ?: ""
                        val serviceChargeValue = serviceChargeValue1?.selectedItem?.toString() ?: ""
                        val invoiceNoValue = invoiceNoValue1?.text?.toString() ?: ""
                        val invoiceBillAmountValue = invoiceBillAmountValue1?.text?.toString() ?: ""
                        val spareNeededOthersValue = StextView?.text?.toString() ?: ""



                        // Create a data object to hold these values
                        val data = hashMapOf(
                            "Status" to statusValue,
                            "NoOfCoppies" to noOfCoppiesValue,
                            "Observation" to observationValue,
                            "WorkDone" to workdoneValue,
                            "Reason" to reasonValue,
                            "Spare" to spareValue,
                            "ServiceCharge" to serviceChargeValue,
                            "InvoiceNo" to invoiceNoValue,
                            "InvoiceBillAmount" to invoiceBillAmountValue,
                            "SpareNeededOthers" to spareNeededOthersValue,
                            "serviceId" to serviceId,
                            "C_Ph_no" to customer_phno,
                            "EngineerId" to currentEngineerId
                        )



                        // Replace "Service_info" with your actual Firestore collection name
                        firestore.collection("Service_info")
                            .add(data)
                            .addOnSuccessListener { documentReference ->

                                Log.d("Firestore", "Document added with ID: ${documentReference.id}")
                                // You can perform actions after the data is successfully uploaded here.
                                // For example, display a success message or navigate to another screen.
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding document", e)
                                // Handle the error, e.g., display an error message to the user.
                            }
                        Log.d("ph_no","$customer_phno")

                        cloudMessaging.sendCallUpdateNotification(selectedStatus, serviceId, customerPhone);
                    }



                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case when nothing is selected (if needed)
                }
            }

        }

        return cardView
    }

    private fun createCardContainerItem(cardView: View): LinearLayout {
        val containerItem = LinearLayout(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.card_margin)
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
        containerItem.layoutParams = layoutParams
        containerItem.orientation = LinearLayout.VERTICAL
        containerItem.addView(cardView)
        return containerItem
    }
}