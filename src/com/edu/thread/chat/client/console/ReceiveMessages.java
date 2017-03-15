package com.edu.thread.chat.client.console;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessages extends Thread {
	
	private Socket socket;
	private String nick = null;

	public ReceiveMessages(Socket socket, String nick){
		this.socket = socket;
		this.nick = nick;
	}
	
	public void run(){
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            //log.error("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException ex) {
            //log.error("El socket no se creo correctamente. ");
        }
        
        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        String[] palabras;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();
                if (mensaje.startsWith("Privado")) {
                	palabras = mensaje.split(" ");
                	//Como los privados empiezan por Privado para "nick" de, la palabra[2] será el nick
                	if (palabras[2].equals(nick)) {
                		System.out.println(mensaje);
                	}
                } else {
                System.out.println(mensaje);
                }
            } catch (IOException ex) {
                //log.error("Error al leer del stream de entrada: " + ex.getMessage());
                conectado = false;
            } catch (NullPointerException ex) {
                //log.error("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }
}
