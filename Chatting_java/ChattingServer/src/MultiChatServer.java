import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


class Client {
	String username;
	String userId;

	public Client(String username, String userId) {
		this.username = username;
		this.userId=userId;
	}
}
public class MultiChatServer {
	private ServerSocket ss = null;
	private Socket s = null;
	

	HashMap<String, Thread> map = new HashMap<>();
	
	ArrayList<ClientThread> clientThread = new ArrayList<ClientThread>();
	
	public void start() {
		try {

			ss = new ServerSocket(8888);
			System.out.println("server start");
			while (true) {
				s = ss.accept();

				ClientThread c = new ClientThread();

				clientThread.add(c);

				c.start();
			}
		} catch (Exception e) {
			System.out.println("[Multi Server]start() Exception 발생!!");
		}
	}

	public static void main(String[] args) {
		MultiChatServer server = new MultiChatServer();
		server.start();

	}
	
	void msgSendAll(String msg) {
		for (ClientThread ct : clientThread) {
			ct.outMsg.println(msg);
			
		}
	}

	class ClientThread extends Thread{
	//	DBConn dbConn = new DBConn();
		String msg;
		String[] rmsg;
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;
		Client client;
		String ready = null;
		
		public void run() {
			
			boolean status = true;
			System.out.println("##Chatting start...");
			
			try {
				
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);
				
				while (status) {
					msg = inMsg.readLine();
					rmsg = msg.split("/");
					String tmsg = "";
					if (msg != null) {
						if (rmsg[0].equals("Login")) {
							if(map.containsKey(rmsg[1])) {
								Thread getFromName = (Thread)map.get(rmsg[1]);
								for(ClientThread ct : clientThread) {
									if(ct.equals(getFromName)) {
										ct.outMsg.println("NoLogin/"+rmsg[1]);
									}
								}
							}else {
							map.put(rmsg[1], Thread.currentThread());
							System.out.println(Thread.currentThread());
							msgSendAll("Login/"+rmsg[1]);
							msgSendAll("Client_number/"+ map.size());
							}
							
							String t="";
							for(Entry<String,Thread> entry : map.entrySet()) {
								t=t+entry.getKey()+"#";
							}
							
							msgSendAll("Client_List/"+t);
							System.out.println("이름:"+t);
							
						}else if(rmsg[0].equals("Logout")) {
							map.remove(rmsg[1]);
							msgSendAll("Logout/"+rmsg[1]);
							msgSendAll("Client_number/"+map.size());
						}else if(rmsg[0].equals("Whisper")) {
							Thread getName = (Thread)map.get(rmsg[2]);
							Thread getFromName = (Thread)map.get(rmsg[1]);
							
							for(ClientThread ct : clientThread) {
							
								if(ct.equals(getFromName) || ct.equals(getName)) {
									ct.outMsg.println("Whisper/"+rmsg[1]+"/"+rmsg[2]+"/"+rmsg[3]);
								}
							}
							
							
						}else if(rmsg[1].contains("바보") || rmsg[1].contains("멍청이")) {
							msgSendAll("비속어 방지/"+rmsg[0]+"/"+rmsg[1]);
						}
						
						else {
							msgSendAll(msg);
						}
						msg=null;
					}
				}
				
				map.remove(rmsg[1]);
				msgSendAll("Client_number/"+map.size());
				clientThread.remove(this);
				System.out.println("##" + this.getName() + "stop!!");
				this.interrupt();
				}catch(IOException e) {
					clientThread.remove(this);
					System.out.println("[ChatThread]run() IOException 발생!!");
			}
		}
		
	}

}
