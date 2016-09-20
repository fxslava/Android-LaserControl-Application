package ru.innolaser_service.lasersettings;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LaserControl_ControlSolidStateLaser extends AppCompatActivity implements View.OnClickListener {
    LaserData AppData;
    Button buttonStart;
    Button buttonStop;
    ToggleButton buttonSimmer;
    TextView textEnergy;
    TextView textFrequency;
    TextView textDuration;
    TextView textState;
    LaserControl_ControlSolidStateLaser activity;
    Thread socketListenerThread;

    /*class SocketListenerThread implements Runnable {
        public Thread t;
        boolean stop = false;
        boolean suspended = false;
        String response;

        SocketListenerThread() {
            Log.d("LASLOG", "Socket listener.");
        }

        public void run() {
            Log.d("LASLOG", "Socket listener running.");

            Socket socket     = AppData.GetMainSocket();
            BufferedReader in = AppData.GetBufferedReader();

            while (!stop) {
                try {
                    synchronized (socket) {
                        response = in.readLine();
                        Log.d("LASLOG", "Server: " + response + "- Received");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textState.setText(response.toString());
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                synchronized (this) {
                    while (suspended) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.d("LASLOG", "Socket listener exiting.");
        }

        public void start () {
            Log.d("LASLOG", "Socket listener started.");
            if (t == null) {
                t = new Thread (this);
                t.start ();
            }
        }

        public void stop () {
            Log.d("LASLOG", "Socket listener stop.");
            stop = true;
        }

        void suspend() {
            suspended = true;
        }

        synchronized void resume() {
            suspended = false;
            notify();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        AppData = LaserData.getInstance();
        buttonStart   = (Button)       findViewById(R.id.buttonStart);
        buttonStop    = (Button)       findViewById(R.id.buttonStop);
        buttonSimmer  = (ToggleButton) findViewById(R.id.toggleSimmer);
        textEnergy    = (TextView)     findViewById(R.id.editTextEnergy);
        textFrequency = (TextView)     findViewById(R.id.editTextFrequency);
        textDuration  = (TextView)     findViewById(R.id.editTextDuration);
        textState     = (TextView)     findViewById(R.id.textViewState);
        activity = this;

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonSimmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Socket socket     = AppData.GetMainSocket();
                BufferedReader in = AppData.GetBufferedReader();
                PrintWriter out   = AppData.GetPrintWriter();

                if (socket != null && socket.isConnected()) {
                    if (isChecked) {
                        out.println("LAS:0:SIMMER=ON");
                        out.flush();
                        Log.d("LASLOG", "Simmer On");
                    }
                    else {
                        out.println("LAS:0:SIMMER=OFF");
                        out.flush();
                        Log.d("LASLOG", "Simmer Off");
                    }
                }
            }
        });

        TextWatcher twe = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Socket socket     = AppData.GetMainSocket();
                BufferedReader in = AppData.GetBufferedReader();
                PrintWriter out   = AppData.GetPrintWriter();

                if (socket != null && socket.isConnected()) {
                    out.println("LAS:0:ENERGY=" + s);
                    out.flush();
                    Log.d("LASLOG", "Energy set");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher twf = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Socket socket     = AppData.GetMainSocket();
                BufferedReader in = AppData.GetBufferedReader();
                PrintWriter out   = AppData.GetPrintWriter();

                if (socket != null && socket.isConnected()) {
                    out.println("LAS:0:FREQUENCY=" + s);
                    out.flush();
                    Log.d("LASLOG", "Frequency set");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher twd = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Socket socket     = AppData.GetMainSocket();
                BufferedReader in = AppData.GetBufferedReader();
                PrintWriter out   = AppData.GetPrintWriter();
                if (socket != null && socket.isConnected()) {
                    out.println("LAS:0:DURATION=" + s);
                    out.flush();
                    Log.d("LASLOG", "Duration set");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        textEnergy.addTextChangedListener(twe);
        textFrequency.addTextChangedListener(twf);
        textDuration.addTextChangedListener(twd);

        socketListenerThread = new Thread(new Runnable() {
            String response;

            @Override
            public void run() {
                Socket socket     = AppData.GetMainSocket();
                BufferedReader in = AppData.GetBufferedReader();

                Log.d("LASLOG", "Start socket listener");

                boolean isConnected;
                //synchronized (socket) {
                   isConnected = socket.isConnected();
                //}
                while (isConnected) {
                    //synchronized (socket) {
                        try {
                            response = in.readLine();
                            Log.d("LASLOG", "Server: " + response + "- Received");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textState.setText(response.toString());
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isConnected = socket.isConnected();
                    //}
                }
                Log.d("LASLOG", "Listener closed");
            }
        });
        socketListenerThread.start();
    }

    @Override
    protected void onDestroy() {
        Log.d("LASLOG", "Control activity destroy");
        //socketListenerThread.

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Socket socket     = AppData.GetMainSocket();
        BufferedReader in = AppData.GetBufferedReader();
        PrintWriter out   = AppData.GetPrintWriter();

        if (socket != null && socket.isConnected()) {
            switch (v.getId()) {
                case R.id.buttonStart: {
                    out.println("LAS:0:ENERGY=" + textEnergy.getText().toString());
                    out.println("LAS:0:DURATION=" + textDuration.getText().toString());
                    out.println("LAS:0:FREQUENCY=" + textFrequency.getText().toString());
                    out.println("LAS:0:START");
                    out.flush();
                    Log.d("LASLOG", "Laser start");
                    break;
                }
                case R.id.buttonStop: {
                    out.println("LAS:0:STOP");
                    out.flush();
                    Log.d("LASLOG", "Laser stop");
                    break;
                }
            }
        }
    }
}