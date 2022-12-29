package com.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.util.*



class GetAddressFromLatLng(
    context: Context, private val latitude: Double,
    private val longitude: Double
) {

    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener

     fun getAddress(vararg params: Void?): String{
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addressList != null && addressList.isNotEmpty()) {
                val address: Address = addressList[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append(",")
                }
                sb.deleteCharAt(sb.length - 1)
                onPostExecute(sb.toString())
                return sb.toString()
            }
        } catch (e: Exception) {
            Log.e("Saved Places", "Unable connect to Geocoder")
            onPostExecute(null)
        }
        return ""
    }


    private fun onPostExecute(resultString: String?) {
        if ( resultString == null){
            mAddressListener.onError()
        } else {
            mAddressListener.onAddressFound(resultString)
        }
    }


    fun setAddressListener (addressListener: AddressListener){
        mAddressListener = addressListener
    }

    interface AddressListener{
        fun onAddressFound(address: String?)
        fun onError()
    }


}
