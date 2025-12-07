package com.revature.ExpenseReport.Controller;

import com.revature.ExpenseReport.Service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses") // domain:port/api/expenses
public class ExpenseController {
    // Fields
    private final ExpenseService service;

    // Constructor
    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    // Methods
    @GetMapping // domain:port/api/expenses
    public List<ExpenseDTO> getAllExpenses() {
        return service.getAllExpenses(); // all of the expenses!
    }

    @GetMapping("/search") // domain:port/api/expenses/search?merchant=Walmart
    public List<ExpenseDTO> search (@RequestParam String merchant) {
        return service.searchByExpenseMerchant(merchant); // all expenses for a merchant
    }

    // Create an expense
    @PostMapping
    public ExpenseDTO create(@RequestBody ExpenseWOIDDTO expensedto) {
        return service.create(expensedto);
    }


    // Expense lookup by > a value? > $100

    // Get expense by ID
    @GetMapping("/{id}")
    public ExpenseDTO getById(@PathVariable String id){
        return service.getById(id);
    }

    // Update/modify an expense
    @PutMapping("/{id}")
    public ExpenseDTO update(@PathVariable String id, @RequestBody ExpenseDTO dto) {
        return service.update(id, dto);
    }

    // Delete an expense
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}




