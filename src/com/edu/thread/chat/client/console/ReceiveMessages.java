package com.edu.thread.chat.client.console;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveMessages extends Thread {
	
	private Socket socket;
	private String minick = null;
	private String misala = "global";

	public ReceiveMessages(Socket socket, String nick, String sala){
		this.socket = socket;
		this.minick = nick;
		this.misala = sala;
	}
	
	public void run(){
        // Obtiene el flujo de entrada del socket
		System.out.println("He creado un hilo de receiveMessajes");
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
                	if (palabras[2].equals(minick)) {
                		System.out.println(mensaje);
                	}
                } else {
                	String sala = mensaje.substring(0, mensaje.indexOf('|'));
                	sala = sala.substring(sala.indexOf(':')+2, sala.length());
                	String texto = mensaje.substring(mensaje.indexOf('|'), mensaje.length());
                	String nick = texto.substring(1, texto.indexOf(':'));
                	texto = texto.substring(texto.indexOf(':') + 2, texto.length());

                	if (texto.startsWith("NEWROOM") && nick.equals(minick.toUpperCase())){
                		/*ConsoleClient client = new ConsoleClient();
                		client.setNick(nick);
                		client.setNombreSala(sala);*/
                		
                		System.out.println("El usuario " + nick + " entra en la sala " + sala);
                		misala = sala;
                		/*ReceiveMessages escucha = new ReceiveMessages(client.getSocket(), client.getNick(), client.getNombreSala());
                		escucha.start();*/
                	}else{
                		if (sala.equals(misala) && !texto.startsWith("NEWROOM")){
                			//System.out.println(mensaje);

                        	System.out.println((nick.equals(minick.toUpperCase()) ? "Yo: " : nick + ": ") + texto);
                		}
                	}
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
