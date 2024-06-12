package com.project.back.service.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.project.back.common.util.TelNumberAuthNumberUtil;
import com.project.back.dto.request.auth.CheckBusinessRegistrationRequestDto;
import com.project.back.dto.request.auth.CheckEmailIdRequestDto;
import com.project.back.dto.request.auth.CheckNicknameRequestDto;
import com.project.back.dto.request.auth.CheckTelNumberAuthRequestDto;
import com.project.back.dto.request.auth.FindEmailRequestDto;
import com.project.back.dto.request.auth.NewPasswordRequestDto;
import com.project.back.dto.request.auth.PasswordResetRequestDto;
import com.project.back.dto.request.auth.SignInRequestDto;
import com.project.back.dto.request.auth.SignUpRequestDto;
import com.project.back.dto.request.auth.TelNumberAuthRequestDto;
import com.project.back.dto.response.ResponseDto;
import com.project.back.dto.response.auth.FindEmailResponseDto;
import com.project.back.dto.response.auth.SignInResponseDto;
import com.project.back.entity.AuthNumberEntity;
import com.project.back.entity.UserEntity;
import com.project.back.provider.JwtProvider;
import com.project.back.provider.SmsProvider;
import com.project.back.repository.AuthNumberRepository;
import com.project.back.repository.UserRepository;
import com.project.back.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
  private final UserRepository userRepository;
  private final AuthNumberRepository authNumberRepository;

  private final SmsProvider smsProvider;
  private final JwtProvider jwtProvider;

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Override
  public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {
    String accessToken = null;

    try {
      String userEmailId = dto.getUserEmailId();
      String password = dto.getPassword();

      UserEntity userEntity = userRepository.findByUserEmailId(userEmailId);
      if (userEntity == null) return ResponseDto.signInFailed();

      String encodedPassword = userEntity.getPassword();

      boolean isMatched = passwordEncoder.matches(password, encodedPassword);
      if (!isMatched) return ResponseDto.signInFailed();

      accessToken = jwtProvider.create(userEmailId);
      if (accessToken == null) return ResponseDto.tokenCreationFailed();
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return SignInResponseDto.success(accessToken);
  }

  @Override
  public ResponseEntity<ResponseDto> emailIdCheck(CheckEmailIdRequestDto dto) {
    try {
      String userEmailId = dto.getUserEmailId();

      boolean existedUser = userRepository.existsByUserEmailId(userEmailId);
      if (existedUser) return ResponseDto.duplicatedEmailId();
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  @Override
  public ResponseEntity<ResponseDto> nicknameCheck(CheckNicknameRequestDto dto) {
    try {
      String nickname = dto.getNickname();

      boolean existedNickname = userRepository.existsByNickname(nickname);
      if (existedNickname) return ResponseDto.duplicatedNickname();
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  @Override
  public ResponseEntity<ResponseDto> telNumberAuth(TelNumberAuthRequestDto dto) {
    try {
      String userTelNumber = dto.getUserTelNumber();

      String authNumber = TelNumberAuthNumberUtil.createNumber();

      AuthNumberEntity authNumberEntity = new AuthNumberEntity(userTelNumber, authNumber);
      authNumberRepository.save(authNumberEntity);

      smsProvider.sendAuthNumber(userTelNumber, authNumber);
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  @Override
  public ResponseEntity<ResponseDto> telNumberAuthCheck(CheckTelNumberAuthRequestDto dto) {
    try {
      String userTelNumber = dto.getUserTelNumber();
      String authNumber = dto.getAuthNumber();

      boolean isMatched = authNumberRepository.existsByTelNumberAndAuthNumber(userTelNumber, authNumber);
      if (!isMatched) return ResponseDto.authenticationFailed();
    } catch(Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  @Override
  public ResponseEntity<ResponseDto> businessRegistrationCheck(CheckBusinessRegistrationRequestDto dto) {
    try {
      String businessRegistrationNumber = dto.getBusinessRegistrationNumber();

      boolean existedBusinessRegistrationNumber = userRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber);
      if (existedBusinessRegistrationNumber) return ResponseDto.duplicatedBusinessRegistrationNumber();
      
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
  return ResponseDto.success();
  }

  @Override
  public ResponseEntity<ResponseDto> signUp(SignUpRequestDto dto) {
    try {
      String userEmailId = dto.getUserEmailId();
      String password = dto.getPassword();
      String userNickName = dto.getNickname();
      String userTelNumber = dto.getUserTelNumber();
      String authNumber = dto.getAuthNumber();
      String businessRegistrationNumber = dto.getBusinessRegistrationNumber();
      String userRole; //수정



      boolean existedUser = userRepository.existsByUserEmailId(userEmailId);
      if (existedUser) return ResponseDto.duplicatedEmailId();

      boolean existedNickname = userRepository.existsByNickname(userNickName);
      if (existedNickname) return ResponseDto.duplicatedNickname();

      if (StringUtils.hasText(businessRegistrationNumber)) {
        boolean existedBusinessRegistrationNumber = userRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber);
        if (existedBusinessRegistrationNumber) return ResponseDto.duplicatedBusinessRegistrationNumber();
      }
      
      //추가
      if(businessRegistrationNumber=="")
      {
          userRole="ROLE_USER";
      }
      else
      {
          userRole="ROLE_CEO";
      }
      //추가

      boolean isMatched = authNumberRepository.existsByTelNumberAndAuthNumber(userTelNumber, authNumber);
      if (!isMatched) return ResponseDto.authenticationFailed();

      String encodedPassword = passwordEncoder.encode(password);
      dto.setPassword(encodedPassword);

      UserEntity userEntity = new UserEntity(dto,userRole);
      userRepository.save(userEntity);
    } catch(Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  @Override
  public ResponseEntity<? super FindEmailResponseDto> findEmail(FindEmailRequestDto dto) {
    try {
    
      String userName = dto.getUserName();
      String userTelNumber = dto.getUserTelNumber();

      UserEntity userEntity = userRepository.findByUserNameAndUserTelNumber(userName, userTelNumber);
      if (userEntity == null) return ResponseDto.noExistUser();

      String userEmailId = userEntity.getUserEmailId();

      return FindEmailResponseDto.success(userEmailId);

      // boolean isMatched = userRepository.existsByUserNameAndUserTelNumber(userTelNumber);
      // if (!isMatched) return ResponseDto.authenticationFailed();
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    
  }

  // 비밀번호 재설정 (이메일이랑 전화번호를 받아서 확인하는 코드)
  @Override
  public ResponseEntity<ResponseDto> passwordReset(PasswordResetRequestDto dto) {
    
    try {

      String userEmailId = dto.getUserEmailId();
      String userTelNumber = dto.getUserTelNumber();
      // 링크코드 어려우니깐 일단 보류
      // String resetLinkCode = PasswordResetLinkCodeUtil.createCode();

      // smsProvider.sendPasswordResetLink(userEmailId, userTelNumber);

      boolean isMatched = userRepository.existsByUserEmailIdAndUserTelNumber(userEmailId, userTelNumber);
      if (!isMatched) return ResponseDto.authenticationFailed();

    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

  // @Override
  // public ResponseEntity<ResponseDto> passwordReset(PasswordResetRequestDto dto) {
  //   try {
  //     String userTelNumber = dto.getUserTelNumber();
      
  //     String resetLinkCode = PasswordResetLinkCodeUtil.createCode();

  //     smsProvider.sendPasswordResetLink(userTelNumber, resetLinkCode);
  //   } catch (Exception exception) {
  //     exception.printStackTrace();
  //     return ResponseDto.databaseError();
  //   }
  //   return ResponseDto.success();
  // }

  @Override
  public ResponseEntity<ResponseDto> newPassword(NewPasswordRequestDto dto, String userEmailId) {
    
    try {

      String password = dto.getPassword();

      UserEntity userEntity = userRepository.findByUserEmailId(userEmailId);
      System.out.println(userEmailId);
      if (userEntity == null) return ResponseDto.noExistUser();

      // boolean isUser = userEmailId.equals(userEmailId);
      // if (!isUser) return ResponseDto.noExistUser();

      boolean isMatched = userRepository.existsById(userEmailId);
      if (!isMatched) return ResponseDto.authenticationFailed();

      String encodedPassword = passwordEncoder.encode(password);

      dto.setPassword(encodedPassword);

      userEntity.setPassword(encodedPassword);
      
      userRepository.save(userEntity);
      
    } catch(Exception exception) {
      exception.printStackTrace();
      return ResponseDto.databaseError();
    }
    return ResponseDto.success();
  }

}