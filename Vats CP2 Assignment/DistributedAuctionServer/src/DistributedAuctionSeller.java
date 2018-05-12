import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DistributedAuctionApp.*;
public class DistributedAuctionSeller {
	static DistributedAuction impl;

	public static void main(String args[])throws IOException{
		int menu_choice = 0;
		String sellerID = "";
		String itemName = "";
		double startPrice = 0;
		DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus status = new DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
			//initializing ORB
			ORB orb = ORB.init(args, null);

			//obtaining root naming
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			//Using NamingContext
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			//HERE object Reference resolving Naming
			String name = "Auction";
			impl = DistributedAuctionHelper.narrow(ncRef.resolve_str(name));

			System.out.println("Handle obtained on server object: "+ impl);

			while(menu_choice!=5){
				System.out.println("***SELLER MENU***");
				System.out.println (" Select from the following options: ");	
				System.out.println("1. Place item for auction"); 
				System.out.println("2. Sell the current item"); 
				System.out.println("3. Get the highest bidder"); 
				System.out.println("4. Get the status of auction"); 
				System.out.println("5. Quit");

				menu_choice = Integer.parseInt(br.readLine());

				switch(menu_choice){
				case 1:
					System.out.println("Item Name:");
					itemName = br.readLine();
					System.out.println("Seller ID:");
					sellerID = br.readLine();
					System.out.println("Initial Price:");
					startPrice = Double.parseDouble(br.readLine());
					boolean offer_success = impl.offerItem(itemName, sellerID, startPrice);
					if(offer_success){
						System.out.println("Item placed successfully");
					}else{
						System.out.println("Item cannot be placed");
					}
					break;
				case 2:
					System.out.println("Enter SellerID");
					sellerID = br.readLine();
					System.out.println("Selling the item" + itemName);
					boolean sell_success = impl.sell(sellerID);
					if(sell_success){
						System.out.println("Item sold successfully");
					}else{
						System.out.println("Item cannot be sold");
					}
					break;
				case 3:					
					System.out.println("Enter SellerID");
					sellerID = br.readLine();
					String highest_bidder = impl.viewHighBidder(sellerID);
					System.out.println("The highest bidder is: "+ highest_bidder);
					break;
				case 4:
					System.out.println("Enter SellerID");
					sellerID = br.readLine();
					status = impl.viewAuctionStatus(sellerID);
					System.out.println("Item "+ status.itemName+ "Bidder ID "+status.userID  +"BidPrice "+ status.bidPrice);
					break;
				case 5:
					System.exit(1);
					break;
				}
			}
		} catch (Exception e){
			System.out.println("SELLER SIDE ERROR:  "+ e);
			e.printStackTrace(System.out);
		}
	}
}
