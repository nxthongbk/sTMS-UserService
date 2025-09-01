package com.scity.user.service;

import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.contact_management.ContactManagementDTO;
import com.scity.user.model.dto.contact_management.ContactManagementDetailDTO;
import com.scity.user.model.entity.ContactManagement;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IContactManagementService {
    PageRes<ContactManagementDetailDTO> getAllContacts(String keyword, int page, int size);
    ContactManagementDTO getContactById(UUID id);
    ContactManagementDTO createContact(ContactManagementDTO contact);
    ContactManagementDTO updateContact(UUID id, ContactManagementDTO contact);
    void deleteContact(UUID id);
    ContactManagementDTO handlerRedpoint(UUID id);
}
