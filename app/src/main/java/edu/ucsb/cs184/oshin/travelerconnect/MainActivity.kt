package edu.ucsb.cs184.oshin.travelerconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.view.View
import android.widget.Button
import android.widget.EditText


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth!!.currentUser != null) {
            val toast = Toast.makeText(this, mAuth!!.currentUser!!.uid, Toast.LENGTH_LONG)
            toast.show()
            val currentUser = mAuth?.currentUser
        }

        val signupB: View = findViewById(R.id.signup_button)
        val loginB: Button = findViewById(R.id.Login)
        var emailV: EditText = findViewById(R.id.user_login)
        var passwordV: EditText = findViewById(R.id.password_login)


        loginB.setOnClickListener {
            val email = emailV.text.toString()
            val pass = passwordV.text.toString()
            if (email.isNotEmpty() and pass.isNotEmpty()) {
                // login
                logIn(email, pass)
            } else {
                // ask to enter email and password
                val toast = Toast.makeText(this, "Please enter an email and password to login.", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        signupB.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        val testLogout: Button = findViewById(R.id.button2)
        testLogout.setOnClickListener {
            mAuth!!.signOut()
            val toast = Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        // TODO: add logic for checking if need to go to info page or general page
    }

    private fun logIn(email: String, pass: String) {
        // TODO: add login code from firebase authentication tools
        mAuth!!.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.getCurrentUser()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    val toast = Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                    toast.show()
                    updateUI(null)
                }
            }
    }
}
