package com.finzly.config_management.Exception;

public class UpdateFailedException extends Exception{
  public UpdateFailedException() {
  }

  public UpdateFailedException(String message) {
    super(message);
  }

}