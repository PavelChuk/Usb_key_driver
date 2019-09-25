package com.company;

import org.usb4java.*;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws Exception {


        int vid=1423;
        int pid=25479;

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SecureRandom secureRandom = new SecureRandom();
                Date dateNow = new Date();
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh mm ss");
                byte[] token = new byte[20];
                secureRandom.nextBytes(token);
                System.out.println("Текущее время " + formatForDateNow.format(dateNow));
                FileWriter fw = null;
                try {
                    fw = new FileWriter("D:\\passlog.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fw.write(new BigInteger(1, token).toString(16)/*+" "+formatForDateNow.format(dateNow)*/);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Context context = new Context();
                int result = LibUsb.init(context);
                if(result != LibUsb.SUCCESS)
                {
                    throw new LibUsbException("Unable to initialize the usb device",result);
                }
                DeviceList list = new DeviceList();
                result = LibUsb.getDeviceList(context, list);
                boolean status=false;
                if(result < 0 )throw new LibUsbException("Unable to get device list",result);
                {

                    for(Device device : list)
                    {
                        DeviceDescriptor device_descriptor = new DeviceDescriptor();
                        result = LibUsb.getDeviceDescriptor(device, device_descriptor);
                        if(result != LibUsb.SUCCESS)
                            throw new LibUsbException("Unable to get device descriptor : ",result);
                        System.out.println("Product id is : "+device_descriptor.idProduct()+" "
                                +"Vendor id is : "+device_descriptor.idVendor());
                        if(device_descriptor.idProduct()==pid && device_descriptor.idVendor()==vid) {

                            status=true;
                        }
                        }
                }
                if(status==false)
                {
                    FileWriter fw1 = null;
                    try {
                        fw1 = new FileWriter("D:\\passlog.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fw1.write("NULL");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fw1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
                }
            }
        },0,5*1000);



    }
}
