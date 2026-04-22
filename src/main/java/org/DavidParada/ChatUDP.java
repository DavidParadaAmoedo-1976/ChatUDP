package org.DavidParada;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ChatUDP {
    public static void main(String[] args) {
        int puertoLocal = 20000;
        int puertoDestino = 20000;

        try (DatagramSocket socket = new DatagramSocket(puertoLocal);
             Scanner teclado = new Scanner(System.in)) {

            System.out.print("Introduce tu nombre: ");
            String nombre = teclado.nextLine();

            System.out.print("Introduce la IP del otro usuario: ");
            String hostDestino = teclado.nextLine();

            System.out.println("Chat UDP iniciado. Escribe 'bye' para salir.");

            Thread hiloLectura = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];

                    while (!socket.isClosed()) {
                        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                        socket.receive(paquete);

                        String mensaje = new String(
                                paquete.getData(),
                                0,
                                paquete.getLength()
                        );

                        System.out.println("\n" + mensaje);
                        System.out.print("> ");

                        if (mensaje.toLowerCase().contains("bye")) {
                            System.out.println("El otro usuario salió.");
                            socket.close();
                            break;
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Conexión cerrado.");
                } catch (IOException e) {
                    System.out.println("Error al recibir datos.");
                }
            });

            hiloLectura.setDaemon(true);
            hiloLectura.start();

            while (!socket.isClosed()) {
                System.out.print("> ");
                String texto = teclado.nextLine();

                String mensaje = nombre + ": " + texto;
                byte[] datos = mensaje.getBytes();

                DatagramPacket paquete = new DatagramPacket(
                        datos,
                        datos.length,
                        InetAddress.getByName(hostDestino),
                        puertoDestino
                );

                socket.send(paquete);

                if (texto.equalsIgnoreCase("bye")) {
                    socket.close();
                    break;
                }
            }
        } catch (SocketException e) {
            System.err.println("Error con la conexión: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("IP no válida.");
        } catch (IOException e) {
            System.err.println("Error de entrada y o salida: " + e.getMessage());
        }
    }
}
