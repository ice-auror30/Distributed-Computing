import java.text.*;
import org.stellar.sdk.KeyPair;
import java.net.*;
import java.io.*;
import java.util.*;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.*;
import org.stellar.sdk.responses.operations.*;
import org.stellar.sdk.requests.*;
import java.security.*;
import org.apache.commons.codec.digest.DigestUtils;


class Banker {
	public static void main(String argv[]) throws Exception {
		ServerSocket ss = new ServerSocket(6789);
		// StateHandle sh = new StateHandle();
		KeyPair serverAccount;
		String accountId;
		char[] secretSeed;
		Server server = new Server("https://horizon-testnet.stellar.org");//use the stellar test server for demo
		Network.useTestNetwork();
		System.out.println("Server's Keys:");
		System.out.println("Public: GAJZMVKUUGBOKPNASYSXIOFZGD4VRE5DCNGOOBEU6YOGJLR5LYZLKNG7");
		System.out.println("Secret: SA2EHVOP3DUV7ZOQAYXYLJA7E3CBFLDHQKSQZCQUSN6ATWKYKYYOCD7K");
		serverAccount = KeyPair.fromAccountId("GAJZMVKUUGBOKPNASYSXIOFZGD4VRE5DCNGOOBEU6YOGJLR5LYZLKNG7");
		accountId = serverAccount.getAccountId();
		//GBYDYDL5OWUGPW2HFNUIY7LBLYHFV47ZBGLS5G3AVIZGSJUJ5SQZGWKG
		KeyPair serverAccount_private = KeyPair.fromSecretSeed("SA2EHVOP3DUV7ZOQAYXYLJA7E3CBFLDHQKSQZCQUSN6ATWKYKYYOCD7K");
		try{
			server.accounts().account(serverAccount);
		}catch(IOException ex){
			System.out.println("Failure to login.");
			return;
		}
		System.out.println("***START STELLAR COIN FLIP GAME***");
		Thread sh = new StateHandler();
		sh.start();
		// Keep adding new client handler when new client joint in the game
		while (true) {
			Socket s = ss.accept();
			String socketAddress = s.getRemoteSocketAddress().toString();
			System.out.println("New client: "+socketAddress);		
			DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
			Thread t = new ClientHandler(server, s, outToClient, serverAccount, serverAccount_private);			
			t.start();
			
		}
	}
}


// supervisor of all the client
class StateHandler extends Thread{
	@Override
	public void run() 
	{
		while(true){
			// use two lock steps to control the state of client
			if(ClientHandler.startNewRound && ClientHandler.clientWaitingtoPlay == ClientHandler.numberOfClients && ClientHandler.clientWaitingtoPlay>=1){
				ClientHandler.startNewRound = false;
				ClientHandler.startPlay = true;
				ClientHandler.clientWaitingforNewRound = 0;
			}

			if(ClientHandler.startPlay && ClientHandler.clientWaitingforNewRound == ClientHandler.numberOfClients && ClientHandler.clientWaitingforNewRound>=1){
				ClientHandler.startPlay = false;
				ClientHandler.startNewRound = true;
				ClientHandler.clientWaitingtoPlay = 0;
				ClientHandler.clientAlreadySentHash = 0;	
				System.out.println("---------Start New Round---------");			
			}
			try{
				Thread.sleep(2000); //supervise every 2 seconds
			}catch(InterruptedException e){
				System.out.println("Supervisor is down! The program has to exit.");
				System.exit(1);
			}
			
		}
		
	}


}


//ClientHandler class
class ClientHandler extends Thread {

	static int numberOfClients = 0;
	static int clientAlreadySentHash = 0;
	public static boolean startNewRound = true;
	public static boolean startPlay = false;
	public static int clientWaitingtoPlay = 0;
	public static int clientWaitingforNewRound= 0;
	static int totalSeed = 0;
	static float totalDeposit_user1 = 0;
	static float totalDeposit_user2 = 0;
	BufferedReader dis;
	int choice;
	final DataOutputStream dos;
	final Socket s;
	final String address;
	State state;
	Server server;
	AccountResponse sourceAccount;
	float deposit;
	KeyPair source;
	KeyPair source_pub;
	KeyPair serverAccount;
	KeyPair serverAccount_private;
	String encryptedString;
	String guess = null, seed = null;
	public enum State{
		stellar_log_in, add_balance, check_history, take_deposit, receive_hash, wait_user_guess, receive_guess_info, start_game, wait_start_game, wait_new_round;
	}

	// Constructor
	public ClientHandler(Server server, Socket s, DataOutputStream dos, KeyPair serverAccount, KeyPair serverAccount_private) 
	{
		address = s.getRemoteSocketAddress().toString();
		System.out.println(address+" log into the server.");
		this.server = server;
		choice = -1;
		this.s = s;
		this.dos = dos;
		this.serverAccount = serverAccount;
		this.serverAccount_private = serverAccount_private;
		state = State.stellar_log_in;
		numberOfClients ++;
	}


	public boolean stellarLogin(){
		while(true){
			String received = null;
			try{
				received = dis.readLine();	
			}catch(Exception e){
				System.out.println("Client: "+address+" connection Lost.");
				return false;
			}
			try{
				source = KeyPair.fromSecretSeed(received);
				sourceAccount = server.accounts().account(source);
				dos.writeBytes("T"+"\n");
			}catch(Exception e){
				try{
					dos.writeBytes("F"+"\n");//log in incorrect
				}catch(Exception ee){
					System.out.println("Client: "+address+" connection Lost.");
					return false;
				}
				System.out.println("Client: "+address+" log in error. Wait for another try...");
				continue;
			}


			try{
				received = dis.readLine();	
			}catch(Exception e){
				System.out.println("Client: "+address+" connection Lost.");
				return false;
			}
			try{
				source_pub = KeyPair.fromAccountId(received);
				try{
					server.accounts().account(source_pub);
				}catch(IOException ex){
					System.out.println("Client public ID incorrect. Wait for another try...");
					continue;
				}
				dos.writeBytes("T"+"\n");
				return true;
			}catch(Exception e){
				try{
					dos.writeBytes("F"+"\n");//log in incorrect
				}catch(Exception ee){
					System.out.println("Client: "+address+" connection Lost.");
					return false;
				}
				System.out.println("Client: "+address+" log in error. Wait for another try...");
			}
		}
	}


	public boolean getDeposit(){
		while(true){
			String received = null;
			try{
				received = dis.readLine();	

			}catch(Exception e){
				System.out.println("Client: "+address+" connection Lost.");
				return false;
			}
			try{
				deposit = Float.valueOf(received);//check whether the input is a number
				try{
					Transaction transaction = new Transaction.Builder(sourceAccount)
							.addOperation(new PaymentOperation.Builder(serverAccount, new AssetTypeNative(), received).build())
							// A memo allows you to add your own metadata to a transaction. It's
							// optional and does not affect how Stellar treats the transaction.
							.addMemo(Memo.text("Transaction#1"))
							.build();
					// Sign the transaction to prove you are actually the person sending it.
					transaction.sign(source);

					// And finally, send it off to Stellar!
					try {
						SubmitTransactionResponse response = server.submitTransaction(transaction);
						System.out.println("Success! "+address+" Amount deposited: "+received+". Stellar Response: "+response);
						dos.writeBytes("T"+"\n");
						return true;
					} catch (Exception e) {
						System.out.println("Something went wrong!");
						System.out.println(e.getMessage());
						// If the result is unknown (no response body, timeout etc.) we simply resubmit
						// already built transaction:
						// SubmitTransactionResponse response = server.submitTransaction(transaction);
					}	
				}catch(Exception e){
					System.out.println(e);
					try{
					dos.writeBytes("F"+"\n");//input format incorrect
					}catch(Exception ee){
						System.out.println("Client: "+address+" connection lost");
						return false;
					}
					System.out.println("Client: "+address+" error");
				}
			}catch(Exception e){
				try{
					dos.writeBytes("F"+"\n");//input format incorrect
				}catch(Exception ee){
					System.out.println("Client: "+address+" connection lost");
					return false;
				}
				System.out.println("Client: "+address+" deposit error");
			}
		}
	}

	public boolean receiveHash(){
		while(true){
			String received = null;
			try{
				received = dis.readLine();	
			}catch(Exception e){
				System.out.println("Client: "+address+" connection lost");
				return false;
			}
			try{
				encryptedString = received;
				dos.writeBytes("T"+"\n");
				clientAlreadySentHash ++;
				return true;
			}catch(Exception e){
				try{
					dos.writeBytes("F"+"\n");//log in incorrect
				}catch(Exception ee){
					System.out.println("Client: "+address+" connection lost");
					return false;
				}
				System.out.println("Client: "+address+" hash process error");
			}
		}
	}

	public boolean checkReadyUser(){
		while(true){
			try{
				if(clientAlreadySentHash == numberOfClients && clientAlreadySentHash>=1){
					try{				
						dos.writeBytes("ALLREADY"+"\n");
						return true;
					}catch(Exception e){}
				}
				Thread.sleep(1000); // Check ready user every 1 second.
			} 
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();
			}
		}
		
	}

	public boolean receiveGuess(){
		MessageDigest messageDigest;
		while(true){
			String received = null;
			try{
				received = dis.readLine();
			}catch(Exception e){
				System.out.println("Client: "+address+" connection lost");
				return false;
			}
			String split[] = received.split("\\s+");
			guess = split[0];
			seed = split[1];
			try{
				try{messageDigest = MessageDigest.getInstance("MD5");}
				catch(NoSuchAlgorithmException nae){continue;}
				String encryptedString_2nd = new DigestUtils(messageDigest).digestAsHex(guess+seed);
				if(encryptedString_2nd.equals(encryptedString)){
					System.out.println("Client: "+address+" Guess and Seed match with hashing.");
					dos.writeBytes("T"+"\n");
					totalSeed += Integer.valueOf(seed);
					if(guess.equals("0")) totalDeposit_user1+=deposit;
					else totalDeposit_user2+=deposit;
					clientWaitingtoPlay ++;
					return true;
				}else{
					System.out.println("Client: "+address+" doesn't match");
					clientAlreadySentHash--;
					return false;
				}
			}catch(Exception e){
				try{
					dos.writeBytes("F"+"\n");//log in incorrect
				}catch(Exception ee){
					System.out.println("Client: "+address+" connection lost");
					return false;
				}
				System.out.println("Client: "+address+" hash process error");
			}
		}
	}

	public boolean playGame(){
		float totalDeposit = totalDeposit_user1+totalDeposit_user2;
		float prizePool = totalDeposit*0.95f;
		int rem = totalSeed%2;
		float totalDepositRightGuess = (rem==0?totalDeposit_user1:totalDeposit_user2);
		if(rem == Integer.valueOf(guess)){// win the bet
			float bouns = prizePool*(deposit/totalDepositRightGuess);
			
			try{
				AccountResponse serverAccount_private_res = server.accounts().account(serverAccount_private);
				Transaction transaction = new Transaction.Builder(serverAccount_private_res)
					.addOperation(new PaymentOperation.Builder(source_pub, new AssetTypeNative(), String.valueOf(bouns)).build())
					// A memo allows you to add your own metadata to a transaction. It's
					// optional and does not affect how Stellar treats the transaction.
					.addMemo(Memo.text("Test Transaction"))
					.build();

				// Sign the transaction to prove you are actually the person sending it.
				transaction.sign(serverAccount_private);
				SubmitTransactionResponse response = server.submitTransaction(transaction);
				System.out.println("Success! Send money: "+String.valueOf(bouns)+" to "+address+". Stellar Response: "+response);
			}catch(IOException ex){
				System.out.println("Cannot find source account");
				return false;
			}catch(Exception e){
				System.out.println(e);
				System.out.println("Client: "+address+" error");
				return false;
			}

			try{
				dos.writeBytes(String.valueOf(bouns)+"\n");
			}catch(Exception ee){
				System.out.println("Client: "+address+" connection lost");
				return false;
			}

		}else{
			try{
				dos.writeBytes("F"+"\n");
			}catch(Exception ee){
				System.out.println("Client: "+address+" connection lost");
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() 
	{
		String received;
		String toreturn;
		try{
			dis = new BufferedReader(new InputStreamReader(s.getInputStream()));   
		}catch(IOException e){

		}
		while (true)
		{
			try {
					
				switch(state){
					case stellar_log_in:
						if(stellarLogin()==true) state = State.take_deposit;
						else{							
							numberOfClients--;
							return;
						}
						break;
					case take_deposit:
						if(getDeposit()==true) state = State.receive_hash;
						else{
							numberOfClients--;
							return;
						}
						break;
					case receive_hash:
						if(receiveHash()==true) state = State.wait_user_guess;
						else{							
							numberOfClients--;
							return;
						}
					case wait_user_guess:
						if(checkReadyUser()==true) state = State.receive_guess_info;
						else{
							numberOfClients--;
							return;
						}
						break;
					case receive_guess_info:
						if(receiveGuess()==true){
							state = State.wait_start_game;
						}
						else{							
							numberOfClients--;
							return;
						}
						break;
					case wait_start_game:
						if(startPlay) state = State.start_game;		
						break;				
					case start_game:
						if(playGame()==true){
							clientWaitingforNewRound++;
							System.out.println(clientWaitingforNewRound);					
							state = State.wait_new_round;
						}
						else{					
							numberOfClients--;
							return;
						}
						break;
					case wait_new_round:
						if(startNewRound){
							
							state = State.take_deposit;
						}
					default:
						break;
				}

			} catch (Exception e) {
				System.out.println("Exception: lost connection from: "+address+".");				
				numberOfClients--;
				return;
			}
			try{
				Thread.sleep(1000); 
			}catch(InterruptedException e){
				System.out.println("Thread error");
				numberOfClients--;
			}
		}
	}
}