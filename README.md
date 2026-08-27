# CS4200 — Artificial Intelligence

Coursework projects for CS4200 (Artificial Intelligence) at Cal Poly Pomona: classical
search, local search, and adversarial search, each implemented from scratch with no AI
libraries. Every project is an interactive console program that also reports search cost
and runtime so the algorithms can be benchmarked against each other.

| Project | Problem | Techniques | Language |
|---|---|---|---|
| [project1](project1) | 8-Puzzle | A\* search, two admissible heuristics, BFS state-space map | Java |
| [project2](project2) | n-Queens | Steepest-ascent hill climbing, min-conflicts | Java |
| [project3](project3) | Connect Four (8×8) | Iterative-deepening minimax, alpha-beta pruning, move ordering | C++ |

## Project 1 — 8-Puzzle solver

A\* over the 8-puzzle state space, with `f = g + h` ordering in a priority queue,
parent-pointer path reconstruction, and duplicate-state handling that keeps the lowest
`g` seen per board.

- **h1** — number of misplaced tiles.
- **h2** — Manhattan distance of each tile from its goal position.
- A breadth-first sweep of the reachable state space precomputes a board → optimal-depth
  map, so test boards can be drawn at a requested solution depth (2–20).
- Boards are validated with an inversion-parity solvability check before search.
- Single-board mode prints the solution path, search cost, and search time per heuristic;
  multi-test mode reports the averages used to compare h1 against h2.

```bash
javac project1/Puzzle.java
java project1.Puzzle
```

Boards are entered row by row with `0` as the blank:

```
1 2 5
3 4 8
6 7 0
```

Sample runs at fixed depths are in `project1/Length{4,8,12,16,20}.txt` and `project1/output.txt`.

## Project 2 — n-Queens local search

Two local-search strategies on a randomly initialized board of `n` queens (one per column),
scored by the number of attacking pairs.

- **Steepest ascent** — evaluates every single-queen move and takes the one that most
  reduces total attacks.
- **Min-conflicts** — picks a random conflicted queen and moves it to its least-conflicting
  square, breaking ties at random, bounded by a user-supplied max-steps limit.
- Multi-test mode reports completion rate, average steps, and average runtime, which is how
  the two strategies were compared.

```bash
javac project2/nQueen.java
java project2.nQueen
```

Recorded runs for n = 8, 12, and 16 are in `project2/SteepestAscentOutputs/` and
`project2/MinConflictOutputs/`.

## Project 3 — Connect Four game agent

A human-vs-computer Connect Four variant on an 8×8 board where a piece may be placed on any
empty square adjacent to an existing piece (no gravity). Four in a row horizontally or
vertically wins.

- **Iterative deepening** to depth 8, so the agent always has a usable move when its clock
  expires, with a user-chosen time limit of 5–30 seconds enforced inside the search.
- **Alpha-beta pruning** over the minimax tree.
- **Move ordering** — candidate moves are generated only next to existing pieces, then
  sorted by immediate win/block potential, neighbor density, and distance to center, with
  the previous iteration's best move searched first.
- **Evaluation** scores every horizontal and vertical four-square window and rewards central
  placement.

```bash
g++ -std=c++17 project3/game.cpp -o project3/game
./project3/game
```

Sample games are in `project3/output{1,2,3,4}.txt`.
