package com.scity.user.service.impl;

import com.scity.user.exception.BadRequestException;
import com.scity.user.exception.ForbiddenException;
import com.scity.user.exception.NotFoundException;
import com.scity.user.interceptor.SessionHelper;
import com.scity.user.model.constant.ERole;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupDTO;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupRequestDTO;
import com.scity.user.model.entity.FunctionGroup;
import com.scity.user.model.entity.Permission;
import com.scity.user.model.entity.PermissionGroup;
import com.scity.user.repository.FunctionGroupRepository;
import com.scity.user.repository.PermissionGroupRepository;
import com.scity.user.repository.PermissionRepository;
import com.scity.user.repository.UserRepository;
import com.scity.user.service.IAuthService;
import com.scity.user.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionService {
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionGroupRepository permissionGroupRepository;
    @Autowired
    private FunctionGroupRepository functionGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionHelper sessionHelper;
    @Autowired
    private IAuthService authService;

    @Override
    public PageRes<Permission> getListPermission(UUID functionGroupId, String tenantCode, UUID permissionGroupId, String keyword, int page, int size) throws NotFoundException {
        UUID defaultUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Page<Permission> permissions;
        if (permissionGroupId != null) {
            PermissionGroup permissionGroup = permissionGroupRepository.findById(permissionGroupId)
                    .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), permissionGroupId));
            permissions = permissionRepository.getPage(functionGroupId == null ? defaultUUID : functionGroupId, permissionGroup.getPermissionIds(), keyword, PageRequest.of(page, size));
        } else {
            permissions = permissionRepository.getPage(functionGroupId == null ? defaultUUID : functionGroupId, keyword, PageRequest.of(page, size));
        }
        return new PageRes<>(permissions.getContent(), page, size, (int) permissions.getTotalElements());
    }

    @Override
    public PageRes<FunctionGroup> getListFunctionGroup(String keyword, String tenantCode, int page, int size) {
        Page<FunctionGroup> functionGroups = functionGroupRepository.getPage(keyword, PageRequest.of(page, size, Sort.by("sort_number")));
        return new PageRes<>(functionGroups.getContent(), page, size, (int) functionGroups.getTotalElements());
    }

    @Override
    public PageRes<PermissionGroupDTO> getListPermissionGroup(String keyword, String tenantCode, int page, int size) {
        if (tenantCode == null || !sessionHelper.getRoles().contains(ERole.SYSADMIN.name()))
            tenantCode = sessionHelper.getTenantId();
        Page<PermissionGroup> permissionGroups = permissionGroupRepository.getPage(keyword, tenantCode, PageRequest.of(page, size, Sort.by("name")));

        List<PermissionGroupDTO> permissionGroupDTOs = permissionGroups.stream().map(pg -> {
            int userCount = pg.getUsers().size();
            return new PermissionGroupDTO(pg, userCount);
        }).toList();

        return new PageRes<>(permissionGroupDTOs, page, size, (int) permissionGroups.getTotalElements());
    }

    @Override
    public PermissionGroup createNewPermissionGroup(PermissionGroupRequestDTO permissionGroupRequest) {
        String tenantCode = permissionGroupRequest.getTenantCode();
        if (tenantCode == null || !sessionHelper.getRoles().contains(ERole.SYSADMIN.name()))
            tenantCode = sessionHelper.getTenantId();
        PermissionGroup group = permissionGroupRepository.findGroupByName(permissionGroupRequest.getName());
        if (group != null) {
            throw new BadRequestException("Tên cấu hình phân quyền đã tồn tại");
        }
        PermissionGroup permissionGroup = new PermissionGroup(permissionGroupRequest, sessionHelper.getCurrentUserId());
        permissionGroup.setTenantCode(tenantCode);
        permissionGroup.setCode(permissionGroupRepository.maxCodePermissionGroup() + 1);
        return permissionGroupRepository.save(permissionGroup);
    }

    @Override
    public PermissionGroup updatePermissionGroup(UUID id, PermissionGroupRequestDTO permissionGroupRequest) throws NotFoundException {
        PermissionGroup permissionGroup = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), id));
        if (!permissionGroup.getTenantCode().equals(sessionHelper.getTenantId()) && !sessionHelper.getRoles().contains(ERole.SYSADMIN.name()))
            throw new ForbiddenException("No permission");
        permissionGroup.update(permissionGroupRequest, sessionHelper.getCurrentUserId());
        authService.setGatewayFilter(id.toString());
        return permissionGroupRepository.save(permissionGroup);
    }

    @Override
    public void deletePermissionGroup(UUID id) throws NotFoundException {
        PermissionGroup permissionGroup = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), id));
        if (!permissionGroup.getTenantCode().equals(sessionHelper.getTenantId()) && !sessionHelper.getRoles().contains(ERole.SYSADMIN.name()))
            throw new ForbiddenException("No permission");
        long totalUser =  userRepository.countAllByPermissionGroup(id);
        if (totalUser > 0)
            throw new BadRequestException("Có " + totalUser + " nhân viên đang được gán nhóm quyền này. Vui lòng gán nhóm quyền khác cho nhân viên trước khi thực hiện xóa nhóm quyền này");
        permissionGroupRepository.delete(permissionGroup);
    }

    @Override
    public PermissionGroupDTO getPermissionGroup(UUID id) throws NotFoundException {
        PermissionGroup existsPG = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), id));

        int userCount = existsPG.getUsers().size();
        return new PermissionGroupDTO(existsPG, userCount);
    }
}
