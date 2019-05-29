package com.example.patryk.zagrajmy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class EventsMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng clickedPosition;

    private View inflatedView = null;
    private AppCompatButton routeButton;

    private DatabaseReference mDatabase;

    private ArrayList<LatLng> locationsList = new ArrayList<>();

    public int getLayoutId() {
        return R.layout.events_map;
    }

    public EventsMap() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.events_map, container, false);

        routeButton = (AppCompatButton) inflatedView.findViewById(R.id.route_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference("eventsData");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.events_map);
        mapFragment.getMapAsync(this);

        routeButton.setEnabled(false);
        routeButton.setAlpha(0.5f);

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeString = "https://www.google.com/maps/dir/?api=1&travelmode=driving&destination=";
                routeString += clickedPosition.latitude + "%2C" + clickedPosition.longitude;
                Uri uri = Uri.parse(routeString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MapsInitializer.initialize(getContext());

        mMap = map;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickedPosition = marker.getPosition();
                routeButton.setEnabled(true);
                routeButton.setAlpha(1.0f);

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                routeButton.setEnabled(false);
                routeButton.setAlpha(0.5f);
            }
        });

        addValuesToMap();
    }

    private void addValuesToMap() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EventData eventData = dataSnapshot.getValue(EventData.class);
                List<String> eventParticipants = eventData.getEventParticipants();
                Set<String> eventParticipantsSet = new HashSet<>(eventParticipants);

                if (eventParticipantsSet.contains(LoginActivity.currentUserId)) {
                    LatLng eventCoords = new LatLng(eventData.getEventLat(), eventData.getEventLong());
                    mMap.addMarker(new MarkerOptions().position(eventCoords)
                            .title(eventData.getEventType()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventCoords, 16));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 5);
                            mMap.animateCamera(yourLocation);
                        }
                    }
                });
    }
}
