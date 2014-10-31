package com.electronicpanopticon.demo.yolo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.joda.money.Money;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Baker <ignatz@gmail.com>
 */
public class ItemTest {

  static final Logger LOG = LoggerFactory.getLogger(ItemTest.class);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public void determineCumulativeDiscount() {

    Money cost = Money.parse("USD 1.00");
    Money list = Money.parse("USD 2.00");

    Item item =
        new Item("test1", "determineCumulativeDiscount", cost, new RetailPrice(list,
            LocalDate.now()));

    Discount tenPercent2Days = new Discount(DiscountPercent.TEN, LocalDate.now().plusDays(2));
    item.addDiscount(tenPercent2Days);

    Discount fifteenPercent3Days =
        new Discount(DiscountPercent.FIFTEEN, LocalDate.now().plusDays(3), LocalDate.now().plusDays(6));
    item.addDiscount(fifteenPercent3Days);

    assertEquals(cost, item.getCost());
    assertEquals(list, item.determineCurrentRetailPrice().getMoney());

    LOG.debug("Now Current List Price: " + item.determineCurrentRetailPrice());
    LOG.debug("Now Discount Price: " + item.applyDiscounts(LocalDate.now()));

    LOG.debug("2 Day Current List Price: " + item.determineCurrentRetailPrice(LocalDate.now().plusDays(2)));
    LOG.debug("2 Day Discount Price: " + item.applyDiscounts(LocalDate.now().plusDays(2)));

    LOG.debug("3 Day Current List Price: " + item.determineCurrentRetailPrice(LocalDate.now().plusDays(3)));
    LOG.debug("3 Day Discount Price: " + item.applyDiscounts(LocalDate.now().plusDays(3)));
    LOG.debug("3 Day Cumulative Discount: " + item.determineCumulativeDiscount(LocalDate.now().plusDays(3)));
    // XXX This could be less brittle
    assertEquals(24, item.determineCumulativeDiscount(LocalDate.now().plusDays(3)));

    LOG.debug("10 Day Current List Price: " + item.determineCurrentRetailPrice(LocalDate.now().plusDays(10)));
    LOG.debug("10 Day Discount Price: " + item.applyDiscounts(LocalDate.now().plusDays(10)));
    assertEquals(item.determineCurrentRetailPrice(), item.determineCurrentRetailPrice(LocalDate.now().plusDays(10)));

    assertTrue("Not yet implemented", true);
  }

  @Test
  public void determineLongestDiscountDaysCount_noDiscount_returnsNegative1() {
    Money cost = Money.parse("USD 1.00");
    Money list = Money.parse("USD 2.00");
    Item item =
        new Item("test1", "determineCumulativeDiscount", cost, new RetailPrice(list,
            LocalDate.now()));

    List<Discount> discounts = item.determineDiscountsThatApply(LocalDate.now());
    assertEquals(0, discounts.size());
    assertEquals(-1, item.determineLongestDiscountDaysCount(LocalDate.now()));
  }

  @Test
  public void isRetailPriceStable() {
    Money cost = Money.parse("USD 1.00");
    Money list = Money.parse("USD 2.00");
    Item item =
        new Item("test1", "determineCumulativeDiscount", cost, new RetailPrice(list,
            LocalDate.now().minusDays(31)));
    assertTrue(item.isRetailPriceStable());

    item =
        new Item("test1", "determineCumulativeDiscount", cost, new RetailPrice(list,
            LocalDate.now().minusDays(30)));
    assertFalse(item.isRetailPriceStable());
  }


}
