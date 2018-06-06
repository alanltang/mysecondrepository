package com.pingpong.app;

public class ErrorBean {
	private String errormsg;
  private String detailmsg;
  private boolean requirelogin = false;

	public String geterrormsg() {return errormsg == null? "" : errormsg;}
  public String getdetailmsg() { return detailmsg == null? "" : detailmsg;}
  public boolean getrequirelogin() { return requirelogin;}
	public void seterrormsg(String errormsg) { this.errormsg=errormsg ; }
  public void setdetailmsg(String detailmsg) { this.detailmsg=detailmsg ; }
  public void setrequirelogin(boolean requirelogin) { this.requirelogin=requirelogin;}
} 
	



		