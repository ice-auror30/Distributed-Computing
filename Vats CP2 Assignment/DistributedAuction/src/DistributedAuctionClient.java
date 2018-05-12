import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DistributedAuctionApp.*;

public class DistributedAuctionClient {
	static DistributedAuction impl;

	public static void main(String args[])throws IOException{
		String userID = "";
		double price = 0;
		int menu_choice = 0;
		DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus status = new DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
			//initialize the ORB
			ORB orb = ORB.init(args, null);

			//obtaining Root Naming
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			//using NamingContextExt 
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			//object reference is resolved in naming
			String name = "Auction";
			impl = DistributedAuctionHelper.narrow(ncRef.resolve_str(name));

			while(menu_choice!=4){
				System.out.println("***BUYER MENU***");
				System.out.println (" Select from the following options: ");	
				System.out.println("1. View the status of auction"); 
				System.out.println("2. Place a bid"); 
				System.out.println("3. View the bid status"); 
				System.out.println("4. Quit");

				menu_choice = Integer.parseInt(br.readLine());

				switch(menu_choice){
				case 1:
					System.out.println("Input the user ID");
					userID = br.readLine();
					status = impl.viewAuctionStatus(userID);
					System.out.println("Item "+ status.itemName+ "Bidder ID "+status.userID  +"BidPrice "+ status.bidPrice);
					break;
				case 2:
					System.out.println("Input the user ID");
					userID = br.readLine();
					System.out.println("Input the bid price");
					price = Double.parseDouble(br.readLine());
					boolean bid_success = impl.bid(userID, price);
					if(bid_success){
						System.out.println("Bid placed successfully");
					}else{
						System.out.println("Bid could not be placed");
					}
					break;
				case 3:
					System.out.println("Input User ID");
					userID = br.readLine();
					String message = impl.viewBidStatus(userID);
					System.out.println(message);
					break;
				case 4:
					System.exit(1);
					break;
				}
			}
		}catch (Exception e) {
			System.out.println("CLIENT SIDE ERROR: "+ e);
			e.printStackTrace(System.out);
		}
	}
}
