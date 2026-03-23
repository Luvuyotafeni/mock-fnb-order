package com.fnb.paymentService.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String cardHolderName;
    private String cardNumber;     // mock — e.g. "4111111111111111"
    private String expiryDate;     // mock — e.g. "12/26"
    private String cvv;
}
