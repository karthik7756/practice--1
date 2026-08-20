package com.example.financialapp.config;
import com.example.financialapp.model.Account;
import com.example.financialapp.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
@Configuration
public class DataInitializer {
  @Bean CommandLineRunner load(AccountRepository r){return args->{if(r.count()==0){r.save(new Account("100001","Rahul",new BigDecimal("50000.00")));r.save(new Account("100002","Priya",new BigDecimal("25000.00")));}};}
}
