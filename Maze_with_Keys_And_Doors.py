import csv
import random
from collections import deque

def solve_maze(grid):
    rows, cols = len(grid), len(grid[0])
    start = exit_pos = None
    
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 'S': start = (r, c)
            elif grid[r][c] == 'E': exit_pos = (r, c)
            
    if not start or not exit_pos: return -1, []

    # Queue stores: (row, col, bitmask, distance)
    q = deque([(start[0], start[1], 0, 0)])
    # Visited stores: (row, col, bitmask) -> (prev_r, prev_c, prev_mask)
    visited = {(start[0], start[1], 0): None}
    final_state = None

    while q:
        r, c, mask, dist = q.popleft()

        if (r, c) == exit_pos:
            final_state = (r, c, mask)
            break

        for dr, dc in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            nr, nc = r + dr, c + dc

            if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] != '#':
                cell = grid[nr][nc]
                n_mask = mask

                if cell.islower(): # Pick up key
                    n_mask |= (1 << (ord(cell) - ord('a')))
                elif cell.isupper(): # Check door
                    if not (mask & (1 << (ord(cell.lower()) - ord('a')))):
                        continue # Locked

                if (nr, nc, n_mask) not in visited:
                    visited[(nr, nc, n_mask)] = (r, c, mask)
                    q.append((nr, nc, n_mask, dist + 1))

    if not final_state: return -1, []

    # Reconstruct path
    path = set()
    curr = final_state
    while curr:
        path.add((curr[0], curr[1]))
        curr = visited[curr]

    # Generate output grid
    out_grid = [list(row) for row in grid]
    for r, c in path:
        if out_grid[r][c] in ['.', 'S', 'E']:
            out_grid[r][c] = 'x'
        else: # Append 'x' to keys/doors to keep them identifiable
            out_grid[r][c] += 'x' 
            
    return dist, out_grid

def generate_solvable_maze(N, T):
    # Generates a simple snake-like guaranteed path, places keys then doors on it
    grid = [['#' for _ in range(N)] for _ in range(N)]
    path = []
    
    # Create a basic winding path
    r, c = 0, 0
    while r < N:
        grid[r][c] = '.'
        path.append((r, c))
        if r % 2 == 0:
            if c < N - 1: c += 1
            else: r += 1
        else:
            if c > 0: c -= 1
            else: r += 1
            
    grid[path[0][0]][path[0][1]] = 'S'
    grid[path[-1][0]][path[-1][1]] = 'E'
    
    # Distribute keys and doors along the path
    # Ensure key 'a' is placed before door 'A' sequentially
    indices = sorted(random.sample(range(1, len(path)-1), T * 2))
    for i in range(T):
        kr, kc = path[indices[i*2]]
        dr, dc = path[indices[i*2 + 1]]
        grid[kr][kc] = chr(ord('a') + i)
        grid[dr][dc] = chr(ord('A') + i)
        
    return grid

# Example Usage
if __name__ == "__main__":
    N, T = 10, 3
    maze = generate_solvable_maze(N, T)
    dist, solved_grid = solve_maze(maze)
    
    print(f"Shortest Path Length: {dist}")
    with open('solved_maze.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerows(solved_grid)