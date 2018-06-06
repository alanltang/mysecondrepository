package com.pingpong.app;

public class ConfigException extends RuntimeException {
  private boolean requireLogin = false;
  
  public ConfigException(String errorMsg)
  {
    super(errorMsg);
    requireLogin = false;
  }
  
  public ConfigException(String errorMsg, boolean requireLogin)
  {
    super(errorMsg);
    this.requireLogin = requireLogin;
  }
  
  public boolean getRequireLogin()
  {
    return requireLogin;
  }
}
  