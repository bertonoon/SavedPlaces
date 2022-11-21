package eu.tutorials.savedplaces.activities

import android.Manifest.permission.*
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import eu.tutorials.savedplaces.R
import eu.tutorials.savedplaces.database.DatabaseHandler
import eu.tutorials.savedplaces.models.SavedPlaceModel
import kotlinx.android.synthetic.main.activity_add_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0

    private var etDate: EditText? = null
    private var tvAddImage : TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        val toolbar : Toolbar? = findViewById(R.id.toolbar_add_place)
        etDate = findViewById(R.id.et_date)
        tvAddImage = findViewById(R.id.tv_add_image)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener{
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }

        etDate?.setOnClickListener(this)
        tvAddImage?.setOnClickListener(this)
        btn_save?.setOnClickListener(this)


    }


    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.getDefault())
        etDate?.setText(sdf.format(cal.time).toString())
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date -> {
                DatePickerDialog(this@AddPlaceActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery",
                "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0 ->  choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when {
                    et_title.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "Please enter a title",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    et_description.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "Please enter a description",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    et_location.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "Please enter a location",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "Please select an image",
                            Toast.LENGTH_LONG
                        ).show()
                    } else -> {
                        val savedPlaceModel = SavedPlaceModel(
                            0,
                            et_title.text.toString(),
                            saveImageToInternalStorage.toString(),
                            et_description.text.toString(),
                            et_date.text.toString(),
                            et_location.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                    val dbHandler = DatabaseHandler(this)
                    val addSavedPlace = dbHandler.addSavedPlace(savedPlaceModel)
                    if (addSavedPlace > 0){
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "The place details are inserted successful",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        }
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY_CODE){
                if (data != null){
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        iv_place_image.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddPlaceActivity,"Failed to load the image from gallery",Toast.LENGTH_LONG).show()
                    }
                }
            } else if (requestCode == CAMERA_CODE){
                val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
                try {
                    saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)
                    iv_place_image.setImageBitmap(thumbNail)
                } catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@AddPlaceActivity,"Failed to load the image from camera",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun takePhotoFromCamera(){
        Dexter.withContext(this@AddPlaceActivity)
            .withPermissions(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE,
                CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report!!.areAllPermissionsGranted()){
                        val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(galleryIntent, CAMERA_CODE)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    showRationaleDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this@AddPlaceActivity)
            .withPermissions(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report!!.areAllPermissionsGranted()){
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY_CODE)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    showRationaleDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this@AddPlaceActivity).setMessage(
                    "It looks like you have turned off permission required for this feature." +
                    "It can be enabled under the Application Settings.")
            .setPositiveButton("Go TO SETTINGS"){
                _,_ ->
                try {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap : Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }


    companion object {
        private const val GALLERY_CODE = 1
        private const val CAMERA_CODE = 2
        private const val IMAGE_DIRECTORY = "SavedPlacesImages"
    }

}