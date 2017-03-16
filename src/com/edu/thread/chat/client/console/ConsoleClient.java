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
import com.edu.thread.chat.server.Sala;

public class ConsoleClient {
	
	private Socket socket;
	private DataOutputStream salidaDatos;
	private String nick = null;
	private String nombreSala;
	
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
	
	public void send(String text, String sala){
		try {
            salidaDatos.writeUTF("sala: " +sala +"|" +getNick().toUpperCase() +": "+ text);
        } catch (IOException ex) {
            //log.error("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
	}
	
	public void sendPrivado(String nick_destino, String nick_origen, String text){
		try {
            salidaDatos.writeUTF("Privado para " +nick_destino +" de " +nick_origen.toUpperCase() +": "+ text);
           //System.out.println("sssssPrivado para " +nick_destino +" de " +nick_origen.toUpperCase() +": "+ text);
        } catch (IOException ex) {
            //log.error("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
	}
	
	public String getNombreSala() {
		return nombreSala;
	}

	public void setNombreSala(String nombreSala) {
		this.nombreSala= nombreSala;
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
		Query q = new Query(new Conexion());
		Scanner teclado = new Scanner(System.in);
		String texto = null, nick_destino;
		String [] palabras;
		System.out.println("Indica tu nick:");
		texto = teclado.nextLine();
		//Si el usuario existe en el sistema lo actualizamos como online, 
		//en caso de que no exista lo damos de alta
		if (q.existsUser(texto)) {
			q.setUserOnline(texto);
		} else {q.newUser(texto);}
		
		client.setNick(texto);
		client.setNombreSala("global");
		
		//Creamos un hilo de escucha para que reciba mensajes de otros usuarios
		ReceiveMessages escucha = new ReceiveMessages(client.socket, client.getNick(), client.getNombreSala());
		escucha.start();
		
		q.showUsersOnline();
		boolean inchat = true;
		while (inchat){
			System.out.println("Escribe en el chat " + client.getNombreSala() + ":");
			texto = teclado.nextLine();
			palabras = texto.split(" ");
			switch (palabras[0]) {
				case "USUARIOS_R": 	q.showAllUsers();
									break;
				case "USUARIOS_A":	q.showUsersOnline();
									break;
				case "CHATPRIVADO":	//Se gestionará un chat privado
									System.out.println("Indica el nick del usuario para el envio del mensaje privado");
									nick_destino = teclado.nextLine();
									System.out.print("Texto a enviar a "+nick_destino +": ");
									texto = teclado.nextLine();
									client.sendPrivado(nick_destino, client.getNick(), texto);
									break;
				case "SYSTEM":		//Mostraremos información del servidor
									break;
				case "LOGOUT_A":	//El usuario saldrá de la sesión activa
									q.setUserOffline(client.getNick());
									inchat = false;
									break;
				case "LOGOUT_B":	//El usuario saldrá de la sesión activa y se dará de baja de la BBDD
									q.setUserOffline(client.getNick());
									q.deleteUser(client.getNick());
									inchat = false;
									break;
				case "NEWROOM":		texto = palabras[0]+"|"+palabras[1];
									System.out.println("texto solicitando newroom " +texto);
									client.send(texto, palabras[1]);
									client.setNombreSala(palabras[1]);
									break;
				default: 			client.send(texto, client.getNombreSala());
									break;
			}
			
		}
		
		//Una vez salimos del bucle cerramos el socket para cerrar la consola
		try {
			client.getSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Adios");
	}
}
