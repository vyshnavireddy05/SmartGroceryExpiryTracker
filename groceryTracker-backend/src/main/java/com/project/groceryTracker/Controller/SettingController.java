package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.SettingDTO;
import com.project.groceryTracker.Service.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/Details")
    public ResponseEntity<SettingDTO> getSettings(
            @AuthenticationPrincipal UserDetails userDetails) {

        SettingDTO settings =
                settingService.getSettings(userDetails.getUsername());

        return ResponseEntity.ok(settings);
    }

    @PutMapping("/update")
    public ResponseEntity<SettingDTO> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SettingDTO dto) {

        SettingDTO updatedSettings =
                settingService.updateSettings(
                        userDetails.getUsername(), dto);

        return ResponseEntity.ok(updatedSettings);
    }
}
