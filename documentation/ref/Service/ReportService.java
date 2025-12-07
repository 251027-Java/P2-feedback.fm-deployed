package com.revature.ExpenseReport.Service;

import com.revature.ExpenseReport.Controller.ExpenseDTO;
import com.revature.ExpenseReport.Controller.ReportDTO;
import com.revature.ExpenseReport.Model.Report;
import com.revature.ExpenseReport.Repository.ReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReportService {
    // Fields
    private final ReportRepository repository;

    // Constructor
    public ReportService(ReportRepository repo){
        this.repository = repo;
    }

    // Methods

    public List<ReportDTO> getAll() {
        return repository.findAll().stream().map(this::mapToDto).toList();
    }

    public ReportDTO getById(String id) {
        return repository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    public ReportDTO create(ReportDTO dto) {
        Report entity = new Report(dto.title(), dto.status());
        return mapToDto(repository.save(entity));
    }

    public ReportDTO update(String id, ReportDTO dto) {
        Report entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        entity.setReportTitle(dto.title());
        entity.setReportStatus(dto.status());

        return mapToDto(repository.save(entity));
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    private ReportDTO mapToDto(Report report) {
        List<ExpenseDTO> expenseDtos = report.getReportExpenses().stream()
                .map(e -> new ExpenseDTO(e.getId(), e.getDate(), e.getValue(), e.getMerchant()))
                .toList();

        return new ReportDTO(report.getReportId(), report.getReportTitle(), report.getReportStatus(), expenseDtos);
    }
}

