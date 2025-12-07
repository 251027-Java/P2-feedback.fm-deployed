package com.revature.ExpenseReport.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO (
        String expenseId,
        LocalDate expenseDate,
        BigDecimal expenseValue,
        String expenseMerchant
) {}
