package com.company;

import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
	    // Envio el id
        Socket serverSocket = null;
        try {
            serverSocket = new Socket("localhost", 10000);
            DataOutputStream dos = new DataOutputStream(serverSocket.getOutputStream());
            dos.writeInt(8);

            InputStream is = serverSocket.getInputStream();
            OutputStream os = new FileOutputStream("factura.pdf");

            copy(is,os);
            is.close();
            os.close();
            dos.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void copy(InputStream in, OutputStream out) throws IOException {

        byte[] buf = new byte[512];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            System.out.println(len);
            out.write(buf, 0, len);
        }
    }
}
