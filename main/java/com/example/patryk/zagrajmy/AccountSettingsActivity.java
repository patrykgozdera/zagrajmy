package com.example.patryk.zagrajmy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatPreferenceActivity {
    private FirebaseAuth mAuth;

    private  String firstName, lastName, email, phoneNb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ustawienia konta");

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new AccountMainSettingsFragment()).commit();

        FirebaseUser user = mAuth.getCurrentUser();

        firstName = user.getDisplayName() != null ?
                user.getDisplayName().split(" ")[0] :
                null;

        lastName = user.getDisplayName() != null ?
                user.getDisplayName().split(" ")[1] :
                null;

        email = user.getEmail();

        phoneNb = user.getPhoneNumber();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fName", firstName);
        editor.putString("lName", lastName);
        editor.putString("uEmail", email);
        editor.putString("uPhone", phoneNb);
        editor.commit();

    }

    public static class AccountMainSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.account_preferences);

            bindPreferenceSummaryToValue(findPreference("fName"));
            bindPreferenceSummaryToValue(findPreference("lName"));
            bindPreferenceSummaryToValue(findPreference("uEmail"));
            bindPreferenceSummaryToValue(findPreference("uPhone"));
        }

        @Override
        public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen,
                                              Preference preference) {
            String key = preference.getKey();
            if (key.equals("deletePref")) {
                showDialog();
                return true;
            }
            return false;
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(android.R.drawable.ic_menu_delete);
            builder.setTitle("Czy na pewno chcesz usunąć dane?");

            builder.setPositiveButton(Constants.DIALOG_OPTION_YES, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    String key = mDatabase.child("eventsData").getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, null);
                    mDatabase.updateChildren(childUpdates);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            preference.setSummary(stringValue);

            switch (preference.getKey()) {
                case "fName":
                    //
                    break;
                case "lName":
                    //
                    break;
                case "uEmail":
                    //
                    break;
                case "uPhone":
                    //
                    break;
            }

            return true;
        }
    };
}
