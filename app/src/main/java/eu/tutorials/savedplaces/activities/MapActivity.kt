package eu.tutorials.savedplaces.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.tutorials.savedplaces.R
import eu.tutorials.savedplaces.models.SavedPlaceModel
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mSavedPlaceDetails : SavedPlaceModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mSavedPlaceDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS)
                    as SavedPlaceModel
        }

        if(mSavedPlaceDetails != null){
            setSupportActionBar(toolbar_map)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mSavedPlaceDetails!!.title
            toolbar_map.setNavigationOnClickListener{
                onBackPressed()
            }
            val supportMapFragment : SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        if(mSavedPlaceDetails != null) {
            val position = LatLng(mSavedPlaceDetails!!.latitude, mSavedPlaceDetails!!.longitude)
            googleMap.addMarker(MarkerOptions()
                .position(position)
                .title(mSavedPlaceDetails!!.location))
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position,15f)
            googleMap.animateCamera(newLatLngZoom)
        }
    }
}