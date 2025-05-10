package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.LostAndFoundEntity;
import com.g1appdev.Hubbits.service.LostAndFoundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/lostandfound")
@CrossOrigin
public class LostAndFoundController {

    @Autowired
    private LostAndFoundService service;

    @PostMapping
    public ResponseEntity<String> createReport(
            @RequestParam("reporttype") String reporttype,
            @RequestParam("petname") String petname,
            @RequestParam("datereported") String datereported,
            @RequestParam("lastseen") String lastseen,
            @RequestParam("description") String description,
            @RequestParam("creatorid") int creatorid,
            @RequestParam(value = "imagefile", required = false) MultipartFile imageFile) {

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(datereported);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd.");
        }

        LostAndFoundEntity report = new LostAndFoundEntity();
        report.setReporttype(reporttype);
        report.setPetname(petname);
        report.setDatereported(date);
        report.setLastseen(lastseen);
        report.setDescription(description);
        report.setCreatorid(creatorid);

        service.createReport(report, imageFile);
        return ResponseEntity.ok("Report created successfully.");
    }

    @GetMapping
    public ResponseEntity<List<LostAndFoundEntity>> getReports(
            @RequestParam(value = "creatorid", required = false) Integer creatorId) {
        List<LostAndFoundEntity> reports;
        if (creatorId != null) {
            reports = service.getReportsByCreatorId(creatorId);
        } else {
            reports = service.getAllReports();
        }
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReport(
            @PathVariable int id,
            @RequestParam("reporttype") String reporttype,
            @RequestParam("petname") String petname,
            @RequestParam("datereported") String datereported,
            @RequestParam("lastseen") String lastseen,
            @RequestParam("description") String description,
            @RequestParam(value = "imagefile", required = false) MultipartFile imageFile) {

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(datereported);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd.");
        }

        LostAndFoundEntity updatedReport = new LostAndFoundEntity();
        updatedReport.setReporttype(reporttype);
        updatedReport.setPetname(petname);
        updatedReport.setDatereported(date);
        updatedReport.setLastseen(lastseen);
        updatedReport.setDescription(description);

        try {
            service.updateReport(id, updatedReport, imageFile);
            return ResponseEntity.ok("Report updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable int id) {
        try {
            service.deleteReport(id);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}