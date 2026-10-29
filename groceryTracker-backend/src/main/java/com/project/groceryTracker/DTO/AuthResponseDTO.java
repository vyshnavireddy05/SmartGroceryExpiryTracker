
package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
}