package com.revature.ExpenseReport;

import com.revature.ExpenseReport.Model.AppUser;
import com.revature.ExpenseReport.Model.Expense;
import com.revature.ExpenseReport.Model.Report;
import com.revature.ExpenseReport.Repository.AppUserRepository;
import com.revature.ExpenseReport.Repository.ExpenseRepository;
import com.revature.ExpenseReport.Repository.ReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class ExpenseReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseReportApplication.class, args);
	}

    @Bean // Bean is a single method that is run after the application is started
    CommandLineRunner seedData (ExpenseRepository expenseRepository, ReportRepository reportRepository, AppUserRepository appUserRepository) {
        return args -> {

            // Report seed
            var r1 = new Report("Plano", "DRAFT");
            var r2 = new Report("Reston", "SUBMITTED");
            reportRepository.save(r1);
            reportRepository.save(r2);

            // Expenses seed
            var e1 = new Expense(LocalDate.now(), new BigDecimal(59.99), "Walmart");
            e1.setReport(r1);

            var e2 = new Expense(LocalDate.now().minusDays(1), new BigDecimal(14.75), "Starbucks");
            e2.setReport(r1);

            var e3 = new Expense(LocalDate.now().minusDays(25), new BigDecimal(99.88), "Buffalo Wild Wings");
            e3.setReport(r2);

            expenseRepository.saveAll(List.of(e1, e2, e3));

            // AppUser seed
            appUserRepository.save(new AppUser("admin", "password123", "ADMIN"));
            appUserRepository.save(new AppUser("user", "secret", "USER"));
        };
    }
}




// Params
// report id, expense id


// verify they exist in the database
// retrieve from the db
// is the expense already on a report?
//  - if it is...?
//      - stop the action, alert the user
//  - if it's not...?
//      -attach the expense to the report, and the report id to the expense

// save to the db












