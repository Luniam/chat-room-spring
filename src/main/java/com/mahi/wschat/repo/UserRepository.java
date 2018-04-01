package com.mahi.wschat.repo;

import com.mahi.wschat.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByName(String name);
}
