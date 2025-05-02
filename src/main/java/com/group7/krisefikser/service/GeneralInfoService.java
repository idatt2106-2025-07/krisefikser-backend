package com.group7.krisefikser.service;

import com.group7.krisefikser.dto.request.GeneralInfoRequest;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.mapper.GeneralInfoMapper;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.repository.GeneralInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to general information.
 * This class provides methods to retrieve,
 * add, update, and delete general information.
 * It uses the GeneralInfoRepository
 * to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class GeneralInfoService {
  private final GeneralInfoRepository generalInfoRepo;

  public List<GeneralInfo> getAllGeneralInfo() {
    return generalInfoRepo.getAllGeneralInfo();
  }

  /**
   * Retrieves general information by theme.
   * This method filters the general information
   * based on the specified theme.
   *
   * @param theme the theme to filter the general information
   * @return a list of GeneralInfo objects containing details of the general information
   */
  public List<GeneralInfo> getGeneralInfoByTheme(Theme theme) {
    return generalInfoRepo.getGeneralInfoByTheme(theme);
  }

  /**
   * Adds a new general information entry to the repository.
   * This method converts the GeneralInfoRequest
   * object to a GeneralInfo object
   * and then calls the repository method to add it.
   *
   * @param generalInfoRequest the request object containing details of the general
   *                           information to be added
   */
  public void addGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper
        .INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    generalInfoRepo.addGeneralInfo(info);
  }

  /**
   * Updates an existing general information entry in the repository.
   * This method converts the GeneralInfoRequest
   * object to a GeneralInfo object
   * and then calls the repository method to update it.
   *
   * @param generalInfoRequest the request object containing details of the general
   */
  public void updateGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper
        .INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    generalInfoRepo.updateGeneralInfo(info);
  }

  /**
   * Deletes an existing general information entry from the repository.
   * This method retrieves the ID from the GeneralInfoRequest
   * object and calls the repository method to delete it.
   *
   * @param generalInfoRequest the request object containing the ID
   *                           of the general information to be deleted
   */
  public void deleteGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper
        .INSTANCE.generalInfoRequestToGeneralInfo(generalInfoRequest);
    Long id = info.getId();
    generalInfoRepo.deleteGeneralInfo(id);
  }
}
