package com.revature.ExpenseReport.Repository;

import com.revature.ExpenseReport.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {}
