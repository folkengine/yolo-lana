package com.electronicpanopticon.demo.yolo;

import java.time.LocalDate;

/**
 * An ajustment to the price of an item, either by a fixed amount or a percentage. Sets the date in
 * which it goes in effect and provides methods to determine how long it has been in effect.
 *
 * @author Chris Baker <ignatz@gmail.com>
 */
public abstract class PriceAdjustment {

  private final LocalDate startDate;

  /**
   *
   * @param startDate When the adjustment goes into effect.
   */
  public PriceAdjustment(final LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * The date in which the ListPrice goes into effect.
   *
   * @return LocalDate
   */
  public final LocalDate getStartDate() {
    return this.startDate;
  }

  /**
   * Determines how many days a ListPrice has been in effect against the current LocalDate.
   *
   * @return long
   */
  public final long daysInEffect() {
    return this.daysInEffect(LocalDate.now());
  }

  /**
   * Determines how many days a ListPrice has been in effect against an arbitrary end date.
   *
   * @param comparison LocalDate to compare against.
   * @return long
   */
  public abstract long daysInEffect(final LocalDate comparison);


  /**
   * Verifies that a discount applies given the passed in date.
   *
   * @param comparison LocalDate to compare against.
   * @return Does the Discount apply given the passed in date.
   */
  public final boolean applies(final LocalDate comparison) {
    return (this.daysInEffect(comparison) >= 0);
  }
}
