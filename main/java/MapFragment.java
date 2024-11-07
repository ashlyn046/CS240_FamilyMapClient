package com.bignerdranch.android.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Event;
import model.Person;

public class MapFragment extends Fragment implements    OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

        private GoogleMap map;
        private TextView eventInfo;
        private ImageView genderImage;
        private boolean eventIsSelected = false;
        private String currAssociatedPersonId;
        private Event currEvent;
        private Line line;


        @Override
        public View onCreateView(   @NonNull LayoutInflater layoutInflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            super.onCreateView(layoutInflater, container, savedInstanceState);

            View view = layoutInflater.inflate(R.layout.fragment_map, container, false);
            eventInfo = view.findViewById(R.id.mapEventInfo);
            genderImage = view.findViewById(R.id.mapLogoType);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;

            mapFragment.getMapAsync(this);

            setHasOptionsMenu(true);

            LinearLayout linearLayout = view.findViewById(R.id.mapLinearLayout);

            linearLayout.setOnClickListener(v -> {

                if (eventIsSelected)
                {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra(PersonActivity.CURR_PERSON_KEY, currAssociatedPersonId);
                    startActivity(intent);

                }
            });

            return view;
        }

        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
        {
            super.onCreateOptionsMenu(menu, inflater);
            if(requireActivity().getIntent().getExtras() == null)
            {
                inflater.inflate(R.menu.item_header, menu);
            }
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {
            Intent intent;

            switch(item.getItemId())
            {
                case R.id.search_button:
                    intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.settings_button:
                    intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap)
        {
            map = googleMap;
            drawMarkers();
        }

        @Override
        public void onResume()
        {
            super.onResume();
            if(!Objects.isNull(map))
            {
                drawMarkers();
            }

            boolean displayCurrEvent = false;

            if(!Objects.isNull(currEvent))
            {
                for(Map.Entry<Person, List<Event>> entry : DataCache.getUserEvents().entrySet())
                {
                    if(!Objects.isNull(entry.getValue()))
                    {
                        for(Event event : entry.getValue())
                        {
                            if(currEvent.getEventID().compareTo(event.getEventID()) == 0)
                            {
                                displayCurrEvent = true;
                            }
                        }
                    }
                }

                if(displayCurrEvent)
                {
                    focus_map(currEvent);
                }
                else
                {
                    genderImage.setImageResource(R.drawable.baseline_android_24);
                    eventInfo.setText(R.string.mapFragmentClickMessage);
                }
            }
        }

        public void drawMarkers()
        {
            float permColorCode = 0.0F;
            float tempColorCode = permColorCode;
            map.clear();

            for(Map.Entry<String, Event> entry : DataCache.getEvents().entrySet())
            {
                if(DataCache.getColors() == null)
                {
                    DataCache.assignEventColor(   entry.getValue().getEventType().toLowerCase(),
                            permColorCode);
                }
                else if (DataCache.getColors().get(entry.getValue().getEventType().toLowerCase())
                        != null)
                {
                    tempColorCode = DataCache.getColors().get(entry.getValue().getEventType()
                            .toLowerCase());
                }
                else
                {
                    permColorCode += 30;

                    if(permColorCode > 360)
                    {
                        permColorCode %= 30;
                        permColorCode += 3;
                    }

                    tempColorCode = permColorCode;
                    DataCache.assignEventColor(   entry.getValue().getEventType().toLowerCase(),
                            permColorCode);
                }

                Marker marker = map.addMarker(new MarkerOptions().
                        position(new LatLng(entry.getValue().getLatitude(),
                                entry.getValue().getLongitude())).
                        icon(BitmapDescriptorFactory.defaultMarker(tempColorCode)));
                assert marker != null;
                marker.setTag(entry.getValue());
            }
            map.setOnMarkerClickListener(this);

            if(!Objects.isNull(getArguments()))
            {
                assert getArguments() != null;
                String currEventId = getArguments().getString(EventActivity.CURR_EVENT_KEY);
                Event currEvent = DataCache.getEventById(currEventId);
                focus_map(currEvent);
            }
        }

        @Override
        public boolean onMarkerClick(@NonNull Marker marker)
        {

            Event clickedEvent = (Event) marker.getTag();
            currEvent = clickedEvent;
            assert clickedEvent != null;
            focus_map(clickedEvent);

            return false;
        }

        public void focus_map(Event clickedEvent)
        {
            eventIsSelected = true;
            Person associatedPerson = DataCache.getPersonById(clickedEvent.getPersonID());
            currAssociatedPersonId = associatedPerson.getPersonID();

            map.animateCamera(CameraUpdateFactory.newLatLng
                    (new LatLng(clickedEvent.getLatitude(), clickedEvent.getLongitude())));

            String eventInfoString =    associatedPerson.getFirstName() + " " +
                    associatedPerson.getLastName() + "\n" +
                    clickedEvent.getEventType() + ": " +
                    clickedEvent.getCity() + ", " +
                    clickedEvent.getCountry() + " (" +
                    clickedEvent.getYear() + ")";

            eventInfo.setText(eventInfoString);

            if(associatedPerson.getGender().compareTo("m") == 0)
            {
                genderImage.setImageResource(R.drawable.baseline_man_24);
            }
            else
            {
                genderImage.setImageResource(R.drawable.baseline_woman_24);
            }

            if(line.getLines() != null)
            {
                for (Polyline p : line.getLines())
                {
                    p.remove();
                }
            }

            List<Polyline> polylineList = new ArrayList<>();
            if(line.getSpouseLines())
            {
                if (DataCache.getPersonById(associatedPerson.getSpouseID()) != null)
                {
                    Person associatedSpouse = DataCache.getPersonById(associatedPerson.getSpouseID());
                    if(DataCache.getUserEvents().get(associatedSpouse) != null)
                    {
                        Event spouseBirthday = Objects.requireNonNull
                                (DataCache.getUserEvents().get(associatedSpouse)).get(0);

                        LatLng startPoint = new LatLng( clickedEvent.getLatitude(),
                                clickedEvent.getLongitude());

                        LatLng endPoint = new LatLng(   spouseBirthday.getLatitude(),
                                spouseBirthday.getLongitude());

                        PolylineOptions options = new PolylineOptions()
                                .add(startPoint)
                                .add(endPoint)
                                .color(Color.BLUE)
                                .geodesic(false)
                                .width(20);
                        Polyline polyline = map.addPolyline(options);
                        polylineList.add(polyline);
                    }
                }
            }

            if(line.getFamilyTreeLines())
            {
                family_tree_lines(associatedPerson, clickedEvent, 20, polylineList);
            }

            if(line.getLifeStoryLines())
            {
                if(DataCache.getUserEvents().get(associatedPerson) != null)
                {
                    Event birthEvent =  Objects.requireNonNull
                            (DataCache.getUserEvents().get(associatedPerson)).get(0);

                    life_story_lines(associatedPerson, birthEvent, 0, polylineList);
                }
            }

            line.setLines(polylineList);
        }

        public void family_tree_lines(Person person, Event event, int width,
        List<Polyline> polylineList)
        {
            if(DataCache.getPersonById(person.getFatherID()) != null)
            {
                Person father = DataCache.getPersonById(person.getFatherID());
                Person mother = DataCache.getPersonById(person.getMotherID());
                Event fatherBirth;
                Event motherBirth;

                if (DataCache.getUserEvents().get(father) != null)
                {
                    fatherBirth = Objects.requireNonNull(DataCache.getUserEvents().get(father)).get(0);
                    family_tree_lines(father, fatherBirth, width - 5, polylineList);
                    LatLng startPoint = new LatLng(event.getLatitude(), event.getLongitude());
                    LatLng endPoint = new LatLng(fatherBirth.getLatitude(), fatherBirth.getLongitude());
                    PolylineOptions options = new PolylineOptions()
                            .add(startPoint)
                            .add(endPoint)
                            .color(Color.BLACK)
                            .geodesic(false)
                            .width(width);
                    Polyline polyline = map.addPolyline(options);
                    polylineList.add(polyline);
                }
                if (DataCache.getUserEvents().get(mother) != null)
                {
                    motherBirth = Objects.requireNonNull(DataCache.getUserEvents().get(mother)).get(0);
                    family_tree_lines(mother, motherBirth, width - 5, polylineList);
                    LatLng startPoint = new LatLng(event.getLatitude(), event.getLongitude());
                    LatLng endPoint = new LatLng(motherBirth.getLatitude(), motherBirth.getLongitude());
                    PolylineOptions options = new PolylineOptions()
                            .add(startPoint)
                            .add(endPoint)
                            .color(Color.BLACK)
                            .geodesic(false)
                            .width(width);
                    Polyline polyline = map.addPolyline(options);
                    polylineList.add(polyline);
                }
            }
        }

        public void life_story_lines(Person person, Event prevEvent, int currIndex,
        List<Polyline> polylineList)
        {
            if((DataCache.getUserEvents().get(person) != null) &&
                    ((currIndex + 1) <
                            Objects.requireNonNull(DataCache.getUserEvents().get(person)).size()))
            {
                currIndex += 1;

                Event nextEvent =   Objects.requireNonNull(DataCache.getUserEvents()
                        .get(person)).get(currIndex);

                LatLng startPoint = new LatLng(prevEvent.getLatitude(), prevEvent.getLongitude());
                LatLng endPoint = new LatLng(nextEvent.getLatitude(), nextEvent.getLongitude());

                PolylineOptions options = new PolylineOptions()
                        .add(startPoint)
                        .add(endPoint)
                        .color(Color.RED)
                        .geodesic(false)
                        .width(20);
                Polyline polyline = map.addPolyline(options);
                polylineList.add(polyline);

                life_story_lines(person, nextEvent, currIndex, polylineList);
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState)
        {
            super.onSaveInstanceState(outState);
            boolean mMenuItemChecked = false;
            outState.putBoolean("menu_item_checked", mMenuItemChecked);
        }

}