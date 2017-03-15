package com.edu.thread.chat.client.console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.edu.bd.Conexion;
import com.edu.bd.Query;
import com.edu.thread.chat.client.ConexionServidor;

public class ConsoleClient {
	
	private Socket socket;
	private DataOutputStream salidaDatos;
	private String nick = null;
	
	public ConsoleClient(){
		// Se crea el socket para conectar con el Sevidor del Chat
        try {
            socket = new Socket("127.0.0.1", 1234);
            salidaDatos = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            //log.error("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        } catch (IOException ex) {
            //log.error("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        }
        new ConexionServidor(socket, null, "Juan");
	}
	
	public void send(String text){
		try {
            salidaDatos.writeUTF(getNick().toUpperCase() +": "+ text);
        } catch (IOException ex) {
            //log.error("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataOutputStream getSalidaDatos() {
		return salidaDatos;
	}

	public void setSalidaDatos(DataOutputStream salidaDatos) {
		this.salidaDatos = salidaDatos;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public static void main(String args[]){
		ConsoleClient client = new ConsoleClient();
		new ReceiveMessages(client.socket).start();
		Query q = new Query(new Conexion());
		Scanner teclado = new Scanner(System.in);
		String texto = null;
		System.out.println("Indica tu nick:");
		texto = teclado.nextLine();
		//Si el usuario existe en el sistema lo actualizamos como online, 
		//en caso de que no exista lo damos de alta
		if (q.existsUser(texto)) {
			q.setUserOnline(texto);
		} else {q.newUser(texto);}
		
		client.setNick(texto);
		q.showUsersOnline();
		while (true){
			System.out.println("Escribe en el chat:");
			texto = teclado.nextLine();
			client.send(texto);
		}
	}
}
