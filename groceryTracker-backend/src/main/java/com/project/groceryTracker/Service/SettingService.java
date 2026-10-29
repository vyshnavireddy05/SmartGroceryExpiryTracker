package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.SettingDTO;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SettingService {

    private final UserRepo userRepository;

    public SettingService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    public SettingDTO getSettings(String email) {
        User user = getUserByEmail(email);

        SettingDTO dto = new SettingDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNum(user.getPhoneNum());
        dto.setNotificationsEnabled(user.isNotificationsEnabled());
        dto.setExpiryRemainderDays(user.getExpiryRemainderDays());

        return dto;
    }

    @Transactional
    public SettingDTO updateSettings(String email, SettingDTO dto) {
        User user = getUserByEmail(email);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNum(dto.getPhoneNum());

        // Keep the account email unchanged for now.
        // Email changes need a dedicated flow to handle JWTs safely.

        user.setNotificationsEnabled(dto.isNotificationsEnabled());
        user.setExpiryRemainderDays(dto.getExpiryRemainderDays());

        User updatedUser = userRepository.save(user);

        SettingDTO response = new SettingDTO();
        response.setFirstName(updatedUser.getFirstName());
        response.setLastName(updatedUser.getLastName());
        response.setEmail(updatedUser.getEmail());
        response.setPhoneNum(updatedUser.getPhoneNum());
        response.setNotificationsEnabled(
                updatedUser.isNotificationsEnabled());
        response.setExpiryRemainderDays(
                updatedUser.getExpiryRemainderDays());

        return response;
    }
}
