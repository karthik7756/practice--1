package com.example.financialapp.controller;
import com.example.financialapp.model.Account;
import com.example.financialapp.repository.AccountRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/accounts")
public class AccountController {
  private final AccountRepository repository;
  public AccountController(AccountRepository r){repository=r;}
  @GetMapping public List<Account> all(){return repository.findAll();}
  @GetMapping("/{accountNumber}") public Account one(@PathVariable String accountNumber){
    return repository.findByAccountNumber(accountNumber).orElseThrow(()->new RuntimeException("Account not found"));
  }
}
