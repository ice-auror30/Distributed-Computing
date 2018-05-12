import java.util.ArrayList;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DistributedAuctionApp.DistributedAuction;
import DistributedAuctionApp.DistributedAuctionHelper;
import DistributedAuctionApp.DistributedAuctionPOA;
import DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus;
import DistributedAuctionApp.DistributedAuctionPackage.IncorrectBidException;
import DistributedAuctionApp.DistributedAuctionPackage.IncorrectOfferException;
import DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellException;
import DistributedAuctionApp.DistributedAuctionPackage.IncorrectStatusException;

class DistributedAuctionImpl extends DistributedAuctionPOA{
	private String itemName;
	private String sellerID;
	private String userID;
	private double startPrice;
	private double currentPrice;
	private ArrayList<String> AuctionItemList; //The itemDescription
	private ArrayList<Double> AuctionBidPriceList;
	private ArrayList<String> userData; //The userID
	private DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus status = new DistributedAuctionApp.DistributedAuctionPackage.AuctionStatus();

	private ORB orb;

	//	public DistributedAuctionImpl(ORB orb_VAL) {
	//		orb = orb_VAL;
	//	}
	public void setORB(ORB orb2) {
		// TODO Auto-generated method stub
		orb = orb2;

	}
	/*
	 * offerItem
	 * @see DistributedAuctionApp.DistributedAuctionOperations#offerItem(java.lang.String, java.lang.String, double)
	 * if auction is empty, any user can offer item for sale
	 * Needed: user ID, item description, optional initial price
	 * Not permitted if auction is currently busy
	 */
	@Override
	public boolean offerItem(String itemName, String sellerID, double startPrice) throws IncorrectOfferException {
		// TODO Auto-generated method stub
		try{
			if(AuctionItemList.isEmpty()){
				this.sellerID = sellerID;
				this.itemName = itemName;
				if ((Double)startPrice == null){
					this.startPrice = 0.0d;
				}else{
					this.startPrice = startPrice;
				}
				currentPrice = startPrice;
				AuctionItemList.add(itemName);
				AuctionBidPriceList.add(startPrice);
				userData.add("No Bidder");
				System.out.println("Item: "+ itemName + "placeed for auction by: "+ sellerID + "Starting Price: "+startPrice);	
			}
		}catch(Exception e){
			System.out.println(e);
			System.exit(1);
		}
		return true;	
	}
	/*
	 * (non-Javadoc)
	 * @see DistributedAuctionApp.DistributedAuctionOperations#viewHighBidder(java.lang.String)
	 * identity of current High Bidder
	 * Needed: SellerID
	 */
	@Override
	public String viewHighBidder(String sellerId) throws IncorrectSellException {
		// TODO Auto-generated method stub
		String highestBidderID = "no bidder";
		int lengthList = userData.size();
		if(this.sellerID.equalsIgnoreCase(sellerId)){
			highestBidderID = userData.get(lengthList-1);
		}
		System.out.println("Highest Bidder is: "+highestBidderID);
		return highestBidderID;
	}
	/*
	 * (non-Javadoc)
	 * @see DistributedAuctionApp.DistributedAuctionOperations#viewAuctionStatus()
	 * any user can see the status (empty/active). If active description of item and price.
	 * Identity of current high bidder visible only to the seller
	 * Needed: userID
	 */
	@Override
	public AuctionStatus viewAuctionStatus(String userID) throws IncorrectStatusException {
		// TODO Auto-generated method stub
		if(AuctionBidPriceList.isEmpty()){
			status.bidPrice = 0;
			status.itemName = "No Item";
			status.userID = "No User";
		}
		else{
			if(userID.equalsIgnoreCase(sellerID)){
				try {
					status.userID = viewHighBidder(userID);
				} catch (IncorrectSellException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				status.userID = "Hidden";
			}
			status.itemName = itemName;
			status.bidPrice = currentPrice;
		}
		return status;
	}
	/*
	 * (non-Javadoc)
	 * @see DistributedAuctionApp.DistributedAuctionOperations#viewBidStatus(java.lang.String)
	 * To be called by the bidder to determine whether he is the highest bidder or not. Fails
	 * if auction is not active (no item for sale || item is sold)
	 * Needed: bidderID 
	 */
	@Override
	public String viewBidStatus(String userID) throws IncorrectBidException {
		// TODO Auto-generated method stub
		String bidStatus;
		if(AuctionBidPriceList.size() == 0)
		{
			bidStatus = "Auction not Active";
		}else{
			if (userData.get(userData.size()-1).equalsIgnoreCase(userID)){
				bidStatus = "You are the highest bidder";
			}
			else{
				bidStatus = "You are not the highest bidder";
			}
		}
		return bidStatus;
	}
	/*
	 * (non-Javadoc)
	 * @see DistributedAuctionApp.DistributedAuctionOperations#bid(java.lang.String)
	 * Any user apart from the seller can bid for the item.
	 * Needed: UserId, Price
	 */
	@Override
	public boolean bid(String userID, double price) throws IncorrectBidException {
		// TODO Auto-generated method stub
		if(!userID.equalsIgnoreCase(sellerID)){
			if(AuctionBidPriceList.size()!=0){				
				if(price > AuctionBidPriceList.get(AuctionBidPriceList.size()-1)){
					{
						userData.add(userID);
						AuctionBidPriceList.add(price);
					}
				}
				currentPrice = price;
				return true;
			}
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see DistributedAuctionApp.DistributedAuctionOperations#sell(java.lang.String)
	 * If at least one bid has been accepted, seller can sell the item. Seller must identify with the sellerID
	 * Needed: sellerID
	 */
	@Override
	public boolean sell(String userID) throws IncorrectSellException {
		// TODO Auto-generated method stub
		String message = "";
		if(userID.equalsIgnoreCase(sellerID)){
			if(AuctionBidPriceList.size()>1){
				message = itemName + " sold for "+ AuctionBidPriceList.get(AuctionBidPriceList.size()-1) + "to "+ userData.get(userData.size()-1);
				AuctionBidPriceList.clear();
				userData.clear();
			}
			System.out.println(message);
			return true;	
		}
		return false;
	}
}

public class DistributedAuctionServer {

	public static void main(String args[]){
		try{
			//initializing the ORB
			ORB orb = ORB.init(args, null);

			//get reference to rootPOA & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			//create servant and register it with the ORB
			DistributedAuctionImpl impl = new DistributedAuctionImpl();
			impl.setORB(orb);

			//get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
			DistributedAuction href = DistributedAuctionHelper.narrow(ref);

			//get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			//use NamingContextExt which is part of the Interoperable Naming Service(INS) specification
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			//bind teh Object Reference in Naming
			String name = "Auction";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);

			//SERVER READY
			System.out.println("***SERVER READY***");

			//wait for invocations from clients
			orb.run();

		}catch(Exception e){
			System.out.println("SERVER Error: ");
			e.printStackTrace(System.out);
		}
		System.out.println("EXITING FROM SERVER....");
	}
}

