package com.bignerdranch.android.client;
import android.util.Log;

import RequestResult.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class ServerProxy {
    //contains exactly the same APIs or methods as your server
    //needs to implement the functions that the client wants to call on the server
    //methods

    //THESE METHODS 1. serialize the request object as JSON
    //2. construct HTTP request
    //3. send req to server (including JSON in body)
    //4. recieve HTTP response with JSON result in body
    //5. Serialize and return

    //create a constructor that takes port and host?
    //instance vars
    static int port;
    static String host;

    //a constructor that takes host and port and sets them
    public ServerProxy(String host, int port){
        this.host = host;
        this.port = port;
    }

    //don't need a main method

    public static LoginResult login(LoginRequest req){
        //this method takes a login request, send a post request to the server and should return a login result

        //creating a login result to return
        LoginResult lres = null;
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            //FIXXX this doesn't work with the port and host that I have here??? need to use datacashe ones
            URL url = new URL("http://" + host+ ":" + port + "/user/login");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);	// There is a request body

            //we DONT need an authtoken for this
            // Add an auth token to the request in the HTTP "Authorization" header
            //http.addRequestProperty("Authorization", "afj232hj2332");

            // Connect to the server and send the HTTP request
            http.connect();

            // now we get the JSON string
            Gson gson = new Gson();
            String reqData = gson.toJson(req); //this takes the request that was passed in and puts it in reqdata

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(reqData, reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully logged in");
                InputStream respBody = http.getInputStream();

                //making the response data into a string
                String respData = readString(respBody);

                //filling the login result
                lres = (LoginResult) gson.fromJson(respData, LoginResult.class);
            }
            else {
                //then the response was not good, mesning the login failed
                lres = new LoginResult( null, null, null, false,"Error: login failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            lres = new LoginResult(null, null, null, false, "Error: login failed");
        }

        return lres;
    }

    public static RegisterResult register(RegisterRequest req) {
        //this method takes a login request, send a post request to the server and should return a login result

        //creating a login result to return
        RegisterResult rres = null;
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + host + ":" + port + "/user/register");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);	// There is a request body

            //we DONT need an authtoken for this
            // Add an auth token to the request in the HTTP "Authorization" header
            //http.addRequestProperty("Authorization", "afj232hj2332");

            // Connect to the server and send the HTTP request
            http.connect();

            // now we get the JSON string
            Gson gson = new Gson();
            String reqData = gson.toJson(req); //this takes the request that was passed in and puts it in reqdata

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(reqData, reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully registered user");
                InputStream respBody = http.getInputStream();

                //making the response data into a string
                String respData = readString(respBody);

                //filling the login result
                rres = (RegisterResult) gson.fromJson(respData, RegisterResult.class);
            }
            else {
                rres = new RegisterResult(null, null, null, false, "Error: register failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            rres= new RegisterResult(null, null, null, false, "Error: register failed");
        }

        return rres;
    }

    //FIXXX
    public static PersonsResult getPeople(String token){
        //getting ALL people associated with a given user

        PersonsResult pres = null;
        try {
            // creating a gson object for later
            Gson gson = new Gson();

            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + host + ":" + port + "/person");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");

            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);


            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", token);

            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                pres = (PersonsResult) gson.fromJson(respData, PersonsResult.class);
            }
            else {
                pres= new PersonsResult(null, true, "Error: couldn't get people");
            }

        } catch (IOException e) {
            e.printStackTrace();
            pres = new PersonsResult( null, true, "Error: couldn't get people");
        }

        return pres;
    }

    //FIXX same as getPeople
    public static EventsResult getEvents(String token) {
        //getting ALL events associated with a given person

        EventsResult eres = null;
        try {
            // creating a gson object for later
            Gson gson = new Gson();

            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + host+ ":" + port + "/event");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");

            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);


            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", token);

            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                eres = (EventsResult) gson.fromJson(respData, EventsResult.class);
            }
            else {
                eres = new EventsResult(null, true, "Error: couldn't get events");
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            eres = new EventsResult( null, true, "Error: couldn't get events");
        }

        return eres;
    }

    ClearResult clear(){
        ClearResult cres = null;

        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + host + ":" + port + "/clear");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();


            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);	// There is a request body

            //we DONT need an authtoken for this
            // Add an auth token to the request in the HTTP "Authorization" header
            //http.addRequestProperty("Authorization", "afj232hj2332");

            // Connect to the server and send the HTTP request
            http.connect();

            // now we get the JSON string
            Gson gson = new Gson();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully cleared");
                InputStream respBody = http.getInputStream();

                //making the response data into a string
                String respData = readString(respBody);

                //filling the login result
                cres = (ClearResult) gson.fromJson(respData, ClearResult.class);
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                //making login result the result that we got, it will be a failed result
                cres = (ClearResult) gson.fromJson(respData, ClearResult.class);
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            cres = null;
        }


        return cres;
    }

    //read and write string functions
    public static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    public static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
