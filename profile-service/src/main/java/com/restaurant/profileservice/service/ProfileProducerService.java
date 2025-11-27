package com.restaurant.profileservice.service;

import com.restaurant.profileservice.event.DeleteProfileEvent;

public interface ProfileProducerService {

    void publishDeleteProfileEvent(DeleteProfileEvent deleteProfileEvent);
}
