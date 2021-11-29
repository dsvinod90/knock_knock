import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class KnockKnockClient {
	private final Socket socket;
	private static final int SLEEP = 1500;
	private static final String HOST_FLAG = "-host";
	private static final String PORT_FLAG = "-port";
	private final DataInputStream reader;
	private final DataOutputStream outputStream;
	public static final String[] RESPONSES = {
		"Who is there?",
		" who?",
		"LOL! Bye"
	};

	public KnockKnockClient(String host, int port) throws IOException {
		this.socket = new Socket(host, port);
		this.reader = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}

	private void closeResources() throws IOException {
		outputStream.close();
		reader.close();
		socket.close();
	}

	public void runClient() throws IOException, InterruptedException {
		String serverMessage;
		int count = 0;
		while (true) {
			serverMessage = reader.readUTF();
			System.out.println("Server ---> " + serverMessage);
			Thread.sleep(SLEEP);
			if (serverMessage.equals(KnockKnockServer.RESPONSES[0])) {
				count++;
				outputStream.writeUTF(RESPONSES[0]);
			} else if (count == 1) {
				outputStream.writeUTF(serverMessage + RESPONSES[1]);
				count++;
			} else if (count == 2) {
				outputStream.writeUTF(RESPONSES[2]);
				outputStream.flush();
				break;
			}
			outputStream.flush();
		}
		closeResources();
	}

	private static boolean argumentsValid(String[] args) {
		return (args.length == 4 && args[0].equals(HOST_FLAG) &&
			args[2].equals(PORT_FLAG) && Integer.parseInt(args[3]) > 1234);
	}

	public static void main(String[] args)
		throws IOException, InterruptedException {
		if (argumentsValid(args)) {
			String host = args[1];
			int port = Integer.parseInt(args[3]);
			KnockKnockClient kkc = new KnockKnockClient(host, port);
			kkc.runClient();
		} else {
			System.out.println("Invalid arguments!");
			System.exit(1);
		}
	}
}
