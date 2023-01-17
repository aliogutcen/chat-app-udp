package com.ogutcenali.client;

import java.awt.TextArea;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JEditorPane;

public class ChatThread extends Thread{
	  private DatagramSocket socket;
	    private byte[] incoming = new byte[256];

	    private JEditorPane editorPane ;

	    public ChatThread(DatagramSocket socket, JEditorPane editorPane) {
	        this.socket = socket;
	        this.editorPane= editorPane;
	    }
	    
	    @Override
	    public void run() {
	        System.out.println("starting thread");
	        while (true) {
	            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
	            try {
	                socket.receive(packet);
				} catch (IOException e) {
	                throw new RuntimeException(e);
	            }
	            String message = new String(packet.getData(), 0, packet.getLength()) + "\n";
	            String current = editorPane.getText();
	            editorPane.setText(current + message);
	        }
	    }
}
