package com.group7.krisefikser.service;

import com.group7.krisefikser.enums.Theme;
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

  public void addGeneralInfo(GeneralInfo info) {
    generalInfoRepo.addGeneralInfo(info);
  }

  public void updateGeneralInfo(GeneralInfo info) {
    generalInfoRepo.updateGeneralInfo(info);
  }

  public void deleteGeneralInfo(Long id) {
    generalInfoRepo.deleteGeneralInfo(id);
  }

}
