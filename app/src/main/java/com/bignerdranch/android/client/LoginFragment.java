package com.bignerdranch.android.client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;

import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import RequestResult.*;



public class LoginFragment extends Fragment {

    //keys
    private static final String SUCCESS_KEY = "SuccessKey";
    private static final String FULL_NAME_KEY = "FullNameKey";

    //fields to get text from
    private EditText host;
    private EditText port;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;

    //gender buttons
    private RadioGroup gender;
    private RadioButton male;

    //buttons
    private Button signIn;
    private Button register;

    //listener
    private Listener listener;

    //listener interface so we can log the user in and switch to the map in the main activity
    public interface Listener{
        void logUserIn();
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setVars(view);
        enableButtons();

        //UPDATE AND CHANGE HANDLER METHOD
        signIn.setOnClickListener(signInView -> {handle("Login");});
        register.setOnClickListener(registerView -> {handle("Registration");});

        return view;
    }

    void setVars(View view) {
        host = view.findViewById(R.id.serverHostID);
        port = view.findViewById(R.id.serverPortID);
        username = view.findViewById(R.id.usernameID);
        password = view.findViewById(R.id.passwordID);
        firstName = view.findViewById(R.id.firstNameID);
        lastName = view.findViewById(R.id.lasNameID);
        email = view.findViewById(R.id.emailID);
        gender = view.findViewById(R.id.genderID);
        male = view.findViewById(R.id.maleID);

        signIn = view.findViewById(R.id.signInID);
        register = view.findViewById(R.id.registerID);
    }

    void addTextWatcher(TextView textView){
        textView.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //don't need to do anything
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableLogin();
                enableRegistration();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //don't need to do anything
            }
        });
    }

    void enableButtons() {
        addTextWatcher(host);
        addTextWatcher(port);
        addTextWatcher(username);
        addTextWatcher(password);
        addTextWatcher(firstName);
        addTextWatcher(lastName);
        addTextWatcher(email);

        gender.setOnCheckedChangeListener((radioGroup, i) -> enableRegistration());
    }

    public void enableLogin(){
        Boolean canLogin = Stream.of(host, port, username, password).allMatch(field -> field.getText().toString().trim().length() > 0);
        signIn.setEnabled(canLogin);
    }

    public void enableRegistration(){
        boolean canRegister = Stream.of(host, port, username, password, firstName, lastName, email)
                .allMatch(field -> field.getText().toString().trim().length() > 0)
                && gender.getCheckedRadioButtonId() != -1;
        register.setEnabled(canRegister);
    }

    private static class RegisterBackgroundTask implements Runnable {
        private final Handler mHandler;
        private final RegisterRequest regReq;

        public RegisterBackgroundTask(Handler messageHandler, RegisterRequest registerRequest) {
            this.mHandler = messageHandler;
            this.regReq = registerRequest;
        }
        @Override
        public void run(){
            RegisterResult regRes = ServerProxy.register(regReq);
            Message message = Message.obtain();
            Bundle mBundle = new Bundle();

            if(regRes.getSuccess() == false || regRes==null){
                mBundle.putBoolean(SUCCESS_KEY, false);
            }
            else{
                String fname = DataCache.cacheData(regRes.getAuthtoken(),regRes.getPersonID());
                mBundle.putString(FULL_NAME_KEY, fname);
                mBundle.putBoolean(SUCCESS_KEY, true);
            }
            message.setData(mBundle);
            mHandler.sendMessage(message);
        }
    }

    public void failureMessage(){

    }

    private static class LoginBackgroundTask implements Runnable {
        private final Handler mHandler;
        private final LoginRequest logReq;

        private LoginBackgroundTask(Handler messageHandler, LoginRequest loginRequest)
        {
            this.mHandler = messageHandler;
            this.logReq = loginRequest;
        }
        @Override
        public void run()
        {
            LoginResult logRes = ServerProxy.login(logReq);
            Message message = Message.obtain();
            Bundle mBundle = new Bundle();

            if(logRes.getSuccess() == false || logRes==null){
                mBundle.putBoolean(SUCCESS_KEY, false);
            }
            else{
                String fname = DataCache.cacheData(logRes.getAuthtoken(),logRes.getPersonID());
                mBundle.putString(FULL_NAME_KEY, fname);
                mBundle.putBoolean(SUCCESS_KEY, true);
            }
            message.setData(mBundle);
            mHandler.sendMessage(message);
        }
    }

    private void handle(String type) {
        // Set host and port for the DataCache
        DataCache.setHost(host.getText().toString());
        DataCache.setPort(port.getText().toString());
        new ServerProxy(host.getText().toString(), Integer.parseInt(port.getText().toString()));

        // Define a handler to handle messages from background tasks
        Handler uiThreadMessageHandler = new Handler(msg -> {
            Bundle bundle = msg.getData();
            boolean success = bundle.getBoolean(SUCCESS_KEY, false);
            String fullName = bundle.getString(FULL_NAME_KEY, "NoName");

            if (success){
                String message = (type.equals("Login"))
                        ? "Successfully LoggedIn User: "
                        : "Successfully Registered User: ";
                Toast.makeText(getActivity(), message + getString(R.string.firstNameResult, fullName), Toast.LENGTH_SHORT).show();
                if (type.equals("Login")){
                    listener.logUserIn();
                }
            } else{
                int messageResId = (type.equals("Login"))
                        ? R.string.login_fail
                        : R.string.register_fail;
                Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Execute a login or registration task based on the type
        if (type.equals("Login")) {
            LoginRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
            LoginBackgroundTask loginTask = new LoginBackgroundTask(uiThreadMessageHandler, loginRequest);
            Executors.newSingleThreadExecutor().submit(loginTask);
        } else if (type.equals("Registration")) {
            String gender = male.isChecked() ? "m" : "f";
            RegisterRequest registerRequest = new RegisterRequest(username.getText().toString(), password.getText().toString(), email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);
            RegisterBackgroundTask registerTask = new RegisterBackgroundTask(uiThreadMessageHandler, registerRequest);
            Executors.newSingleThreadExecutor().submit(registerTask);
        }
    }
}