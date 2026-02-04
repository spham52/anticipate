package com.example.smsserver.service;

import com.example.smsserver.dto.user.UserRegistrationRequestDTO;
import com.example.smsserver.exception.UserAlreadyExistsException;
import com.example.smsserver.exception.UserNotFoundException;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.UserRecord.CreateRequest;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final CaptchaService captchaService;

    @Override
    // saves user information to database and authentication credentials in Firebase auth
    public User registerUser(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        if (userRepository.existsByEmailIgnoreCase(userRegistrationRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        if (userRepository.existsByUsernameIgnoreCase(userRegistrationRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        captchaService.checkCaptcha(userRegistrationRequestDTO.getCaptcha());

        User user = User.builder()
                .email(userRegistrationRequestDTO.getEmail())
                .username(userRegistrationRequestDTO.getUsername())
                .build();

        try {
            // save into Firebase DB
            String firebaseUID = createFirebaseUser(userRegistrationRequestDTO);

            // set Firebase provided UID to local DB
            user.setUserID(firebaseUID);

            // save to local DB
            return userRepository.save(user);
        } catch (Exception e) {
            userRepository.delete(user);
            throw e;
        }
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    // save user credentials into Firebase Auth
    public String createFirebaseUser(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        CreateRequest request = new CreateRequest();
        request.setEmail(userRegistrationRequestDTO.getEmail());
        request.setPassword(userRegistrationRequestDTO.getPassword());
        request.setEmailVerified(true);

        UserRecord userRecord;

        try {
            userRecord = firebaseAuth.createUser(request);
            return userRecord.getUid();
        } catch (FirebaseAuthException e) {
            if (e.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new UserAlreadyExistsException();
            }
            throw new RuntimeException("Failed to create Firebase user");
        }
    }
}
