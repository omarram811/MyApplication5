package com.example.myapplication5;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketViewModel extends ViewModel {
    private MutableLiveData<String> inputMessage = new MutableLiveData<>();
    private MutableLiveData<String> outputMessage = new MutableLiveData<>();

    public void startListening(String serverIp, int serverPort) {

        new Thread(() -> {
            try {
                Socket socket = new Socket(serverIp, serverPort);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(inputMessage.toString().getBytes());
                outputStream.flush();

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder receivedData = new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null){
                    receivedData.append(line);
                    Log.d("Received Lines", line);
                    outputMessage.postValue(line); //to display latest answer only
                }

                Log.d("SnipsChips", receivedData.toString()); //Doesn't log until server closes
                socket.close();
            } catch (IOException e) {
                Log.d("Reached Exception!!!!!", e.getMessage());
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}