package com.example.financialapp.repository;
import com.example.financialapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction,Long>{
  List<Transaction> findByFromAccountOrToAccountOrderByCreatedAtDesc(String fromAccount,String toAccount);
}
