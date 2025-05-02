package com.group7.krisefikser.controller;

import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.GeneralInfo;
import com.group7.krisefikser.service.GeneralInfoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/general-info")
public class GeneralInfoController {
  @Autowired
  private GeneralInfoService generalInfoService;

  @GetMapping("/all")
  public List<GeneralInfo> getAllGeneralInfo() {
    return generalInfoService.getAllGeneralInfo();
  }

  @GetMapping("/{theme}")
  public List<GeneralInfo> getGeneralInfoByTheme(@PathVariable Theme theme) {
    return generalInfoService.getGeneralInfoByTheme(theme);
  }

  @PostMapping("/admin/add")
  public void addGeneralInfo(@RequestBody GeneralInfo info) {
    generalInfoService.addGeneralInfo(info);
  }

  @PutMapping("/admin/update")
  public void updateGeneralInfo(@RequestBody GeneralInfo info) {
    generalInfoService.updateGeneralInfo(info);
  }

  @DeleteMapping("/admin/delete/{id}")
  public void deleteGeneralInfo(@PathVariable Long id) {
    generalInfoService.deleteGeneralInfo(id);
  }
}
