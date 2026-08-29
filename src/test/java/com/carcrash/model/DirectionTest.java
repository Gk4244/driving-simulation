package com.carcrash.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DirectionTest {

    @Test
    void turningRightCyclesClockwise() {
        assertThat(Direction.N.turnRight()).isEqualTo(Direction.E);
        assertThat(Direction.E.turnRight()).isEqualTo(Direction.S);
        assertThat(Direction.S.turnRight()).isEqualTo(Direction.W);
        assertThat(Direction.W.turnRight()).isEqualTo(Direction.N);
    }

    @Test
    void turningLeftCyclesCounterClockwise() {
        assertThat(Direction.N.turnLeft()).isEqualTo(Direction.W);
        assertThat(Direction.W.turnLeft()).isEqualTo(Direction.S);
        assertThat(Direction.S.turnLeft()).isEqualTo(Direction.E);
        assertThat(Direction.E.turnLeft()).isEqualTo(Direction.N);
    }

    @Test
    void movementVectorsMatchCompassDirection() {
        assertThat(Direction.N.dx()).isEqualTo(0);
        assertThat(Direction.N.dy()).isEqualTo(1);
        assertThat(Direction.S.dy()).isEqualTo(-1);
        assertThat(Direction.E.dx()).isEqualTo(1);
        assertThat(Direction.W.dx()).isEqualTo(-1);
    }

    @Test
    void fromSymbolIsCaseInsensitive() {
        assertThat(Direction.fromSymbol("n")).isEqualTo(Direction.N);
        assertThat(Direction.fromSymbol("W")).isEqualTo(Direction.W);
    }

    @Test
    void fromSymbolRejectsInvalidInput() {
        assertThatThrownBy(() -> Direction.fromSymbol("X"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
