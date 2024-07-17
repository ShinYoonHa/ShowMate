package com.culture.CultureService.service;


import com.culture.CultureService.entity.User;
import com.culture.CultureService.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
@Transactional
    public void deleteUser(User user) {
        System.out.println("Deleting user: " + user.getEmail());
        userRepository.delete(user);
        System.out.println("User deleted from repository: " + user.getEmail());
    }



    public User save(User user) {
        return userRepository.save(user);
    }
}
