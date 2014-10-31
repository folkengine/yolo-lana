package com.electronicpanopticon.demo.yolo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.joda.money.Money;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscountTest {

  static final Logger LOG = LoggerFactory.getLogger(DiscountTest.class);

  private static final LocalDate NOW = LocalDate.now();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public void discount_noEndDate() {
    Discount discount = new Discount(DiscountPercent.FIVE, NOW);
    assertNull(discount.getEndDate());
    assertEquals(NOW, discount.getStartDate());
    assertEquals(DiscountPercent.FIVE, discount.getPercent());
  }

  @Test
  public void daysInEffect() {
    // No end date
    Discount discount = new Discount(DiscountPercent.FIVE, NOW);
    LOG.debug(discount.toString());
    long comparisonDays = 5;
    LocalDate comparison = NOW.plusDays(comparisonDays);
    assertEquals(comparisonDays, discount.daysInEffect(comparison));
    assertTrue(discount.applies(comparison));

    // With End date
    discount =
        new Discount(DiscountPercent.FIVE, NOW, NOW.plusDays(comparisonDays + comparisonDays));
    LOG.debug(discount.toString());
    assertEquals(comparisonDays, discount.daysInEffect(comparison));

    assertTrue(discount.applies(comparison));
  }

  @Test
  public void daysInEffect_beforeComparisonAndNoEndDate_returnsNegative() {
    Discount discount = new Discount(DiscountPercent.FIVE, NOW);
    LOG.debug(discount.toString());
    long daysPast = 5;
    LocalDate past = discount.getStartDate().minusDays(daysPast);
    assertEquals(-daysPast, discount.daysInEffect(past));

    // The Discount should also not apply against a LocalDate out of range.
    assertFalse(discount.applies(past));
  }

  @Test
  public void daysInEffect_comparisonBeforeWithEndDate_returnsNegative() {
    LocalDate end = NOW.plusDays(5);
    Discount discount = new Discount(DiscountPercent.FIVE, NOW, end);
    LOG.debug(discount.toString());
    long daysPast = 6;
    LocalDate past = discount.getStartDate().minusDays(daysPast);
    assertEquals(-daysPast, discount.daysInEffect(past));

    // The Discount should also not apply against a LocalDate out of range.
    assertFalse(discount.applies(past));
  }

  @Test
  public void daysInEffect_AfterEndDate_returnsNegative() {
    long endDays = 5;
    Discount discount = new Discount(DiscountPercent.FIFTEEN, NOW, NOW.plusDays(endDays));
    LOG.debug(discount.toString());
    long comparisonDays = endDays + 1;
    LocalDate comparison = NOW.plusDays(comparisonDays);
    long daysInEffect = discount.daysInEffect(comparison);
    assertEquals((endDays - comparisonDays), daysInEffect);

    // The Discount should also not apply against a LocalDate out of range.
    assertFalse(discount.applies(comparison));
  }

  /**
   * Applies date range should be inclusive.
   */
  @Test
  public void applies_0days() {
    long endDays = 5l;
    Discount discount = new Discount(DiscountPercent.TEN, NOW, NOW.plusDays(endDays));
    LOG.debug(discount.toString());
    LocalDate comparison = NOW;
    assertTrue(discount.applies(comparison));
    assertEquals(0L, discount.daysInEffect(comparison));

    comparison = NOW.plusDays(endDays);
    assertTrue(discount.applies(comparison));
    assertEquals(endDays, discount.daysInEffect(comparison));
  }

  @Test
  public void apply() {
    Discount discount = new Discount(DiscountPercent.FIVE, NOW, NOW.plusDays(5));
    LOG.debug(discount.toString());
    Money greenback = Money.parse("USD 1.00");
    Money discounted = discount.apply(greenback);
    assertEquals(Money.parse("USD 0.95"), discounted);
  }

  @Test
  public void apply_zero() {
    Discount discount = new Discount(DiscountPercent.FIVE, NOW, NOW.plusDays(5));
    LOG.debug(discount.toString());
    Money greenback = Money.parse("USD 0.00");
    Money discounted = discount.apply(greenback);
    assertEquals(Money.parse("USD 0.00"), discounted);
  }
}
