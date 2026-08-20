package com.example.financialapp.service;
import com.example.financialapp.dto.TransferRequest;
import com.example.financialapp.model.*;
import com.example.financialapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class TransactionService {
  private final AccountRepository accounts; private final TransactionRepository transactions;
  public TransactionService(AccountRepository a,TransactionRepository t){accounts=a;transactions=t;}
  @Transactional public Transaction transfer(TransferRequest r){
    if(r.getFromAccount().equals(r.getToAccount())) throw new IllegalArgumentException("Accounts must be different");
    Account from=accounts.findByAccountNumber(r.getFromAccount()).orElseThrow(()->new IllegalArgumentException("Source account not found"));
    Account to=accounts.findByAccountNumber(r.getToAccount()).orElseThrow(()->new IllegalArgumentException("Destination account not found"));
    BigDecimal amount=r.getAmount();
    if(from.getBalance().compareTo(amount)<0) throw new IllegalArgumentException("Insufficient balance");
    from.setBalance(from.getBalance().subtract(amount)); to.setBalance(to.getBalance().add(amount));
    accounts.save(from); accounts.save(to);
    return transactions.save(new Transaction("TXN-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(),from.getAccountNumber(),to.getAccountNumber(),amount,"SUCCESS",LocalDateTime.now()));
  }
  public List<Transaction> history(String account){return transactions.findByFromAccountOrToAccountOrderByCreatedAtDesc(account,account);}
}
