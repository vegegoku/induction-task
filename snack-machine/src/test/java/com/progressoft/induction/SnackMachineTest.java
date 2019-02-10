package com.progressoft.induction;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.progressoft.induction.SnackMachine.DEFAULT_QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SnackMachineTest {

    private SnackMachine snackMachine;

    @BeforeEach
    void setUp() {
        snackMachine = new SnackMachine();
    }

    @Test
    void new_snack_machine_should_have_zero_money() {
        assertThat(snackMachine.moneyInside()).isEqualTo(Money.ZERO);
    }

    @Test
    void when_insert_money_then_money_in_transaction_should_be_equal_to_the_money_inserted() {
        snackMachine.insertMoney(Money.QUARTER_DINAR);
        snackMachine.insertMoney(Money.HALF_DINAR);
        snackMachine.insertMoney(Money.DINAR);

        assertThat(snackMachine.moneyInside()).isEqualTo(Money.ZERO);
        assertThat(snackMachine.moneyInTransaction()).isEqualTo(new Money(BigDecimal.valueOf(1.75)));
    }

    @ParameterizedTest(name = "Insert predefined money {arguments} is accepted")
    @ValueSource(doubles = {0.25, 0.5, 1.0, 5.0, 10.0})
    void snack_machine_accepts_predefined_money_units(double amount) {
        snackMachine.insertMoney(new Money(BigDecimal.valueOf(amount)));
    }

    @ParameterizedTest(name = "Insert non predefined money {arguments} is not accepted")
    @ValueSource(doubles = {0.0, 0.01, 0.05, 0.10, 20.0, 50.0})
    void snack_machine_does_not_accept_non_predefined_money_units(double amount) {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                snackMachine.insertMoney(new Money(BigDecimal.valueOf(amount)));
            }
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insert_null_money_should_fail() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                snackMachine.insertMoney(null);
            }
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void new_snack_machine_should_have_three_types_of_snacks() {
        assertThat(snackMachine.chewingGums().quantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(snackMachine.chips().quantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(snackMachine.chocolates().quantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    void buy_without_inserting_money_should_fail() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                snackMachine.buySnack(SnackType.CHEWING_GUM);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void buying_a_snack_after_inserting_just_enough_money_then_the_money_inside_should_equals_to_money_inserted() {
        snackMachine.insertMoney(Money.QUARTER_DINAR);
        snackMachine.insertMoney(Money.QUARTER_DINAR);

        snackMachine.buySnack(SnackType.CHEWING_GUM);

        assertThat(snackMachine.moneyInTransaction()).isEqualTo(Money.ZERO);
        assertThat(snackMachine.moneyInside()).isEqualTo(Money.HALF_DINAR);
    }

    @Test
    void buying_a_snack_after_inserting_just_enough_money_then_snack_quantity_should_be_decreased() {
        snackMachine.insertMoney(Money.QUARTER_DINAR);
        snackMachine.insertMoney(Money.QUARTER_DINAR);

        snackMachine.buySnack(SnackType.CHEWING_GUM);

        snackMachine.insertMoney(Money.HALF_DINAR);
        snackMachine.insertMoney(Money.HALF_DINAR);

        snackMachine.buySnack(SnackType.CHIPS);

        snackMachine.insertMoney(Money.DINAR);
        snackMachine.insertMoney(Money.DINAR);

        snackMachine.buySnack(SnackType.CHOCOLATE);

        assertThat(snackMachine.chewingGums().quantity()).isEqualTo(DEFAULT_QUANTITY - 1);
        assertThat(snackMachine.chips().quantity()).isEqualTo(DEFAULT_QUANTITY - 1);
        assertThat(snackMachine.chocolates().quantity()).isEqualTo(DEFAULT_QUANTITY - 1);
    }

    @Test
    void buying_unavailable_quantity_of_a_snack_should_fail() {
        while (snackMachine.chewingGums().quantity() > 0) {
            snackMachine.insertMoney(Money.QUARTER_DINAR);
            snackMachine.insertMoney(Money.QUARTER_DINAR);

            snackMachine.buySnack(SnackType.CHEWING_GUM);
        }

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                snackMachine.insertMoney(Money.QUARTER_DINAR);
                snackMachine.insertMoney(Money.QUARTER_DINAR);

                snackMachine.buySnack(SnackType.CHEWING_GUM);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void buying_a_snack_after_inserting_money_less_than_snack_price_should_fail() {
        snackMachine.insertMoney(Money.QUARTER_DINAR);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                snackMachine.buySnack(SnackType.CHEWING_GUM);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void buying_a_snack_after_inserting_money_more_than_snack_price_should_return_change() {
        snackMachine.insertMoney(Money.DINAR);

        Money change = snackMachine.buySnack(SnackType.CHEWING_GUM);

        assertThat(change).isEqualTo(Money.HALF_DINAR);
    }

    @Test
    void buying_a_snack_after_inserting_money_equal_to_snack_price_should_return_zero_change() {
        snackMachine.insertMoney(Money.HALF_DINAR);

        Money change = snackMachine.buySnack(SnackType.CHEWING_GUM);

        assertThat(change).isEqualTo(Money.ZERO);
    }
}
