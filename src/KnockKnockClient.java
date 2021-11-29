import java.io.*;
import java.net.Socket;

public class KnockKnockClient {
	private final Socket socket;
	private DataInputStream reader = null;
	private DataOutputStream outputStream = null;
	private BufferedReader bufferedReader = null;
	private BufferedReader userInput = null;
	private BufferedWriter bufferedWriter = null;
	private static final int SLEEP = 1500;
	private static final String HOST_FLAG = "-host";
	private static final String PORT_FLAG = "-port";
	private static final String INTERACTIVE_FLAG = "-i";
	public static final String WHO_THERE = "who is there?";
	public static final String PERSON_WHO = "who?";
	public static final String EXIT= "exit";
	public static final String PLAY_AGAIN= "play again";

	public KnockKnockClient(String host, int port)
		throws IOException {
		this.socket = new Socket(host, port);
	}

	private void closeResources() throws IOException {
		if (bufferedReader != null) bufferedReader.close();
		if (userInput != null) userInput.close();
		if (bufferedWriter != null) bufferedWriter.close();
		if (outputStream != null) outputStream.close();
		if (reader != null) reader.close();
		socket.close();
	}

	private void runInteractiveClient() throws IOException {
		System.out.println("Starting interactive client ...");
		bufferedReader = new BufferedReader(
			new InputStreamReader(socket.getInputStream())
		);
		userInput = new BufferedReader(new InputStreamReader(System.in));
		bufferedWriter = new BufferedWriter(
			new OutputStreamWriter(socket.getOutputStream())
		);
		String fromServer = null;
		while ((fromServer = bufferedReader.readLine()) != null) {
			System.out.println("Server ---> " + fromServer);
			System.out.print("You ---> ");
			String input = userInput.readLine();
			bufferedWriter.write(input);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}
		closeResources();
	}

	private void runClient() throws IOException, InterruptedException {
		String serverMessage;
		int count = 0;
		reader = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		while (true) {
			serverMessage = reader.readUTF();
			System.out.println("Server ---> " + serverMessage);
			Thread.sleep(SLEEP);
			if (serverMessage.equals(KnockKnockServer.RESPONSES[0])) {
				count++;
				outputStream.writeUTF(WHO_THERE);
			} else if (count == 1) {
				outputStream.writeUTF(serverMessage + " " + PERSON_WHO);
				count++;
			} else if (count == 2) {
				outputStream.writeUTF(EXIT);
				outputStream.flush();
				break;
			}
			outputStream.flush();
		}
		closeResources();
	}

	private static boolean argumentsValid(String[] args) {
		return (args.length == 6 && args[0].equals(HOST_FLAG) &&
			args[2].equals(PORT_FLAG) && Integer.parseInt(args[3]) > 1234 &&
			args[4].equals(INTERACTIVE_FLAG));
	}

	private static boolean isInteractive(String[] args) {
		return (args[5].equalsIgnoreCase("yes"));
	}

	public static void main(String[] args)
		throws IOException, InterruptedException {
		if (argumentsValid(args)) {
			String host = args[1];
			int port = Integer.parseInt(args[3]);
			KnockKnockClient kkc = new KnockKnockClient(host, port);
			if (isInteractive(args)) {
				kkc.runInteractiveClient();
			} else {
				kkc.runClient();
			}
		} else {
			System.out.println("Invalid arguments!");
			System.exit(1);
		}
	}
}
