package DistributedAuctionApp.DistributedAuctionPackage;

/**
* DistributedAuctionApp/DistributedAuctionPackage/IncorrectSellExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DistributedAuctionApp.idl
* Sunday, March 11, 2018 9:34:59 PM CDT
*/

public final class IncorrectSellExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellException value = null;

  public IncorrectSellExceptionHolder ()
  {
  }

  public IncorrectSellExceptionHolder (DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DistributedAuctionApp.DistributedAuctionPackage.IncorrectSellExceptionHelper.type ();
  }

}
