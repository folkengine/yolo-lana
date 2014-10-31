package com.electronicpanopticon.demo.yolo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.joda.money.Money;

/**
 *
 * @author Chris Baker <ignatz@gmail.com>
 *
 */
public class Discount extends PriceAdjustment {

  private final DiscountPercent percent;

  private final LocalDate endDate;

  /**
   * Constructor that has no end date.
   *
   * @param percent The percentage amount of the discount
   * @param startDate The date in which a Discount goes into effect
   */
  public Discount(final DiscountPercent percent, final LocalDate startDate) {
    super(startDate);
    this.percent = percent;
    this.endDate = null;
  }

  /**
   * Constructor with a start and end date.
   *
   * @param percent The percentage amount of the discount
   * @param startDate The date in which a Discount goes into effect
   * @param endDate The date in which the Discount ends
   */
  public Discount(final DiscountPercent percent, final LocalDate startDate,
      final LocalDate endDate) {
    super(startDate);
    this.percent = percent;
    this.endDate = endDate;
  }

  /**
   *
   * @return double The percentage of the discount
   */
  public final DiscountPercent getPercent() {
    return this.percent;
  }

  /**
   * TODO RF: Rewrite with Optional<LocalDate>
   *
   * @return the LocalDate in which the descount ends
   */
  public final LocalDate getEndDate() {
    return this.endDate;
  }

  /**
   * @param comparison LocalDate to compare against.
   * @return long Will return a negative value if the comparison date is outside the discount's
   *         range.
   */
  @Override
  public final long daysInEffect(final LocalDate comparison) {
    if ((this.endDate != null) && comparison.isAfter(this.getEndDate())) {
      return ChronoUnit.DAYS.between(comparison, this.getEndDate());
    }
    return ChronoUnit.DAYS.between(this.getStartDate(), comparison);
  }

  /**
   * Applies the discount to the amount passed in.
   *
   * This method assumes that the caller has already verified that the discount applies.
   *
   * @param initial The starting ammount.
   * @return The amount after the discount has been applied.
   */
  public final Money apply(final Money initial) {
    return this.getPercent().apply(initial);
  }

  @Override
  public String toString() {
    String s = this.getPercent().formatted() + " discount starts " + this.getStartDate();
    if (this.getEndDate() != null) {
      return s + ", ends " + this.getEndDate() + ".";
    } else {
      return s + ".";
    }
  }

}
