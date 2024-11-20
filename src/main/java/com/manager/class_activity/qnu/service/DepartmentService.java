package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.DepartmentRequest;
import com.manager.class_activity.qnu.dto.response.DepartmentResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.SummaryDepartmentResponse;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.DepartmentMapper;
import com.manager.class_activity.qnu.repository.DepartmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);
    DepartmentRepository departmentRepository;
    DepartmentMapper departmentMapper;

    public void saveDepartments(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                if(hadDepartmentName(record.get("name"))){
                    continue;
                }
                Department department = new Department();
                department.setName(record.get("name"));  // Sử dụng tên cột từ header
                department.setUrlLogo(record.get("url_logo")); // Sử dụng tên cột từ header
                departmentRepository.save(department);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }



    public PagedResponse<DepartmentResponse> getDepartments(CustomPageRequest<?> request) {
        Page<Department> departments = departmentRepository.getDepartmentsByPaged(request.toPageable()
                , request.getKeyWord());
        List<DepartmentResponse> departmentResponses = new ArrayList<>();
        for (Department department : departments.getContent()) {
            departmentResponses.add(departmentMapper.toDepartmentResponse(department));
        }
        return new PagedResponse<>(
                departmentResponses,
                departments.getNumber(),
                departments.getTotalElements(),
                departments.getTotalPages(),
                departments.isLast()
        );
    }

    public List<SummaryDepartmentResponse> getSummaryDepartments(){
        List<Department> departments = departmentRepository.getAllByIsDeleted(false);
        List<SummaryDepartmentResponse> summaryDepartmentResponses = new ArrayList<>();
        for (Department department : departments) {
            summaryDepartmentResponses.add(departmentMapper.toSummaryDepartmentResponse(department));
        }
        return summaryDepartmentResponses;
    }


    public DepartmentResponse getDepartmentResponseById(int id) {
        return departmentMapper.toDepartmentResponse(getDepartmentById(id));
    }

    public void updateDepartment(int departmentId, DepartmentRequest request) {
        Department department = getDepartmentById(departmentId);
        departmentMapper.updateDepartment(department, request);
        departmentRepository.save(department);
    }

    public void saveDepartment(DepartmentRequest request) {
        if(hadDepartmentName(request.getName())){
            throw new BadException(ErrorCode.DEPARTMENT_IS_EXISTED);
        }
        Department department = departmentMapper.toDepartment(request);
        departmentRepository.save(department);

    }

    public boolean hadDepartmentName(String name) {
        return !ObjectUtils.isEmpty(departmentRepository.findByNameAndIsDeleted(name, false));
    }

    public void deleteDepartment(int id) {
        Department department = getDepartmentById(id);
        department.setDeleted(true);
        departmentRepository.save(department);
    }

    public Department getDepartmentById(int id) {
        return departmentRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(()-> new BadException(ErrorCode.DEPARTMENT_NOT_FOND));
    }
}

