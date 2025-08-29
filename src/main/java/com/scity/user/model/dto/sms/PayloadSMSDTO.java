package com.scity.user.model.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadSMSDTO {
    private List<SMS> sms;

    @Data
    public static class SMS {
        private String from;
        private String to;
        private String text;
    }
}
