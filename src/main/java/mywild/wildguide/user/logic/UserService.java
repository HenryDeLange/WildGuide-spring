package mywild.wildguide.user.logic;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import mywild.wildguide.framework.error.ForbiddenException;
import mywild.wildguide.framework.security.jwt.TokenService;
import mywild.wildguide.framework.security.jwt.TokenType;
import mywild.wildguide.user.data.UserEntity;
import mywild.wildguide.user.data.UserRepository;
import mywild.wildguide.user.web.Tokens;
import mywild.wildguide.user.web.User;
import mywild.wildguide.user.web.UserLogin;
import mywild.wildguide.user.web.UserMapper;

@Validated
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TokenService tokenService;

    @Transactional
    public Tokens register(@Valid User user) {
        user.setUsername(user.getUsername().trim().toLowerCase());
        UserEntity userEntity = repo.save(UserMapper.INSTANCE.dtoToEntity(user));
        return new Tokens(userEntity.getUsername(), tokenService.generateToken(TokenType.ACCESS, userEntity),
                tokenService.generateToken(TokenType.REFRESH, userEntity));
    }

    public Tokens login(@Valid UserLogin login) {
        Optional<UserEntity> foundEntity = repo.findByUsernameAndPassword(login.getUsername().toLowerCase(),
                login.getPassword());
        if (!foundEntity.isPresent())
            throw new ForbiddenException("Incorrect User credentials!");
        UserEntity entity = foundEntity.get();
        return new Tokens(entity.getUsername(), tokenService.generateToken(TokenType.ACCESS, entity),
                tokenService.generateToken(TokenType.REFRESH, entity));
    }

    public Tokens refresh(long userId) {
        UserEntity userEntity = getValidUser(userId);
        return new Tokens(userEntity.getUsername(), tokenService.generateToken(TokenType.ACCESS, userEntity),
                tokenService.generateToken(TokenType.REFRESH, userEntity));
    }

    /**
     * Make sure the userId is valid.
     */
    private UserEntity getValidUser(long userId) {
        if (userId <= 0)
            throw new ForbiddenException("Incorrect User ID!");
        Optional<UserEntity> userEntity = repo.findById(userId);
        if (!userEntity.isPresent())
            throw new ForbiddenException("Incorrect User ID!");
        return userEntity.get();
    }

}
