package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.request.CreateStaffRequest;
import com.library.librarymanagement.dto.request.UpdateAccountRequest;
import com.library.librarymanagement.dto.response.AccountDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.CreateStaffResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.Role;
import com.library.librarymanagement.entity.SystemUser;
import com.library.librarymanagement.exception.ConstraintViolationException;
import com.library.librarymanagement.exception.ExistAttributeValueException;
import com.library.librarymanagement.exception.ObjectExistedException;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.SystemUserRepository;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.role.RoleRepository;
import com.library.librarymanagement.service.email.EmailService;
import com.library.librarymanagement.util.Mapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemUserRepository systemUserRepository;
    private final ReaderRepository readerRepository;
    private final EmailService emailService;

    @Value("${default.role}")
    private String defaultRole;
    private static final long ROLE_STAFF = 2;
    private static final long ROLE_READER = 3;

    @Override
    @Transactional
    public ApiResponse createAccount(AccountRequest accountRequest) {

        Optional<Role> result = roleRepository.findByName(defaultRole);
        if (!result.isPresent()) {
            throw new ObjectNotExistException("Role Not Found");
        }
        Account account = Mapper.mapDTOToEntity(accountRequest);
        String encodedPassword = passwordEncoder.encode(accountRequest.getPassword());
        account.setPassword(encodedPassword);
        account.setRole(result.get());
        account.setStatus("Active");

        if (accountRepository.findByUsername(accountRequest.getUsername()).isPresent()) {
            throw new ExistAttributeValueException("Username already exists");
        }
        if (accountRepository.findByPhone(accountRequest.getPhone()).isPresent()) {
            throw new ExistAttributeValueException("Phone number already exists");
        }

        try {
            Account savedAccount = accountRepository.save(account);
            boolean success = savedAccount != null && savedAccount.getId() != null;

            return ApiResponse.builder()
                    .success(success)
                    .message("Account created successfully")
                    .build();
        } catch (Exception e) {
            throw new ConstraintViolationException("Violate constraint in database");
        }
    }

    @Override
    public Page<AccountDto> findAll(String fullName, String status, Long roleId, int page, int size) {
        // chuẩn hoá input
        String fn = (fullName == null || fullName.isBlank()) ? null : fullName.trim();
        String st = (status == null || status.isBlank()) ? null : status.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Account> listAccounts = accountRepository.getAllAccounts(fn, st, roleId, pageable);
        return listAccounts.map(a -> AccountDto.builder()
                .id(a.getId())
                .username(a.getUsername())
                .fullName(a.getFullName())
                .email(a.getEmail())
                .phone(a.getPhone())
                .status(a.getStatus())
                .role(a.getRole() != null ? a.getRole().getName() : null)
                .build());
    }

    @Override
    public CreateStaffResponse createStaff(CreateStaffRequest req) {
        // 1) Validate uniqueness
        if (accountRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (accountRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2) Load STAFF role (id = 2)
        Role staffRole = roleRepository.findById(Long.valueOf("2"))
                .orElseThrow(() -> new IllegalStateException("ROLE_STAFF (id=2) not found"));

        // 3) Create Account
        Account acc = new Account();
        acc.setUsername(req.getUsername().trim());
        acc.setEmail(req.getEmail().trim());
        acc.setFullName(req.getFullName().trim());
        acc.setPhone(req.getPhone());
        acc.setStatus((req.getStatus() == null || req.getStatus().isBlank()) ? "ACTIVE" : req.getStatus().trim());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));
        acc.setRole(staffRole);
        accountRepository.save(acc);

        // 4) Create SystemUser (timestamps)
        LocalDateTime now = LocalDateTime.now();
        Timestamp joinTs = Timestamp.valueOf(req.getJoinDate() != null ? req.getJoinDate() : now);
        Timestamp hireTs = Timestamp.valueOf(req.getHireDate() != null ? req.getHireDate() : joinTs.toLocalDateTime());

        SystemUser su = new SystemUser();
        su.setAccount(acc);
        su.setPosition(req.getPosition().trim());
        su.setSalary(req.getSalary());
        su.setJoinDate(joinTs);
        su.setHireDate(hireTs);

        su.setIsDeleted(false);
        su.setCreatedDate(Timestamp.valueOf(now));
        su.setUpdatedDate(null);
        su.setCreatedBy(Long.valueOf("1"));  // default là admin tạo
        su.setUpdatedBy(null);

        systemUserRepository.save(su);

        // 5) Build response
        return CreateStaffResponse.builder()
                .accountId(acc.getId())
                .username(acc.getUsername())
                .fullName(acc.getFullName())
                .email(acc.getEmail())
                .phone(acc.getPhone())
                .status(acc.getStatus())
                .role(staffRole.getName()) // ví dụ ROLE_STAFF

                .systemUserId(su.getId())
                .position(su.getPosition())
                .salary(su.getSalary())
                .joinDate(su.getJoinDate().toLocalDateTime())
                .hireDate(su.getHireDate().toLocalDateTime())
                .createdDate(su.getCreatedDate().toLocalDateTime())
                .build();
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long accountId, UpdateAccountRequest req) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Chỉ cho update Staff/Reader
        Long roleId = acc.getRole() != null ? acc.getRole().getId() : null;
        if (roleId == null || !(roleId == ROLE_STAFF || roleId == ROLE_READER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only STAFF/READER can be updated via this API");
        }

        // username
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            String newUsername = req.getUsername().trim();
            if (accountRepository.existsByUsernameIgnoreCaseAndIdNot(newUsername, accountId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
            }
            acc.setUsername(newUsername);
        }

        // password (mã hoá nếu có)
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            acc.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        // status
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            acc.setStatus(req.getStatus().trim());
        }

        // fullName
        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            acc.setFullName(req.getFullName().trim());
        }

        // email
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String newEmail = req.getEmail().trim();
            if (accountRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, accountId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            acc.setEmail(newEmail);
        }

        // phone
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            acc.setPhone(req.getPhone().trim());
        }

        accountRepository.save(acc);

        return AccountDto.builder()
                .id(acc.getId())
                .username(acc.getUsername())
                .fullName(acc.getFullName())
                .email(acc.getEmail())
                .phone(acc.getPhone())
                .status(acc.getStatus())
                .role(acc.getRole() != null ? acc.getRole().getName() : null)
                .build();
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Role role = acc.getRole();
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found");
        }
        long roleId = role.getId();
        if (roleId != ROLE_STAFF && roleId != ROLE_READER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only STAFF/READER can be deleted");
        }
        //thuc hien doi status o account thanh deleted(softdelete)
        acc.setStatus("DELETED");
        accountRepository.save(acc);
        //thuc hien xoa o 2 bang reader va systemUser
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (roleId == ROLE_STAFF) {
            systemUserRepository.findByAccountId(acc.getId()).ifPresent(staff -> {
                staff.setIsDeleted(true);
                staff.setUpdatedDate(now);
                staff.setUpdatedBy(Long.valueOf("1"));
                systemUserRepository.save(staff);
            });
        } else{
            readerRepository.findByAccountId(acc.getId()).ifPresent(reader -> {
                reader.setIsDeleted(true);
                reader.setUpdatedDate(now);
                reader.setUpdatedBy(Long.valueOf("1"));
                readerRepository.save(reader);
            });
        }
    }

//    @Override
//    @Transactional
//    public void importReaders(MultipartFile file) {
////        try (InputStream is = file.getInputStream();
////             Workbook workbook = new XSSFWorkbook(is)) {
////
////            Sheet sheet = workbook.getSheetAt(0);
////            //start from second row
////            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
////                Row row = sheet.getRow(i);
////                if (row == null) continue;
////
////                String readerCode = row.getCell(0).getStringCellValue();
////                String fullName = row.getCell(1).getStringCellValue();
////                String email = row.getCell(2).getStringCellValue();
////                String clazz = row.getCell(3).getStringCellValue();
////                String department = row.getCell(4).getStringCellValue();
////                String score = row.getCell(5).getStringCellValue();
////                System.out.println(readerCode + " " + fullName + " " + email + " " + clazz + " " + department+ " " + score);
////            }
////
////        } catch (IOException e) {
////            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
////        }
//
//        try (InputStream is = file.getInputStream();
//             Workbook workbook = new XSSFWorkbook(is)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Iterator<Row> iterator = sheet.iterator();
//            while (iterator.hasNext()) {
//                Row nextRow = iterator.next();
//                if (nextRow.getRowNum() == 0) {
//                    continue;
//                }
//                if (isRowEmpty(nextRow)) {
//                    continue;
//                }
//                Iterator<Cell> cellIterator = nextRow.cellIterator();
//                Account account = new Account();
//                Reader reader = new Reader();
//                String fullName = null;
//                while (cellIterator.hasNext()) {
//                    Cell Cell = cellIterator.next();
//                    Object cellValue = getCellValue(Cell);
//                    if (cellValue == null || cellValue.toString().isEmpty()) {
//                        continue; // TODO: handle error
//                    }
//
//                    //todo: chưa status va ma hoa password
//                    int columnIndex = Cell.getColumnIndex();
//                    switch (columnIndex) {
//                        case 0: // reader_code
//                            reader.setReaderCode((String) cellValue);
//                            System.out.println("Reader code "+ reader.getReaderCode());
//                            break;
//                        case 1: // first_name
//                            fullName = (String) cellValue;
//                            break;
//                        case 2: //last_name
//                            fullName = (fullName == null ? "" : fullName + " ").concat((String) cellValue) ;
//                            break;
//                        case 3: // phone //TODO: check type
//                            account.setPhone((String) cellValue);
//                            System.out.println("account phone "+ account.getPhone());
//                            break;
//                        case 4: //email
//                            account.setEmail((String) cellValue);
//                            System.out.println("account email "+ account.getEmail());
//                            break;
//                        case 5: //username
//                            account.setUsername((String) cellValue);
//                            System.out.println("account username "+ account.getUsername());
//                            break;
//                        case 6: //password
//                            account.setPassword((String) cellValue);
//                            System.out.println("account pass "+ account.getPassword());
//                            break;
//                        default:
//                            break;
//                    }
//                    System.out.print("Cell each row: " + cellValue);
//                }
//
//                account.setFullName(fullName);
//                Role userRole = roleRepository.findByName("ROLE_READER")
//                        .orElseThrow(() -> new ObjectNotExistException("Role not found"));
//                account.setRole(userRole);
//                if (accountRepository.existsByUsernameIgnoreCase(account.getUsername())) {
//                    throw new ObjectExistedException(account.getUsername());
//                }
//                if (accountRepository.existsByEmailIgnoreCase(account.getEmail())) {
//                    throw new ObjectExistedException(account.getEmail());
//                }
//
//                try {
//                    accountRepository.save(account);
//                    System.out.println("Account save: " +  account);
//                    Account result = accountRepository.findByUsername(account.getUsername())
//                            .orElseThrow(() -> new ObjectNotExistException("Account just save not found"));
//                    reader.setAccount(account);
//                    System.out.println("Reader save: " +  reader);
//                    readerRepository.save(reader);
//                    emailService.sendAccountInfor(account.getEmail(), account.getUsername(), account.getPassword());
//                } catch (Exception e) {
//                    throw new RuntimeException("Import failed" + e.getMessage(), e);
//                }
//            }
////            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
////                Row row = sheet.getRow(i);
////                if (row == null) continue;
////
////                String readerCode = row.getCell(0).getStringCellValue();
////                String fullName = row.getCell(1).getStringCellValue();
////                String email = row.getCell(2).getStringCellValue();
////                String clazz = row.getCell(3).getStringCellValue();
////                String department = row.getCell(4).getStringCellValue();
////                String score = row.getCell(5).getStringCellValue();
////                System.out.println(readerCode + " " + fullName + " " + email + " " + clazz + " " + department+ " " + score);
////            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
//        }
//    }

//    private boolean isRowEmpty(Row row) {
//        if (row == null) return true;
//
//        short firstCellNum = row.getFirstCellNum();
//        short lastCellNum = row.getLastCellNum();
//        if (firstCellNum < 0 || lastCellNum < 0) return true;
//
//        for (int c = firstCellNum; c < lastCellNum; c++) {
//            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//            if (cell != null && !cell.toString().trim().isEmpty()) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    @Transactional
    public void importReaders(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // ✅ Duyệt theo chỉ số thay vì iterator
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row nextRow = sheet.getRow(i);
                if (nextRow == null || isRowEmpty(nextRow)) {
                    System.out.println("⛔ Gặp dòng trống tại hàng " + (i + 1) + " => dừng import.");
                    break; // 👉 Dừng import hẳn khi gặp dòng trống đầu tiên
                }

                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Account account = new Account();
                Reader reader = new Reader();
                String fullName = null;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    Object cellValue = getCellValue(cell);
                    if (cellValue == null || cellValue.toString().trim().isEmpty()) continue;

                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex) {
                        case 0: // reader_code
                            reader.setReaderCode(cellValue.toString().trim());
                            System.out.println("Reader code: " + reader.getReaderCode());
                            break;
                        case 1: // first_name
                            fullName = cellValue.toString().trim();
                            break;
                        case 2: // last_name
                            fullName = (fullName == null ? "" : fullName + " ") + cellValue.toString().trim();
                            break;
                        case 3: // phone
                            account.setPhone(cellValue.toString().trim());
                            break;
                        case 4: // email
                            account.setEmail(cellValue.toString().trim());
                            break;
                        case 5: // username
                            account.setUsername(cellValue.toString().trim());
                            break;
                        case 6: // password
                            account.setPassword(cellValue.toString().trim());
                            break;
                        default:
                            break;
                    }
                }

                account.setFullName(fullName);
                Role userRole = roleRepository.findByName("ROLE_READER")
                        .orElseThrow(() -> new ObjectNotExistException("Role not found"));
                account.setRole(userRole);
                account.setStatus("ACTIVE");
                String rawPassword = account.getPassword();
                account.setPassword(passwordEncoder.encode(account.getPassword()));

                boolean skip = false;
                // ✅ Check tồn tại username / email
                if (accountRepository.existsByUsernameIgnoreCase(account.getUsername())) {
//                    throw new ObjectExistedException(account.getUsername());
                    skip = true;
                }
                if (accountRepository.existsByEmailIgnoreCase(account.getEmail())) {
//                    throw new ObjectExistedException(account.getEmail());
                    skip = true;
                }

                if (readerRepository.existsByReaderCode(reader.getReaderCode())) {
                    System.out.println("⚠️ Bỏ qua reader (trùng reader_code): " + reader.getReaderCode());
                    skip = true;
                }

                if (skip) continue;

                try {
                    accountRepository.save(account);
                    reader.setAccount(account);
                    readerRepository.save(reader);
                    emailService.sendAccountInfor(account.getEmail(), account.getUsername(), rawPassword);
                    System.out.println("✅ Import thành công: " + account.getUsername());
                } catch (Exception e) {
                    throw new RuntimeException("Import failed: " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();
        if (firstCellNum < 0 || lastCellNum < 0) return true;

        for (int c = firstCellNum; c < lastCellNum; c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false; // có dữ liệu thật
            }
        }
        return true; // toàn bộ ô trống hoặc null
    }

    public static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:  // it only handles for phone so want to handle for integer, bonus
                double num = cell.getNumericCellValue();
                long longValue = (long) num;
                if (longValue == num) {
                    cellValue = String.valueOf(longValue);
                } else {
                    cellValue = String.valueOf(num);
                }
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case BLANK:
                break;
            case _NONE:
                break;
            case ERROR:
                break;
            default:
                break;
        }
        return cellValue;
    }
}
