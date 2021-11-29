import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class KnockKnockServer {
	private static final int SLEEP = 1500;
	private static final String PORT_FLAG = "-port";
	private static final String INTERACTIVE_FLAG = "-i";
	private static final String DELIMITER = " | ";
	private static final String FILEPATH =
		"../punches.txt";
	private static Socket socket;
	private static ServerSocket serverSocket;
	private final int port;
	private DataOutputStream outputStream = null;
	private DataInputStream reader = null;
	private BufferedWriter bufferedWriter = null;
	private BufferedReader bufferedReader = null;
	private String name;
	private String punch;

	public static final String[] RESPONSES = {
		"Knock Knock!!"
	};

	public KnockKnockServer(int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket(port);
		socket = serverSocket.accept();
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
		reader.close();
	}

	private void closeResources() throws IOException {
		if (reader != null) reader.close();
		if (outputStream != null) outputStream.close();
		if (bufferedWriter != null) bufferedWriter.close();
		if (bufferedReader != null) bufferedReader.close();
		socket.close();
		serverSocket.close();
	}

	private void startInteractiveServer() throws IOException {
		System.out.println("Starting interactive server ...");
		bufferedWriter = new BufferedWriter(
			new OutputStreamWriter(socket.getOutputStream())
		);
		bufferedReader = new BufferedReader(
			new InputStreamReader(socket.getInputStream())
		);
		String fromClient = null;
		bufferedWriter.write(RESPONSES[0]);
		bufferedWriter.newLine();
		bufferedWriter.flush();
		while ((fromClient = bufferedReader.readLine()) != null) {
			System.out.println("Client ---> " + fromClient);
			if (KnockKnockClient.WHO_THERE.equalsIgnoreCase(fromClient)) {
				bufferedWriter.write(name);
			} else if ((name + " " + KnockKnockClient.PERSON_WHO).equalsIgnoreCase(fromClient)) {
				bufferedWriter.write(punch);
			} else if (KnockKnockClient.EXIT.equalsIgnoreCase(fromClient)) {
				break;
			} else if (KnockKnockClient.PLAY_AGAIN.equalsIgnoreCase(fromClient)) {
				bufferedWriter.write(RESPONSES[0]);
				pickJoke();
			} else {
				bufferedWriter.write("Invalid input, please try again.");
			}
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}
		closeResources();
	}

	private void startServer() throws IOException, InterruptedException {
		String clientResponse;
		reader = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		outputStream.writeUTF(RESPONSES[0]);
		outputStream.flush();
		while (true) {
			clientResponse = reader.readUTF();
			System.out.println("Client ---> " + clientResponse);
			sleep(SLEEP);
			if (clientResponse.equalsIgnoreCase(KnockKnockClient.WHO_THERE)) {
				outputStream.writeUTF(name);
			} else if (
				clientResponse.equals(name + " " +KnockKnockClient.PERSON_WHO)
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
		return (args.length == 4 &&
			args[0].equals(PORT_FLAG) &&
			Integer.parseInt(args[1]) > 1234 &&
			args[2].equals(INTERACTIVE_FLAG));
	}

	private static boolean isInteractive(String[] args) {
		return (args[3].equalsIgnoreCase("yes"));
	}

	public static void main(String[] args)
		throws IOException, InterruptedException {
		if (argumentsValid(args)) {
			int port = Integer.parseInt(args[1]);
			KnockKnockServer kks = new KnockKnockServer(port);
			kks.pickJoke();
			if (isInteractive(args)) {
				kks.startInteractiveServer();
			} else {
				kks.startServer();
			}
		} else {
			System.out.println("Invalid port");
			System.exit(1);
		}
	}
}
