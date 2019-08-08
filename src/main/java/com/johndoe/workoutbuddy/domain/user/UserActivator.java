package com.johndoe.workoutbuddy.domain.user;

import com.johndoe.workoutbuddy.domain.DomainError;
import com.johndoe.workoutbuddy.domain.SuccessMessage;
import com.johndoe.workoutbuddy.domain.user.dto.UserDto;
import com.johndoe.workoutbuddy.domain.user.dto.UserError;
import com.johndoe.workoutbuddy.domain.user.dto.ActivationTokenDto;
import com.johndoe.workoutbuddy.domain.user.port.UserRepository;
import com.johndoe.workoutbuddy.domain.user.port.VerificationTokenRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.time.LocalDateTime;
import java.util.UUID;

@Log
@RequiredArgsConstructor
class UserActivator {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    Either<DomainError, SuccessMessage> activate(UUID uuid, String username) {
        var token = tokenRepository.findToken(uuid);
        if(token.isPresent()) {
            if(isValid(token.get())) {
                tokenRepository.updateToken(ActivationTokenDto.builder()
                        .uuid(token.get().getUuid())
                        .username(token.get().getUsername())
                        .activated(true)
                        .expirationDateTime(LocalDateTime.now())
                        .build());
            } else {
                log.info("not vaalid");
            }
        } else {
            return Either.left(UserError.INVALID_TOKEN);
        }
        var user = userRepository.findUser(username);

        if(user.isPresent()) {
            activateUser(user.get());
            return Either.right(new SuccessMessage("Account activated"));
        } else {
            return Either.left(UserError.ACTIVATION_FAILED);
        }

    }

    private void activateUser(UserDto userDto) {
        var user = objectMapper.userToEntity(userDto);
        user.activate();
        userRepository.saveUser(objectMapper.userToDto(user));
    }
//
//    private Either<DomainError, SuccessMessage> handleToken(UUID uuid) {
//       return tokenRepository.findToken(uuid).map(this::deactivateToken).orElse(Either.left(UserError.INVALID_TOKEN));
//    }
//
//    private Either<DomainError, SuccessMessage> deactivateToken(ActivationTokenDto token) {
//        return isValid(token) ? deactivate(token) : Either.left(UserError.ACTIVATION_FAILED);
//    }
//
    private boolean isValid(ActivationTokenDto token) {
        return !token.isActivated() && token.getExpirationDateTime().isBefore(LocalDateTime.now());
    }
//
//    private Either<DomainError, SuccessMessage> deactivate(ActivationTokenDto token) {
//        tokenRepository.updateToken(ActivationTokenDto.builder()
//                .uuid(token.getUuid())
//                .username(token.getUsername())
//                .activated(true)
//                .expirationDateTime(LocalDateTime.now())
//                .build());
//        return Either.right(new SuccessMessage());
//    }



}
