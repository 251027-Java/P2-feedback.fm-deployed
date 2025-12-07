package com.revature.ExpenseReport.Controller;

import java.util.List;

public record ReportDTO(
        String id,
        String title,
        String status,
        List<ExpenseDTO> expenses
) {}