package mywild.wildguide.user.logic;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.error.NotFoundException;
import mywild.wildguide.framework.security.jwt.TokenService;
import mywild.wildguide.framework.security.jwt.TokenType;
import mywild.wildguide.user.data.UserEntity;
import mywild.wildguide.user.data.UserRepository;
import mywild.wildguide.user.web.Tokens;
import mywild.wildguide.user.web.User;
import mywild.wildguide.user.web.UserInfo;
import mywild.wildguide.user.web.UserLogin;

@Validated
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper mapper;

    @Transactional
    public @Valid Tokens register(@Valid User user) {
        user.setEmail(passwordEncoder.encode(user.getEmail().trim().toLowerCase()));
        user.setUsername(user.getUsername().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity userEntity = repo.save(mapper.dtoToEntity(user));
        return new Tokens(
            userEntity.getId(),
            userEntity.getUsername(),
            tokenService.generateToken(TokenType.ACCESS, userEntity),
            tokenService.generateToken(TokenType.REFRESH, userEntity));
    }

    public @Valid Tokens login(@Valid UserLogin login) {
        Optional<UserEntity> foundEntity = repo.findByUsername(login.getUsername().toLowerCase());
        if (!foundEntity.isPresent() || !passwordEncoder.matches(login.getPassword(), foundEntity.get().getPassword()))
            throw new ForbiddenException("user.incorrect");
        UserEntity entity = foundEntity.get();
        return new Tokens(
            entity.getId(),
            entity.getUsername(),
            tokenService.generateToken(TokenType.ACCESS, entity),
            tokenService.generateToken(TokenType.REFRESH, entity));
    }

    public @Valid Tokens refresh(long userId) {
        UserEntity userEntity = getValidUser(userId);
        return new Tokens(
            userEntity.getId(),
            userEntity.getUsername(),
            tokenService.generateToken(TokenType.ACCESS, userEntity),
            tokenService.generateToken(TokenType.REFRESH, userEntity));
    }

    public @Valid UserInfo findUserInfo(@Valid @NotEmpty String username) {
        UserEntity foundEntity = repo.findByUsername(username.trim().toLowerCase())
            .orElseThrow(() -> new NotFoundException("user.not-found"));
        return mapper.entityToDto(foundEntity);
    }

    public @Valid UserInfo updateUserProfile(long userId, String description) {
        UserEntity userEntity = getValidUser(userId);
        userEntity.setDescription(description);
        repo.save(userEntity);
        return mapper.entityToDto(userEntity);
    }

    /**
     * Make sure the userId is valid.
     */
    private UserEntity getValidUser(long userId) {
        if (userId <= 0)
            throw new ForbiddenException("user.not-found");
        Optional<UserEntity> userEntity = repo.findById(userId);
        if (!userEntity.isPresent())
            throw new ForbiddenException("user.not-found");
        return userEntity.get();
    }

}
