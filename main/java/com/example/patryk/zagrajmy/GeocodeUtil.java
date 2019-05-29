package com.example.patryk.zagrajmy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeocodeUtil {

    public static LatLng getEventCoordinates(String locationText, Context ctx) {
        Log.d("T", "geoLocate: geolocating");

        LatLng eventCoordinates;
        Geocoder geocoder = new Geocoder(ctx);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(locationText, 1);
        }catch (IOException e){
            Log.e("T", "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            eventCoordinates = new LatLng(address.getLatitude(), address.getLongitude());
            Log.d("T", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            return eventCoordinates;
        }

        return null;
    }
}
