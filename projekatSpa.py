#!/usr/bin/env python3
"""
Dynamic Bureaucrat Simulation - Agentni sistem na šahovskoj tabli n×n.

Figura „Бирократ":
  - Može biti postavljena na bilo koje polje B = {1,...,n} × {1,...,n}
  - Ne napada nijedno polje osim svog: A(p) = {p}
  - Polje koje zauzima je administrativno blokirano
  - Ako je eliminisan, prelazi u kontrolu protivnika i biva ponovo postavljen
"""

import random
import os
from collections import defaultdict
from dataclasses import dataclass, field
from typing import Optional


# ─────────────────────────────────────────────────────────────
# KLASA: Bureaucrat (agent)
# ─────────────────────────────────────────────────────────────
class Bureaucrat:
    """
    Pojedinačni бирократ-agent na tabli.

    Atributi:
        id       - jedinstveni identifikator
        player   - vlasnik (1 ili 2)
        position - (red, kolona) na tabli
        alive    - da li je aktivan na tabli
    """
    _id_counter = 0

    def __init__(self, player: int, position: tuple[int, int]):
        Bureaucrat._id_counter += 1
        self.id = Bureaucrat._id_counter
        self.player = player
        self.position = position
        self.alive = True
        self.moves_made = 0
        self.times_eliminated = 0

    def get_adjacent_fields(self, board_size: int) -> list[tuple[int, int]]:
        """Vraća listu validnih susednih polja (kralj-kretanje: 8 smerova)."""
        r, c = self.position
        adjacent = []
        for dr in (-1, 0, 1):
            for dc in (-1, 0, 1):
                if dr == 0 and dc == 0:
                    continue
                nr, nc = r + dr, c + dc
                if 0 <= nr < board_size and 0 <= nc < board_size:
                    adjacent.append((nr, nc))
        return adjacent

    def choose_move(self, board: "Board") -> tuple[int, int]:
        """
        Ograničeno ponašanje agenta:
        - 70% šansa da se pomeri na nasumično slobodno susedno polje
        - 30% šansa da ostane na mestu (birokratska inercija)
        - Ako nema slobodnih susednih polja, ostaje na mestu
        """
        if random.random() < 0.30:
            return self.position  # birokratska inercija

        adjacent = self.get_adjacent_fields(board.n)
        free_fields = [pos for pos in adjacent if board.is_free(pos)]

        if free_fields:
            return random.choice(free_fields)
        return self.position  # nema slobodnog mesta

    def __repr__(self):
        status = "AKTIVAN" if self.alive else "NEAKTIVAN"
        return (f"Бирократ #{self.id} [Igrač {self.player}] "
                f"pos={self.position} {status}")


# ─────────────────────────────────────────────────────────────
# KLASA: Board (tabla n×n)
# ─────────────────────────────────────────────────────────────
class Board:
    """
    Šahovska tabla dimenzija n×n.

    Svako polje može biti:
    - None (slobodno)
    - referenca na Bureaucrat objekat (administrativno blokirano)
    """

    def __init__(self, n: int):
        self.n = n
        self.grid: list[list[Optional[Bureaucrat]]] = [
            [None for _ in range(n)] for _ in range(n)
        ]

    def is_valid(self, r: int, c: int) -> bool:
        return 0 <= r < self.n and 0 <= c < self.n

    def is_free(self, pos: tuple[int, int]) -> bool:
        r, c = pos
        return self.is_valid(r, c) and self.grid[r][c] is None

    def place(self, bureaucrat: Bureaucrat) -> bool:
        """Postavlja бирократа na tablu. Vraća True ako uspešno."""
        r, c = bureaucrat.position
        if self.is_valid(r, c):
            self.grid[r][c] = bureaucrat
            return True
        return False

    def remove(self, bureaucrat: Bureaucrat):
        """Uklanja бирократа sa table."""
        r, c = bureaucrat.position
        if self.is_valid(r, c) and self.grid[r][c] is bureaucrat:
            self.grid[r][c] = None

    def get_occupant(self, pos: tuple[int, int]) -> Optional[Bureaucrat]:
        r, c = pos
        if self.is_valid(r, c):
            return self.grid[r][c]
        return None

    def count_blocked(self) -> int:
        """Broji ukupan broj administrativno blokiranih polja."""
        count = 0
        for r in range(self.n):
            for c in range(self.n):
                if self.grid[r][c] is not None:
                    count += 1
        return count

    def get_all_free_positions(self) -> list[tuple[int, int]]:
        """Vraća listu svih slobodnih pozicija na tabli."""
        free = []
        for r in range(self.n):
            for c in range(self.n):
                if self.grid[r][c] is None:
                    free.append((r, c))
        return free

    def display(self, step: int = -1):
        """Tekstualni prikaz table sa bojama igrača."""
        header = f"  Korak: {step}" if step >= 0 else ""
        print(f"\n{'═' * (self.n * 4 + 3)} {header}")

        # Zaglavlje kolona
        print("    ", end="")
        for c in range(self.n):
            print(f" {c:2} ", end="")
        print()
        print("   ┌" + "───┬" * (self.n - 1) + "───┐")

        for r in range(self.n):
            print(f" {r:1} │", end="")
            for c in range(self.n):
                occupant = self.grid[r][c]
                if occupant is None:
                    print(" · │", end="")
                elif occupant.player == 1:
                    print(" ① │", end="")
                else:
                    print(" ② │", end="")
            print()
            if r < self.n - 1:
                print("   ├" + "───┼" * (self.n - 1) + "───┤")

        print("   └" + "───┴" * (self.n - 1) + "───┘")
        print(f"{'═' * (self.n * 4 + 3)}")


# ─────────────────────────────────────────────────────────────
# DATACLASS: SimulationStats (statistička analiza)
# ─────────────────────────────────────────────────────────────
@dataclass
class SimulationStats:
    """Prikupljanje i analiza statistike simulacije."""
    blocked_per_step: list[int] = field(default_factory=list)
    collisions_per_step: list[int] = field(default_factory=list)
    player1_count_per_step: list[int] = field(default_factory=list)
    player2_count_per_step: list[int] = field(default_factory=list)
    total_collisions: int = 0
    total_eliminations_p1: int = 0  # koliko puta je igrač 1 izgubio бирократа
    total_eliminations_p2: int = 0
    total_transfers_to_p1: int = 0  # koliko бирократа je prešlo igraču 1
    total_transfers_to_p2: int = 0

    def record_step(self, blocked: int, collisions: int,
                    p1_count: int, p2_count: int):
        self.blocked_per_step.append(blocked)
        self.collisions_per_step.append(collisions)
        self.player1_count_per_step.append(p1_count)
        self.player2_count_per_step.append(p2_count)

    def avg_blocked(self) -> float:
        if not self.blocked_per_step:
            return 0.0
        return sum(self.blocked_per_step) / len(self.blocked_per_step)

    def avg_collisions(self) -> float:
        if not self.collisions_per_step:
            return 0.0
        return sum(self.collisions_per_step) / len(self.collisions_per_step)

    def max_blocked(self) -> int:
        return max(self.blocked_per_step) if self.blocked_per_step else 0

    def min_blocked(self) -> int:
        return min(self.blocked_per_step) if self.blocked_per_step else 0

    def display_summary(self, total_steps: int, board_size: int):
        total_fields = board_size * board_size
        print("\n" + "█" * 60)
        print("█  STATISTIČKA ANALIZA STABILNOSTI SISTEMA")
        print("█" * 60)
        print(f"│")
        print(f"│  Tabla: {board_size}×{board_size} "
              f"({total_fields} polja ukupno)")
        print(f"│  Simuliranih koraka: {total_steps}")
        print(f"│")
        print(f"├── BLOKIRANA POLJA ────────────────────────")
        print(f"│   Prosečno blokiranih po koraku: "
              f"{self.avg_blocked():.2f} / {total_fields} "
              f"({self.avg_blocked() / total_fields * 100:.1f}%)")
        print(f"│   Minimum blokiranih:            {self.min_blocked()}")
        print(f"│   Maksimum blokiranih:            {self.max_blocked()}")
        print(f"│")
        print(f"├── KOLIZIJE I ELIMINACIJE ─────────────────")
        print(f"│   Ukupno kolizija:               {self.total_collisions}")
        print(f"│   Prosečno kolizija po koraku:   "
              f"{self.avg_collisions():.3f}")
        print(f"│   Eliminacija igrača 1:          "
              f"{self.total_eliminations_p1}")
        print(f"│   Eliminacija igrača 2:          "
              f"{self.total_eliminations_p2}")
        print(f"│")
        print(f"├── TRANSFERI KONTROLE ─────────────────────")
        print(f"│   Бирократи prešli igraču 1:     "
              f"{self.total_transfers_to_p1}")
        print(f"│   Бирократи prešli igraču 2:     "
              f"{self.total_transfers_to_p2}")
        print(f"│")
        print(f"├── REDISTRIBUCIJA (kraj simulacije) ───────")
        if self.player1_count_per_step:
            final_p1 = self.player1_count_per_step[-1]
            final_p2 = self.player2_count_per_step[-1]
            init_p1 = self.player1_count_per_step[0]
            init_p2 = self.player2_count_per_step[0]
            print(f"│   Igrač 1: {init_p1} → {final_p1} бирократа "
                  f"(Δ = {final_p1 - init_p1:+d})")
            print(f"│   Igrač 2: {init_p2} → {final_p2} бирократа "
                  f"(Δ = {final_p2 - init_p2:+d})")
        print(f"│")
        print("█" * 60)

    def display_timeline(self, max_width: int = 50):
        """ASCII graf redistribucije бирократа kroz vreme."""
        if not self.player1_count_per_step:
            return

        total = max(
            max(self.player1_count_per_step),
            max(self.player2_count_per_step),
            1
        )
        steps = len(self.player1_count_per_step)

        print("\n┌── GRAFIK: Distribucija бирократа po koracima ──┐")

        # Uzorkujemo ako ima previše koraka
        sample_count = min(steps, 25)
        indices = [int(i * (steps - 1) / (sample_count - 1))
                   for i in range(sample_count)] if sample_count > 1 else [0]

        for idx in indices:
            p1 = self.player1_count_per_step[idx]
            p2 = self.player2_count_per_step[idx]
            bar1 = "█" * int(p1 / total * max_width)
            bar2 = "▓" * int(p2 / total * max_width)
            print(f"│ t={idx:4d} │ P1:{p1:2d} {bar1}")
            print(f"│       │ P2:{p2:2d} {bar2}")
            if idx != indices[-1]:
                print(f"│       ├{'─' * (max_width + 8)}") 

        print("└" + "─" * (max_width + 18) + "┘")
        print("  Legenda: █ = Igrač 1, ▓ = Igrač 2")


# ─────────────────────────────────────────────────────────────
# KLASA: Simulation (glavni event-loop)
# ─────────────────────────────────────────────────────────────
class Simulation:
    """
    Glavni simulator: event-loop sa diskretnim vremenskim koracima.

    Tok jednog koraka:
      1. Svaki živi бирократ bira potez (susedno polje ili ostaje)
      2. Svi se istovremeno uklanjaju sa table
      3. Grupišu se po ciljnom polju
      4. Kolizije se razrešavaju (gubtinik prelazi protivniku, respawnuje se)
      5. Svi se postavljaju na nove pozicije
      6. Prikuplja se statistika
    """

    def __init__(self, n: int = 8, bureaucrats_per_player: int = 4,
                 max_steps: int = 50, verbose: bool = True,
                 display_every: int = 10, seed: Optional[int] = None):
        if seed is not None:
            random.seed(seed)

        Bureaucrat._id_counter = 0  # resetuj brojač

        self.n = n
        self.max_steps = max_steps
        self.verbose = verbose
        self.display_every = display_every
        self.board = Board(n)
        self.bureaucrats: list[Bureaucrat] = []
        self.stats = SimulationStats()
        self.current_step = 0

        self._initialize_agents(bureaucrats_per_player)

    def _initialize_agents(self, per_player: int):
        """Nasumično postavlja бирократе oba igrača na tablu."""
        all_positions = [
            (r, c) for r in range(self.n) for c in range(self.n)
        ]
        random.shuffle(all_positions)

        total_needed = per_player * 2
        if total_needed > len(all_positions):
            raise ValueError(
                f"Previše бирократа ({total_needed}) za tablu "
                f"{self.n}×{self.n} ({len(all_positions)} polja)"
            )

        for i in range(per_player):
            b = Bureaucrat(player=1, position=all_positions[i])
            self.bureaucrats.append(b)
            self.board.place(b)

        for i in range(per_player):
            b = Bureaucrat(player=2,
                           position=all_positions[per_player + i])
            self.bureaucrats.append(b)
            self.board.place(b)

        if self.verbose:
            print("╔══════════════════════════════════════════════╗")
            print("║   DYNAMIC BUREAUCRAT SIMULATION              ║")
            print("║   Agentni sistem na šahovskoj tabli          ║")
            print("╚══════════════════════════════════════════════╝")
            print(f"\n  Tabla: {self.n}×{self.n}")
            print(f"  Бирократа po igraču: {per_player}")
            print(f"  Ukupno agenata: {len(self.bureaucrats)}")
            print(f"  Koraka simulacije: {self.max_steps}")
            print("\n  Početno stanje agenata:")
            for b in self.bureaucrats:
                print(f"    {b}")

    def _get_active_bureaucrats(self) -> list[Bureaucrat]:
        return [b for b in self.bureaucrats if b.alive]

    def _resolve_collisions(self, target_groups: dict):
        """
        Razrešava kolizije kada se 2+ бирократа nađu na istom polju.

        Pravilo:
          - Nasumično se bira preživeli
          - Ostali se eliminišu i prelaze u kontrolu protivnika
          - Eliminisani se respawnuju na nasumično slobodno polje
        """
        step_collisions = 0

        for pos, group in target_groups.items():
            if len(group) == 1:
                # Nema kolizije - postavi normalno
                bureaucrat = group[0]
                bureaucrat.position = pos
                self.board.place(bureaucrat)
            else:
                # KOLIZIJA na polju {pos}!
                step_collisions += 1
                self.stats.total_collisions += 1

                random.shuffle(group)
                survivor = group[0]
                losers = group[1:]

                # Preživeli zauzima polje
                survivor.position = pos
                survivor.moves_made += 1
                self.board.place(survivor)

                if self.verbose:
                    players_involved = set(b.player for b in group)
                    print(f"    ⚡ KOLIZIJA na {pos}! "
                          f"{len(group)} бирократа, "
                          f"igrači: {players_involved}")
                    print(f"       Preživeo: {survivor}")

                # Eliminisani prelaze protivniku
                for loser in losers:
                    old_player = loser.player
                    loser.times_eliminated += 1

                    # Ažuriraj statistiku eliminacije
                    if old_player == 1:
                        self.stats.total_eliminations_p1 += 1
                        self.stats.total_transfers_to_p2 += 1
                        loser.player = 2
                    else:
                        self.stats.total_eliminations_p2 += 1
                        self.stats.total_transfers_to_p1 += 1
                        loser.player = 1

                    # Respawn na slobodno polje
                    free_positions = self.board.get_all_free_positions()
                    if free_positions:
                        new_pos = random.choice(free_positions)
                        loser.position = new_pos
                        self.board.place(loser)
                        if self.verbose:
                            print(f"       Eliminisan → prešao igraču "
                                  f"{loser.player}, respawn na {new_pos}")
                    else:
                        loser.alive = False
                        if self.verbose:
                            print(f"       Eliminisan → nema slobodnog "
                                  f"polja, agent deaktiviran!")

        return step_collisions

    def _tick(self):
        """Jedan vremenski korak simulacije."""
        active = self._get_active_bureaucrats()

        if not active:
            if self.verbose:
                print(f"  [t={self.current_step}] Nema aktivnih agenata!")
            return 0

        # Faza 1: Svaki agent bira potez
        random.shuffle(active)  # fer redosled odlučivanja
        intended_moves: dict[Bureaucrat, tuple[int, int]] = {}
        for b in active:
            intended_moves[b] = b.choose_move(self.board)

        # Faza 2: Ukloni sve sa table (simultano kretanje)
        for b in active:
            self.board.remove(b)

        # Faza 3: Grupiši po ciljnom polju
        target_groups: dict[tuple[int, int], list[Bureaucrat]] = defaultdict(list)
        for b, target_pos in intended_moves.items():
            target_groups[target_pos].append(b)

        # Faza 4: Postavi i razreši kolizije
        step_collisions = self._resolve_collisions(dict(target_groups))

        # Ažuriraj brojač poteza
        for b, target_pos in intended_moves.items():
            if b.alive and target_pos != b.position:
                pass  # pozicija je već ažurirana u _resolve_collisions
            if b.alive:
                b.moves_made += 1

        return step_collisions

    def _collect_stats(self, step_collisions: int):
        """Prikuplja statistiku za trenutni korak."""
        blocked = self.board.count_blocked()
        p1_count = sum(1 for b in self.bureaucrats
                       if b.alive and b.player == 1)
        p2_count = sum(1 for b in self.bureaucrats
                       if b.alive and b.player == 2)
        self.stats.record_step(blocked, step_collisions, p1_count, p2_count)

    def run(self):
        """Glavni event-loop simulacije."""
        if self.verbose:
            self.board.display(step=0)

        # Početna statistika
        self._collect_stats(0)

        for step in range(1, self.max_steps + 1):
            self.current_step = step

            if self.verbose and step % self.display_every == 0:
                print(f"\n  ── Korak {step}/{self.max_steps} "
                      f"{'─' * 30}")

            step_collisions = self._tick()
            self._collect_stats(step_collisions)

            # Periodični prikaz table
            if self.verbose and step % self.display_every == 0:
                p1 = sum(1 for b in self.bureaucrats
                         if b.alive and b.player == 1)
                p2 = sum(1 for b in self.bureaucrats
                         if b.alive and b.player == 2)
                print(f"    Igrač 1: {p1} бирократа | "
                      f"Igrač 2: {p2} бирократа | "
                      f"Blokirano: {self.board.count_blocked()}")
                self.board.display(step=step)

        # Završni prikaz
        if self.verbose:
            print("\n\n  ══ SIMULACIJA ZAVRŠENA ══")
            print("\n  Finalno stanje agenata:")
            for b in self.bureaucrats:
                status = "AKTIVAN" if b.alive else "NEAKTIVAN"
                print(f"    Бирократ #{b.id:2d} | Igrač {b.player} | "
                      f"Pozicija {b.position} | {status} | "
                      f"Poteza: {b.moves_made} | "
                      f"Eliminisan: {b.times_eliminated}x")
            self.board.display(step=self.max_steps)

        # Statistički izveštaj
        self.stats.display_summary(self.max_steps, self.n)
        self.stats.display_timeline()

        return self.stats


# ─────────────────────────────────────────────────────────────
# POKRETANJE
# ─────────────────────────────────────────────────────────────
def main():
    """Glavna funkcija za pokretanje simulacije."""
    print("=" * 60)
    print("  KONFIGURACIJA SIMULACIJE")
    print("=" * 60)

    # Podrazumevani parametri (lako se menjaju)
    CONFIG = {
        "n": 8,                        # dimenzija table
        "bureaucrats_per_player": 5,   # бирократа po igraču
        "max_steps": 100,              # broj vremenskih koraka
        "verbose": True,               # detaljan ispis
        "display_every": 20,           # prikaz table svakih N koraka
        "seed": 42,                    # seed za reproduktivnost
    }

    for key, val in CONFIG.items():
        print(f"  {key:25s} = {val}")
    print("=" * 60)

    # Pokreni simulaciju
    sim = Simulation(**CONFIG)
    stats = sim.run()

    # Dodatna analiza: stabilnost sistema
    print("\n" + "─" * 60)
    print("  ANALIZA STABILNOSTI")
    print("─" * 60)

    if len(stats.blocked_per_step) > 10:
        first_10 = stats.blocked_per_step[:10]
        last_10 = stats.blocked_per_step[-10:]
        avg_first = sum(first_10) / len(first_10)
        avg_last = sum(last_10) / len(last_10)
        print(f"  Prosek blokiranih (prvih 10):    {avg_first:.2f}")
        print(f"  Prosek blokiranih (poslednjih 10): {avg_last:.2f}")
        diff = abs(avg_last - avg_first)
        if diff < 1.0:
            print(f"  → Sistem je STABILAN "
                  f"(razlika: {diff:.2f})")
        else:
            print(f"  → Sistem pokazuje NESTABILNOST "
                  f"(razlika: {diff:.2f})")

    if len(stats.player1_count_per_step) > 10:
        last_p1 = stats.player1_count_per_step[-10:]
        last_p2 = stats.player2_count_per_step[-10:]
        var_p1 = sum((x - sum(last_p1)/10)**2 for x in last_p1) / 10
        var_p2 = sum((x - sum(last_p2)/10)**2 for x in last_p2) / 10
        print(f"\n  Varijansa raspodele (poslednjih 10 koraka):")
        print(f"    Igrač 1: {var_p1:.3f}")
        print(f"    Igrač 2: {var_p2:.3f}")
        total_var = var_p1 + var_p2
        if total_var < 2.0:
            print(f"  → Distribucija je URAVNOTEŽENA")
        else:
            print(f"  → Distribucija je NEURAVNOTEŽENA")

    print("\n" + "═" * 60)
    print("  SIMULACIJA KOMPLETIRANA USPEŠNO")
    print("═" * 60)


if __name__ == "__main__":
    main()