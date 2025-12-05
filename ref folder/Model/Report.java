package com.revature.ExpenseReport.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {

    // Fields
    @Id @GeneratedValue
    private String reportId;
    private String reportTitle;
    private String reportStatus;

    @OneToMany(mappedBy = "report")
    private List<Expense> reportExpenses = new ArrayList<>();

    // Constructor
    public Report (String title, String status) {
        this.reportTitle = title;
        this.reportStatus = status;
    }

    // Methods
}
