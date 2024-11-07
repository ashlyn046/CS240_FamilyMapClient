package com.bignerdranch.android.client;

import org.junit.Test;
import RequestResult.*;
import static org.junit.Assert.*;

//Write JUnit tests to verify that your Server Proxy class works correctly. You should have tests for the following functionality:
//
//        Login method
//        Registering a new user
//        Retrieving people related to a logged in/registered user
//        Retrieving events related to a logged in/registered user

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

//ALL TESTS PASS!
public class ServerProxyTest {

    //necessary variables to create an instance of our server proxy
    String host = "localhost"; //10.0.2.2
    Integer port = 80;

    @Test
    public void RegisterTestPass() {
        //Successfully Registering in a valid user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);

        //clearing db
        sp.clear();

        //calling register on it with a register request
        RegisterRequest rreq= new RegisterRequest("r.username", "password", "email", "fname", "lname", "gender");

        //passing the request to the register function of the sp
        RegisterResult rres = sp.register(rreq);

        //asserting that the login was a success
        assertTrue(rres.getSuccess());
    }

    @Test
    public void RegisterTestFail() {
        //Attempting to register the same user again (this will fail)

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        //calling register on it with a register request
        RegisterRequest rreq= new RegisterRequest("r.username", "password", "email", "fname", "lname", "gender");

        //passing the request to the register function of the sp
        RegisterResult rres = sp.register(rreq);

        //passing it to register function AGAIN
        rres = sp.register(rreq);

        //asserting that the login was a success
        assertFalse(rres.getSuccess());
    }

    @Test
    public void LoginTestPass() {
        //Successfully logging in a valid user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        //Registering a user to then log in
        RegisterRequest rreq= new RegisterRequest("l.username", "password", "email", "fname", "lname", "gender");
        RegisterResult rres = sp.register(rreq);

        //calling login on it with a login request
        LoginRequest lreq= new LoginRequest("l.username", "password");

        //passing the request to the login funciton of the sp
        LoginResult lres = sp.login(lreq);

        //asserting that the login was a success
        assertTrue(lres.getSuccess());
    }

    @Test
    public void LoginTestFail() {
        //Attempting to log in an invalid user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        //creating a login request for an INVALID user
        LoginRequest lreq= new LoginRequest("invalid", "user");

        //passing the request to the login funciton of the sp
        LoginResult lres = sp.login(lreq);

        //asserting that the login was a success
        assertFalse(lres.getSuccess());
    }

    @Test
    public void GetPeopleTestPass() {
        //Successfully getting all the people associated with a user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        //Registering a user to then log in
        RegisterRequest rreq= new RegisterRequest("p.username", "password", "email", "fname", "lname", "gender");
        RegisterResult rres = sp.register(rreq);

        //logging that user in and getting the autoken
        LoginRequest lreq= new LoginRequest("p.username", "password");
        LoginResult lres = sp.login(lreq);
        String token = lres.getAuthtoken();

        //getting all the people
        PersonsResult pres = sp.getPeople(token);

        //asserting that the getpeople request was a success
        assertTrue(pres.getSuccess());
    }

    @Test
    public void GetPeopleTestFail() {
        //Sending in an invalid authtoken

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        String token = "invalid authtoken";

        //getting all the people
        PersonsResult pres = sp.getPeople(token);

        //asserting that the getpeople request was a failure
        assertNull(pres.getData());
    }

    @Test
    public void GetEventsTestPass() {
        //Successfully getting all the people associated with a user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        //Registering a user to then log in
        RegisterRequest rreq= new RegisterRequest("e.username", "password", "email", "fname", "lname", "gender");
        RegisterResult rres = sp.register(rreq);

        //logging that user in and getting the autoken
        LoginRequest lreq= new LoginRequest("e.username", "password");
        LoginResult lres = sp.login(lreq);
        String token = lres.getAuthtoken();

        //getting all the people
        EventsResult eres = sp.getEvents(token);

        //asserting that the getpeople request was a success
        assertTrue(eres.getSuccess());
    }

    @Test
    public void GetEventsTestFail() {
        //Sending in an invalid authtoken

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);
        sp.clear();

        String token = "invalid authtoken";

        //getting all the people
        EventsResult eres = sp.getEvents(token);

        //asserting that the getpeople request was a failure
        assertNull(eres.getData());
    }


    @Test
    public void ClearTestPass() {
        //Successfully getting all the people associated with a user

        //creating an instance of our server proxy
        ServerProxy sp = new ServerProxy(host, port);

        //Registering a user
        RegisterRequest rreq= new RegisterRequest("c.username", "password", "email", "fname", "lname", "gender");
        RegisterResult rres = sp.register(rreq);

        //Making sure the user registered
        assertTrue(rres.getSuccess());

        //clearing the db
        ClearResult cres = sp.clear();

        //asserting that the getpeople request was a success
        assertTrue(cres.getSuccess());
    }

}