package com.example.financialapp.controller;
import com.example.financialapp.dto.TransferRequest;
import com.example.financialapp.model.Transaction;
import com.example.financialapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionService service;
  public TransactionController(TransactionService s){service=s;}
  @PostMapping("/transfer") public Transaction transfer(@Valid @RequestBody TransferRequest r){return service.transfer(r);}
  @GetMapping("/history/{accountNumber}") public List<Transaction> history(@PathVariable String accountNumber){return service.history(accountNumber);}
}
