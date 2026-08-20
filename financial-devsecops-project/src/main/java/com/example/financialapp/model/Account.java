package com.example.financialapp.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="accounts")
public class Account {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,unique=true) private String accountNumber;
  @Column(nullable=false) private String customerName;
  @Column(nullable=false,precision=19,scale=2) private BigDecimal balance;
  public Account() {}
  public Account(String n,String c,BigDecimal b){accountNumber=n;customerName=c;balance=b;}
  public Long getId(){return id;} public String getAccountNumber(){return accountNumber;}
  public String getCustomerName(){return customerName;} public BigDecimal getBalance(){return balance;}
  public void setAccountNumber(String v){accountNumber=v;} public void setCustomerName(String v){customerName=v;} public void setBalance(BigDecimal v){balance=v;}
}
