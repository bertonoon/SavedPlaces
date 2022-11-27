package eu.tutorials.savedplaces.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.tutorials.savedplaces.R
import eu.tutorials.savedplaces.adapters.SavedPlacesAdapter
import eu.tutorials.savedplaces.database.DatabaseHandler
import eu.tutorials.savedplaces.models.SavedPlaceModel
import kotlinx.android.synthetic.main.activity_main.*
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddPlace : FloatingActionButton? = findViewById(R.id.fabAddHappyPlace)
        fabAddPlace?.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
           startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getSavedPlacesListFromLocalDB()

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_saved_places_list.adapter as SavedPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_saved_places_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_saved_places_list.adapter as SavedPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getSavedPlacesListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_saved_places_list)


    }

    private fun getSavedPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getSavedPlaceList : ArrayList<SavedPlaceModel> = dbHandler.getPlacesList()

        if(getSavedPlaceList.size > 0){
            rv_saved_places_list.visibility = View.VISIBLE
            tv_no_records_available.visibility = View.GONE
            setupSavedPlacesRecyclerView(getSavedPlaceList)
        } else {
            rv_saved_places_list.visibility = View.GONE
            tv_no_records_available.visibility = View.VISIBLE
        }
    }

    private fun setupSavedPlacesRecyclerView(savedPlaceList :ArrayList<SavedPlaceModel>){
        rv_saved_places_list.layoutManager = LinearLayoutManager(this)
        rv_saved_places_list.setHasFixedSize(true)
        val placesAdapter = SavedPlacesAdapter(this,savedPlaceList)
        rv_saved_places_list.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : SavedPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: SavedPlaceModel) {
                super.onClick(position, model)
                val intent = Intent(this@MainActivity,SavedPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getSavedPlacesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    companion object{
        const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }




}