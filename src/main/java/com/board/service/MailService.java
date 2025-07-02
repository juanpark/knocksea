package com.board.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.properties.auth-code-expiration-millis}")
  private long expirationMillis;

  @Value("${spring.mail.username}")
  private String sender;

  private static final Map<String, CodeInfo> verificationCodes = new ConcurrentHashMap<>();


  // 인증 메일 전송
  public void sendVerificationCode(String email) {
    MimeMessage mimeMessage = createMessage(email);
    javaMailSender.send(mimeMessage);
  }

  // 인증 코드 확인
  public boolean codeVerification(String email, String verificationCode) {
    CodeInfo codeInfo = verificationCodes.get(email);

    // 인증 코드 없음 처리
    if (codeInfo == null) {
      throw new RuntimeException("인증 코드가 존재하지 않습니다.");
    }

    return codeInfo.code.equals(verificationCode);
  }

  public void expireCode(String email) {
    verificationCodes.remove(email);
  }

  // 메일 폼 생성
  private MimeMessage createMessage(String email) {
    String code = createRandomCode();
    storeCode(email, code);
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    try {
      mimeMessage.addRecipients(MimeMessage.RecipientType.TO, email);
      mimeMessage.setSubject("인증번호입니다.");
      mimeMessage.setFrom(sender);

      mimeMessage.setText(code, "utf-8", "html");
      return mimeMessage;
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  // 인증 번호 생성
  private String createRandomCode() {
    String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 숫자
    log.info("생성된 인증 번호: {}", code);
    return code;
  }

  // 인증번호 저장
  private void storeCode(String email, String code) {
    CodeInfo codeInfo = new CodeInfo(code, expirationMillis);
    verificationCodes.put(email, codeInfo);
  }

  // 내부 클래스 인증 코드 + 만료 시간
  private static class CodeInfo {

    String code;
    long expireAt;

    CodeInfo(String code, long expireAt) {
      this.code = code;
      this.expireAt = expireAt;
    }
  }
}
