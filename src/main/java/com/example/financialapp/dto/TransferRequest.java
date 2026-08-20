package com.example.financialapp.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class TransferRequest {
  @NotBlank private String fromAccount;
  @NotBlank private String toAccount;
  @NotNull @DecimalMin("0.01") private BigDecimal amount;
  public String getFromAccount(){return fromAccount;} public void setFromAccount(String v){fromAccount=v;}
  public String getToAccount(){return toAccount;} public void setToAccount(String v){toAccount=v;}
  public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
}
