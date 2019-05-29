package com.example.patryk.zagrajmy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class NewEventFragment extends Fragment {
    private View inflatedView = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Button addEventDataBtn;
    private Spinner evTypeField;
    private EditText nbOfPeopleField, locationField, dateField, descField;

    private String evType, evLocation, dateString, evDesc;
    private Integer nbOfPeople;
    private List<String> eventParticipants = new ArrayList<>();
    private List<String> eventDeclines = new ArrayList<>();

    private TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_new_event, container, false);
        evTypeField = (Spinner) inflatedView.findViewById(R.id.SpinnerEventType);
        nbOfPeopleField = (EditText) inflatedView.findViewById(R.id.EditTextNbOfPeople);
        locationField = (EditText) inflatedView.findViewById(R.id.EditTextLocation);
        dateField = (EditText) inflatedView.findViewById(R.id.EditTextDateTime);
        dateField.setInputType(InputType.TYPE_NULL);
        descField = (EditText) inflatedView.findViewById(R.id.EditTextDesc);

        addEventDataBtn = (Button) inflatedView.findViewById(R.id.button_upload);
        mDatabase = FirebaseDatabase.getInstance().getReference("eventsData");
        mAuth = FirebaseAuth.getInstance();

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateField.setText("");
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String hour = hourOfDay + "";
                                if (hourOfDay < 10) {
                                    hour = "0" + hourOfDay;
                                }
                                String min = minute + "";
                                if (minute < 10)
                                    min = "0" + minute;

                                dateString += ' ' + (hour + ":" + min);
                                dateField.setText(dateString);
                            }
                        }, mHour, mMinute, false);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String day = dayOfMonth + "";
                                if (dayOfMonth < 10) {
                                    day = "0" + dayOfMonth;
                                }
                                monthOfYear += 1;
                                String month = monthOfYear + "";
                                if (monthOfYear < 10)
                                    month = "0" + monthOfYear;

                                dateString = (day + "-" + month + "-" + year);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        addEventDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nbOfPeopleField.getText().toString().trim().equals("")) {
                    nbOfPeopleField.setError( "Liczba osób jest wymagana!" );
                    return;
                } else if (locationField.getText().toString().trim().equals("")) {
                    locationField.setError( "Lokalizacja jest wymagana!" );
                    return;
                } else if (dateString.trim().equals("")) {
                    dateField.setError( "Data jest wymagana!" );
                    return;
                }

                String id = mDatabase.push().getKey();

                EventData eventData = getEventData();

                mDatabase.child(id).setValue(eventData);
            }
        });
    }

    private EventData getEventData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.evType = evTypeField.getSelectedItem().toString();
        this.nbOfPeople = Integer.parseInt(nbOfPeopleField.getText().toString());
        this.evLocation = locationField.getText().toString();
        this.evDesc = descField.getText().toString();
        LatLng eventCoords = GeocodeUtil.getEventCoordinates(locationField.getText().toString(), getContext());
        this.eventParticipants.add(currentUser.getUid());
        Log.w("T", "eventParticipants: " + eventParticipants);

        return new EventData(
                this.evType,
                this.nbOfPeople,
                this.evLocation,
                this.dateString,
                this.evDesc,
                eventCoords.latitude,
                eventCoords.longitude,
                this.eventParticipants,
                this.eventDeclines
        );
    }

    //Google Calendar API
    /*private void addEventToGoogleCalendar() {
        long startMillis = 0;
        long endMillis = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null, endDate = null;
        try{
            startDate = simpleDateFormat.parse("2019-05-26 23:30:00");
            startMillis = startDate.getTime();
            endDate = simpleDateFormat.parse("2019-05-26 23:45:00");
            endMillis = endDate.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, "Hello Title");
        values.put(CalendarContract.Events.DESCRIPTION, "Add events to Calendar");
        values.put(CalendarContract.Events.CALENDAR_ID, 879);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        *//*Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_CALENDAR)
                .withListener(new PermissionListener()
                {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //try {

                            long startMillis = 0;
                            long endMillis = 0;
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date startDate = null, endDate = null;

                            try{
                                startDate = simpleDateFormat.parse("2019-05-26 21:30:00");
                                startMillis = startDate.getTime();
                                endDate = simpleDateFormat.parse("2019-05-26 21:45:00");
                                endMillis = endDate.getTime();
                            }catch (ParseException e){
                                e.printStackTrace();
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                        int calenderId = -1;
                            String calenderEmaillAddress = "patryk.gozdera@gmail.com";
                            String[] projection = new String[]{
                                    CalendarContract.Calendars._ID,
                                    CalendarContract.Calendars.ACCOUNT_NAME};
                            ContentResolver cr = getContext().getContentResolver();
                            Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), projection,
                                    CalendarContract.Calendars.ACCOUNT_NAME + "=? and (" +
                                            CalendarContract.Calendars.NAME + "=? or " +
                                            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + "=?)",
                                    new String[]{calenderEmaillAddress, calenderEmaillAddress,
                                            calenderEmaillAddress}, null);

                            if (cursor.moveToFirst()) {

                                if (cursor.getString(1).equals(calenderEmaillAddress)) {

                                    calenderId = cursor.getInt(0);
                                }
                            }


                            long start2 = Calendar.getInstance().getTimeInMillis(); // 2011-02-12 12h00
                            long end2 = Calendar.getInstance().getTimeInMillis() + (4 * 60 * 60 * 1000);   // 2011-02-12 13h00

                            String title = "This is my demo test with alaram with 5 minutes";

                            ContentValues cvEvent = new ContentValues();
                            cvEvent.put("calendar_id", calenderId);
                            cvEvent.put(CalendarContract.Events.TITLE, title);

                            cvEvent.put(CalendarContract.Events.DESCRIPTION, String.valueOf(start2));
                            cvEvent.put(CalendarContract.Events.EVENT_LOCATION, "Bhatar,Surat");
                            cvEvent.put(CalendarContract.Events.DTSTART, startMillis);
                            cvEvent.put(CalendarContract.Events.DTEND, endMillis);

                            cvEvent.put("eventTimezone", TimeZone.getDefault().getID());


                            Uri uri = getContext().getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), cvEvent);


                            // get the event ID that is the last element in the Uri

                            long eventID = Long.parseLong(uri.getLastPathSegment());


                            ContentValues values = new ContentValues();

                            values.put(CalendarContract.Reminders.MINUTES, 2);
                            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
                            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
                            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
                            //Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);


                        *//**//*} catch (Exception e) {
                            e.printStackTrace();
                        }*//**//*


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {*//**//* ... *//**//*}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {*//**//* ... *//**//*}
                }).check();*//*
    }*/
}
