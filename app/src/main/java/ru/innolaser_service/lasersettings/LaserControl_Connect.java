package ru.innolaser_service.lasersettings;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LaserControl_Connect extends AppCompatActivity implements View.OnClickListener {
    EditText editTextAddress, editTextPort;
    Button buttonConnect;
    Button buttonDisconnect;
    LaserControl_Connect MainActivity;
    ConnectTask connect_task;
    LaserData AppData;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectButton: {
                connect_task = null;
                connect_task = new ConnectTask(
                        editTextAddress.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()));
                connect_task.execute();
                buttonConnect.setEnabled(false);
                Log.d("LASLOG", "Start connection task");
                break;
            }
            case R.id.disconnectButton: {
                Socket socket = null;
                try {
                    socket = connect_task.get(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                finally {
                    Log.d("LASLOG", "Socket close");
                    try {
                        if (socket != null)
                            socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                buttonConnect.setEnabled(true);
                buttonDisconnect.setEnabled(false);
                break;
            }
        }
    }

    class ConnectTask extends AsyncTask<Void, Void, Socket> {
        String dstAddress;
        int dstPort;
        boolean isConnected;

        ConnectTask(String Host, int Port) {
            dstAddress = Host;
            dstPort = Port;
            isConnected = false;
        }

        @Override
        protected Socket doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                socket.setKeepAlive(false);
                socket.setSoTimeout(30);
                if (socket != null)
                    isConnected = socket.isConnected();
                else
                    isConnected = false;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);
            if (isConnected) {
                buttonConnect.setEnabled(false);
                buttonDisconnect.setEnabled(true);
                AppData.SetMainSocket(socket);
                Intent intent = new Intent(MainActivity, LaserControl_ControlSolidStateLaser.class);
                startActivity(intent);
                Log.d("LASLOG", "Connection complete, start job activity");
            }
            else
            {
                buttonConnect.setEnabled(true);
                buttonDisconnect.setEnabled(false);
                AppData.SetMainSocket(null);
                Log.d("LASLOG", "Connection fail");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonDisconnect = (Button) findViewById(R.id.disconnectButton);
        MainActivity = this;
        AppData = LaserData.getInstance();

        // Setup states
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);

        // Listeners
        buttonConnect.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);
    }
}
