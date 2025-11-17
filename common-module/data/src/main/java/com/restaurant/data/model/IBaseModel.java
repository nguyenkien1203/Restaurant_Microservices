package com.restaurant.data.model;

import java.io.Serializable;

public interface IBaseModel<I> extends Serializable {

    I getId();
}
