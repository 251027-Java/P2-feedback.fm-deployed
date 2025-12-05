package com.revature.ExpenseReport.Repository;

import com.revature.ExpenseReport.Model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
//    Expense findById(String id);
     //@Query("SELECT * FROM expenses WHERE expenseMerchant = merchant")
    List<Expense> findByExpenseMerchant(String merchant);
}


// the interface that we create, paired with the use of JPA, produces the DAO and Repository class for us!