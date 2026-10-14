
import random
import time
import csv
import networkx as nx
import matplotlib.pyplot as plt

# Generate a random conflict graph
def generate_graph(N, density):
    G = nx.Graph()
    G.add_nodes_from(range(1, N + 1))

    for i in range(1, N + 1):
        for j in range(i + 1, N + 1):
            if random.random() < density:
                G.add_edge(i, j)

    return G


# DSatur Greedy Algorithm
def greedy(G, R):
    colors = {}

    while len(colors) < len(G):
        uncolored = [v for v in G if v not in colors]

        v = max(
            uncolored,
            key=lambda x: (
                len({colors[u] for u in G[x] if u in colors}),
                G.degree[x]
            )
        )

        used = {colors[u] for u in G[v] if u in colors}
        c = 0

        while c in used or sum(
            1 for x in colors.values() if x == c
        ) >= R:
            c += 1

        colors[v] = c

    return colors


# Exact Backtracking Algorithm
def exact(G, R, upper):
    N = len(G)

    # Lower bound from hall capacity
    lower = (N + R - 1) // R

    # Greedy clique gives another valid lower bound
    clique = []
    for v in sorted(G, key=lambda x: G.degree[x], reverse=True):
        if all(G.has_edge(v, u) for u in clique):
            clique.append(v)

    lower = max(lower, len(clique))

    def solve(K):
        colors = {}
        count = [0] * K

        def backtrack():
            if len(colors) == N:
                return True

            uncolored = [v for v in G if v not in colors]

            v = max(
                uncolored,
                key=lambda x: (
                    len({colors[u] for u in G[x] if u in colors}),
                    G.degree[x]
                )
            )

            used = {colors[u] for u in G[v] if u in colors}
            max_used = max(colors.values(), default=-1)

            # Avoid trying equivalent unused slot labels
            limit = min(K, max_used + 2)

            for c in range(limit):
                if c in used or count[c] >= R:
                    continue

                colors[v] = c
                count[c] += 1

                if backtrack():
                    return True

                del colors[v]
                count[c] -= 1

            return False

        if backtrack():
            return colors
        return None

    for K in range(lower, upper + 1):
        result = solve(K)
        if result is not None:
            return result, True

    return None, False


# Display timetable
def show_timetable(colors):
    slots = {}

    for course, c in colors.items():
        slots.setdefault(c + 1, []).append(course)

    print("\nFINAL TIMETABLE")
    for slot in sorted(slots):
        print(f"Slot {slot}: {sorted(slots[slot])}")

    return slots


# Save timetable to CSV
def save_csv(slots, R):
    with open("timetable.csv", "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow([f"Hall {i}" for i in range(1, R + 1)])

        for slot in sorted(slots):
            courses = sorted(slots[slot])
            writer.writerow(courses + [""] * (R - len(courses)))

    print("\nTimetable saved as timetable.csv")


# Draw conflict graph
def draw_graph(G, colors):
    node_colors = [colors[v] for v in G.nodes()]

    nx.draw(
        G,
        nx.spring_layout(G, seed=42),
        with_labels=True,
        node_color=node_colors,
        cmap=plt.cm.Set3,
        node_size=800
    )

    plt.title("Random Conflict Graph")
    plt.show()


# Draw timetable grid
def draw_timetable(slots, R):
    fig, ax = plt.subplots(figsize=(7, max(3, len(slots) * 0.6)))

    for row, slot in enumerate(sorted(slots)):
        for col in range(R):
            ax.add_patch(plt.Rectangle(
                (col, len(slots) - row - 1),
                1, 1,
                facecolor=plt.cm.Set3(
                    (slot - 1) % 12 / 12
                ),
                edgecolor="black"
            ))

            if col < len(slots[slot]):
                ax.text(
                    col + 0.5,
                    len(slots) - row - 0.5,
                    str(sorted(slots[slot])[col]),
                    ha="center",
                    va="center"
                )

        ax.text(
            -0.2, len(slots) - row - 0.5,
            f"Slot {slot}", ha="right", va="center"
        )

    ax.set_xlim(0, R)
    ax.set_ylim(0, len(slots))
    ax.set_xticks(range(R + 1))
    ax.set_yticks(range(len(slots) + 1))
    ax.set_xticklabels([])
    ax.set_yticklabels([])
    ax.set_title("Exam Timetable Grid")
    plt.tight_layout()
    plt.show()


# Main program
N = int(input("Enter number of courses (N): "))
R = int(input("Enter hall capacity (R): "))
density = float(input("Enter edge density (0 to 1): "))

if N < 1 or R < 1 or not 0 <= density <= 1:
    raise ValueError("N and R must be positive; density must be 0 to 1.")

G = generate_graph(N, density)

print("\nNumber of courses:", N)
print("Number of conflicts:", G.number_of_edges())
print("Hall capacity:", R)
print("Edge density:", density)

start = time.perf_counter()
g = greedy(G, R)
greedy_time = time.perf_counter() - start
greedy_count = max(g.values()) + 1

start = time.perf_counter()
e, proven = exact(G, R, greedy_count)
exact_time = time.perf_counter() - start

if e is None:
    e = g

exact_count = max(e.values()) + 1

print("\nALGORITHM COMPARISON")
print("DSatur slots:", greedy_count)
print("DSatur time:", round(greedy_time, 6), "seconds")
print("Exact slots:", exact_count)
print("Exact time:", round(exact_time, 6), "seconds")
print("Optimality proven:", proven)

slots = show_timetable(e)
save_csv(slots, R)
draw_graph(G, e)
draw_timetable(slots, R)

# Comparison chart
plt.bar(
    ["DSatur Greedy", "Exact Backtracking"],
    [greedy_count, exact_count]
)
plt.ylabel("Number of slots")
plt.title("Algorithm Comparison")
plt.show()
