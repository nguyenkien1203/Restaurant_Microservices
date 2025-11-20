package com.restaurant.authservice.service;

import com.restaurant.authservice.dto.AuthDto;
import com.restaurant.authservice.dto.AuthFilter;
import com.restaurant.authservice.entity.AuthEntity;
import com.restaurant.authservice.factory.AuthFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.kafkamodule.service.IBaseKafkaProducer;
import com.restaurant.redismodule.exception.CacheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

   private final AuthFactory authFactory;

   private final PasswordEncoder passwordEncoder;



   public AuthDto register(AuthDto authDto) throws DataFactoryException {
       log.info("Registering new user with email: {}", authDto.getEmail());
       AuthFilter authFilter = AuthFilter.builder()
               .email(authDto.getEmail())
               .build();
       if (authFactory.exists(null, authFilter)) {
           throw new DataFactoryException("User with email " + authDto.getEmail() + " already exists");
       }
       String encodedPassword = passwordEncoder.encode(authDto.getPassword());
       // 3. Prepare DTO for creation
       // Note: We set ID to null so the DB generates it
       AuthDto newUser = AuthDto.builder()
               .email(authDto.getEmail())
               .password(encodedPassword)
               .role(AuthEntity.UserRole.USER) // Default role
               .isActive(true)
               .build();
       return authFactory.create(newUser);
   }

   public AuthDto login(String email, String password) throws DataFactoryException, CacheException {
       log.info("Login attempt for email: {}", email);
         AuthFilter authFilter = AuthFilter.builder()
                 .email(email)
                    .build();

         AuthDto authDto = authFactory.getModel(authFilter);
            if (authDto == null) {
                throw new DataFactoryException("User not found with email: " + email);
            }
       // 3. Verify password
       if (!passwordEncoder.matches(password, authDto.getPassword())) {
           throw new DataFactoryException("Invalid email or password");
       }

       // 4. Check if user is active
       if (!authDto.getIsActive()) {
           throw new DataFactoryException("User account is not active");
       }

       // 5. Don't return password in the response
       authDto.setPassword(null);

       return authDto;
   }

   public AuthDto getUserByEmail(String email) throws DataFactoryException, CacheException {
       log.info("Fetching user by email: {}", email);
       
       // Build filter to find user by email
       AuthFilter authFilter = AuthFilter.builder()
               .email(email)
               .build();
       
       // Get user from database via factory (uses cache if available)
       AuthDto authDto = authFactory.getModel(authFilter);
       
       if (authDto == null) {
           throw new DataFactoryException("User not found with email: " + email);
       }
       
       // Don't return password
       authDto.setPassword(null);
       
       return authDto;
   }

}
