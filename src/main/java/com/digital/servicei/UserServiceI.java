package com.digital.servicei;

import com.digital.dto.EmailDto;
import com.digital.dto.ManagerStatusDto;
import com.digital.dto.ResetPasswordDto;
import com.digital.entity.User;

import java.util.List;

public interface UserServiceI {

   String add(User user);

   void updateUser(User user);

   User findUserByUsername(String username);

   String sendOtp(EmailDto emailDto);

   String resetPassword(ResetPasswordDto resetPasswordDto);

   String manageUserStatus(Long userId, ManagerStatusDto manageStatusDto);

    List<User> getAllUsers();

   User getUserById(Long userId);
}
