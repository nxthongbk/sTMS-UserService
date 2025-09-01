package com.scity.user.model.dto.user;

import com.scity.user.model.dto.location.LocationDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLocationDTO {
    private LocationDTO location;
    private List<UserElementDTO> users;
}
