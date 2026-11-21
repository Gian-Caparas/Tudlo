package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ForbiddenException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.ClassListDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ClassListService extends CrudService<ClassList, ClassListDto.Request, ClassListDto.Response> {

    private final ClassListRepository classListRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final SubjectOfferingService offeringService;
    private final SubjectOfferingRepository offeringRepository;
    private final TeacherRepository teacherRepository;
    private final SectionService sectionService;
    private final AccessPolicy accessPolicy;

    public ClassListService(
            ClassListRepository classListRepository,
            StudentRepository studentRepository,
            UserRepository userRepository,
            UserService userService,
            SubjectOfferingService offeringService,
            SubjectOfferingRepository offeringRepository,
            TeacherRepository teacherRepository,
            SectionService sectionService,
            AccessPolicy accessPolicy) {
        this.classListRepository = classListRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.offeringService = offeringService;
        this.offeringRepository = offeringRepository;
        this.teacherRepository = teacherRepository;
        this.sectionService = sectionService;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected JpaRepository<ClassList, Long> repository() {
        return classListRepository;
    }

    @Override
    protected String entityName() {
        return "ClassList";
    }

    @Override
    protected ClassList newEntity() {
        return new ClassList();
    }

    @Override
    protected List<ClassList> filterForRead(List<ClassList> entities) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return entities.stream()
                    .filter(cl -> cl.getOffering() != null
                            && cl.getOffering().getTeacher() != null
                            && teacher.getId().equals(cl.getOffering().getTeacher().getId()))
                    .toList();
        }
        return entities;
    }

    @Override
    protected void beforeRead(ClassList entity) {
        accessPolicy.requireOfferingReadAccess(entity.getOffering());
    }

    @Override
    protected void beforeUpdate(ClassList existing, ClassListDto.Request request) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void beforeDelete(ClassList existing) {
        accessPolicy.requireOfferingWriteAccess(existing.getOffering());
    }

    @Override
    protected void apply(ClassList entity, ClassListDto.Request request) {
        SubjectOffering offering = offeringService.resolveOffering(
                request.offeringId(), request.subjectName(), request.classSection());
        accessPolicy.requireOfferingWriteAccess(offering);

        userService.ensureStudent(request.studentId(), request.firstName(), request.lastName());
        Student student = studentRepository.findById(request.studentId().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.studentId()));

        classListRepository.findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .filter(existing -> entity.getId() == null || !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new ConflictException("Student " + student.getStudentId()
                            + " is already enrolled in offering " + offering.getId());
                });

        entity.setOffering(offering);
        entity.setStudent(student);
    }

    @Override
    protected ClassListDto.Response toResponse(ClassList entity) {
        SubjectOffering offering = entity.getOffering();
        Student student = entity.getStudent();
        return new ClassListDto.Response(
                entity.getId(),
                offering != null ? offering.getId() : null,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                student != null ? student.getStudentId() : null,
                student != null ? student.getFirstName() : null,
                student != null ? student.getLastName() : null,
                null
        );
    }

    @Transactional
    public List<ClassListDto.Response> bulkImport(ClassListDto.BulkImportRequest request) {
        SubjectOffering offering = resolveOrCreateOfferingForImport(request);
        accessPolicy.requireOfferingWriteAccess(offering);

        List<ClassListDto.Response> results = new ArrayList<>();
        for (ClassListDto.StudentRecord record : request.students()) {
            String studentId = record.studentId().trim();
            userService.ensureStudent(studentId, record.firstName(), record.lastName());
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

            Optional<ClassList> existing = classListRepository
                    .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId());
            if (existing.isPresent()) {
                results.add(toResponse(existing.get()));
                continue;
            }

            ClassList enrollment = new ClassList();
            enrollment.setOffering(offering);
            enrollment.setStudent(student);
            results.add(toResponse(classListRepository.saveAndFlush(enrollment)));
        }
        return results;
    }

    private SubjectOffering resolveOrCreateOfferingForImport(ClassListDto.BulkImportRequest request) {
        if (request.offeringId() != null) {
            return offeringService.getOfferingEntity(request.offeringId());
        }
        String subjName = request.subjectName() != null ? request.subjectName().trim() : "";
        String sectName = request.classSection() != null ? request.classSection().trim() : "";
        if (subjName.isEmpty() || sectName.isEmpty()) {
            throw new BadRequestException("Either offeringId or both subjectName and classSection are required");
        }

        User current = accessPolicy.requireCurrentUser();
        Teacher teacher;
        if (current.getUserType() == UserType.TEACHER) {
            Teacher currentTeacher = accessPolicy.requireCurrentTeacher();
            if (request.teacherId() != null && !currentTeacher.getId().equals(request.teacherId())) {
                throw new ForbiddenException("Teachers may only import class lists for their own offerings");
            }
            teacher = currentTeacher;
        } else if (current.getUserType() == UserType.ADMIN) {
            if (request.teacherId() == null) {
                throw new BadRequestException("teacherId is required when Admin imports without offeringId");
            }
            teacher = teacherRepository.findById(request.teacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.teacherId()));
        } else {
            throw new ForbiddenException("Teacher or Admin role required");
        }

        Subject subject = offeringService.findOrCreateSubject(subjName);
        Section section = sectionService.findOrCreate(sectName);

        return offeringRepository.findByTeacherIdAndSubjectIdAndSectionId(
                        teacher.getId(), subject.getId(), section.getId())
                .orElseGet(() -> {
                    SubjectOffering newOffering = new SubjectOffering();
                    newOffering.setTeacher(teacher);
                    newOffering.setSubject(subject);
                    newOffering.setSection(section);
                    return offeringRepository.saveAndFlush(newOffering);
                });
    }

    @Transactional(readOnly = true)
    public List<ClassListDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return classListRepository.findByOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassListDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> classListRepository.findByOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassListDto.Response> getByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        User current = accessPolicy.requireCurrentUser();
        List<ClassList> list = classListRepository.findByStudentStudentId(studentId);
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            list = list.stream()
                    .filter(cl -> cl.getOffering() != null
                            && cl.getOffering().getTeacher() != null
                            && teacher.getId().equals(cl.getOffering().getTeacher().getId()))
                    .toList();
        }
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ClassListDto.ExcelImportResult importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls"))) {
            throw new BadRequestException("Only .xlsx and .xls files are supported");
        }

        // Extract subject name from filename (before extension)
        String subjectName = filename.substring(0, filename.lastIndexOf('.')).trim();
        if (subjectName.isEmpty()) {
            throw new BadRequestException("Subject name cannot be determined from filename");
        }

        User current = accessPolicy.requireCurrentUser();
        Teacher teacher;
        if (current.getUserType() == UserType.TEACHER) {
            teacher = accessPolicy.requireCurrentTeacher();
        } else if (current.getUserType() == UserType.ADMIN) {
            throw new ForbiddenException("Admin must use bulk-import endpoint with teacherId");
        } else {
            throw new ForbiddenException("Teacher role required");
        }

        List<ClassListDto.OfferingInfo> offeringsInfo = new ArrayList<>();
        int totalImported = 0;
        int totalCreatedAccounts = 0;
        int totalSkipped = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            int sheetCount = workbook.getNumberOfSheets();

            // Check for empty workbook
            boolean hasData = false;
            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() > 1) { // More than just header row
                    hasData = true;
                    break;
                }
            }
            if (!hasData) {
                throw new BadRequestException("Workbook is empty or contains only headers");
            }

            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sectionName = sheet.getSheetName().trim();

                // Skip empty sheets
                if (sheet.getPhysicalNumberOfRows() <= 1) {
                    continue;
                }

                // Find or create offering for this teacher, subject, section
                Subject subject = offeringService.findOrCreateSubject(subjectName);
                Section section = sectionService.findOrCreate(sectionName);

                SubjectOffering offering = offeringRepository.findByTeacherIdAndSubjectIdAndSectionId(
                        teacher.getId(), subject.getId(), section.getId())
                        .orElseGet(() -> {
                            SubjectOffering newOffering = new SubjectOffering();
                            newOffering.setTeacher(teacher);
                            newOffering.setSubject(subject);
                            newOffering.setSection(section);
                            return offeringRepository.saveAndFlush(newOffering);
                        });

                // Parse header row to find column indices
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    continue;
                }

                int idCol = -1, firstNameCol = -1, lastNameCol = -1;
                for (Cell cell : headerRow) {
                    String header = getCellStringValue(cell).toLowerCase().replaceAll("[^a-z0-9]", "");
                    if (header.matches("studentid|stundentid|id|studentno|studentnumber|idnumber|useridnumber")) {
                        idCol = cell.getColumnIndex();
                    } else if (header.matches("firstname|studentfirstname|givenname|fname")) {
                        firstNameCol = cell.getColumnIndex();
                    } else if (header.matches("lastname|studentlastname|surname|familyname|lname")) {
                        lastNameCol = cell.getColumnIndex();
                    }
                }

                if (idCol == -1 || firstNameCol == -1 || lastNameCol == -1) {
                    throw new BadRequestException(
                            "Sheet '" + sectionName + "' must have columns: User ID Number, Firstname, Lastname");
                }

                // Process data rows
                int importedInSheet = 0;
                int createdInSheet = 0;
                int skippedInSheet = 0;

                Iterator<Row> rowIterator = sheet.iterator();
                rowIterator.next(); // skip header

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row == null) continue;

                    Cell idCell = row.getCell(idCol);
                    Cell firstNameCell = row.getCell(firstNameCol);
                    Cell lastNameCell = row.getCell(lastNameCol);

                    if (idCell == null || getCellStringValue(idCell).trim().isEmpty()) {
                        continue; // skip rows without student ID
                    }

                    String studentId = getCellStringValue(idCell).trim();
                    String firstName = firstNameCell != null ? getCellStringValue(firstNameCell).trim() : "";
                    String lastName = lastNameCell != null ? getCellStringValue(lastNameCell).trim() : "";

                    if (firstName.isEmpty() || lastName.isEmpty()) {
                        continue;
                    }

                    // Check if student already enrolled
                    Optional<ClassList> existing = classListRepository
                            .findByOfferingIdAndStudentStudentId(offering.getId(), studentId);
                    if (existing.isPresent()) {
                        skippedInSheet++;
                        continue;
                    }

                    // Ensure student user account exists
                    boolean wasNew = userRepository.findByUserIdNumber(studentId).isEmpty();
                    userService.ensureStudent(studentId, firstName, lastName);
                    if (wasNew) {
                        createdInSheet++;
                    }

                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

                    ClassList enrollment = new ClassList();
                    enrollment.setOffering(offering);
                    enrollment.setStudent(student);
                    classListRepository.saveAndFlush(enrollment);

                    importedInSheet++;
                }

                totalImported += importedInSheet;
                totalCreatedAccounts += createdInSheet;
                totalSkipped += skippedInSheet;

                offeringsInfo.add(new ClassListDto.OfferingInfo(
                        offering.getId(), offering.getSubjectName(), offering.getClassSection()));
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }

        return new ClassListDto.ExcelImportResult(
                totalImported, totalCreatedAccounts, totalSkipped, offeringsInfo);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    CellType cachedType = cell.getCachedFormulaResultType();
                    return getCellStringValueFromType(cachedType, cell);
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }

    private String getCellStringValueFromType(CellType type, Cell originalCell) {
        if (type == CellType.NUMERIC) {
            if (originalCell != null && DateUtil.isCellDateFormatted(originalCell)) {
                return originalCell.getDateCellValue().toString();
            }
            if (originalCell != null) {
                return NumberToTextConverter.toText(originalCell.getNumericCellValue());
            }
            return "";
        } else if (type == CellType.STRING) {
            return originalCell != null ? originalCell.getRichStringCellValue().getString() : "";
        } else if (type == CellType.BOOLEAN) {
            return originalCell != null ? String.valueOf(originalCell.getBooleanCellValue()) : "";
        }
        return "";
    }
}
