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
    private var Uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth!!.currentUser != null) {
            val intent = Intent(this, Main_UserMenu::class.java).apply {
                putExtra("uid", Uid)
            }
            startActivity(intent)
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
                val toast = Toast.makeText(
                    this,
                    "Please enter an email and password to login.",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }

        signupB.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val intent = Intent(this, Main_UserMenu::class.java).apply {
            putExtra("uid", Uid)
        }
        startActivity(intent)
    }

    private fun logIn(email: String, pass: String) {
        // TODO: add login code from firebase authentication tools
        mAuth!!.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.getCurrentUser()
                    Uid =  mAuth!!.currentUser!!.uid
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