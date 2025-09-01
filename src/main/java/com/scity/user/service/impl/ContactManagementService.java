package com.scity.user.service.impl;

import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.tenant.TenantDetailDTO;
import com.scity.user.model.dto.contact_management.ContactManagementDTO;
import com.scity.user.model.dto.contact_management.ContactManagementDetailDTO;
import com.scity.user.model.entity.ContactManagement;
import com.scity.user.repository.ContactManagementRepository;
import com.scity.user.service.IContactManagementService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactManagementService implements IContactManagementService {
    @Autowired
    private ContactManagementRepository contactManagementRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageRes<ContactManagementDetailDTO> getAllContacts(String keyword, int page, int size) {
        Sort sort = Sort.by( "created_at").descending();
        Page<ContactManagement> contacts = contactManagementRepository.findAll(keyword, PageRequest.of(page, size, sort));
        return new PageRes<>(modelMapper.map(contacts.getContent(), new TypeToken<List<ContactManagementDetailDTO>>() {
                }.getType()),
                page,
                size,
                (int) contacts.getTotalElements());
    }

    @Override
    public ContactManagementDTO getContactById(UUID id) {
        Optional<ContactManagement> contactOptional = contactManagementRepository.findById(id);
        return contactOptional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public ContactManagementDTO  createContact(ContactManagementDTO  contactDTO) {
        ContactManagement contact = new ContactManagement(contactDTO);
        contact.setTick(false);
        ContactManagement savedContact = contactManagementRepository.save(contact);
        return convertToDTO(savedContact);
    }

    @Override
    public ContactManagementDTO  updateContact(UUID id, ContactManagementDTO  contactDTO) {
        ContactManagement existingContact = contactManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + id));
        existingContact.setFullName(contactDTO.getFullName());
        existingContact.setEmail(contactDTO.getEmail());
        existingContact.setPhone(contactDTO.getPhone());
        existingContact.setTenantName(contactDTO.getTenantName());
        existingContact.setProductPackage(contactDTO.getProductPackage());
        existingContact.setNote(contactDTO.getNote());
        ContactManagement updatedContact = contactManagementRepository.save(existingContact);
        return convertToDTO(updatedContact);
    }
    @Override
    public ContactManagementDTO  handlerRedpoint(UUID id) {
        ContactManagement existingContact = contactManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + id));
        existingContact.setTick(!existingContact.isTick());
        ContactManagement updatedContact = contactManagementRepository.save(existingContact);
        return convertToDTO(updatedContact);
    }

    @Override
    public void deleteContact(UUID id) {
        contactManagementRepository.deleteById(id);
    }


    private ContactManagementDTO convertToDTO(ContactManagement contact) {
        return modelMapper.map(contact, ContactManagementDTO.class);
    }

    private ContactManagement convertToEntity(ContactManagementDTO contactDTO) {
        return modelMapper.map(contactDTO, ContactManagement.class);
    }
}
