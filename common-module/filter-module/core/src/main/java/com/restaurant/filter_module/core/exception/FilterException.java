package com.restaurant.filter_module.core.exception;

import com.restaurant.data.enums.IBaseErrorCode;

/**
 * The type Filter exception.
 */
public class FilterException extends Exception {
  private final IBaseErrorCode iBaseErrorCode;
  private final String message;

  /**
   * Instantiates a new Filter exception.
   *
   * @param iBaseErrorCode the base error code
   * @param message        the message
   */
  public FilterException(IBaseErrorCode iBaseErrorCode, String message) {
    this.iBaseErrorCode = iBaseErrorCode;
    this.message = message;
  }

  /**
   * Instantiates a new Filter exception.
   *
   * @param message the message
   */
  public FilterException(String message) {
    this.iBaseErrorCode = null;
    this.message = message;
  }
}
