import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Savepoint;
import java.util.Scanner;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
public class CreateAccount {
	static KeyPair pair, pair1;
	static String accountId;
	static char[] secretSeed;
	static String myToken = null;
	private static void create_user_account(){
		// creating a completely new and unique pair of keys
		pair = KeyPair.random();
		pair1 = KeyPair.random();
		accountId = pair.getAccountId();
		//GBYDYDL5OWUGPW2HFNUIY7LBLYHFV47ZBGLS5G3AVIZGSJUJ5SQZGWKG
		secretSeed = pair.getSecretSeed();
		//SBMYTCROKMYUDPIRFGZFV2QNAK3C5ZASEN6RVOU4KMVDXNYM47LKUSYJ
		System.out.println("Secret Seed: "+new String(pair.getSecretSeed()));
		System.out.println("Account ID(public Key): "+pair.getAccountId());
	}
	private static void fundAccount(String accountID){
		String friendbotURL = String.format("https://friendbot.stellar.org/?addr=%s", accountId);
		try {
			InputStream response = new URL(friendbotURL).openStream();
			String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
			System.out.println("SUCCESS! You have a new account \n" + body);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void checkBalance(String accountId)throws IOException{
		Server server = new Server("https://horizon-testnet.stellar.org");
		AccountResponse account = server.accounts().account(pair);
		System.out.println("Balances for account: " + pair.getAccountId());
		for (AccountResponse.Balance balance : account.getBalances()) {
			System.out.println(String.format("Type: %s, Code: %s, Balance: %s", balance.getAssetType(), balance.getAssetCode(), balance.getBalance()));
		}		
	}

	private static String getAccountID(){
		return accountId = pair.getAccountId();
	}

	private static void makePayment(String targetID) throws IOException{
		AccountResponse  sourceAccount;
		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");

		//KeyPair source = KeyPair.fromSecretSeed("SCZANGBA5YHTNYVVV4C3U252E2B6P6F5T3U6MM63WBSBZATAQI3EBTQ4");
		KeyPair source = KeyPair.fromSecretSeed(pair.getSecretSeed());
		KeyPair destination = KeyPair.fromAccountId(targetID);

		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or there was another error.
		server.accounts().account(destination);

		// If there was no error, load up-to-date information on your account.
		sourceAccount = server.accounts().account(source);

		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "10").build())
				// A memo allows you to add your own metadata to a transaction. It's
				// optional and does not affect how Stellar treats the transaction.
				.addMemo(Memo.text("Test Transaction"))
				.build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);

		// And finally, send it off to Stellar!
		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Success!");
			System.out.println(response);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			System.out.println(e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response = server.submitTransaction(transaction);
		}	}

	private static void getHistory(String accountID){
		Server server = new Server("https://horizon-testnet.stellar.org");
		System.out.println("Getting History!");
		KeyPair account = KeyPair.fromAccountId(accountID);

		// Create an API call to query payments involving the account.
		PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account);

		// If some payments have already been handled, start the results from the
		// last seen payment. (See below in `handlePayment` where it gets saved.)
		String lastToken = loadLastPagingToken();
		if (lastToken != null) {
			paymentsRequest.cursor(lastToken);
		}

		// `stream` will send each recorded payment, one by one, then keep the
		// connection open and continue to send you new payments as they occur.
		paymentsRequest.stream(new EventListener<OperationResponse>() {
			@Override
			public void onEvent(OperationResponse payment) {
				// Record the paging token so we can start from here next time.
				savePagingToken(payment.getPagingToken());

				// The payments stream includes both sent and received payments. We only
				// want to process received payments here.
				if (payment instanceof PaymentOperationResponse) {
					if (((PaymentOperationResponse) payment).getTo().equals(account)) {
						return;
					}

					String amount = ((PaymentOperationResponse) payment).getAmount();

					Asset asset = ((PaymentOperationResponse) payment).getAsset();
					String assetName;
					if (asset.equals(new AssetTypeNative())) {
						assetName = "lumens";
					} else {
						StringBuilder assetNameBuilder = new StringBuilder();
						assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
						assetNameBuilder.append(":");
						assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId());
						assetName = assetNameBuilder.toString();
					}

					StringBuilder output = new StringBuilder();
					output.append(amount);
					output.append(" ");
					output.append(assetName);
					output.append(" from ");
					output.append(((PaymentOperationResponse) payment).getFrom().getAccountId());
					System.out.println(output.toString());
				}

			}
		});
	}
	protected static void savePagingToken(String pagingToken) {
		myToken = pagingToken;
		System.out.println(String.format("myToken is %s",myToken));
	}
	private static String loadLastPagingToken() {
		return myToken;
	}
	public static void main(String args[])throws IOException
	{
		String choice;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.println("Enter 1 for creating an account");
			System.out.println("Enter 2 for funding the account");
			System.out.println("Enter 3 for making a transaction");
			System.out.println("Enter 4 for checking the history of payments");
			choice = br.readLine();
			switch(choice){
			case("1"): create_user_account();
				break;
			case("2"): System.out.println("Input your account ID");
				String accountID = br.readLine();
				fundAccount(accountID);
				break;
			case("3"): System.out.println("Input the target account");
				String targetID = br.readLine();
				makePayment(targetID);
				break;
			case("4"): System.out.println("Input your account ID");
				String accID = br.readLine();
				checkBalance(accID);
				System.out.println("History: ");
				getHistory(accID);
				break;
			}
		}
	}
}
