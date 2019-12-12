
package edu.ucsb.cs184.oshin.travelerconnect

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var interests: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    mAuth = FirebaseAuth.getInstance()
    database = FirebaseDatabase.getInstance()

    // Set up spinner
    val genderS: Spinner = findViewById(R.id.gender_spinner)
    genderS.prompt = "gender"
}

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth!!.currentUser != null) {
            val toast = Toast.makeText(this, mAuth!!.currentUser!!.uid, Toast.LENGTH_LONG)
            toast.show()
            val currentUser = mAuth?.currentUser
        }

        val bioV: EditText = findViewById(R.id.bio)
        val curlocV: EditText = findViewById(R.id.currentLoc)
        val phoneV: EditText = findViewById(R.id.phone)
        val emailV: EditText = findViewById(R.id.user)
        val passwordV: EditText = findViewById(R.id.pass)
        val genderS: Spinner = findViewById(R.id.gender_spinner)
        val confirmB: Button = findViewById(R.id.confirm)
        val backB: Button = findViewById(R.id.back)
        val addInterestsB: Button = findViewById(R.id.add_interests)

        addInterestsB.setOnClickListener {
            // inflate custom layout and set up popup window
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.add_interests, null)
            val popupWindow = PopupWindow(
                view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }

            val tv1: TextView = view.findViewById(R.id.textView3)
            val tv2: TextView = view.findViewById(R.id.textView10)
            val tv3: TextView = view.findViewById(R.id.textView8)
            val tv4: TextView = view.findViewById(R.id.textView2)
            val tv5: TextView = view.findViewById(R.id.textView4)
            val tv6: TextView = view.findViewById(R.id.textView7)
            val tv7: TextView = view.findViewById(R.id.textView5)

            tv1.setOnClickListener {
                interests!!.add(tv1.text.toString())
                tv1.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv2.setOnClickListener {
                interests!!.add(tv2.text.toString())
                tv2.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv3.setOnClickListener {
                interests!!.add(tv3.text.toString())
                tv3.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv4.setOnClickListener {
                interests!!.add(tv4.text.toString())
                tv4.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv5.setOnClickListener {
                interests!!.add(tv5.text.toString())
                tv5.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv6.setOnClickListener {
                interests!!.add(tv6.text.toString())
                tv6.setBackgroundColor(Color.parseColor("#008577"))
            }

            tv7.setOnClickListener {
                interests!!.add(tv7.text.toString())
                tv7.setBackgroundColor(Color.parseColor("#008577"))
            }

            // set up exiting popup window
            val closeButton: Button = view.findViewById(R.id.close_popup)

            closeButton.setOnClickListener {
                popupWindow.dismiss()
            }

            // Show popup window
            TransitionManager.beginDelayedTransition(sign_up)
            popupWindow.showAtLocation(
                sign_up,
                Gravity.CENTER,
                0,
                0
            )
        }

        confirmB.setOnClickListener {
            val email = emailV.text.toString()
            val pass = passwordV.text.toString()
            val phone = phoneV.text.toString()
            val bio = bioV.text.toString()
            val gender = genderS.selectedItem.toString()
            val curloc = curlocV.text.toString()

            if (mAuth!!.currentUser != null) {
                updateInfo(phone, bio, gender, curloc)
                Toast.makeText(this, "Fields Updated", Toast.LENGTH_SHORT)
                finish()
            } else {
                if (email.isNotEmpty() and pass.isNotEmpty() and bio.isNotEmpty() and phone.isNotEmpty() and gender.isNotEmpty() and curloc.isNotEmpty()) {
                    // create account
                    createAccount(email, pass, phone, bio, gender, curloc)
                } else {
                    // ask to enter email and password
                    val toast =
                        Toast.makeText(
                            this,
                            "Please fill in all fields to sign up",
                            Toast.LENGTH_SHORT
                        )
                    toast.show()
                }
            }
        }

        backB.setOnClickListener {
            if (mAuth!!.currentUser != null) {
                mAuth!!.signOut()
                val toast = Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT)
                toast.show()
            }
            finish()
        }
    }

    private fun updateInfo(phone: String, bio: String, gender: String, curloc: String) {
        val user = mAuth!!.currentUser
        val uid = user!!.uid

        val myRef = database!!.getReference("UserInfo/$uid")

        if (!phone.isNullOrBlank()) {
            myRef.child("Contact").setValue(phone)
        }
        if (!bio.isNullOrBlank()) {
            myRef.child("Bio").setValue(bio)
        }
        if (!phone.isNullOrBlank()) {
            myRef.child("Contact").setValue(phone)
        }
        if (!gender.isNullOrBlank()) {
            myRef.child("Gender").setValue(gender)
        }
        if (!curloc.isNullOrBlank()) {
            myRef.child("Visit").setValue(curloc)
        }
        for(i in 0 until interests.size-1) {
            myRef.child("Interests").child("interest_" + (i+1).toString()).setValue(interests!!.get(i))
        }

    }

    private fun createAccount(email: String, pass: String, phone: String, bio: String, gender: String, curloc: String) {
        // TODO: add validation for email/password
        // Creates account through firebase
        mAuth!!.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    val uid = user!!.uid

                    val myRef = database!!.getReference("UserInfo/$uid")
                    myRef.child("Bio").setValue(bio)
                    myRef.child("Contact").setValue(phone)
                    myRef.child("Gender").setValue(gender)
                    myRef.child("Visit").setValue(curloc)
                    for(i in 0 until interests.size-1) {
                        myRef.child("Interests").child("interest_" + (i+1).toString()).setValue(interests!!.get(i))
                    }

                    val toast = Toast.makeText(this, myRef.child("Bio").toString(), Toast.LENGTH_LONG)
                    toast.show()
                } else {
                    // If sign in fails, display a message to the user
                    // TODO: make failure message more user friendly (for incorrectly formatted email/bad password)
                    val toast = Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                    toast.show()
                }
            }

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        // current placeholder
    }
}

