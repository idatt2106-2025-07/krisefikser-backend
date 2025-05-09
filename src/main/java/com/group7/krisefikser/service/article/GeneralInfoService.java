package com.group7.krisefikser.service.article;

import com.group7.krisefikser.dto.request.article.GeneralInfoRequest;
import com.group7.krisefikser.dto.response.article.GeneralInfoResponse;
import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.mapper.article.GeneralInfoMapper;
import com.group7.krisefikser.model.article.GeneralInfo;
import com.group7.krisefikser.repository.article.GeneralInfoRepository;
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

  public List<GeneralInfoResponse> getAllGeneralInfo() {
    return GeneralInfoMapper
        .INSTANCE.generalInfoToResponseList(generalInfoRepo.getAllGeneralInfo());
  }

  /**
   * Retrieves general information by theme.
   * This method filters the general information
   * based on the specified theme.
   *
   * @param theme the theme to filter the general information
   * @return a list of GeneralInfo objects containing details of the general information
   */
  public List<GeneralInfoResponse> getGeneralInfoByTheme(Theme theme) {
    return GeneralInfoMapper
        .INSTANCE.generalInfoToResponseList(generalInfoRepo.getGeneralInfoByTheme(theme));
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
  public GeneralInfoResponse addGeneralInfo(GeneralInfoRequest generalInfoRequest) {
    GeneralInfo info = GeneralInfoMapper
        .INSTANCE.requestToGeneralInfo(generalInfoRequest);
    GeneralInfo savedInfo = generalInfoRepo.addGeneralInfo(info);
    return GeneralInfoMapper
        .INSTANCE.generalInfoToResponse(savedInfo);
  }

  /**
   * Updates an existing general information entry in the repository.
   * This method converts the GeneralInfoRequest
   * object to a GeneralInfo object
   * and then calls the repository method to update it.
   *
   * @param generalInfoRequest the request object containing details of the general
   *                            information to be updated
   * @param id the ID of the general information to be updated
   */
  public GeneralInfoResponse updateGeneralInfo(GeneralInfoRequest generalInfoRequest, Long id) {
    GeneralInfo info = GeneralInfoMapper
        .INSTANCE.requestToGeneralInfo(generalInfoRequest);
    GeneralInfo updatedInfo = generalInfoRepo.updateGeneralInfo(info, id);
    return GeneralInfoMapper
        .INSTANCE.generalInfoToResponse(updatedInfo);
  }

  /**
   * Deletes an existing general information entry from the repository.
   * This method retrieves the ID from the GeneralInfoRequest
   * object and calls the repository method to delete it.
   *
   * @param id the ID of the general information to be deleted
   */
  public void deleteGeneralInfo(Long id) {
    generalInfoRepo.deleteGeneralInfo(id);
  }
}
