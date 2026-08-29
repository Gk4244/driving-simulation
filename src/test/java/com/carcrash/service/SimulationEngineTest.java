package com.carcrash.service;

import com.carcrash.model.Car;
import com.carcrash.model.Direction;
import com.carcrash.model.Field;
import com.carcrash.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulationEngineTest {

    private SimulationEngine engine;
    private Field field;

    @BeforeEach
    void setUp() {
        engine = new SimulationEngine();
        field = new Field(10, 10);
    }

    @Test
    void scenario1_singleCarEndsAtExpectedPositionAndDirection() {
        // From the problem statement: A at (1,2) facing N, commands FFRFFFFRRL
        // expected result: A ends at (5,4) facing S.
        Car a = new Car("A", new Position(1, 2), Direction.N, "FFRFFFFRRL");

        engine.simulate(field, List.of(a));

        assertThat(a.getPosition()).isEqualTo(new Position(5, 4));
        assertThat(a.getDirection()).isEqualTo(Direction.S);
        assertThat(a.isCollided()).isFalse();
    }

    @Test
    void scenario2_twoCarsCollideAtStepSevenPositionFiveFour() {
        // From the problem statement's multi-car example.
        Car a = new Car("A", new Position(1, 2), Direction.N, "FFRFFFFRRL");
        Car b = new Car("B", new Position(7, 8), Direction.W, "FFLFFFFFFF");

        engine.simulate(field, List.of(a, b));

        assertThat(a.isCollided()).isTrue();
        assertThat(a.getPosition()).isEqualTo(new Position(5, 4));
        assertThat(a.getCollisionStep()).isEqualTo(7);
        assertThat(a.getCollidedWith()).containsExactly("B");

        assertThat(b.isCollided()).isTrue();
        assertThat(b.getPosition()).isEqualTo(new Position(5, 4));
        assertThat(b.getCollisionStep()).isEqualTo(7);
        assertThat(b.getCollidedWith()).containsExactly("A");
    }

    @Test
    void carIgnoresForwardCommandThatWouldLeaveTheField() {
        // At (0,0) facing South, F would go to y=-1, which is out of bounds.
        Car a = new Car("A", new Position(0, 0), Direction.S, "F");

        engine.simulate(field, List.of(a));

        assertThat(a.getPosition()).isEqualTo(new Position(0, 0));
        assertThat(a.getDirection()).isEqualTo(Direction.S);
    }

    @Test
    void carContinuesExecutingCommandsAfterAnIgnoredMove() {
        // Off-the-edge F is ignored, but subsequent commands still run.
        Car a = new Car("A", new Position(0, 0), Direction.S, "FRF");

        engine.simulate(field, List.of(a));

        // F ignored (would leave field), R -> facing W, F ignored (would leave field, x=-1)
        assertThat(a.getPosition()).isEqualTo(new Position(0, 0));
        assertThat(a.getDirection()).isEqualTo(Direction.W);
    }

    @Test
    void threeCarsCanAllCollideAtTheSamePointOnTheSameStep() {
        Car a = new Car("A", new Position(0, 5), Direction.E, "F");
        Car b = new Car("B", new Position(2, 5), Direction.W, "F");
        Car c = new Car("C", new Position(1, 5), Direction.N, "");

        engine.simulate(field, List.of(a, b, c));

        assertThat(a.isCollided()).isTrue();
        assertThat(b.isCollided()).isTrue();
        assertThat(c.isCollided()).isTrue();
        assertThat(a.getCollidedWith()).containsExactlyInAnyOrder("B", "C");
        assertThat(b.getCollidedWith()).containsExactlyInAnyOrder("A", "C");
        assertThat(c.getCollidedWith()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void carFreezesAfterCollisionAndDoesNotExecuteFurtherCommands() {
        Car a = new Car("A", new Position(0, 0), Direction.E, "FF");
        Car b = new Car("B", new Position(1, 0), Direction.W, "LL"); // stays put, just rotates

        // Step 1: A moves E to (1,0) -> collides with B (which hasn't moved). A freezes.
        engine.simulate(field, List.of(a, b));

        assertThat(a.isCollided()).isTrue();
        assertThat(a.getCollisionStep()).isEqualTo(1);
        // A's second F command must never execute since it's frozen after step 1.
        assertThat(a.getPosition()).isEqualTo(new Position(1, 0));
    }

    @Test
    void carAtStartingPositionOutsideFieldFailsFast() {
        Car a = new Car("A", new Position(20, 20), Direction.N, "F");

        assertThatThrownBy(() -> engine.simulate(field, List.of(a)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void simulateWithNoCarsIsANoOp() {
        assertThatCode(() -> engine.simulate(field, List.of())).doesNotThrowAnyException();
    }
}
