package com.bignerdranch.android.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_GROUP_POSITION = 0;
    private static final int EVENT_GROUP_POSITION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = findViewById(R.id.searchActivitySearchView);

        RecyclerView recyclerView = findViewById(R.id.searchActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ActionBar actionBar = getSupportActionBar();

        if (!Objects.isNull(actionBar))
        {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_back_arrow_24);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {

                DataCache.search(newText);

                List<Person> people = DataCache.getPersonSearchList();
                List<Event> events = DataCache.getEventSearchList();

                SearchAdapter adapter = new SearchAdapter(people, events);
                recyclerView.setAdapter(adapter);

                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        private SearchAdapter(List<Person> people, List<Event> events)
        {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position)
        {
            return position < this.people.size() ? PERSON_GROUP_POSITION : EVENT_GROUP_POSITION;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view;

            if(viewType == PERSON_GROUP_POSITION)
            {
                view = getLayoutInflater().inflate(R.layout.item_person, parent, false);
            }
            else
            {
                view = getLayoutInflater().inflate(R.layout.item_event, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position)
        {
            if(position < this.people.size())
            {
                holder.bind(this.people.get(position));
            }
            else
            {
                holder.bind(this.events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount()
        {
            return this.people.size() + this.events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView genderImage;
        private final TextView topView;
        private final TextView bottomView;

        private final int viewType;
        private Person person;
        private Event event;

        public SearchViewHolder(@NonNull View itemView, int viewType)
        {
            super(itemView);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENT_GROUP_POSITION)
            {
                genderImage = null;
                topView = itemView.findViewById(R.id.expandableListEventDetails);
                bottomView = itemView.findViewById(R.id.expandableListEventAssociatedPerson);
            }
            else
            {
                genderImage = itemView.findViewById(R.id.expandableListPersonGender);
                topView = itemView.findViewById(R.id.expandableListPersonName);
                bottomView = null;
            }
        }

        private void bind(Person person)
        {
            this.person = person;
            if(person.getGender().compareTo("m") == 0)
            {
                genderImage.setImageResource(R.drawable.baseline_man_24);
            }
            else
            {
                genderImage.setImageResource(R.drawable.baseline_woman_24);
            }

            String fullName = person.getFirstName() + " " + person.getLastName();

            topView.setText(fullName);
            topView.setGravity(Gravity.CENTER_VERTICAL);

        }

        private void bind(Event event)
        {
            this.event = event;

            String eventDetails = event.getEventType() + ": " + event.getCity() + ", " +
                    event.getCountry() + " (" + event.getYear() + ")";
            topView.setText(eventDetails);

            Person associatedPerson = DataCache.getPersonById(event.getPersonID());
            String associatedPersonString = associatedPerson.getFirstName() + " " +
                    associatedPerson.getLastName();
            bottomView.setText(associatedPersonString);

        }

        @Override
        public void onClick(View v)
        {

            if(this.viewType == EVENT_GROUP_POSITION)
            {
                Event currEvent = this.event;

                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.CURR_EVENT_KEY, currEvent.getEventID());
                startActivity(intent);
            }
            else
            {
                String currPerson = this.person.getPersonID();
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.CURR_PERSON_KEY, currPerson);
                startActivity(intent);
            }

        }
    }
}