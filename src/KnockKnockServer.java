import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class KnockKnockServer {
	private static final int SLEEP = 1500;
	private static final String PORT = "-port";
	private static final String DELIMITER = " | ";
	private static final String FILEPATH =
		"/Users/VinodDalavai/IntelliJProjects/KnockKnock/punches.txt";
	private static Socket socket;
	private static ServerSocket serverSocket;
	private final int port;
	private final DataOutputStream outputStream;
	private final DataInputStream reader;
	private String name;
	private String punch;

	public static final String[] RESPONSES = {
		"Knock Knock!!"
	};

	public KnockKnockServer(int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket(port);
		socket = serverSocket.accept();
		outputStream = new DataOutputStream(socket.getOutputStream());
		reader = new DataInputStream(socket.getInputStream());
	}

	private int getTotalNumberOfLines() throws IOException {
		int totalLines = 0;
		BufferedReader reader = new BufferedReader(new FileReader(FILEPATH));
		while (reader.readLine() != null)
			totalLines++;
		return totalLines;
	}

	private void pickJoke() throws IOException {
		int currentLine = 0;
		String pickedLine = null;
		int totalLines = getTotalNumberOfLines();
		int randomLineNumber = (int) (Math.random() * totalLines);
		BufferedReader reader = new BufferedReader(new FileReader(FILEPATH));
		while (currentLine != randomLineNumber) {
			pickedLine = reader.readLine();
			currentLine++;
		}
		if (pickedLine == null) {
			System.out.println("Unable to pick a joke. Please try again.");
			System.exit(1);
		}
		String[] jokeArray = pickedLine.split(DELIMITER, 3);
		name = jokeArray[0];
		punch = jokeArray[2];
	}

	private void closeResources() throws IOException {
		reader.close();
		outputStream.close();
		socket.close();
		serverSocket.close();
	}

	private void startServer() throws IOException, InterruptedException {
		String clientResponse;
		outputStream.writeUTF(RESPONSES[0]);
		outputStream.flush();
		while (true) {
			clientResponse = reader.readUTF();
			System.out.println("Client ---> " + clientResponse);
			sleep(SLEEP);
			if (clientResponse.equals(KnockKnockClient.RESPONSES[0])) {
				outputStream.writeUTF(name);
			} else if (
				clientResponse.equals(name + KnockKnockClient.RESPONSES[1])
				) {
					outputStream.writeUTF(punch);
			} else {
				break;
			}
			outputStream.flush();
		}
		closeResources();
	}

	private static boolean argumentsValid(String[] args) {
		return (args.length == 2 &&
			args[0].equals(PORT) &&
			Integer.parseInt(args[1]) > 1234);
	}

	public static void main(String[] args)
		throws IOException, InterruptedException {
		if (argumentsValid(args)) {
			int port = Integer.parseInt(args[1]);
			KnockKnockServer kks = new KnockKnockServer(port);
			kks.pickJoke();
			kks.startServer();
		} else {
			System.out.println("Invalid port");
			System.exit(1);
		}
	}
}
