package com.scity.user.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

  @Autowired
  private StringEncryptor stringEncryptor;

  public String encryptData(String data) {
    return stringEncryptor.encrypt(data);
  }

  public String decryptData(String encryptedData) {
    return stringEncryptor.decrypt(encryptedData);
  }
}