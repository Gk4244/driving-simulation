# Driving Simulation (Car Crash)

A Spring Boot backend + a small browser UI for the car-crash driving simulation assignment.

## How to run

```
./start.sh
```

That's it — no manual `mvn install`, no separate frontend build. `start.sh`:

1. Uses `mvn` if it's already on your `PATH`; otherwise it downloads a local copy of Maven
   into `.maven-local/` (requires internet access, as the assignment says is available).
2. Runs a **fresh** `mvn clean package` — nothing is cached or reused between runs.
3. Starts the resulting jar with `java -jar`.

Once you see `Starting the Driving Simulation server...`, open **http://localhost:8080** in a browser.

Every run is a brand-new JVM process with an empty, in-memory field and car list — stop it
(`Ctrl+C`) and run `./start.sh` again and you'll start from a completely clean slate, as required.

### Using the UI

1. **Create the field** — enter width and height, click "Create field".
2. **Add cars** — for each car, enter its name, starting x/y, starting direction, and its
   command string (only `L`, `R`, `F`, case-insensitive), click "Add car". Repeat for as many
   cars as you like. The table below updates live.
3. **Run simulation** — click "Run simulation" to execute every car's commands and see the
   final position/direction of each car, or which car(s) it collided with, at which step, and where.
4. **Start over** — clears everything (field + cars) and returns to step 1.

### Using the REST API directly (e.g. via curl)

```
curl -X POST localhost:8080/api/field   -H 'Content-Type: application/json' -d '{"width":10,"height":10}'
curl -X POST localhost:8080/api/cars    -H 'Content-Type: application/json' -d '{"name":"A","x":1,"y":2,"direction":"N","commands":"FFRFFFFRRL"}'
curl -X POST localhost:8080/api/cars    -H 'Content-Type: application/json' -d '{"name":"B","x":7,"y":8,"direction":"W","commands":"FFLFFFFFFF"}'
curl -X GET  localhost:8080/api/cars
curl -X POST localhost:8080/api/simulate
curl -X POST localhost:8080/api/reset
```

### Running the tests

```
mvn test
```

(Or, on first run, `./.maven-local/apache-maven-*/bin/mvn test` if Maven had to be bootstrapped.)

## Project layout

```
src/main/java/com/carcrash/
  model/       Direction, Position, Field, Car  — pure domain classes, no framework dependency
  service/     SimulationEngine (the actual simulation algorithm) + SimulationService (in-memory state)
  web/         SimulationController (REST API) + request/response DTOs
  exception/   GlobalExceptionHandler — turns bad input into clean 400/409 JSON, not stack traces
src/main/resources/static/   index.html, app.js, style.css — the browser UI
src/test/java/               unit tests for the engine + rotation logic, plus a MockMvc test of the full API flow
```

`SimulationEngine` has zero Spring dependencies by design — it's plain Java, so the core
simulation logic (the part actually being assessed) is trivially unit-testable and reusable
outside of a web context (e.g. from a CLI) if needed.

## Design decisions & assumptions

- **Interface choice**: the assignment allows CLI or browser UI. I built a small Spring Boot
  REST backend with a plain HTML/JS/CSS frontend (no Node/React build step) so the whole thing
  is a single Java application, single `start.sh`, and demonstrates both frontend and backend
  work in one submission.
- **Lock-step command execution**: commands are executed one "tick" at a time across *all*
  cars simultaneously (every car's 1st command runs, then every car's 2nd command, etc.),
  rather than running one car's entire command string before moving to the next car. This is
  required for "collides at step 7" to be a meaningful, well-defined statement — otherwise
  step numbers would depend on car processing order.
- **Collision definition**: two (or more) cars are considered collided if they occupy the
  same cell *after* a tick. I deliberately do **not** detect cars that swap or "pass through"
  each other's positions within the same tick without ever sharing a cell — the problem
  statement's example only covers the same-cell case, so I kept the simpler, unambiguous
  definition rather than guessing at swap semantics.
- **Frozen after collision**: once a car has collided, it stops executing any further
  commands (it's wrecked), but it remains on the field, so a car crashing into it later is
  still detected and reported. Non-collided cars keep running their remaining commands.
- **Ignored moves**: an `F` that would take a car outside the field boundary is silently
  ignored (the car stays put) and the car continues on to its next command — it isn't treated
  as an error or a stopping condition.
- **Unequal command lengths**: cars aren't required to have the same number of commands. A
  car simply does nothing once it runs out of commands, while other cars keep going.
- **Duplicate starting positions**: rejected up front as an invalid setup (`400 Bad Request`)
  rather than treated as an instant collision at step 0 — the problem statement doesn't
  describe this case, and refusing it avoids an ambiguous "step 0 collision" concept.
- **Validation**: field dimensions must be positive; a car's name must be unique and
  non-empty; its starting position must be inside the field; its command string may only
  contain `L`, `R`, `F` (case-insensitive) and may be empty (a car that never moves is valid).
- **State/persistence**: everything lives in a single in-memory Spring `@Service` bean.
  Nothing is written to disk or a database, so a fresh process (i.e. every `start.sh` run)
  always starts empty — this satisfies the "clean start every run" requirement without needing
  any explicit cleanup step.
- **No external simulation libraries**: the engine (rotation, movement, boundary checks,
  collision detection) is all hand-written in `SimulationEngine`/`Direction`/`Car`. The only
  third-party dependency is `spring-boot-starter-web`, which is just plumbing (HTTP server,
  JSON (de)serialization) — it doesn't solve any part of the actual simulation problem.
