package com.example.ec_2024b_back.share.domain.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.ec_2024b_back.share.domain.models.Address.DetailAddress;
import com.example.ec_2024b_back.share.domain.models.Address.Municipalities;
import com.example.ec_2024b_back.share.domain.models.Address.Prefecture;
import com.example.ec_2024b_back.share.domain.models.Address.Zipcode;
import com.example.ec_2024b_back.utils.Fast;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Fast
public class AddressTests {
  @Test
  void testAddress() {
    var zipcode = new Zipcode("123-4567");
    var prefecture = Prefecture.fromName("東京都");
    var municipalities = new Municipalities("新宿区");
    var detailAddress = new DetailAddress("1-1-1");
    var address = new Address(zipcode, prefecture, municipalities, detailAddress);
    assertEquals(zipcode, address.zipcode());
    assertEquals(prefecture, address.prefecture());
    assertEquals(municipalities, address.municipalities());
    assertEquals(detailAddress, address.detailAddress());
  }

  // --- Zipcode Tests ---
  @Test
  void testZipcode_valid() {
    assertDoesNotThrow(() -> new Zipcode("123-4567"));
  }

  @Test
  void testZipcode_empty() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new Zipcode(""));
    assertEquals("郵便番号は空にできません", exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1234567", "12-3456", "abc-defg", "123-456"})
  void testZipcode_invalidFormat(String invalidCode) {
    var exception = assertThrows(IllegalArgumentException.class, () -> new Zipcode(invalidCode));
    assertEquals("郵便番号のフォーマットが不正です", exception.getMessage());
  }

  // --- Prefecture Tests ---
  @ParameterizedTest
  @CsvSource({"北海道,HOKKAIDO", "東京都,TOKYO", "沖縄県,OKINAWA"})
  void testPrefecture_fromName_valid(String name, String expected) {
    assertEquals(expected, Prefecture.fromName(name).toString());
  }

  @Test
  void testPrefecture_fromName_invalid() {
    var exception = assertThrows(IllegalArgumentException.class, () -> Prefecture.fromName("海外"));
    assertEquals("Invalid prefecture name: 海外", exception.getMessage());
  }

  // --- Municipalities Tests ---
  @Test
  void testMunicipalities_valid() {
    assertDoesNotThrow(() -> new Municipalities("千代田区"));
  }

  @Test
  void testMunicipalities_empty() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new Municipalities(""));
    assertEquals("市区町村は空にできません", exception.getMessage());
  }

  // --- DetailAddress Tests ---
  @Test
  void testDetailAddress_valid() {
    assertDoesNotThrow(() -> new DetailAddress("丸の内1-1-1"));
  }

  @Test
  void testDetailAddress_empty() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new DetailAddress(""));
    assertEquals("番地以下の詳細住所は空にできません", exception.getMessage());
  }
}
