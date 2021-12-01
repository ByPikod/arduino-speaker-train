package me.pikod.siyer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PiManager extends Thread{
    private static PiManager instance;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    final private PiLogger logger = new PiLogger();

    public String[] sounds;

    final public String hc05_adress = "00:20:12:08:96:D5";
    public BluetoothAdapter btAdapter;
    public BluetoothDevice hc05;
    public BluetoothSocket btSocket;
    private AppCompatActivity main;

    public Typeface fontAwesome;

    public PiManager(AppCompatActivity main) {

        this.main = main;
        this.fontAwesome = main.getResources().getFont(R.font.fontawesome);
        instance = this;

    }

    // Listener
    @Override
    public void run(){

        String sdata = "";
        List<String> slist = new ArrayList<>();
        boolean isReading = false;
        boolean isReadingList = false;

        while(btSocket.isConnected()){

            int readed;

            try {

                readed = in.read();

            } catch (IOException e) {

                terminate();
                getLogger().Warn("Failed to read bluetooth socket: "+e.toString());
                break;

            }

            if(isReading){

                if(readed == 255) continue;

                if(readed == 252){

                    isReadingList = false;
                    isReading = false;

                    String[] arr = new String[slist.size()];
                    slist.toArray(arr);
                    slist.clear();

                    onStringList(arr);

                    continue;

                }else
                if(readed == 254){

                    if(isReadingList) slist.add(sdata); else
                        onString(sdata);

                    sdata = "";
                    isReading = isReadingList;

                    continue;

                }

                sdata += (char) readed;
                continue;

            }
            // Beginning of the string data.
            if(readed == 253){

                isReadingList = true;
                isReading = true;

            } else
            if(readed == 255) isReading = true; else onByte(readed);

        }

    }

    private void onStringList(String[] list){

        if(list[0].equals("ssl")){

            sounds = new String[list.length-1];
            System.arraycopy(list, 1, sounds, 0, list.length-1);
            Intent intent = new Intent(main, SoundPlayer.class);
            main.startActivity(intent);
            return;
        }
        System.out.println("sa");

    }

    private void onString(String data){}

    private void onByte(int data){

        new Thread() {
            @Override
            public void run() {

                Looper.prepare();
                Toast t = Toast.makeText(main.getApplicationContext(), "Byte: "+data, Toast.LENGTH_SHORT);
                t.show();
                Looper.loop();

            }
        }.start();

    }

    public static PiManager getInstance() {
        return instance;
    }
    public OutputStream out;
    public InputStream in;

    public int getErrorByID(int id){

        switch(id){

            case 0:
                return R.string.done_successfully;
            case 1:
                return R.string.bluetooth_have_to_be_opened;
            case 2:
                return R.string.connect_to_hc05;
            case 3:
                return R.string.output_input_error;
            default:
                return R.string.unexpected_error;

        }

    }

    public byte tryConnect()  {

        if(btSocket != null && btSocket.isConnected()) {

            getLogger().Info("Bluetooth already connected.");
            return 0;

        }

        //<Adapter>.getBondedDevices() // Telefonun Bluetooth İle Bağlı Olduğu Cihazların Adresslerini Döndürür (Array);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {

            getLogger().Warn("Bluetooth have to be opened.");
            return 1;

        }

        hc05 = btAdapter.getRemoteDevice(hc05_adress);

        try {

            btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
            btSocket.connect();

        } catch (IOException e) {

            getLogger().Warn("Failed to connect HC-05: "+e.toString());
            return 2;

        }

        try{

            out = btSocket.getOutputStream();
            in = btSocket.getInputStream();

        }catch(Exception e){

            try{

                btSocket.close();

            }catch(IOException ignored){}
            System.out.println("Failed to initialize output and input channels: "+e.toString());
            return 3;

        }

        this.start();
        return 0;

    }

    private void send(byte[] bytes){

        try {

            out.write(bytes);

        } catch (IOException e) {

            terminate();
            getLogger().Warn("Failed to send packet, connection terminated.");

        }

    }

    public void sendByte(byte b){

        byte[] bytes = new byte[1];
        bytes[0] = b;
        send(bytes);

    }

    public void sendString(String data){

        // 255 başlatır.
        // 254 bitirir.
        byte[] chars = data.getBytes();
        byte[] bytes = new byte[chars.length+2];

        bytes[0] = (byte) 255; // Start of the string data
        System.arraycopy(chars, 0, bytes, 1, chars.length);
        bytes[bytes.length-1] = (byte) 254; // End of the string data

        send(bytes);

    }

    public void terminate(){

        try {

            btSocket.close();
            Intent intent = new Intent(main, MainActivity.class);
            main.startActivity(intent);
            main.finish();

        } catch (IOException ignored) {}

    }

    public void setActivity(AppCompatActivity main){

        this.main = main;

    }

    public PiLogger getLogger(){
        return logger;
    }

}
