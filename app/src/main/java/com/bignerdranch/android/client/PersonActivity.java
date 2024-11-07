package com.bignerdranch.android.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    public static final String CURR_PERSON_KEY = "CurrPersonKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();

        setContentView(R.layout.activity_person);
        Person currPerson = getCurrentPerson();
        updateView(currPerson);
        setupActionBar(ab);
    }

    private Person getCurrentPerson() {
        Intent intent = getIntent();
        String currPersonId = intent.getStringExtra(CURR_PERSON_KEY);
        return DataCache.getPersonById(currPersonId);
    }

    public void updateView(Person currPerson){
        // Set person name
        String nameString = currPerson.getFirstName() + " " + currPerson.getLastName();
        TextView personField = findViewById(R.id.personActivityName);
        personField.setText(nameString);

        // Set person gender
        String genderString = currPerson.getGender().equals("m") ? "Male" : "Female";
        TextView genderField = findViewById(R.id.personActivityGender);
        genderField.setText(genderString);

        // Set expandable list view
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        List<Event> events = DataCache.getUserEvents().getOrDefault(currPerson, new ArrayList<>());
        List<Person> people = DataCache.getPersonFamilyMembers(currPerson);
         expandableListView.setAdapter(new PersonELAdapter(events, people));
    }

    private void setupActionBar(ActionBar ab) {
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.baseline_back_arrow_24);
            ab.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    private class PersonELAdapter extends BaseExpandableListAdapter{
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;
        private final List<Event> events;
        private final List<Person> people;

        public PersonELAdapter(List<Event> events, List<Person> people){
            this.events = events;
            this.people = people;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) { //FIXXXX
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case PERSON_GROUP_POSITION:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Invalid Group Position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {  ///FIXXX
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return R.string.eventGroupTitle;
                case PERSON_GROUP_POSITION:
                    return R.string.personGroupTitle;
                default:
                    throw new IllegalArgumentException("Invalid Group Position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) { //FIXXX
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                case PERSON_GROUP_POSITION:
                    return people.get(childPosition);
                default:
                    throw new IllegalArgumentException("Invalid Group Position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expandable_list, parent, false);
            }
            TextView titleView = convertView.findViewById(R.id.listTitle);
            titleView.setText((int) getGroup(groupPosition));
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, int childNow, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            View view;
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    view = getLayoutInflater().inflate(R.layout.item_event, parent, false);
                    Event currEvent = events.get(childNow);
                    Person person1 = DataCache.getPersonById(currEvent.getPersonID());
                    setUpEventView(view, currEvent, person1);
                    break;
                case PERSON_GROUP_POSITION:
                    view = getLayoutInflater().inflate(R.layout.item_person, parent, false);
                    Person person2 = people.get(childNow);
                    if (Objects.isNull(person2)) {
                        return convertView;
                    }
                    setUpPersonView(view, person2, childNow);
                    break;
                default:
                    view = null;
            }

            return view;
        }

        private void setUpEventView(View view, Event currEvent, Person currPerson) {
            ImageView currMarker = view.findViewById(R.id.expandableListEventMarker);
            currMarker.setImageResource(R.drawable.baseline_marker_24);

            TextView eventStuff = view.findViewById(R.id.expandableListEventDetails);
            eventStuff.setText(currEvent.getEventType() + ": " +
                    currEvent.getCity() + ", " +
                    currEvent.getCountry() + " (" +
                    currEvent.getYear() + ")");

            TextView name1 = view.findViewById(R.id.expandableListEventAssociatedPerson);
            name1.setText(currPerson.getFirstName() + " " +
                    currPerson.getLastName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventID = currEvent.getEventID();
                    Intent currIntent = new Intent(PersonActivity.this, EventActivity.class);
                    currIntent.putExtra(EventActivity.CURR_EVENT_KEY, eventID);
                    startActivity(currIntent);
                }
            });
        }

        private void setUpPersonView(View view, Person person, int childNow) {
            ImageView currGen = view.findViewById(R.id.expandableListPersonGender);
            currGen.setImageResource(person.getGender().compareTo("m") == 0 ? R.drawable.baseline_man_24 : R.drawable.baseline_woman_24);

            TextView pname2 = view.findViewById(R.id.expandableListPersonName);
            String personNameString = person.getFirstName() + " " +
                    person.getLastName();
            pname2.setText(personNameString);

            TextView pRel = view.findViewById(R.id.expandableListPersonRelationship);
            final int fatherNow = 0;
            final int motherNow = 1;
            final int spouseNow = 2;

            switch (childNow) {
                case fatherNow:
                    pRel.setText(R.string.father);
                    break;
                case motherNow:
                    pRel.setText(R.string.mother);
                    break;
                case spouseNow:
                    pRel.setText(R.string.spouse);
                    break;
                default:
                    pRel.setText(R.string.child);
                    break;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Person newPerson = people.get(childNow);
                    updateView(newPerson);
                }
            });
        }

    }
}