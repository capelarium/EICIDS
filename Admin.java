import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import mail.SendingEmail;

public class Admin {
	private static SendingEmail sed;
	static File arq_ips = new File("alertas.txt");
	static FileOutputStream fos_arq_ip;
	static BufferedOutputStream buf_arq_ip;
	static DataOutputStream dados_arq_ip;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("usage: java Admin [porta]");
			return;
		}

		System.out.println("Initializing Admin...\n");

		try {
			int door = Integer.parseInt(args[0]);
			System.out.println("Porta " + door + " aberta.");
			ServerSocket servidor = new ServerSocket(door);
			System.out.println("Waiting for connection...");

			while (true) {
				Socket sock_node = servidor.accept();
				System.out.println("Connected at "
						+ sock_node.getInetAddress().getHostAddress());
				InputStream input = sock_node.getInputStream();
				System.out.println("\nWaiting for data...\n");
				while (sock_node.isConnected()) {
					Scanner s = new Scanner(input);
					String alerta;

					fos_arq_ip = new FileOutputStream(arq_ips);
					buf_arq_ip = new BufferedOutputStream(fos_arq_ip);
					dados_arq_ip = new DataOutputStream(buf_arq_ip);

					while (s.hasNextLine()) {
						alerta = s.nextLine();
						if (alerta.length() > 0) {
							System.out.println(alerta);
							dados_arq_ip.writeBytes(alerta + "\n");
							buf_arq_ip.flush();
						}
					}
					s.close();

					dados_arq_ip.close();
					buf_arq_ip.close();
					fos_arq_ip.close();
				}
				input.close();
				sock_node.close();
				servidor.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void enviar_email(String texto) {
		try {
			sed = new SendingEmail(
					"jdias@ifto.edu.br, leonardomelo91@gmail.com",
					"Alertas do MACIDS", texto);
			sed.enviar();
		} catch (Exception e) {
			System.out.println("Erro ao enviar e-mail de alerta.");
			e.printStackTrace();
		}
	}
}
