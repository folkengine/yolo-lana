package com.electronicpanopticon.demo.yolo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;

import org.joda.money.Money;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Baker <ignatz@gmail.com>
 */
public class RetailPriceTest {

  static final Logger LOG = LoggerFactory.getLogger(RetailPriceTest.class);

  @Test
  public void retailPrice() {
    Money price = Money.parse("USD 23.87");
    RetailPrice list = new RetailPrice(price, LocalDate.now());
    assertEquals(price, list.getMoney());
    assertEquals(LocalDate.now(), list.getStartDate());
  }

  @Test
  public void applies_inPast_returnsFalse() {
    RetailPrice list = new RetailPrice(Money.parse("USD 9.99"), LocalDate.now());
    LocalDate past = LocalDate.now().minusDays(6);
    assertFalse("For a date in the past, a Price doesn't apply.", list.applies(past));
  }

}
