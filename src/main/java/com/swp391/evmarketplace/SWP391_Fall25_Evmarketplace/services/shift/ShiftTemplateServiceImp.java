package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.CreateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.UpdateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftTemplateResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Branch;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ShiftTemplate;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BranchRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ShiftTemplateRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShiftTemplateServiceImp implements ShiftTemplateService {
    @Autowired
    private ShiftTemplateRepository shiftTemplateRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private ShiftAvailabilityService shiftAvailabilityService;

    private ShiftTemplateResponseDTO toDto(ShiftTemplate e) {
        ShiftTemplateResponseDTO dto = new ShiftTemplateResponseDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setName(e.getName());
        dto.setItemType(e.getItemType());
        if (e.getBranch() != null) {
            dto.setBranchId(e.getBranch().getId());
            dto.setBranchName(e.getBranch().getName());
        }
        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setIsActive(e.getIsActive());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }


    //for manager/ admin
    @Transactional
    @Override
    public BaseResponse<ShiftTemplateResponseDTO> create(CreateShiftTemplateDTO dto) {
        BaseResponse<ShiftTemplateResponseDTO> res = new BaseResponse<>();

        // Validate time window
        String timeErr = validateTimeWindow(dto.getStartTime(), dto.getEndTime());
        if (timeErr != null) {
            res.setStatus(400);
            res.setSuccess(false);
            res.setMessage(timeErr);
            return res;
        }

        // unique code
        Optional<ShiftTemplate> exists = shiftTemplateRepository.findByCode(dto.getCode());
        if (exists.isPresent()) {
            res.setStatus(400);
            res.setSuccess(false);
            res.setMessage("CODE_ALREADY_EXISTS");
            return res;
        }

        ShiftTemplate entity = new ShiftTemplate();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setItemType(dto.getItemType());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE);

        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId())
                    .orElse(null);
            entity.setBranch(branch);
        }

    // check duplicate shift
    Long branchId = entity.getBranch() != null ? entity.getBranch().getId() : null;
    var overlaps = shiftTemplateRepository.findOverlaps(branchId, entity.getItemType(),
        entity.getStartTime(), entity.getEndTime(), null);
    if (!overlaps.isEmpty()) {
        res.setStatus(409);
        res.setSuccess(false);
        res.setMessage("SHIFT_TEMPLATE_OVERLAP");
        return res;
    }

        ShiftTemplate saved = shiftTemplateRepository.save(entity);

        res.setStatus(201);
        res.setSuccess(true);
        res.setMessage("CREATED");
        res.setData(toDto(saved));
        return res;
    }

    @Override
    public BaseResponse<ShiftTemplateResponseDTO> getById(Long id) {
        BaseResponse<ShiftTemplateResponseDTO> res = new BaseResponse<>();
        return shiftTemplateRepository.findById(id)
                .map(e -> {
                    res.setStatus(200);
                    res.setSuccess(true);
                    res.setMessage("OK");
                    res.setData(toDto(e));
                    return res;
                })
                .orElseGet(() -> {
                    res.setStatus(404);
                    res.setSuccess(false);
                    res.setMessage(ErrorCode.BRANCH_NOT_FOUND.name()); // reuse generic not found
                    return res;
                });
    }

    @Override
    public BaseResponse<PageResponse<ShiftTemplateResponseDTO>> list(int page, int size, String sort, String dir) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ShiftTemplate> pages = shiftTemplateRepository.findAll(pageable);
        List<ShiftTemplateResponseDTO> items = pages.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        PageResponse<ShiftTemplateResponseDTO> pr = PageResponse.<ShiftTemplateResponseDTO>builder()
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .hasNext(pages.hasNext())
                .hasPrevious(pages.hasPrevious())
                .page(pages.getNumber())
                .size(pages.getSize())
                .items(items)
                .build();

        BaseResponse<PageResponse<ShiftTemplateResponseDTO>> res = new BaseResponse<>();
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("OK");
        res.setData(pr);
        return res;
    }

    @Transactional
    @Override
    public BaseResponse<ShiftTemplateResponseDTO> update(Long id, UpdateShiftTemplateDTO dto) {
        BaseResponse<ShiftTemplateResponseDTO> res = new BaseResponse<>();
        Optional<ShiftTemplate> op = shiftTemplateRepository.findById(id);
        if (op.isEmpty()) {
            res.setStatus(404);
            res.setSuccess(false);
            res.setMessage("SHIFT_TEMPLATE_NOT_FOUND");
            return res;
        }
        ShiftTemplate e = op.get();
        if (dto.getName() != null) e.setName(dto.getName());
        if (dto.getItemType() != null) e.setItemType(dto.getItemType());
        LocalTime newStart = dto.getStartTime() != null ? dto.getStartTime() : e.getStartTime();
        LocalTime newEnd   = dto.getEndTime()   != null ? dto.getEndTime()   : e.getEndTime();
        String timeErr = validateTimeWindow(newStart, newEnd);
        if (timeErr != null) {
            res.setStatus(400);
            res.setSuccess(false);
            res.setMessage(timeErr);
            return res;
        }
        if (dto.getStartTime() != null) e.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) e.setEndTime(dto.getEndTime());
        
        // không thể set inactive khi đang được sử dụng
        if (dto.getIsActive() != null) {
            boolean targetActive = dto.getIsActive();
            if (!targetActive && isTemplateInUse(e.getId())) {
                res.setStatus(400);
                res.setSuccess(false);
                res.setMessage("SHIFT_TEMPLATE_IN_USE");
                return res;
            }
            e.setIsActive(targetActive);
        }

        if (dto.getBranchId() != null) {
            Branch b = branchRepository.findById(dto.getBranchId()).orElse(null);
            e.setBranch(b);
        }

        // Overlap re-check after applying changes
        Long branchId = e.getBranch() != null ? e.getBranch().getId() : null;
        var overlaps = shiftTemplateRepository.findOverlaps(branchId, e.getItemType(),
                e.getStartTime(), e.getEndTime(), e.getId());
        if (!overlaps.isEmpty()) {
            res.setStatus(409);
            res.setSuccess(false);
            res.setMessage("SHIFT_TEMPLATE_OVERLAP");
            return res;
        }

        ShiftTemplate saved = shiftTemplateRepository.save(e);
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("UPDATED");
        res.setData(toDto(saved));
        return res;
    }

    @Transactional
    @Override
    public BaseResponse<?> delete(Long id) {
        BaseResponse<?> res = new BaseResponse<>();
        Optional<ShiftTemplate> op = shiftTemplateRepository.findById(id);
        if (op.isEmpty()) {
            res.setStatus(404);
            res.setSuccess(false);
            res.setMessage("SHIFT_TEMPLATE_NOT_FOUND");
            return res;
        }
        ShiftTemplate e = op.get();
        // Soft delete = set inactive, but forbid if in use
        if (isTemplateInUse(e.getId())) {
            res.setStatus(400);
            res.setSuccess(false);
            res.setMessage("SHIFT_TEMPLATE_IN_USE");
            return res;
        }
        e.setIsActive(false);
        shiftTemplateRepository.save(e);
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("INACTIVATED");
        return res;
    }

//
    private boolean isTemplateInUse(Long templateId) {
        return shiftAvailabilityService.isTemplateInUse(templateId);
    }

    private String validateTimeWindow(LocalTime start, LocalTime end) {
        if (start == null || end == null) return "INVALID_TIME";
        // No overnight shifts & start < end
        if (!start.isBefore(end)) return "INVALID_TIME_RANGE"; // includes overnight and equal times
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 45) return "SHIFT_DURATION_TOO_SHORT";
        if (minutes > 12 * 60) return "SHIFT_DURATION_TOO_LONG";
        return null;
    }
}
