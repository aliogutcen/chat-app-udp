package com.ogutcenali.client;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Client extends JFrame {

	private static final int SERVER_PORT = 5000; // send to server
	private static final DatagramSocket socket;
	private static final InetAddress address;
	
	private JScrollPane scrollPane;
	private JFrame frmChat;
	private JEditorPane editorPane;
	private JPanel contentPane;
	private JTextField textField;
	
	static {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	static {
		try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	
	public Client(String name) {
		setTitle(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 769, 638);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(115, 105, 504, 325);
		contentPane.add(scrollPane);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

					String x = name + "\n" + time + ":" + textField.getText();
					editorPane.setText(editorPane.getText() + textField.getText() + "\n");
					byte[] msg = x.getBytes();
					textField.setText("");

					DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
					try {
						socket.send(send);
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}

				}
			}
		});
		textField.setBounds(115, 497, 504, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		ChatThread clientThread = new ChatThread(socket, editorPane);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "ChatScreen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(91, 69, 550, 384);
	
		contentPane.add(panel);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Send Message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(91, 464, 550, 80);
		contentPane.add(panel_1);

		JButton btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.exit(EXIT_ON_CLOSE);
			}
		});
		btnNewButton.setBounds(664, 576, 89, 23);
		contentPane.add(btnNewButton);
		
		clientThread.start();
		
		byte[] uuid = ("init;" + name).getBytes();
		DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
		try {
			socket.send(initialize);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
