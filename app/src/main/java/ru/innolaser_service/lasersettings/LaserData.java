package ru.innolaser_service.lasersettings;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Vladislav&Maya on 18.09.2016.
 */
public class LaserData {
    private static LaserData ourInstance = new LaserData();

    public static LaserData getInstance() {
        return ourInstance;
    }

    Socket main_socket;
    BufferedReader in;
    PrintWriter out;

    private LaserData() {
        main_socket = null;
        in = null;
        out = null;
    }

    public void SetMainSocket(Socket socket) {
        Log.d("LASLOG", "Socket set");
        if (socket != null)
        {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                Log.d("LASLOG", "Socket successfully set");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        main_socket = socket;
    }

    public Socket GetMainSocket() {
        return main_socket;
    }

    public BufferedReader GetBufferedReader() {
        return in;
    }

    public PrintWriter GetPrintWriter() {
        return out;
    }
}
