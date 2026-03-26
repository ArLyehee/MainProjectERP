package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeePhotoController {

    private final EmployeeService service;

    public EmployeePhotoController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/employees/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String ext = file.getOriginalFilename() != null
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = "emp_" + id + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        file.transferTo(new File(uploadDir + filename));

        String photoPath = "/uploads/employees/" + filename;
        service.updatePhoto(id, photoPath);

        return ResponseEntity.ok(Map.of("photoPath", photoPath));
    }
}
