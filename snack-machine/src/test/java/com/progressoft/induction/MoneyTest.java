package com.progressoft.induction;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyTest {

    @Test
    void new_money_with_negative_value_should_fail() {
        assertThatThrownBy(() -> new Money(BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_two_money_should_be_correct() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money halfDinar = new Money(BigDecimal.valueOf(0.5));

        Money expected = oneDinar.add(halfDinar);
        assertThat(expected).isEqualTo(new Money(BigDecimal.valueOf(1.5)));
    }

    @Test
    void comparing_money_is_less_than_null_should_be_false() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));

        assertThat(oneDinar.isLessThan(null)).isFalse();
    }

    @Test
    void comparing_money_is_less_than_bigger_money_should_be_false() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money twoDinar = new Money(BigDecimal.valueOf(2));

        assertThat(oneDinar.isLessThan(twoDinar)).isTrue();
    }

    @Test
    void comparing_money_is_less_than_smaller_money_should_be_true() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money halfDinar = new Money(BigDecimal.valueOf(0.5));

        assertThat(oneDinar.isLessThan(halfDinar)).isFalse();
    }

    @Test
    void comparing_money_is_less_than_same_amount_of_money_should_be_false() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money anotherOneDinar = new Money(BigDecimal.valueOf(1));

        assertThat(oneDinar.isLessThan(anotherOneDinar)).isFalse();
    }

    @Test
    void subtract_two_money_should_be_correct() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money halfDinar = new Money(BigDecimal.valueOf(0.5));

        Money expected = oneDinar.subtract(halfDinar);
        assertThat(expected).isEqualTo(new Money(BigDecimal.valueOf(0.5)));
    }

    @Test
    void subtract_money_from_smaller_money_should_fail() {
        Money oneDinar = new Money(BigDecimal.valueOf(1));
        Money halfDinar = new Money(BigDecimal.valueOf(0.5));

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                halfDinar.subtract(oneDinar);
            }
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
