package DistributedAuctionApp;

/**
* DistributedAuctionApp/DistributedAuctionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DistributedAuctionApp.idl
* Sunday, March 11, 2018 9:34:59 PM CDT
*/

public final class DistributedAuctionHolder implements org.omg.CORBA.portable.Streamable
{
  public DistributedAuctionApp.DistributedAuction value = null;

  public DistributedAuctionHolder ()
  {
  }

  public DistributedAuctionHolder (DistributedAuctionApp.DistributedAuction initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DistributedAuctionApp.DistributedAuctionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DistributedAuctionApp.DistributedAuctionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DistributedAuctionApp.DistributedAuctionHelper.type ();
  }

}
