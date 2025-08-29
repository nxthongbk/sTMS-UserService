package com.scity.user.service.impl;

import com.scity.user.exception.BadRequestException;
import com.scity.user.exception.ForbiddenException;
import com.scity.user.exception.NotFoundException;
import com.scity.user.interceptor.SessionHelper;
import com.scity.user.model.constant.EDefaultValue;
import com.scity.user.model.constant.ERole;
import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.IData;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.auth.ForgetPasswordDTO;
import com.scity.user.model.dto.auth.OtpDTO;
import com.scity.user.model.dto.auth.ResetPasswordDTO;
import com.scity.user.model.dto.sms.PayloadSMSDTO;
import com.scity.user.model.dto.tenant.*;
import com.scity.user.model.dto.location.LocationDTO;
import com.scity.user.model.dto.user.*;
import com.scity.user.model.entity.Tenant;
import com.scity.user.model.entity.Permission;
import com.scity.user.model.entity.PermissionGroup;
import com.scity.user.model.entity.User;
import com.scity.user.repository.TenantRepository;
import com.scity.user.repository.PermissionGroupRepository;
import com.scity.user.repository.PermissionRepository;
import com.scity.user.repository.UserRepository;
import com.scity.user.service.IAuthService;
import com.scity.user.service.IUserService;
import com.scity.user.utils.ClientUtils;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.gson.Gson;
import com.scity.user.utils.SMSJwtUtils;
import com.scity.user.utils.PhoneNumberUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private static final String OTP_CHARS = "0123456789";
    private static final int OTP_LENGTH = 6;
    @Autowired
    private SessionHelper sessionHelper;
    @Autowired
    private TenantRepository TenantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionGroupRepository permissionGroupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IAuthService authService;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private ClientUtils clientUtils;
    @Value("${bloom-filter-permission}")
    private int bloomFilterTotal;
    @Autowired
    private Gson gson;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Transactional
    public UserDetailDTO createNewUser(UserCreateDTO userCreate) {
        if (userRepository.existsByUsername(userCreate.getUsername()))
            throw new BadRequestException("Tên đăng nhập đã tồn tại", 40010);
        User user = modelMapper.map(userCreate, User.class);
        user.setNew(sessionHelper.getCurrentUserId());
        user.setPassword(passwordEncoder.encode(user.getUsername() + user.getPhone()));
        user.setCode(userRepository.getMaxCodeUser() + 1);
        userRepository.save(user);
        return getUserDetail(user);
    }

    @Override
    @Transactional
    public TenantDetailDTO createNewTenant(TenantRequestDTO tenantCreate) {
        if (userRepository.existsByUsername(tenantCreate.getUsername()))
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        if (TenantRepository.existsByName(tenantCreate.getName()))
            throw new BadRequestException("Tên khách hàng đã tồn tại");
        if (TenantRepository.existsByEmail(tenantCreate.getEmail()))
            throw new BadRequestException("Email đã tồn tại");
        if (TenantRepository.existsByPhone(tenantCreate.getPhone()))
            throw new BadRequestException("Số điện thoại đã tồn tại");
        Tenant tenant = modelMapper.map(tenantCreate, Tenant.class);
        tenant.setNew(sessionHelper.getCurrentUserId());
        User user = new User(
                tenant.getUsername(),
                tenant.getPhone(),
                passwordEncoder.encode(tenant.getPhone()),
                tenant.getEmail(),
                tenant.getCode(),
                List.of(ERole.TENANT.name()),
                sessionHelper.getCurrentUserId()
        );
        tenant.setTenantId(TenantRepository.getMaxTenantId() + 1);
        TenantRepository.save(tenant);
        String tenantCode = tenant.getId().toString().substring(0, 8).toLowerCase();
        if (TenantRepository.countByCode(tenantCode) > 1)
            throw new BadRequestException("Có lỗi hệ thống vui lòng thử lại sau");
        tenant.setCode(tenantCode);
        user.setTenantCode(tenantCode);
        user.setName(tenant.getName());
        user.setCode(userRepository.getMaxCodeUser() + 1);
        userRepository.save(user);

//        SmsDTO sms = new SmsDTO("Tài khoản sCity của quý khách đã được khởi tạo với thông tin như sau: Tên đăng nhập: " + tenant.getUsername() + ", Mật khẩu: " + tenant.getUsername() + tenant.getPhone() + ".", user.getPhone());
//        clientUtils.sendSMS(sms);
        return modelMapper.map(tenant, TenantDetailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<UserDetailDTO> getAllUser(String keyword, String tenantCode, int page, int size, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "created_at");
        if (tenantCode == null || !sessionHelper.getRoles().contains(ERole.SYSADMIN.name()))
            tenantCode = sessionHelper.getTenantId();
        Page<User> users = userRepository.getPage(keyword, tenantCode, PageRequest.of(page, size, sort));
        List<TenantDetailDTO> TenantDetailDTOS = getAllTenant(users.stream().map(User::getTenantCode).toList());
        List<UserDetailDTO> userDetailDTOs = modelMapper.map(users.getContent(), new TypeToken<List<UserDetailDTO>>() {
        }.getType());
        for (UserDetailDTO userDetailDTO : userDetailDTOs) {
            for (TenantDetailDTO tenant : TenantDetailDTOS) {
                if (userDetailDTO.getTenantCode().equals(tenant.getCode())) {
                    userDetailDTO.setTenant(tenant);
                    break;
                }
            }
        }
        return new PageRes<>(userDetailDTOs, page, size, (int) users.getTotalElements());
    }

    @Override
    @Transactional
    public PageRes<TenantDetailDTO> getAllTenant(String keyword, String status, int page, int size) {
        Sort sort = Sort.by("tenant_id");
        Page<Tenant> tenants = TenantRepository.findAll(keyword, status, PageRequest.of(page, size, sort));
        return new PageRes<>(modelMapper.map(tenants.getContent(), new TypeToken<List<TenantDetailDTO>>() {
        }.getType()),
                page,
                size,
                (int) tenants.getTotalElements());
    }


    @Transactional
    public List<TenantDetailDTO> getAllTenant(List<String> codes) {
        List<Tenant> tenants = TenantRepository.findAllByCode(codes);
        return modelMapper.map(tenants, new TypeToken<List<TenantDetailDTO>>() {
        }.getType());
    }

    @Override
    public boolean existsByCode(String code) {
        return TenantRepository.existsByCode(code);
    }

    @Override
    public TenantBasicDTO getTenantDetailByCode(String code) {
        Tenant tenant = TenantRepository.findTenantByCode(code);
        return modelMapper.map(tenant, TenantBasicDTO.class);
    }

    @Override
    @Transactional
    public UserDetailDTO createNewStaff(StaffCreateDTO staffCreate) throws NotFoundException {
        staffCreate.setPhone(staffCreate.getPhone().replace("+84", "0"));
        if (userRepository.existsByPhone(staffCreate.getPhone()))
            throw new BadRequestException("Số điện thoại đã tồn tại");
        if (userRepository.existsByUsername(staffCreate.getUsername()))
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        if (userRepository.existsByName(staffCreate.getName()))
            throw new BadRequestException("Tên khách hàng đã tồn tại");
        User user = new User(staffCreate);
        PermissionGroup permissionGroup = permissionGroupRepository.findById(staffCreate.getPermissionGroupId())
                .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), staffCreate.getPermissionGroupId()));
        user.setPermissionGroup(permissionGroup);
        user.setNew(sessionHelper.getCurrentUserId());
        user.setPassword(passwordEncoder.encode(staffCreate.getPhone()));
        String tenantCode;
        if (staffCreate.getTenantCode() != null && TenantRepository.existsByCode(staffCreate.getTenantCode())) {
            tenantCode = staffCreate.getTenantCode();
        } else
            throw new BadRequestException("Mã công ty không hợp lệ");
        user.setTenantCode(tenantCode);
        user.setRoles(List.of(ERole.STAFF.name()));
        user.setAssignAllLocations(staffCreate.isAssignAllLocations());
        user.setLocationIds(staffCreate.getLocationIds());
        user.setCode(userRepository.getMaxCodeUser() + 1);
        userRepository.save(user);
        return getUserDetail(user);
    }

    @Override
    public UserDetailDTO updateStaff(UUID id, StaffUpdateDTO staffUpdate) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        if (user.getRoles().contains(ERole.SYSADMIN.name())
            || user.getRoles().contains(ERole.TENANT.name())
            || (!user.getTenantCode().equals(sessionHelper.getTenantId())
                && !sessionHelper.getRoles().contains(ERole.SYSADMIN.name())))
            throw new ForbiddenException("No permission");

        staffUpdate.setPhone(staffUpdate.getPhone().replace("+84", "0"));
        if (!user.getPhone().equals(staffUpdate.getPhone()) && userRepository.existsByPhone(staffUpdate.getPhone()))
            throw new BadRequestException("Số điện thoại đã tồn tại");
        if (!user.getName().equals(staffUpdate.getName()) && userRepository.existsByUsername(staffUpdate.getName()))
            throw new BadRequestException("Tên nhân sự đã tồn tại");

        PermissionGroup permissionGroup = permissionGroupRepository.findById(staffUpdate.getPermissionGroupId())
                .orElseThrow(() -> new NotFoundException(PermissionGroup.class.getSimpleName(), staffUpdate.getPermissionGroupId()));
        user.setPermissionGroup(permissionGroup);
        user.update(user.getUsername(), staffUpdate.getPhone(), user.getEmail(), user.getRoles(), sessionHelper.getCurrentUserId());
        user.setAvatarUrl(staffUpdate.getAvatarUrl());
        user.setLocationIds(staffUpdate.getLocationIds());
        user.setName(staffUpdate.getName());
        user.setAssignAllLocations(staffUpdate.isAssignAllLocations());
        authService.setGatewayFilter(user.getId().toString());
        userRepository.save(user);
        return getUserDetail(user);
    }

    @Override
    @Transactional
    public PageRes<String> getAllNameTenant(String keyword, int page, int size, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "created_at");
        Page<String> tenantsName = TenantRepository.findAllName(keyword, PageRequest.of(page, size, sort));
        return new PageRes<>(tenantsName.getContent(),
                page,
                size,
                (int) tenantsName.getTotalElements());
    }

    @Override
    @Transactional
    public TenantDetailDTO updateTenant(UUID id, TenantRequestDTO tenantUpdate) throws NotFoundException {
        Tenant tenant = TenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), id));
        if (!tenant.getUsername().equals(tenantUpdate.getUsername()) && userRepository.existsByUsername(tenantUpdate.getUsername()))
            throw new BadRequestException("Tên đăng nhập đã tồn tại", 40010);
        if (!tenant.getName().equals(tenantUpdate.getName()) && TenantRepository.existsByName(tenantUpdate.getName()))
            throw new BadRequestException("Tên khách hàng đã tồn tại", 40011);
        if (!tenant.getEmail().equals(tenantUpdate.getEmail()) && TenantRepository.existsByEmail(tenantUpdate.getEmail()))
            throw new BadRequestException("Email đã tồn tại", 40012);
        if (!tenant.getPhone().equals(tenantUpdate.getPhone()) && TenantRepository.existsByPhone(tenantUpdate.getPhone()))
            throw new BadRequestException("Số điện thoại đã tồn tại", 40013);
        boolean updateName = !tenantUpdate.getName().equals(tenant.getName());
        User user = userRepository.findByUsername(tenant.getUsername())
                .orElse(new User(
                        tenant.getUsername(),
                        tenant.getPhone(),
                        tenant.getEmail(),
                        tenant.getCode(),
                        passwordEncoder.encode(tenant.getPhone()),
                        List.of(ERole.TENANT.name()),
                        sessionHelper.getCurrentUserId()
                ));
        tenant.update(tenantUpdate, sessionHelper.getCurrentUserId());
        user.update(tenant.getUsername(), tenant.getPhone(), tenant.getEmail(), user.getRoles(), sessionHelper.getCurrentUserId());
        user.setName(tenant.getName());
        TenantRepository.save(tenant);
        userRepository.save(user);

        if (updateName)
            kafkaTemplate.send("update_device_from_tenant_topic", gson.toJson(tenant));

        return modelMapper.map(tenant, TenantDetailDTO.class);
    }

    @Override
    public TenantDetailDTO getTenant(UUID id) throws NotFoundException {
        Tenant tenant = TenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), id));
        return modelMapper.map(tenant, TenantDetailDTO.class);
    }

    @Override
    @Transactional
    public TenantDetailDTO updateStatus(UUID id, EUserStatus status) throws NotFoundException {
        Tenant tenant = TenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), id));
        User user = userRepository.findByUsername(tenant.getUsername())
                .orElse(new User(
                        tenant.getUsername(),
                        tenant.getPhone(),
                        tenant.getPhone(),
                        tenant.getCode(),
                        passwordEncoder.encode(tenant.getPhone()),
                        List.of(ERole.TENANT.name()),
                        sessionHelper.getCurrentUserId()
                ));
        if (tenant.getStatus().equals(EUserStatus.ACTIVE.name()) && status.equals(EUserStatus.BLOCKED)) {
            authService.setGatewayFilter(tenant.getCode());
        }
        tenant.setStatus(status.name());
        user.setStatus(status.name());
        TenantRepository.save(tenant);
        userRepository.save(user);
        return modelMapper.map(tenant, TenantDetailDTO.class);
    }

    @Override
    @Transactional
    public TenantDetailDTO updatePasswordTenant(UUID id, String newPassword) throws NotFoundException {
        if (newPassword == null || newPassword.length() < 8)
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự");
        Tenant tenant = TenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), id));
        User user = userRepository.findByUsername(tenant.getUsername())
                .orElse(new User(
                                tenant.getUsername(),
                                tenant.getPhone(),
                                tenant.getPhone(),
                                tenant.getCode(),
                                passwordEncoder.encode(tenant.getPhone()),
                                List.of(ERole.TENANT.name()),
                                sessionHelper.getCurrentUserId()
                        )
                );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return modelMapper.map(tenant, TenantDetailDTO.class);
    }

    @Override
    @Transactional
    public UserDetailDTO updatePassword(UUID id, String newPassword) throws NotFoundException {
        if (newPassword == null || newPassword.length() < 8)
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        if (!sessionHelper.getRoles().contains(ERole.SYSADMIN.name())
            && (!sessionHelper.getTenantId().equals(user.getTenantCode())
                || user.getRoles().contains(ERole.TENANT.name()))
        )
            throw new ForbiddenException("No permission");
        if (user.getRoles().contains(ERole.SYSADMIN.name()))
            throw new ForbiddenException("No permission");
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return getUserDetail(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getMyProfile() throws NotFoundException {
        User user = userRepository.findById(sessionHelper.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), sessionHelper.getCurrentUserId()));
        Tenant tenant = user.getTenantCode().equals(EDefaultValue.TENANT_CODE.getValue()) ? null :
                TenantRepository.findByCode(user.getTenantCode())
                        .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), "code", user.getTenantCode()));

        UserProfileDTO userProfile = modelMapper.map(user, UserProfileDTO.class);
        if (tenant != null)
            userProfile.setTenant(modelMapper.map(tenant, TenantBasicDTO.class));
        else
            userProfile.setTenant(null);
        return userProfile;
    }

    @Override
    public UserBasicDTO getUserInfo(UUID id) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        return new UserBasicDTO(user.getId(), user.getName(), user.getPhone(), user.getUsername(), user.getAvatarUrl());
    }

    @Override
    @Transactional
    public UserProfileDTO updateMyProfile(UserProfileRequestDTO userProfileRequest) throws NotFoundException {
        User user = userRepository.findById(sessionHelper.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), sessionHelper.getCurrentUserId()));
        user.setAvatarUrl(userProfileRequest.getAvatarUrl());
        user.setUpdatedAt(new Date());
        user.setUpdatedBy(sessionHelper.getCurrentUserId());
        user.setPhone(userProfileRequest.getPhone());
        if (user.getRoles().contains(ERole.TENANT.name())) {
            user.setEmail(userProfileRequest.getEmail());
        }
        userRepository.save(user);
        UserProfileDTO userProfile = modelMapper.map(user, UserProfileDTO.class);
        Tenant tenant = user.getTenantCode().equals(EDefaultValue.TENANT_CODE.getValue()) ? null :
                TenantRepository.findByCode(user.getTenantCode())
                        .orElseThrow(() -> new NotFoundException(Tenant.class.getSimpleName(), "code", user.getTenantCode()));
        if (tenant != null) {
            if (user.getRoles().contains(ERole.TENANT.name())) {
                tenant.setEmail(userProfileRequest.getEmail());
                tenant.setPhone(userProfileRequest.getPhone());
                TenantRepository.save(tenant);
            }
            userProfile.setTenant(modelMapper.map(tenant, TenantBasicDTO.class));
        } else
            userProfile.setTenant(null);
        return userProfile;
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) throws NotFoundException {
        if (newPassword == null || newPassword.length() < 8)
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự");
        User user = userRepository.findById(sessionHelper.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), sessionHelper.getCurrentUserId()));
        if (!authService.comparePasswords(oldPassword, user.getPassword()))
            throw new BadRequestException("Sai mật khẩu!", 40010);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void sendOtp(ForgetPasswordDTO otpRequest) throws NotFoundException {
        User user = userRepository.findByUsername(otpRequest.getUsername())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), "username", otpRequest.getUsername()));
        if (user.getPhone() == null)
            throw new BadRequestException("Tài khoản này chưa đăng ký số điện thoại", 40014);
        if (!user.getPhone().equals(otpRequest.getPhone()))
            throw new BadRequestException("Số điện thoại không chính xác", 40015);
        if (user.getUpdatedAt().after(new Date(new Date().getTime() - 60000))) {
            throw new BadRequestException("Vui lòng chờ sau " + (new Date(user.getUpdatedAt().getTime() + 60000).getTime() - new Date().getTime()) / 1000 + " giây nữa cho yêu cầu gửi lại mã OTP");
        }
        user.setOneTimeOtp(generateOtp());
        user.setOptCount(0);
        user.setOtpExpiry(System.currentTimeMillis() + 120000);
        user.setUpdatedAt(new Date());

        String accessToken = SMSJwtUtils.createJwt();
        PayloadSMSDTO payloadSMS = new PayloadSMSDTO();
        List<PayloadSMSDTO.SMS> toSMSList = new ArrayList<>();

        PayloadSMSDTO.SMS sms = new PayloadSMSDTO.SMS();
        sms.setFrom("VFT");
        sms.setTo(PhoneNumberUtils.convertToInternationalFormat(user.getPhone()));
        sms.setText("Mã OTP được gửi từ hệ thống sCity của quý khách là " + user.getOneTimeOtp() + ", Vui lòng không chia sẻ mã này.");

        toSMSList.add(sms);
        payloadSMS.setSms(toSMSList);

        clientUtils.sendSMS(accessToken, payloadSMS);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public String compareOtp(OtpDTO otpRespond) throws NotFoundException {
        User user = userRepository.findByUsername(otpRespond.getUsername())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), "username", otpRespond.getUsername()));
        if (user.getOneTimeOtp() == null)
            throw new BadRequestException("Vui lòng thử lại sau");
        if (!user.getOneTimeOtp().equals(otpRespond.getOneTimeOtp())) {
            user.setOptCount(user.getOptCount() + 1);
            user.setOptCount(user.getOptCount() + 1);
            if (user.getOptCount() >= 3) {
                user.setOptCount(0);
                user.setOneTimeOtp(null);
            }
            userRepository.save(user);
            throw new BadRequestException("Mã OTP không hợp lệ", 40016);
        }
        if (user.getOtpExpiry() < System.currentTimeMillis())
            throw new BadRequestException("Mã OTP đã hết hạn", 40017);
        String newToken = generateToken();
        user.setOneTimeOtp(null);
        user.setAccessToken(newToken);
        userRepository.save(user);
        return newToken;
    }

    @Override
    @Transactional
    public UserDetailDTO resetPassword(ResetPasswordDTO newPassword) throws NotFoundException {
        if (newPassword.getNewPassword() == null || newPassword.getNewPassword().length() < 8)
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự");
        User user = userRepository.findByUsername(newPassword.getUsername())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), "username", newPassword.getUsername()));
        if (user.getAccessToken() == null || !newPassword.getToken().equals(user.getAccessToken()))
            throw new BadRequestException("Token không hợp lệ", 40018);

        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        user.setAccessToken(null);
        userRepository.save(user);
        return getUserDetail(user);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public UserDetailDTO getUserDetail(User user) {
        UserDetailDTO userDetail = modelMapper.map(user, UserDetailDTO.class);
        List<Permission> permissions;
        if (user.getPermissionGroup() != null && user.getPermissionGroup().getPermissionIds() != null &&
            !user.getPermissionGroup().getPermissionIds().isEmpty()) {
            permissions = permissionRepository.findAllById(user.getPermissionGroup().getPermissionIds());
        } else {
            permissions = new ArrayList<>();
        }
        userDetail.setPermissionBloomFilter(getPermission(permissions));
        userDetail.setPermissions(permissions.stream().map(Permission::getCode).toList());
        Tenant tenant = TenantRepository.findByCode(user.getTenantCode())
                .orElse(null);
        if (tenant != null) {
            userDetail.setTenant(modelMapper.map(tenant, TenantDetailDTO.class));
        }
        return userDetail;
    }

    @Override
    public UserDetailDTO updateUserStatus(UUID id, EUserStatus status) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        if (!sessionHelper.getRoles().contains(ERole.SYSADMIN.name())) {
            if (!user.getTenantCode().equals(sessionHelper.getTenantId())) {
                throw new ForbiddenException("No permission");
            }
        }
        if (status.equals(EUserStatus.BLOCKED) && user.getStatus().equals(EUserStatus.ACTIVE.name())) {
            authService.setGatewayFilter(user.getId().toString());
        }
        user.setStatus(status.name());
        return getUserDetail(userRepository.save(user));
    }

    @Override
    public PageRes<UserElementDTO> getStaffPage(String keyword, String tenantCode, UUID locationId, UUID permissionGroupId, String status, int page, int size) {
        UUID defaultUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if (!sessionHelper.getRoles().contains(ERole.SYSADMIN.name())) {
            tenantCode = sessionHelper.getTenantId();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("code", "name").ascending());
        Page<User> userPage = userRepository.getStaffPage(
                keyword == null ? "" : keyword,
                tenantCode == null ? "" : tenantCode,
                locationId == null ? defaultUUID : locationId,
                status == null ? "" : status,
                permissionGroupId == null ? defaultUUID : permissionGroupId,
                pageable
        );
        List<UserElementDTO> userElementDTOs = new ArrayList<>();
        for (User user : userPage.getContent()) {
            UserElementDTO userElementDTO = modelMapper.map(user, UserElementDTO.class);
            userElementDTO.setLocations(new ArrayList<>());

            boolean isNotEmpty = user.getLocationIds() != null && !user.getLocationIds().isEmpty();
            List<LocationDTO> locations = clientUtils.getLocations(isNotEmpty ? user.getLocationIds() : new ArrayList<>(), tenantCode, user.isAssignAllLocations());
            if (user.isAssignAllLocations()) {
                locations = locations.stream()
                        .filter(locationDTO -> {
                            if (locationDTO.getOperatorId() != null) {
                                locationDTO.setOperator(locationDTO.getOperatorId().equals(user.getId()));
                                return locationDTO.getOperatorId().equals(user.getId());
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            } else {
                for (LocationDTO locationDTO : locations) {
                    locationDTO.setOperator(locationDTO.getOperatorId() != null && locationDTO.getOperatorId().equals(user.getId()));
                }
            }
            userElementDTO.setLocations(locations);
            userElementDTOs.add(userElementDTO);
        }
        userElementDTOs.sort((user1, user2) -> Boolean.compare(user2.isAssignAllLocations(), user1.isAssignAllLocations()));

        // Ensure users with isAssignAllLocations = true are included even if they don't match locationId
        List<UserElementDTO> finalUserElementDTOs = userElementDTOs.stream()
                .filter(userElementDTO -> userElementDTO.isAssignAllLocations() || locationId == null || userElementDTO.getLocations().stream()
                        .anyMatch(locationDTO -> locationDTO.getId().equals(locationId)))
                .collect(Collectors.toList());

        return new PageRes<>(finalUserElementDTOs, page, size, (int) userPage.getTotalElements());
    }

    @Override
    public UserElementDTO getStaffDetail(UUID id, String tenantCode) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));

        UserElementDTO userElementDTO = modelMapper.map(user, UserElementDTO.class);
        boolean isNotEmpty = user.getLocationIds() != null && !user.getLocationIds().isEmpty();
        List<LocationDTO> locations = clientUtils.getLocations(isNotEmpty ? user.getLocationIds() : new ArrayList<>(), tenantCode, user.isAssignAllLocations());

        if (user.isAssignAllLocations()) {
            locations = locations.stream()
                    .filter(locationDTO -> {
                        if (locationDTO.getOperatorId() != null) {
                            return locationDTO.getOperatorId().equals(user.getId());
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } else {
            for (LocationDTO locationDTO : locations) {
                locationDTO.setOperator(locationDTO.getOperatorId() != null && locationDTO.getOperatorId().equals(user.getId()));
            }
        }

        userElementDTO.setLocations(locations);

        return userElementDTO;
    }


//    @Override
//    public void updateLocationName(UUID id, String name) {
//        List<User> users = userRepository.findAllByLocationId(id);
//        if (users.isEmpty()) return;
//        users.forEach(user -> user.setLocationName(name));
//        userRepository.saveAll(users);
//    }

    @Override
    public void deleteStaff(UUID id) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        if (!sessionHelper.getRoles().contains(ERole.SYSADMIN.name())) {
            if (user.getTenantCode() == null || !user.getTenantCode().equals(sessionHelper.getTenantId()) ||
                user.getRoles().contains(ERole.TENANT.name()) || user.getRoles().contains(ERole.SYSADMIN.name())) {
                throw new ForbiddenException("Không có quyền xóa tài khoản này");
            }
        }
        authService.setGatewayFilter(user.getId().toString());
        userRepository.delete(user);
    }

    private String getPermission(List<Permission> permissions) {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.unencodedCharsFunnel(), bloomFilterTotal, 0.01);
        for (Permission permission : permissions) {
            bloomFilter.put(permission.getCode());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            bloomFilter.writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    @Override
    public List<TenantStatusStatisticsProjection> getStatusStatistics() {
        return TenantRepository.getStatusStatistics();
    }

    @Override
    public PageRes<TenantStatusDTO> getAllTenantStatus(int size, int page) {
        Page<Tenant> tenants = TenantRepository.findAllTenantStatus(PageRequest.of(page, size));

        return new PageRes<>(modelMapper.map(tenants.getContent(), new TypeToken<List<TenantStatusDTO>>() {
        }.getType()),
                page,
                size,
                (int) tenants.getTotalElements());
    }

    @Override
    public List<String> getAllTenantCode() {
        return TenantRepository.getAllTenantCode();
    }

    @Override
    public List<StatisticUserDTO> getUserStatistic(String tenantCode) {
        if (!sessionHelper.getRoles().contains(ERole.SYSADMIN.name()) || tenantCode == null)
            tenantCode = sessionHelper.getTenantId() == null ? "" : sessionHelper.getTenantId();

        List<IData> userStatisticUser = userRepository.statisticStaff(tenantCode);
        StatisticUserDTO statisticUserActive = new StatisticUserDTO();
        statisticUserActive.setStatus(EUserStatus.ACTIVE.name());
        statisticUserActive.setTotal(0);
        StatisticUserDTO statisticUserBlock = new StatisticUserDTO();
        statisticUserBlock.setStatus(EUserStatus.BLOCKED.name());
        statisticUserBlock.setTotal(0);
        for (IData data : userStatisticUser) {
            if (data.getCode().equals(EUserStatus.ACTIVE.name())) {
                statisticUserActive.setTotal(Integer.parseInt(String.valueOf(data.getValue())));
            } else if (data.getCode().equals(EUserStatus.BLOCKED.name())) {
                statisticUserBlock.setTotal(Integer.parseInt(String.valueOf(data.getValue())));
            }
        }
        return List.of(statisticUserActive, statisticUserBlock);
    }

    @Override
    public void migrateTenant() {
        List<Tenant> tenants = TenantRepository.findAll();
        for (Tenant tenant : tenants) {
            tenant.setTenantId(TenantRepository.getMaxTenantId() + 1);
            TenantRepository.save(tenant);
        }
    }

    @Override
    public void migrateUser() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setCode(userRepository.getMaxCodeUser() + 1);
            userRepository.save(user);
        }
    }

    @Override
    public List<UserElementDTO> getUsersWithPermissionSMS(String tenantCode, UUID locationId) {
        UUID defaultUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

        List<User> listUsers = userRepository.findUsersByPermissionName("RECEIVE_SMS", tenantCode, locationId == null  ? defaultUUID : locationId);
        List<UserElementDTO> userElementDTOs = new ArrayList<>();
        for (User user : listUsers) {
            UserElementDTO userElementDTO = modelMapper.map(user, UserElementDTO.class);
            userElementDTOs.add(userElementDTO);
        }
        return userElementDTOs;
    }
}