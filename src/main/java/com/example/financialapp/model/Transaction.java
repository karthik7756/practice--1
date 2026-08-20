package com.example.financialapp.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="transactions")
public class Transaction {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,unique=true) private String transactionReference;
  @Column(nullable=false) private String fromAccount;
  @Column(nullable=false) private String toAccount;
  @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
  @Column(nullable=false) private String status;
  @Column(nullable=false) private LocalDateTime createdAt;
  public Transaction(){}
  public Transaction(String r,String f,String t,BigDecimal a,String s,LocalDateTime c){transactionReference=r;fromAccount=f;toAccount=t;amount=a;status=s;createdAt=c;}
  public Long getId(){return id;} public String getTransactionReference(){return transactionReference;}
  public String getFromAccount(){return fromAccount;} public String getToAccount(){return toAccount;}
  public BigDecimal getAmount(){return amount;} public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
}
