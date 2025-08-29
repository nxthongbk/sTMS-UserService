package com.scity.user.service;

import com.scity.user.exception.NotFoundException;
import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.auth.ForgetPasswordDTO;
import com.scity.user.model.dto.auth.OtpDTO;
import com.scity.user.model.dto.auth.ResetPasswordDTO;
import com.scity.user.model.dto.tenant.*;
import com.scity.user.model.dto.user.*;
import com.scity.user.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserDetailDTO createNewUser(UserCreateDTO userCreate);

    TenantDetailDTO createNewTenant(TenantRequestDTO tenantCreate);

    PageRes<UserDetailDTO> getAllUser(String keyword, String tenantCode, int page, int size, String sortOrder);

    PageRes<TenantDetailDTO> getAllTenant(String keyword, String status, int page, int size);

    PageRes<String> getAllNameTenant(String keyword, int page, int size, String sortOrder);

    TenantDetailDTO updateTenant(UUID id, TenantRequestDTO tenantUpdate) throws NotFoundException;

    TenantDetailDTO getTenant(UUID id) throws NotFoundException;

    TenantDetailDTO updateStatus(UUID id, EUserStatus status) throws NotFoundException;

    TenantDetailDTO updatePasswordTenant(UUID id, String newPassword) throws NotFoundException;

    UserDetailDTO updatePassword(UUID id, String newPassword) throws NotFoundException;

    UserProfileDTO getMyProfile() throws NotFoundException;

    UserBasicDTO getUserInfo(UUID id) throws NotFoundException;

    UserProfileDTO updateMyProfile(UserProfileRequestDTO userProfileRequest) throws NotFoundException;

    void updatePassword(String oldPassword, String newPassword) throws NotFoundException;

    void sendOtp(ForgetPasswordDTO otpRequest) throws NotFoundException;

    String compareOtp(OtpDTO otp) throws NotFoundException;

    UserDetailDTO resetPassword(ResetPasswordDTO newPassword) throws NotFoundException;

    boolean existsByCode(String code);

    TenantBasicDTO getTenantDetailByCode(String code);

    UserDetailDTO createNewStaff(StaffCreateDTO staffCreate) throws NotFoundException;

    UserDetailDTO updateStaff(UUID id, StaffUpdateDTO staffUpdate) throws NotFoundException;

    UserDetailDTO getUserDetail(User user);
    UserElementDTO getStaffDetail(UUID id, String tenantCode) throws NotFoundException;

    UserDetailDTO updateUserStatus(UUID id, EUserStatus status) throws NotFoundException;

    PageRes<UserElementDTO> getStaffPage(String keyword, String tenantCode, UUID locationId, UUID permissionGroupId, String status, int page, int size);

//    void updateLocationName(UUID id, String name);

    void deleteStaff(UUID id) throws NotFoundException;
    List<TenantStatusStatisticsProjection> getStatusStatistics();
    PageRes<TenantStatusDTO> getAllTenantStatus(int size, int page);
    List<String> getAllTenantCode();

    List<StatisticUserDTO> getUserStatistic(String tenantCode);

    void migrateTenant();

    void migrateUser();

    List<UserElementDTO> getUsersWithPermissionSMS(String tenantCode, UUID locationId);
}