package com.scity.user.controller;

import com.scity.user.aop.annotation.AuthorizeRequest;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.ResModel;
import com.scity.user.model.dto.tenant.TenantDetailDTO;
import com.scity.user.model.dto.contact_management.ContactManagementDTO;
import com.scity.user.model.dto.contact_management.ContactManagementDetailDTO;
import com.scity.user.model.entity.ContactManagement;
import com.scity.user.service.IContactManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contact")
public class ContactManagementController {

    @Autowired
    private IContactManagementService contactManagementService;

    @PostMapping("/noauth")
    @Operation(summary = "Create new contact management")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40008: Invalid phone number\n
                    40009: Invalid email"""),})
    public ResModel<ContactManagementDTO> createContactManagement(
            @RequestBody @Valid ContactManagementDTO contactManagementDTO) {
        ContactManagementDTO createdContactManagement = contactManagementService.createContact(contactManagementDTO);
        return ResModel.ok(createdContactManagement);
    }


    @GetMapping()
    @Operation(summary = "Get Contact Management by page")
    @AuthorizeRequest(roles = {"SYSADMIN"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    public ResModel<PageRes<ContactManagementDetailDTO>> getAllContactManagementEntries(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResModel.ok(contactManagementService.getAllContacts(keyword, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get contact management by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    public ResModel<ContactManagementDTO> getContactManagementById(@PathVariable UUID id) {
        ContactManagementDTO contactManagementDTO = contactManagementService.getContactById(id);
        return ResModel.ok(contactManagementDTO);
    }

    @PutMapping("/handler-tick/{id}")
    @AuthorizeRequest(roles = {"SYSADMIN"})
    @Operation(summary = "-tick contact management by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    public ResModel<ContactManagementDTO> updateContactManagement(
            @PathVariable UUID id) {
        ContactManagementDTO updatedContactManagement = contactManagementService.handlerRedpoint(id);
        return ResModel.ok(updatedContactManagement);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete contact management by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    public ResponseEntity<Void> deleteContactManagement(@PathVariable UUID id) {
        contactManagementService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

