package com.electronicpanopticon.demo.yolo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.joda.money.Money;

/**
 *
 * @author Chris Baker <ignatz@gmail.com>
 */
public class RetailPrice extends PriceAdjustment {

  private final Money money;

  /**
   *
   * @param amount How much an Item is listed as costing to a consumer.
   * @param date When this ListPrice goes into effect
   */
  public RetailPrice(final Money money, final LocalDate date) {
    super(date);
    this.money = money;
  }

  /**
   *
   * @return Money
   */
  public final Money getMoney() {
    return this.money;
  }

  /**
   * Determines how many days a RetailPrice has been in effect against an arbitrary end date. If the
   * comparison date is before the startdate it will return a negative number.
   *
   * @param comparison LocalDate to compare against.
   * @return long
   */
  @Override
  public final long daysInEffect(final LocalDate comparison) {
    return ChronoUnit.DAYS.between(this.getStartDate(), comparison);
  }

}
