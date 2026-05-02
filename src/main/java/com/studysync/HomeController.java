package com.studysync;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;

@Controller
public class HomeController {
    private final FileRepository fileRepository;

    public HomeController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(org.springframework.ui.Model model,
                            jakarta.servlet.http.HttpSession session) {

        model.addAttribute("files", fileRepository.findAll());

        String role = (String) session.getAttribute("role");
        model.addAttribute("role", role);   // ✅ ADD THIS LINE

        return "dashboard";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        jakarta.servlet.http.HttpSession session) {

        if (username.equals("4CSE17") && password.equals("4CSE17")) {
            session.setAttribute("role", "ADMIN");   // ✅ admin
        } else {
            session.setAttribute("role", "USER");    // ✅ normal user
        }

        return "redirect:/dashboard";
    }
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        // ✅ check empty file
        if (file.isEmpty()) {
            System.out.println("No file selected");
            return "upload";
        }

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = uploadDir + file.getOriginalFilename();

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(filePath);
            fileEntity.setUploadTime(java.time.LocalDateTime.now().toString());

            fileRepository.save(fileEntity);
            file.transferTo(new File(filePath));

            System.out.println("File uploaded: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/dashboard";
    }
    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id,
                             jakarta.servlet.http.HttpSession session) {

        String role = (String) session.getAttribute("role");

        if ("ADMIN".equals(role)) {
            fileRepository.deleteById(id);
        } else {
            System.out.println("Only admin can delete!");
        }

        return "redirect:/dashboard";
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

        FileEntity file = fileRepository.findById(id).orElse(null);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }
    @GetMapping("/files")
    public String filesPage(org.springframework.ui.Model model) {
        model.addAttribute("files", fileRepository.findAll());
        return "files";
    }
    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }
    @GetMapping("/settings")
    public String settingsPage() {
        return "settings";
    }
}