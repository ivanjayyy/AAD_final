package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.entity.UserProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfilePicRepo extends JpaRepository<UserProfilePic, Long> {
    UserProfilePic findByUser(User user);
}
