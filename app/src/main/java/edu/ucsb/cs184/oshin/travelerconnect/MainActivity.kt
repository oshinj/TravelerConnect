package edu.ucsb.cs184.oshin.travelerconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

/*
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOpenActivity : Button = findViewById<Button>(R.id.Login)
        btnOpenActivity.setOnClickListener{
                // Handler code here.
                val intent = Intent(this, Main_UserMenu::class.java);
                startActivity(intent);

            print("clicked login");
        }

    }
}
