package com.example.patryk.zagrajmy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventsListViewFragment extends Fragment {

    private View inflatedView = null;

    private TextView textView;

    private DatabaseReference mDatabase;
    private ArrayList<EventData> eventsList = new ArrayList<>();
    private ArrayList<String> eventsIds = new ArrayList<>();
    private ArrayList<String> eventsInfo = new ArrayList<>();

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_events_listview, container, false);

        this.mDatabase = FirebaseDatabase.getInstance().getReference("eventsData");

        this.textView = (TextView) inflatedView.findViewById(R.id.textView4);
        this.listView = (ListView) inflatedView.findViewById(R.id.events_list_view);

        this.adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, this.eventsInfo);
        this.listView.setAdapter(adapter);

        addValuesToEventsListView();

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String evId = eventsIds.get(position);
                Log.w("T", "evId: " + evId);
                EventData eventData = eventsList.get(position);
                List<String> eventParticipants = eventData.getEventParticipants();
                Set<String> eventParticipantsSet = new HashSet<>(eventParticipants);
                if (!eventParticipantsSet.contains(LoginActivity.currentUserId)) {
                    showAddDialog(position);
                } else {
                    showDeleteDialog(position);
                }
            }
        });
    }

    private void addValuesToEventsListView() {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EventData eventData = dataSnapshot.getValue(EventData.class);
                Log.w("T", "eventData.getNbOfPeople: " + eventData.getNbOfPeople());
                List<String> eventDeclines = eventData.getEventDeclines();
                Log.w("T", "eventDeclines: " + eventDeclines);
                Set<String> eventDeclinesSet = new HashSet<>();
                if (eventDeclines != null) {
                    eventDeclinesSet = new HashSet<>(eventDeclines);
                }
                Log.w("T", "eventDeclinesSet: " + eventDeclinesSet);
                if (eventDeclinesSet.size() == 0 || !eventDeclinesSet.contains(LoginActivity.currentUserId)) {
                    eventsList.add(eventData);
                    eventsIds.add(dataSnapshot.getKey());

                    eventsInfo.add(LP_Util.getEventInfo(eventData));
                    adapter.notifyDataSetChanged();
                    if (eventsInfo.size() == 1) {
                        textView.setText("Dostępne aktywności:");
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

    private void showAddDialog(final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_person_add_black_24dp);
        builder.setTitle("Czy chcesz dołączyć do wydarzenia?");

        builder.setPositiveButton(Constants.DIALOG_OPTION_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String evId = eventsIds.get(position);
                Log.w("T", "evId: " + evId);
                EventData eventData = eventsList.get(position);
                List<String> eventParticipants = eventData.getEventParticipants();
                Set<String> eventParticipantsSet = new HashSet<>(eventParticipants);
                if (!eventParticipantsSet.contains(LoginActivity.currentUserId)) {
                    eventsInfo.remove(LP_Util.getEventInfo(eventData));
                    eventParticipants.add(LoginActivity.currentUserId);
                    eventData.setEventParticipants(eventParticipants);
                    eventsInfo.add(LP_Util.getEventInfo(eventData));
                    adapter.notifyDataSetChanged();

                    Map<String, Object> eventsData = new HashMap<>();
                    eventsData.put("/" + evId, eventData);
                    mDatabase.updateChildren(eventsData);
                }
            }
        });

        builder.setNegativeButton(Constants.DIALOG_OPTION_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String evId = eventsIds.get(position);
                EventData eventData = eventsList.get(position);
                List<String> eventDeclines = eventData.getEventDeclines() != null ?
                        eventData.getEventDeclines() :
                        new ArrayList<String>();
                eventDeclines.add(LoginActivity.currentUserId);
                eventData.setEventDeclines(eventDeclines);

                Map<String, Object> eventsData = new HashMap<>();
                eventsData.put("/" + evId, eventData);
                mDatabase.updateChildren(eventsData);

                eventsInfo.remove(LP_Util.getEventInfo(eventData));
                adapter.notifyDataSetChanged();
                if (eventsInfo.isEmpty()) {
                    textView.setText("Brak dostępnych aktywności.");
                }
            }
        });

        builder.show();
    }

    private void showDeleteDialog(final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_data_delete);
        builder.setTitle("Czy chcesz wycofać się z tej aktywności?");

        builder.setPositiveButton(Constants.DIALOG_OPTION_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String evId = eventsIds.get(position);
                EventData eventData = eventsList.get(position);
                List<String> eventParticipants = eventData.getEventParticipants();
                Set<String> eventParticipantsSet = new HashSet<>(eventParticipants);
                if (eventParticipantsSet.contains(LoginActivity.currentUserId)) {
                    eventsInfo.remove(LP_Util.getEventInfo(eventData));
                    adapter.notifyDataSetChanged();
                    if (eventsInfo.isEmpty()) {
                        textView.setText("Brak dostępnych aktywności.");
                    }

                    eventParticipants.remove(LoginActivity.currentUserId);
                    eventData.setEventParticipants(eventParticipants);

                    List<String> eventDeclines = eventData.getEventDeclines() != null ?
                            eventData.getEventDeclines() :
                            new ArrayList<String>();
                    eventDeclines.add(LoginActivity.currentUserId);
                    eventData.setEventDeclines(eventDeclines);

                    Map<String, Object> eventsData = new HashMap<>();
                    eventsData.put("/" + evId, eventData);
                    mDatabase.updateChildren(eventsData);
                }
            }
        });

        builder.setNegativeButton(Constants.DIALOG_OPTION_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

}
