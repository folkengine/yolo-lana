package com.electronicpanopticon.demo.yolo;

import java.math.RoundingMode;
import java.text.NumberFormat;

import org.joda.money.Money;

/**
 * Enumeration containing standard Discount percentages used by Discount Objects.
 *
 * @author Chris Baker <ignatz@gmail.com>
 *
 */
public enum DiscountPercent {

  FIVE(0.05d), TEN(0.1d), FIFTEEN(0.15d), TWENTY(0.2d), TWENTYFIVE(0.25d), THIRTY(0.30d), THIRTYFIVE(
      0.35d), FORTY(0.4d), FORTYFIVE(0.45d), FIFTY(0.5d), FIFTYFIVE(0.55d), SIXTY(0.6d), SEVENTYFIVE(
          0.75d);

  /**
   * The actual discount amount as a decimal.
   */
  final Double percent;

  /**
   * Private Enum Constructor.
   *
   * @param percent Sets the actual discount amount.
   */
  private DiscountPercent(final double percent) {
    this.percent = percent;
  }

  /**
   *
   * @return the actual percent value.
   */
  public Double getPercent() {
    return this.percent;
  }

  /**
   *
   * @return The amount formatted as a percentage.
   */
  public String formatted() {
    return NumberFormat.getPercentInstance().format(this.percent);
  }

  /**
   * Apply the discount against a specific amount of money.
   *
   * @param initial The starting amount to apply the discount against.
   * @return Returns the amount after the discount has been applied.
   */
  public Money apply(final Money initial) {
    return initial.multipliedBy((1.0 - this.percent), RoundingMode.HALF_DOWN);
  }
}
