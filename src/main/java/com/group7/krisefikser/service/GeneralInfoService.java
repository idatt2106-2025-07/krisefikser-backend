package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.mapper.GeneralInfoMapper;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.repository.GeneralInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralInfoService {
  private final GeneralInfoRepository generalInfoRepo;

  public List<GeneralInfo> getAllGeneralInfo() {
    return generalInfoRepo.getAllGeneralInfo();
  }

  public List<GeneralInfo> getGeneralInfoByTheme(Theme theme) {
    return generalInfoRepo.getGeneralInfoByTheme(theme);
  }

  public void addGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper.INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    generalInfoRepo.addGeneralInfo(info);
  }

  public void updateGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper.INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    generalInfoRepo.updateGeneralInfo(info);
  }

  public void deleteGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper.INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    Long id = info.getId();
    generalInfoRepo.deleteGeneralInfo(id);
  }

}
