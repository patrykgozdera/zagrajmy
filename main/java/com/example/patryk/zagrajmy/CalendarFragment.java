package com.example.patryk.zagrajmy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private View inflatedView = null;

    private CompactCalendarView compactCalendarView;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private long startMillis = 0;

    private TextView calendarMonthInfo;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventsInfo = new ArrayList<>();

    private DatabaseReference mDatabase;

    public CalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_calendar, container, false);

        this.compactCalendarView = (CompactCalendarView) inflatedView.findViewById(R.id.compactcalendar_view);
        this.compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        this.calendarMonthInfo = (TextView) inflatedView.findViewById(R.id.calendar_month_info);
        this.calendarMonthInfo.setText(dateFormatMonth.format(this.compactCalendarView.getFirstDayOfCurrentMonth()));

        this.listView = (ListView) inflatedView.findViewById(R.id.events_calendar_list_view);

        this.adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, this.eventsInfo);
        this.listView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("eventsData");

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                eventsInfo.clear();
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                for (Event e : events) {
                    Log.w("T", "event: " + e);
                    Log.w("T", "getData: " + e.getData().toString());
                    eventsInfo.add(e.getData().toString());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarMonthInfo.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        addValuesToEventsCalendar();
    }

    private void addValuesToEventsCalendar() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EventData eventData = dataSnapshot.getValue(EventData.class);
                List<String> eventParticipants = eventData.getEventParticipants();
                Set<String> eventParticipantsSet = new HashSet<>(eventParticipants);
                if (eventParticipantsSet.contains(LoginActivity.currentUserId) && eventData.getEventDate() != null) {
                    try {
                        startMillis = simpleDateFormat.parse(eventData.getEventDate()).getTime();
                        Log.w("T", "startMillis: " + startMillis);
                        Event ev1 = new Event(Color.BLUE, startMillis, LP_Util.getEventInfo(eventData));
                        compactCalendarView.addEvent(ev1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
}
