package com.realestate.backend.dto.response;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String roomId;
    private Long propertyId;
    private Long sellerId;
    private Long buyerId;

    public RoomResponse(String roomId, Long propertyId, Long sellerId, Long buyerId, Object o) {
    }
}
