package com.example.backend.service.user;


import com.example.backend.dto.mypage.main.MypageMainDto;
import com.example.backend.dto.user.UserDTO;
import com.example.backend.dto.user.UserModifyDTO;
import com.example.backend.dto.user.UserRegisterDTO;
import com.example.backend.entity.Users;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface UserService {

   void registerUser(UserRegisterDTO userRegisterDTO, boolean isAdmin);

   UserDTO getKakaoMember(String accessToken);

   default UserDTO entityToDTO(Users user) {
      UserDTO userDTO = new UserDTO(
              user.getUserId(),
               user.getEmail(),
               user.getPassword(),
               user.getGrade(),
               user.getNickname(),
               user.getPhoneNum(),
               user.getProfileImg(),
               user.isSocial(),
               user.isRole());

      return userDTO;
   }

   List<String> getProfileFromKakaoToken(String accessToken);

   String makeTempPassword();

   Users makeSocialUser(List<String> socialAccountList);

   void modifyUser(UserModifyDTO userModifyDTO);

   MypageMainDto getMyPageInfo(Long userId);
}