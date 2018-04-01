package com.mahi.wschat.service;


import com.mahi.wschat.pojo.User;
import com.mahi.wschat.repo.UserRepository;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Inject
    UserRepository userRepository;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

}
