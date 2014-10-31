package com.electronicpanopticon.demo.yolo;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main class for the exercise. An Item is something with a RetailPrice and an optional
 * collection of discounts.
 *
 * TODO RF: Moving Calculations out of the Domain Class for better seperation of concerns.
 * TODO RF: Creating Jackson custom deserializers for Joda Money and Java 8 LocalDate to simplify
 * faking.
 *
 * @author Chris Baker <ignatz@gmail.com>
 */
public class Item implements Serializable {

  private static final long serialVersionUID = -6767025725779379377L;

  static final Logger LOG = LoggerFactory.getLogger(Item.class);

  static final int RED_PENCIL_MAX_DISCOUNT = 30;

  static final int RED_PENCIL_DAYS_WINDOW = 30;

  private final String sku;

  private final String name;

  private final Money cost;

  private final Set<RetailPrice> retailPrices = Collections
      .synchronizedSet(new HashSet<RetailPrice>());

  private final Set<Discount> discounts = Collections.synchronizedSet(new HashSet<Discount>());

  /**
   *
   * @param sku The unique identifier for the Item
   * @param name What it is called
   * @param cost The actual cost to the retailer for the Item
   * @param list The initial ListPrice to set the Item.
   */
  public Item(final String sku, final String name, final Money cost, final RetailPrice list) {
    this.sku = sku;
    this.name = name;
    this.cost = cost;
    this.retailPrices.add(list);
  }

  //\\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
  // ACCESSORS

  /**
   *
   * @return String
   */
  public final String getSku() {
    return this.sku;
  }

  /**
   *
   * @return String
   */
  public final String getName() {
    return this.name;
  }

  /**
   *
   * @return Money
   */
  public final Money getCost() {
    return this.cost;
  }

  /**
   * Commenting this out to simplify things, since it's not needed for the reqs.
   *
   * @param e A new ListPrice to add to the Item.
   */
  // public final void addListPrice(final RetailPrice e) {
  // this.getListPrices().add(e);
  // }

  /**
   *
   * @return List<ListPrice>
   */
  public final Set<RetailPrice> getRetailPrices() {
    return this.retailPrices;
  }

  /**
   * Adds a discount to the Item.
   *
   * @param discount
   */
  public final void addDiscount(final Discount discount) {
    this.getDiscounts().add(discount);
  }

  /**
   *
   *
   * @return A list of all the Discounts that have been set for this Item.
   */
  public final Set<Discount> getDiscounts() {
    return this.discounts;
  }

  // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
  // CALCULATIONS

  /**
   * Returns the current ListPrice as of now.
   *
   * @return What the Item is currently listing for.
   */
  public final RetailPrice determineCurrentRetailPrice() {
    return this.determineCurrentRetailPrice(LocalDate.now());
  }

  /**
   * Filters out RetailPrices that haven't gone into effect yet, sorts them by date, and then
   * returns the latest one.
   *
   * @param asOf
   * @return The current RetailPrice for the Item
   */
  public final RetailPrice determineCurrentRetailPrice(final LocalDate asOf) {
    if (this.getRetailPrices().size() == 1) {
      return this.getRetailPrices().iterator().next();
    }
    return this.getRetailPrices().stream().filter(p -> p.applies(asOf))
        .sorted(comparing(RetailPrice::getStartDate)).reduce((previous, current) -> current).get();
  }



  /**
   * Returns all of the Discounts that apply against the current date.
   *
   * @return
   */
  public final List<Discount> determineDiscountsThatApply() {
    return this.determineDiscountsThatApply(LocalDate.now());
  }

  /**
   * Returns all of the Discounts that apply against the supplied date.
   *
   * @param asOf
   * @return
   */
  public final List<Discount> determineDiscountsThatApply(final LocalDate asOf) {
    if (this.getDiscounts().size() < 1) {
      return new ArrayList<Discount>();
    }
    return this.getDiscounts().stream().filter(p -> p.applies(asOf))
        .sorted(comparing(Discount::getStartDate)).collect(toList());
  }

  public final Money applyDiscounts() {
    return this.applyDiscounts(LocalDate.now());
  }

  /**
   * TODO RF: For Loop :-P
   *
   * @param asOf
   * @return
   */
  public final Money applyDiscounts(final LocalDate asOf) {
    Money amount = this.determineCurrentRetailPrice(asOf).getMoney();
    if (this.getDiscounts().size() < 1) {
      return amount;
    }
    for (Discount discount : this.determineDiscountsThatApply(asOf)) {
      amount = discount.apply(amount);
    }
    return amount;
  }

  public final int determineCumulativeDiscount() {
    return this.determineCumulativeDiscount(LocalDate.now());
  }

  /**
   * Compare the value after discounts have been applied, compare it to the ListPrice, and determine
   * the cumulative discount's percentage.
   *
   * @param asOf
   * @return
   */
  public final int determineCumulativeDiscount(final LocalDate asOf) {
    BigDecimal list =
        this.determineCurrentRetailPrice(asOf).getMoney().getAmount()
        .setScale(2, RoundingMode.HALF_DOWN);
    BigDecimal discounted =
        this.applyDiscounts(asOf).getAmount().setScale(2, RoundingMode.HALF_DOWN);

    BigDecimal percentage = discounted.divide(list).setScale(2, RoundingMode.HALF_DOWN);

    return 100 - percentage.multiply(new BigDecimal(100)).intValue();
  }

  public final int determineLongestDiscountDaysCount() {
    return this.determineLongestDiscountDaysCount(LocalDate.now());
  }


  public final Optional<Discount> getEarliestDiscount() {
    return this.getEarliestDiscount(LocalDate.now());
  }

  public final Optional<Discount> getEarliestDiscount(final LocalDate asOf) {
    List<Discount> discounts = this.determineDiscountsThatApply(asOf);
    if (discounts.size() < 1) {
      return Optional.empty();
    }
    return Optional.of(discounts.get(0));
  }

  /**
   * Returns the longest daysInEffect for the current discount.
   *
   * @param asOf
   * @return
   */
  public final int determineLongestDiscountDaysCount(final LocalDate asOf) {
    Optional<Discount> discount = this.getEarliestDiscount(asOf);
    if (!discount.isPresent()) {
      return -1;
    }
    return (int) discount.get().daysInEffect(asOf);
  }

  //\\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
  // RED PENCIL CALCULATIONS

  public final boolean isWithinRedPencilWindow() {
    return this.isWithinRedPencilWindow(LocalDate.now());
  }

  public final boolean isWithinRedPencilWindow(final LocalDate asOf) {
    int longestDiscountDaysCount = this.determineLongestDiscountDaysCount(asOf);
    if (longestDiscountDaysCount < 0) {
      LOG.debug("Not Red Pencil. No discount in effect.");
      return false;
    }
    if (longestDiscountDaysCount > RED_PENCIL_DAYS_WINDOW) {
      LOG.debug("Not Red Pencil. Discount in effect for more than 30 days. ("
          + longestDiscountDaysCount + ")");
      return false;
    }
    return true;
  }

  public final boolean hasPriceIncrease() {
    return this.hasPriceIncrease(LocalDate.now());
  }

  public final boolean hasPriceIncrease(final LocalDate asOf) {
    List<Discount> discounts = this.determineDiscountsThatApply(asOf);
    if (discounts.size() < 1) {
      return false;
    }
    return discounts.stream().filter(d -> d.getEndDate() != null)
        .anyMatch(d -> d.getEndDate().isBefore(asOf));
  }

  public final boolean isGreaterThanMaxRedPencilDiscount() {
    return this.isGreaterThanMaxRedPencilDiscount(LocalDate.now());
  }

  public final boolean isGreaterThanMaxRedPencilDiscount(final LocalDate asOf) {
    int cumulativeDiscount = this.determineCumulativeDiscount(asOf);
    if (cumulativeDiscount > RED_PENCIL_MAX_DISCOUNT) {
      LOG.debug("cumulativeDiscount (" + cumulativeDiscount + ") > max discount ("
          + RED_PENCIL_MAX_DISCOUNT + ")");
      return true;
    }
    return false;
  }

  public final boolean hasAPreviousDiscountWithinRedPencilWindow() {
    return this.hasAPreviousDiscountWithinRedPencilWindow(LocalDate.now());
  }

  public final boolean hasAPreviousDiscountWithinRedPencilWindow(final LocalDate asOf) {
    Optional<Discount> discountOption = this.getEarliestDiscount(asOf);
    if (!discountOption.isPresent()) {
      return false;
    }
    Discount discount = discountOption.get();

    /**
     * TODO RF: Hack :-P
     */
    try {
      // Filter so that we only get a Discount that expires before the current discount window starts.
      Discount expired =
          this.getDiscounts().stream().filter(d -> d.getEndDate() != null)
          .filter(d -> d.getEndDate().isBefore(discount.getStartDate()))
          .sorted(comparing(Discount::getEndDate)).reduce((previous, current) -> current).get();

      if (expired == null) {
        return false;
      }
      int between = (int) ChronoUnit.DAYS.between(expired.getEndDate(), discount.getStartDate());

      return (between <= RED_PENCIL_DAYS_WINDOW);
    } catch (NoSuchElementException e) {
      return false;
    }


  }

  public final boolean isRetailPriceStable() {
    return this.isRetailPriceStable(LocalDate.now());
  }

  public final boolean isRetailPriceStable(final LocalDate asOf) {
    Optional<Discount> discount = this.getEarliestDiscount(asOf);
    int stability;
    if (!discount.isPresent()) {
      // No Discount is present, so we apply the test based upon how many days since the RetailPrice
      // was set.
      stability = (int) ChronoUnit.DAYS.between(this.determineCurrentRetailPrice(asOf).getStartDate(), asOf);
    } else {
      stability =
          (int) ChronoUnit.DAYS.between(this.determineCurrentRetailPrice(asOf).getStartDate(),
              discount.get().getStartDate());
    }
    return (stability > RED_PENCIL_DAYS_WINDOW);
  }

  public final boolean isRedPencil() {
    return this.isRedPencil(LocalDate.now());
  }

  /**
   * THE BIG KAHUNA
   *
   * @param asOf
   * @return
   */
  public final boolean isRedPencil(final LocalDate asOf) {

    if (!this.isWithinRedPencilWindow(asOf)) {
      return false;
    }

    if (this.hasPriceIncrease(asOf)) {
      return false;
    }

    if (this.isGreaterThanMaxRedPencilDiscount(asOf)) {
      return false;
    }

    if (this.hasAPreviousDiscountWithinRedPencilWindow(asOf)) {
      return false;
    }

    if (!this.isRetailPriceStable(asOf)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    StringBuffer buffie = new StringBuffer();

    buffie.append("Item: {sku: " + this.getSku() + ", name: " + this.getName() + ", RetailPrice: "
        + this.determineCurrentRetailPrice().getMoney() + "}");

    if (this.isRedPencil()) {
      buffie.append(" RED PENCIL!!!");
    }

    for (Discount discount : this.getDiscounts().stream().sorted(comparing(Discount::getStartDate))
        .collect(toList())) {
      buffie.append("\n\tDiscount: " + discount);
    }

    return buffie.toString();
  }

  public static void main(String... args) {

  }
}
