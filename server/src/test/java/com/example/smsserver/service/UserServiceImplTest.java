package com.example.smsserver.service;

import com.example.smsserver.dto.user.UserRegistrationRequestDTO;
import com.example.smsserver.exception.UserAlreadyExistsException;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.UserRepository;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.firebase.auth.UserRecord.CreateRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Test
        // test for when email already exists in the database
    void registerUser_throwsException_whenEmailAlreadyExists() {
        UserRegistrationRequestDTO request = buildUserRegistrationRequestDTO();

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
        // test for when username already exists in the database
    void registerUser_throwsException_whenUsernameAlreadyExists() {
        UserRegistrationRequestDTO request = buildUserRegistrationRequestDTO();
        when(userRepository.existsByUsernameIgnoreCase("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
        // test case when the UserRegistrationRequest is valid
    void registerUser_whenValidUser() throws FirebaseAuthException {
        UserRegistrationRequestDTO request = buildUserRegistrationRequestDTO();

        // validity checks return valid
        when(userRepository.existsByUsernameIgnoreCase("john")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);

        // mock createFirebaseUser func
        UserRecord userRecord = mock(UserRecord.class);
        when(userRecord.getUid()).thenReturn("uid-123");
        when(firebaseAuth.createUser(any(CreateRequest.class))).thenReturn(userRecord);

        // build User, return in userRepository and userService
        User user = buildUser();
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = userService.registerUser(request);

        assert (savedUser.getUsername().equals("john"));
        assert (savedUser.getEmail().equals("user@example.com"));
        assert (savedUser.getUserID().equals("uid-123"));

    }

    @Test
    // return valid UID when createFirebaseUser function is called
    void createFirebaseUser_returnsUid() throws FirebaseAuthException {

        UserRecord mockRecord = mock(UserRecord.class);
        when(mockRecord.getUid()).thenReturn("uid-123");
        when(firebaseAuth.createUser(any(CreateRequest.class))).thenReturn(mockRecord);

        UserRegistrationRequestDTO userRegistrationRequestDTO = buildUserRegistrationRequestDTO();

        String uid = userService.createFirebaseUser(userRegistrationRequestDTO);
        assert(uid).equals("uid-123");
    }

    @Test
    void createFirebaseUser_throwsUserAlreadyExistsException_whenEmailExists() throws FirebaseAuthException {
        when(firebaseAuth.createUser(any(CreateRequest.class))).
                thenThrow(new FirebaseAuthException(ErrorCode.ALREADY_EXISTS, "EMAIL_EXISTS",
                        null, null, null));

        UserRegistrationRequestDTO userRegistrationRequestDTO = buildUserRegistrationRequestDTO();

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userRegistrationRequestDTO));
    }

    @Test
    void createFirebaseUser_throwsRunTimeException() throws FirebaseAuthException {
        when(firebaseAuth.createUser(any(CreateRequest.class)))
                .thenThrow(new FirebaseAuthException(ErrorCode.ABORTED, "ANYTHING",
                        null, null, null));

        UserRegistrationRequestDTO userRegistrationRequestDTO = buildUserRegistrationRequestDTO();
        assertThrows(RuntimeException.class, () -> userService.registerUser(userRegistrationRequestDTO));
    }

    UserRegistrationRequestDTO buildUserRegistrationRequestDTO() {
        return UserRegistrationRequestDTO.builder()
                .email("user@example.com")
                .username("john")
                .password("testing123")
                .build();
    }

    User buildUser() {
        return User.builder()
                .email("user@example.com")
                .username("john")
                .userID("uid-123")
                .build();
    }
}