package com.connectly.Connectly_member_service.controller;

import com.connectly.Connectly_member_service.Repository.UserRepository;
import com.connectly.Connectly_member_service.dto.AuthRequest;
import com.connectly.Connectly_member_service.dto.AuthResponse;
import com.connectly.Connectly_member_service.dto.UserDto;
import com.connectly.Connectly_member_service.model.User;
import com.connectly.Connectly_member_service.service.CustomUserDetailsService;
import com.connectly.Connectly_member_service.util.AuthEntryPointJwt;
import com.connectly.Connectly_member_service.util.JwtUtil;
import com.connectly.Connectly_member_service.utils.dateTime.DateTimePatternConverter;
import com.connectly.Connectly_member_service.utils.emailValidation.MailValidation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.log4j.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public static final Logger LOGGER = Logger.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            LOGGER.info("Attempting authentication for: " + authRequest.getEmail());
            // Verify user exists
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getEmail()));
            LOGGER.info("user found");

            // Verify password matches
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                System.out.println();
                LOGGER.info("Password does not match!");
                return ResponseEntity.status(403).body("Invalid email or password.");
            }

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            // Load user details and generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            String jwt = jwtUtil.generateToken(userDetails, user.getApiToken(),user.getRoles(), user.getEmail());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            LOGGER.info("JWT is generated to the usermail "+user.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            LOGGER.info("Authentication failed: Invalid email or password.");
            return ResponseEntity.status(403).body("Invalid email or password.");
        } catch (Exception e) {
            LOGGER.info("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(500).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/dummy")
    public ResponseEntity<String> dummyCheckpoint(){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hey Hrithik");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || !MailValidation.isValidEmail(user.getEmail())) {
            LOGGER.info("Provide valid credentials (email/password)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide valid credentials (email/password)");
        }
        if (!user.getConfirmPassword().equals(user.getPassword()) || user.getPassword().isEmpty() || user.getConfirmPassword().isEmpty()) {
            LOGGER.info("Passwords do not match or are empty!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match or are empty!");
        }
        if (user.getFirstName().isEmpty()) {
            LOGGER.info("Enter your first name!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your first name!");
        }
        if (!user.getPhoneNumber().matches("\\d{10}")) {
            LOGGER.info("Enter a valid number!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter a valid number!");
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            LOGGER.info("Assign a role!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assign a role!");
        }


        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            LOGGER.info( user.getEmail()+". Email already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        String autoLoginKey=jwtUtil.generateAutoLoginKey();

        user.setFirstName(user.getFirstName());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setMonthlyEmailCredits(100);
        user.setYearlyEmailCredits(500);
        user.setMonthlyPhoneCredits(50);
        user.setYearlyPhoneCredits(150);
        user.setMonthlyLinkedinCredits(50);
        user.setYearlyLinkedinCredits(150);
        user.setProENabled(false);
        user.setBasicEnabled(true);
        user.setEnterpriseEnabled(false);
        user.setNoOfEmailsPerMonth(100);
        user.setNoOfEmailsPerYear(300);
        user.setUserStatus(true);
        String currentDate = DateTimePatternConverter.getCurrentDate();
        user.setCreatedDate(currentDate);
        user.setYearlyPlanActive(true);
        user.setEmailValidationEnabled(false);
        user.setZohoIntegrationEnabled(false);
        user.setHubspotIntegrationEnabled(false);
        user.setNoOfSequencecs(1000);
        user.setConfirmPassword("");
        user.setAutoLoginKey(autoLoginKey);
        user.setApiToken(new ObjectId().toString());
        user.setRoles(user.getRoles());
        userRepository.save(user);
        LOGGER.info( user.getEmail()+". User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @GetMapping("/ak")
    public ResponseEntity<?> loginUsingAutologinKey(@RequestParam String loginKey){
        if (loginKey == null || loginKey.trim().isEmpty()) {
            LOGGER.info("Incorrect autoLoginKey: "+loginKey);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter a valid key");
        }
        User user=userRepository.findByAutoLoginKey(loginKey).orElseThrow(()-> new RuntimeException("No user found"));

        UserDto userDto = new UserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getPhoneNumber(),
                user.getRoles()
        );
        LOGGER.info("Return the user for the given autoLoginKey. The user mail is "+userDto.getEmail());
        return ResponseEntity.ok(userDto);
    }
}
