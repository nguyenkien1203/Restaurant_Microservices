package com.restaurant.data.enums;

import java.io.Serializable;


public interface IBaseErrorCode extends Serializable {
    /**
     * Gets error code.
     *
     * @return the error code
     */
    String getErrorCode();

    /**
     * Gets message code.
     *
     * @return the message code
     */
    String getMessageCode();

    /**
     * Gets http status code.
     *
     * @return the http status code
     */
    int getHttpStatusCode();
}
