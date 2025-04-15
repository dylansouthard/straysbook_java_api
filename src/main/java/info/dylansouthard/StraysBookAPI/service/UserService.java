package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.dto.user.UserPrivateDTO;
import info.dylansouthard.StraysBookAPI.dto.user.UserPublicDTO;
import info.dylansouthard.StraysBookAPI.errors.ErrorFactory;
import info.dylansouthard.StraysBookAPI.mapper.UserMapper;
import info.dylansouthard.StraysBookAPI.model.user.User;
import info.dylansouthard.StraysBookAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserPublicDTO fetchUserDetails(Long userId) {
        User user = userRepository.findActiveById(userId).orElseThrow(ErrorFactory::userNotFound);
        return userMapper.toUserPublicDTO(user);
    }

    public UserPrivateDTO fetchMyProfile(User user) {
        if (user == null) throw ErrorFactory.auth();
        User foundUser = userRepository.findActiveById(user.getId()).orElseThrow(ErrorFactory::userNotFound);
        return userMapper.toUserPrivateDTO(foundUser);
    }


}
