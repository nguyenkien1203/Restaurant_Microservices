package com.restaurant.profileservice.service;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.profileservice.dto.CreateProfileRequest;
import com.restaurant.profileservice.dto.ProfileDto;
import com.restaurant.profileservice.dto.UpdateProfileRequest;
import com.restaurant.profileservice.filter.ProfileFilter;
import com.restaurant.redismodule.exception.CacheException;

import java.util.List;

public interface ProfileService {
    /**
     * Create a new profile for a user
     */
    ProfileDto createProfile(Long userId, CreateProfileRequest request) throws DataFactoryException;

    /**
     * Get profile by profile ID (used by admin)
     */
    ProfileDto getProfileById(Long id) throws CacheException, DataFactoryException;

    /**
     * Get profile by user ID
     */
    ProfileDto getProfileByUserId(Long userId) throws CacheException, DataFactoryException;

    /**
     * Update profile by profile ID
     */
    ProfileDto updateProfile(Long id, UpdateProfileRequest request) throws DataFactoryException, CacheException;

    /**
     * Delete profile by profile ID
     */
    void deleteProfile(Long id) throws DataFactoryException;

    /**
     * Get all profiles with filtering (admin only)
     */
    List<ProfileDto> getAllProfiles(ProfileFilter filter) throws CacheException, DataFactoryException;

    /**
     * Auto-create profile from user registration event
     */
    void createProfileFromUserRegistration(Long userId, String email, String fullName, String phone, String address) throws DataFactoryException;
}
