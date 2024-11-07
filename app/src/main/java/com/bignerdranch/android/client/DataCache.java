package com.bignerdranch.android.client;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import RequestResult.EventsResult;
import RequestResult.PersonsResult;
import model.Event;
import model.Person;

public class DataCache {

    //creating a private static variable, this will only happen one time
    private static DataCache instance = new DataCache();

    //here, we make a getInstance function that creates our instance
    //now we need a method to access it
    public static DataCache getInstance(){
        return instance;
    }

    //BUT there is a default constructor that might be used to
    //create another!
    //create a private constructor to override default
    private DataCache(){
        //do nothing so you can't accidentally get more than one
        //no other way to get an instance
    }

    //host and port and user's person obj
    private static String port;
    private static String host;
    private static Person user;

    //other vars i might need
    //make a map keyed by personID and eventID
    static Map<String, Person> people;
    static Map<String, Event> events;

    //you'll need to display lists of all events for each person
    //keyed by personID
    static Map<Person, List<Event>> personEvents;

    //all the children for each person
    private static Map<Person, List<Person>> parentsChildren = new HashMap<>(); //NOTE

    //FIX I DONT THINK I WILL ENED THISSS!!!! and not changed
    private static Map<Person, List<Event>> userEvents;
    private static Map<Person, List<Event>> fatherEvents = new HashMap<>();
    private static Map<Person, List<Event>> motherEvents = new HashMap<>();


    //settings
    private static Map<String, Float> colors = new HashMap<>();
    private static List<Event> eventSearchList;
    private static List<Person> personSearchList;

    //Booleans for whether they match certain settings
    private static boolean fatherValues = true;
    private static boolean motherValues = true;
    private static boolean maleEvents = true;
    private static boolean femaleEvents = true;

    //getters and setters (autogened)


    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        DataCache.port = port;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        DataCache.host = host;
    }

    public static Person getUser() {
        return user;
    }

    public static void setUser(Person user) {
        DataCache.user = user;
    }

    public static Map<String, Person> getPeople() {
        return people;
    }

    public static void setPeople(Map<String, Person> people) {
        DataCache.people = people;
    }

    public static Map<String, Event> getEvents() {
        return events;
    }

    public static void setEvents(Map<String, Event> events) {
        DataCache.events = events;
    }

    public static Map<Person, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public static void setPersonEvents(Map<Person, List<Event>> personEvents) {
        DataCache.personEvents = personEvents;
    }

    public static Map<Person, List<Person>> getParentsChildren() {
        return parentsChildren;
    }

    public static void setParentsChildren(Map<Person, List<Person>> parentsChildren) {
        DataCache.parentsChildren = parentsChildren;
    }

    public static Map<Person, List<Event>> getUserEvents() {
        return userEvents;
    }

    public static void setUserEvents(Map<Person, List<Event>> userEvents) {
        DataCache.userEvents = userEvents;
    }

    public static Map<Person, List<Event>> getFatherEvents() {
        return fatherEvents;
    }

    public static void setFatherEvents(Map<Person, List<Event>> fatherEvents) {
        DataCache.fatherEvents = fatherEvents;
    }

    public static Map<Person, List<Event>> getMotherEvents() {
        return motherEvents;
    }

    public static void setMotherEvents(Map<Person, List<Event>> motherEvents) {
        DataCache.motherEvents = motherEvents;
    }


    public static Map<String, Float> getColors() {
        return colors;
    }

    public static void setColors(Map<String, Float> colors) {
        DataCache.colors = colors;
    }

    public static List<Event> getEventSearchList() {
        return eventSearchList;
    }

    public static void setEventSearchList(List<Event> eventSearchList) {
        DataCache.eventSearchList = eventSearchList;
    }

    public static List<Person> getPersonSearchList() {
        return personSearchList;
    }

    public static void setPersonSearchList(List<Person> personSearchList) {
        DataCache.personSearchList = personSearchList;
    }

    public static boolean getFatherValues() {
        return fatherValues;
    }

    public static void setFatherValues(boolean fatherValues) {
        DataCache.fatherValues = fatherValues;
    }

    public static boolean getMotherValues() {
        return motherValues;
    }

    public static void setMotherValues(boolean motherValues) {
        DataCache.motherValues = motherValues;
    }

    public static boolean getMaleEvents() {
        return maleEvents;
    }

    public static void setMaleEvents(boolean maleEvents) {
        DataCache.maleEvents = maleEvents;
    }

    public static boolean getFemaleEvents() {
        return femaleEvents;
    }

    public static void setFemaleEvents(boolean femaleEvents) {
        DataCache.femaleEvents = femaleEvents;
    }

    //OTHER FUNCTIONS
    public static Person getPersonById(String personId) {
        return getPeople().get(personId);
    }
    public static Event getEventById(String eventId) {
        return getEvents().get(eventId);
    }


    //FUNCTIONS FOR INITIAL DATA CACHE (DONE!!!)
    public static String cacheData(String authToken, String personID){
        getInstance();

        //getting maps and results set up
        PersonsResult pRes = ServerProxy.getPeople(authToken);
        EventsResult eRes = ServerProxy.getEvents(authToken);

        Map<String, Person> people2 = Arrays.stream(pRes.getData()).collect(Collectors.toMap(Person::getPersonID, Function.identity()));
        Map<String, Event> events2 = Arrays.stream(eRes.getData()).collect(Collectors.toMap(Event::getEventID, Function.identity()));
        Map<Person, List<Event>> personEvents2 = new HashMap<>();
        setPeople(people2);
        setEvents(events2);
        setPersonEvents(personEvents2);

        //making families
        Arrays.asList(pRes.getData())
                .forEach(person -> {
                    people.put(person.getPersonID(), person);
                    Optional.ofNullable(DataCache.getPersonById(person.getFatherID()))
                            .ifPresent(father -> parentsChildren.computeIfAbsent(father, k -> new ArrayList<>()).add(person));
                    Optional.ofNullable(DataCache.getPersonById(person.getMotherID()))
                            .ifPresent(mother -> parentsChildren.computeIfAbsent(mother, k -> new ArrayList<>()).add(person));
                });
        setUser(getPersonById(personID));

        //setting personEvents
        for (Event event : eRes.getData()) {
            events.put(event.getEventID(), event);

            List<Event> personEventsList = personEvents.computeIfAbsent(getPersonById(event.getPersonID()), k -> new ArrayList<>());

            if (personEventsList.isEmpty() || personEventsList.get(0).getYear() > event.getYear()) {
                personEventsList.add(0, event);
            }else {
                personEventsList.add(event);
            }

            //creating lists with just the male and just the female events
            Map<Person, List<Event>> maleEventsList = getPersonById(event.getPersonID()).getGender().compareTo("m") == 0 ?
                    Collections.singletonMap(getPersonById(event.getPersonID()), personEventsList) :
                    Collections.emptyMap();
            Map<Person, List<Event>> femaleEventsList = getPersonById(event.getPersonID()).getGender().compareTo("f") == 0 ?
                    Collections.singletonMap(getPersonById(event.getPersonID()), personEventsList) :
                    Collections.emptyMap();
        }
        setUserEvents(getPersonEvents());

        setFamilySide(getPersonById(user.getFatherID()), false, true);
        setFamilySide(getPersonById(user.getMotherID()), true, false);

        //returning the full name of the user
        return user.getFirstName() + " " + user.getLastName();
    }

    protected static void setFamilySide(Person currPerson, boolean maternal, boolean paternal) {
        if(maternal){
            getMotherEvents().put(currPerson, getPersonEvents().get(currPerson));
        }
        else if(paternal){
            getFatherEvents().put(currPerson, DataCache.getPersonEvents().get(currPerson));
        }

        if(!Objects.isNull(currPerson.getFatherID()) && !Objects.isNull(currPerson.getMotherID())) {
            setFamilySide(getPersonById(currPerson.getFatherID()), maternal, paternal);
            setFamilySide(getPersonById(currPerson.getMotherID()), maternal, paternal);
        }
    }


    //FUNCTIONS FOR MAP FRAGMENT
    public static boolean hasEventColor(String eventType) {
        if(eventType==null){
            return false;
        }
        return colors.containsKey(eventType);
    }

    public static float getEventColor(String eventType){
        return colors.get(eventType);
    }

    public static void assignEventColor(String eventType, float color) {
        if(Objects.isNull(colors))
            colors = new HashMap<>();

        colors.put(eventType, color);
    }



    //FUNCTIONS FOR PERSON ACTIVITY
    public static List<Person> getPersonFamilyMembers(Person person)
    {
        List<Person> family = new ArrayList<>();

        family.add(getPersonById(person.getMotherID()));
        family.add(getPersonById(person.getFatherID()));
        family.add(getPersonById(person.getSpouseID()));

        List<Person> children = getParentsChildren().get(person);

        if(!Objects.isNull(children))
        {
            Objects.requireNonNull(children, "children must not be null");
            family.addAll(children);
        }

        return family;
    }



    //FUNCTIONS FOR SEARCH ACTIVITY (DONE!!!)
    public static void search(String query){
        setPersonSearchList(searchPeople(query.toLowerCase()));
        setEventSearchList(searchEvents(query));
    }

    private static List<Person> searchPeople(String query) {
        return DataCache.getPeople().values().stream()
                .filter(p -> containsIgnoreCase(p.getFirstName(), query) ||
                        containsIgnoreCase(p.getLastName(), query))
                .collect(Collectors.toList());
    }

    private static boolean containsIgnoreCase(String str1, String str2) {
        return str1.toLowerCase().contains(str2.toLowerCase());
    }

    private static List<Event> searchEvents(String query) {
        return DataCache.getUserEvents().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().filter(event ->
                        Stream.of(event.getCountry(), event.getCity(), event.getEventType(),
                                        event.getYear().toString(),
                                        DataCache.getPersonById(event.getPersonID()).getFirstName(),
                                        DataCache.getPersonById(event.getPersonID()).getLastName())
                                .map(String::toLowerCase)
                                .anyMatch(field -> field.contains(query.toLowerCase()))))
                .collect(Collectors.toList());
    }



    //FUNCTIONS FOR SETTINGS ACTIVITY (FIX)
    public static void determineCurrentEvents()
    {
        Map<Person, List<Event>> newCurrentEvents = new HashMap<>();

        if (getFatherValues())
            newCurrentEvents.putAll(DataCache.getFatherEvents());

        if (getMotherValues())
            newCurrentEvents.putAll(DataCache.getMotherEvents());

        newCurrentEvents.put(   DataCache.getUser(),
                DataCache.getPersonEvents().get(DataCache.getUser()));

        Person currUserSpouse = DataCache.getPersonById(DataCache.getUser().getSpouseID());
        newCurrentEvents.put(currUserSpouse, DataCache.getPersonEvents().get(currUserSpouse));

        removePeople(newCurrentEvents);

        Map<String, Event> eventIdMap = new HashMap<>();

        for(Map.Entry<Person, List<Event>> entry: newCurrentEvents.entrySet())
        {
            for(Event event : entry.getValue())
                eventIdMap.put(event.getEventID(), event);
        }

        DataCache.setEvents(eventIdMap);
        DataCache.setUserEvents(newCurrentEvents);

    }
    protected static void removePeople(Map<Person, List<Event>> newCurrentEvents)
    {
        List<Person> peopleToRemoveGender = new ArrayList<>();

        if(!DataCache.getMaleEvents())
        {
            for(Map.Entry<Person, List<Event>> entry: newCurrentEvents.entrySet())
            {
                if(entry.getKey().getGender().compareTo("m") == 0)
                    peopleToRemoveGender.add(entry.getKey());
            }
        }
        if (!DataCache.getFemaleEvents())
        {
            for(Map.Entry<Person, List<Event>> entry: newCurrentEvents.entrySet())
            {
                if(entry.getKey().getGender().compareTo("f") == 0)
                    peopleToRemoveGender.add(entry.getKey());
            }
        }

        for(Person person : peopleToRemoveGender)
            newCurrentEvents.remove(person);
    }

    public static void clear(){ //(DONE)
        port = "";
        host = "";
        user = null;

        people = new HashMap<>();
        events = new HashMap<>();

        personEvents = new HashMap<>();
        parentsChildren = new HashMap<>();

        userEvents = new HashMap<>();
        fatherEvents = new HashMap<>();
        motherEvents = new HashMap<>();
        eventSearchList = null;
        personSearchList = null;


        fatherValues = true;
        motherValues = true;
        maleEvents = true;
        femaleEvents = true;
    }
}

