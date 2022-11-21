package eu.tutorials.savedplaces.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.tutorials.savedplaces.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddPlace : FloatingActionButton? = findViewById(R.id.fabAddHappyPlace)
        fabAddPlace?.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
           startActivity(intent)

        }


    }

}