package com.group7.krisefikser.model;

import com.group7.krisefikser.enums.Theme;
import com.group7.krisefikser.model.article.GeneralInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralInfoTest {

  @Test
  void testNoArgsConstructor() {
    GeneralInfo info = new GeneralInfo();

    assertThat(info.getId()).isNull();
    assertThat(info.getTheme()).isNull();
    assertThat(info.getTitle()).isNull();
    assertThat(info.getContent()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    GeneralInfo info = new GeneralInfo(1L, Theme.BEFORE_CRISIS, "Title", "Some content");

    assertThat(info.getId()).isEqualTo(1L);
    assertThat(info.getTheme()).isEqualTo(Theme.BEFORE_CRISIS);
    assertThat(info.getTitle()).isEqualTo("Title");
    assertThat(info.getContent()).isEqualTo("Some content");
  }

  @Test
  void testSettersAndGetters() {
    GeneralInfo info = new GeneralInfo();

    info.setId(2L);
    info.setTheme(Theme.DURING_CRISIS);
    info.setTitle("Updated Title");
    info.setContent("Updated content");

    assertThat(info.getId()).isEqualTo(2L);
    assertThat(info.getTheme()).isEqualTo(Theme.DURING_CRISIS);
    assertThat(info.getTitle()).isEqualTo("Updated Title");
    assertThat(info.getContent()).isEqualTo("Updated content");
  }

  @Test
  void testEqualsAndHashCode() {
    GeneralInfo info1 = new GeneralInfo(1L, Theme.AFTER_CRISIS, "Info", "Content");
    GeneralInfo info2 = new GeneralInfo(1L, Theme.AFTER_CRISIS, "Info", "Content");

    assertThat(info1).isEqualTo(info2);
    assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
  }

  @Test
  void testToString() {
    GeneralInfo info = new GeneralInfo(1L, Theme.BEFORE_CRISIS, "Title", "Content");

    String toString = info.toString();
    assertThat(toString).contains("id=1", "theme=BEFORE_CRISIS", "title=Title", "content=Content");
  }
}