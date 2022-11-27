package eu.tutorials.savedplaces.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.savedplaces.R
import eu.tutorials.savedplaces.models.SavedPlaceModel
import kotlinx.android.synthetic.main.activity_saved_place_detail.*

class SavedPlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_place_detail)

        var savedPlaceDetailModel : SavedPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            savedPlaceDetailModel =
                intent.getSerializableExtra(
                    MainActivity.EXTRA_PLACE_DETAILS) as SavedPlaceModel
        }

        if (savedPlaceDetailModel != null){
            setSupportActionBar(toolbar_happy_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = savedPlaceDetailModel.title

            toolbar_happy_place_detail.setNavigationOnClickListener{
                onBackPressed()
            }

            iv_place_image.setImageURI(Uri.parse(savedPlaceDetailModel.image))
            tv_description.text = savedPlaceDetailModel.description
            tv_location.text = savedPlaceDetailModel.location
        }
    }
}