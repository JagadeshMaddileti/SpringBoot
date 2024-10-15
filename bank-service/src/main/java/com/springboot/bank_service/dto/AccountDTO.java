package com.springboot.bank_service.dto;

import lombok.Data;

@Data
public class AccountDTO {
   private Long id;
   private Long accountNumber;
   private String accountType;
   private int balance;
   private Long bankId;
}
