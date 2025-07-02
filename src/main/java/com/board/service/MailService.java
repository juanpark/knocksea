package com.board.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

  private static String code;

  // 인증 메일 전송
  public void sendVerificationCode(String email){
    MimeMessage mimeMessage = createMessage(email);
    javaMailSender.send(mimeMessage);
  }

  // 메일 폼 생성
  private MimeMessage createMessage(String email) {
    createRandomCode();

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    try{
      mimeMessage.addRecipients(MimeMessage.RecipientType.TO,email);
      mimeMessage.setSubject("인증번호입니다.");
      mimeMessage.setFrom(sender);

      mimeMessage.setText(code,"utf-8","html");
      return mimeMessage;
    }catch(MessagingException e){
      throw new RuntimeException(e);
    }
  }

  // 인증 번호 생성
  private void createRandomCode(){
    code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자
    log.info("인증 번호: {}",code);
  }
}
