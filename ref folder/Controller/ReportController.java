package com.revature.ExpenseReport.Controller;

import com.revature.ExpenseReport.Service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    // Fields
    private final ReportService service;

    // Constructor
    public ReportController(ReportService service){
        this.service = service;
    }

    // Methods
    @GetMapping
    public List<ReportDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReportDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDTO create(@RequestBody ReportDTO report) {
        return service.create(report);
    }

    @PutMapping("/{id}")
    public ReportDTO update(@PathVariable String id, @RequestBody ReportDTO report) {
        return service.update(id, report);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
