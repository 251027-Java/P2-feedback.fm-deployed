package com.revature.ExpenseReport.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseWOIDDTO (
        LocalDate expenseDate,
        BigDecimal expenseValue,
        String expenseMerchant
) {}