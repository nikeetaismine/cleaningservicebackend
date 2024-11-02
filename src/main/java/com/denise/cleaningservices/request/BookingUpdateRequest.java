package com.denise.cleaningservices.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class BookingUpdateRequest {

    private String status;
    private Long workerId;

}
