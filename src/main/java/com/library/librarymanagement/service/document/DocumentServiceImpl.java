package com.library.librarymanagement.service.document;

import com.library.librarymanagement.dto.request.CreateDocumentRequest;
import com.library.librarymanagement.dto.request.UpdateDocumentRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.DocumentDto;
import com.library.librarymanagement.dto.response.UploadDocumentResponse;
import com.library.librarymanagement.entity.Category;
import com.library.librarymanagement.entity.Document;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.admin.DocumentRepository;
import com.library.librarymanagement.repository.CategoryRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;

    @Value("${file.upload.dir:uploads/documents}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx"};

    @Override
    public UploadDocumentResponse uploadFile(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return UploadDocumentResponse.builder()
                        .success(false)
                        .message("File is empty")
                        .build();
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return UploadDocumentResponse.builder()
                        .success(false)
                        .message("File size exceeds maximum limit of 10MB")
                        .build();
            }

            // Validate file extension
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            if (!isAllowedExtension(fileExtension)) {
                return UploadDocumentResponse.builder()
                        .success(false)
                        .message("File type not allowed. Allowed types: PDF, DOC, DOCX, XLS, XLSX")
                        .build();
            }

            // Create upload directory if it doesn't exist
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            // Save file
            Files.write(filePath, file.getBytes());

            log.info("File uploaded successfully: {}", uniqueFileName);

            return UploadDocumentResponse.builder()
                    .success(true)
                    .message("File uploaded successfully")
                    .filePath(filePath.toString())
                    .fileName(uniqueFileName)
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("Error uploading file", e);
            return UploadDocumentResponse.builder()
                    .success(false)
                    .message("Error uploading file: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse createDocument(CreateDocumentRequest request) {
        try {
            // Verify category exists
            Category category = categoryRepository.findByName(request.getCategoryName())
                    .orElseThrow(() -> new ObjectNotExistException("Category not found: " + request.getCategoryName()));

            // Get current user ID
            Long userId = getCurrentUserId();

            // Create document
            Document document = Document.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .category(category)
                    .filePath(request.getFilePath())
                    .fileName(request.getFileName())
                    .accessLevel(request.getAccessLevel())
                    .importedDate(new Date())
                    .createdBy(userId)
                    .isDeleted(false)
                    .build();

            documentRepository.save(document);

            log.info("Document created successfully with ID: {}", document.getId());

            return ApiResponse.builder()
                    .success(true)
                    .message("Document created successfully")
                    .build();

        } catch (ObjectNotExistException e) {
            log.error("Category not found", e);
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error creating document", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error creating document: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> getAllDocuments(String title, String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = documentRepository.findAllDocuments(title, categoryName, pageable);
        return documents.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDto getDocumentById(Long id) {
        Document document = documentRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ObjectNotExistException("Document not found with ID: " + id));
        return convertToDto(document);
    }

    @Override
    @Transactional
    public ApiResponse updateDocument(Long id, UpdateDocumentRequest request) {
        try {
            Document document = documentRepository.findByIdAndNotDeleted(id)
                    .orElseThrow(() -> new ObjectNotExistException("Document not found with ID: " + id));

            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                document.setTitle(request.getTitle());
            }

            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                document.setDescription(request.getDescription());
            }

            if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
                Category category = categoryRepository.findByName(request.getCategoryName())
                        .orElseThrow(() -> new ObjectNotExistException("Category not found: " + request.getCategoryName()));
                document.setCategory(category);
            }

            if (request.getFileName() != null && !request.getFileName().isBlank()) {
                document.setFileName(request.getFileName());
            }

            if (request.getAccessLevel() != null && !request.getAccessLevel().isBlank()) {
                document.setAccessLevel(request.getAccessLevel());
            }

            documentRepository.save(document);

            log.info("Document updated successfully with ID: {}", id);

            return ApiResponse.builder()
                    .success(true)
                    .message("Document updated successfully")
                    .build();

        } catch (ObjectNotExistException e) {
            log.error("Error updating document", e);
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error updating document", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error updating document: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse deleteDocument(Long id) {
        try {
            Document document = documentRepository.findByIdAndNotDeleted(id)
                    .orElseThrow(() -> new ObjectNotExistException("Document not found with ID: " + id));

            document.setIsDeleted(true);
            documentRepository.save(document);

            log.info("Document deleted successfully with ID: {}", id);

            return ApiResponse.builder()
                    .success(true)
                    .message("Document deleted successfully")
                    .build();

        } catch (ObjectNotExistException e) {
            log.error("Error deleting document", e);
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error deleting document", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error deleting document: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDto> getPublicDocuments(String title, String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = documentRepository.findPublicDocuments(title, categoryName, pageable);
        return documents.map(this::convertToDto);
    }

    private DocumentDto convertToDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .categoryName(document.getCategory() != null ? document.getCategory().getName() : null)
                .filePath(document.getFilePath())
                .fileName(document.getFileName())
                .accessLevel(document.getAccessLevel())
                .importedDate(document.getImportedDate())
                .createdBy(document.getCreatedBy())
                .isDeleted(document.getIsDeleted())
                .build();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the current authenticated user's account ID
     * @return Account ID of the current user, or null if not authenticated
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long accountId = userDetails.getAccountId();
            log.info("Current user account ID: {}", accountId);
            return accountId;
        }
        
        log.warn("Principal is not an instance of CustomUserDetails: {}", principal.getClass().getName());
        return null;
    }
    
    /**
     * Get the current authenticated user's role
     * @return Role name (e.g., "ROLE_ADMIN", "ROLE_LIBRARIAN", "ROLE_USER")
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Check if the current user has admin or librarian role
     * @return true if user is admin or librarian
     */
    private boolean isAdminOrLibrarian() {
        String role = getCurrentUserRole();
        return role != null && (role.equals("ROLE_ADMIN") || role.equals("ROLE_LIBRARIAN"));
    }
}

