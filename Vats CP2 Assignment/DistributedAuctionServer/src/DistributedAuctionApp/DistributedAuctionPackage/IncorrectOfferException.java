package DistributedAuctionApp.DistributedAuctionPackage;


/**
* DistributedAuctionApp/DistributedAuctionPackage/IncorrectOfferException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DistributedAuctionApp.idl
* Sunday, March 11, 2018 9:34:59 PM CDT
*/

public final class IncorrectOfferException extends org.omg.CORBA.UserException
{
  public String description = null;

  public IncorrectOfferException ()
  {
    super(IncorrectOfferExceptionHelper.id());
  } // ctor

  public IncorrectOfferException (String _description)
  {
    super(IncorrectOfferExceptionHelper.id());
    description = _description;
  } // ctor


  public IncorrectOfferException (String $reason, String _description)
  {
    super(IncorrectOfferExceptionHelper.id() + "  " + $reason);
    description = _description;
  } // ctor

} // class IncorrectOfferException
