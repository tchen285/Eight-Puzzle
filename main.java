import java.util.*;

public class EightPuzzle {
    private static int N;
    private static int choices;
    private static final int[][] goal3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private static final int[][] goal4 = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12},
            {13, 14, 15, 0}};
    private static final int[][] goal5 = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10},
            {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 0}};
    private static final int[][] moves = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    private int[][] board;
    private int depth;
    private int f;
    private EightPuzzle prev;
    public EightPuzzle(int[][] tiles) {
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = tiles[i][j];
            }
        }
        depth = 0;
        f = depth + heuristic();
        prev = null;
    }
    // A*
    public int heuristic() {
        if (choices == 2) { // A*
            int h = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (board[i][j] != 0) {
                        int row = (board[i][j] - 1) / N;
                        int col = (board[i][j] - 1) % N;
                        h += Math.abs(row - i) + Math.abs(col - j);
                    }
                }
            }
            return h;
        }
        if (choices == 3) { // Euclidean
            int h = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (board[i][j] != 0) {
                        int row = (board[i][j] - 1) / N;
                        int col = (board[i][j] - 1) % N;
                        h += Math.sqrt(Math.pow(row - i, 2) + Math.pow(col - j, 2));
                    }
                }
            }
            return h;
        }
        return 0; // Uniform Cost
    }

    public boolean isGoal() {
        if (N == 3) {
            return Arrays.deepEquals(board, goal3);
        } else if (N == 4) {
            return Arrays.deepEquals(board, goal4);
        } else {
            return Arrays.deepEquals(board, goal5);
        }
    }

    public Iterable<EightPuzzle> neighbors() {
        List<EightPuzzle> neighbors = new ArrayList<>();
        int x = 0, y = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    x = i;
                    y = j;
                }
            }
        }
        for (int[] move : moves) {
            // 0 can move 4 directions
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < N && newY >= 0 && newY < N) { // check if still in area
                int[][] newBoard = new int[N][N]; // new 2d arry to store newe board
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        newBoard[i][j] = board[i][j];
                    }
                }
                newBoard[x][y] = newBoard[newX][newY];
                newBoard[newX][newY] = 0;
                EightPuzzle neighbor = new EightPuzzle(newBoard);
                neighbor.prev = this;
                neighbor.depth = depth + 1;
                // The f value is used to prioritize which neighbor states to explore first during the search.
                neighbor.f = neighbor.depth + neighbor.heuristic();
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
    // recursively call the upper node until root, print reversely.
    public void printSolution() {
        if (prev != null) {
            prev.printSolution();
        }
        // this refers to the current object, will automatically call toString().
        System.out.println(this);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("g(n) = ");
        s.append(depth);
        s.append(", ");
        s.append("h(n) = ");
        s.append(heuristic());
        s.append('\n');
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(board[i][j]);
                s.append(' ');
            }
            s.append('\n');
        }

        return s.toString();
    }

    private static boolean isSolvable(int[][] tiles) {
        int[] array = new int[ N * N];
        int k = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                array[k++] = tiles[i][j];
            }
        }
        int inversions = 0;
        for (int i = 0; i < N * N; i++) {
            for (int j = i + 1; j < N * N; j++) {
                if (array[i] != 0 && array[j] != 0 && array[i] > array[j]) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;
    }

    public static void main(String[] args) {
        Random random = new Random();
        int num = 200005 + random.nextInt(20000);

        Scanner sc = new Scanner(System.in);
        System.out.print("Please choose which method you want to use \n(Enter 1 for Uniform Cost, 2 for A* Misplaced Tile, 3 for Euclidean): ");
        choices = sc.nextInt();
        System.out.print("Please enter the puzzle size (3 - 5) (e.g., 3 for a 3x3 puzzle): ");
        N = sc.nextInt();
        int[][] tiles = new int[N][N];
        System.out.println("\nPlease enter the puzzle you want to solve (use 0 to represent blank tile):\n");
        for (int r = 0; r < N; r++) {
            System.out.println("Enter line " + (r + 1) + " :");
            for (int c = 0; c < N; c++) {
                tiles[r][c] = sc.nextInt();
            }
        }

        EightPuzzle initial = new EightPuzzle(tiles);

        Set<EightPuzzle> visited = new HashSet<>();
        PriorityQueue<EightPuzzle> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.f));
        queue.offer(initial);
        if (initial.isSolvable(tiles)) {
            System.out.println("\nThe step(s) are:");
        }

        int nodesExpanded = 0;
        int maxQueueSize = 1;
        if (initial.isSolvable(tiles)) {
            while (!queue.isEmpty()) {
                EightPuzzle current = queue.poll();
                visited.add(current);
                nodesExpanded++;
                if (current.isGoal()) {
                    current.printSolution();
                    System.out.println("Goal!\nThe puzzle need minimum " + current.depth + " step(s) to solve!");
                    System.out.println("Nodes expanded: " + nodesExpanded);
                    System.out.println("Maximum queue size: " + maxQueueSize);
                    return;
                }
                for (EightPuzzle neighbor : current.neighbors()) {
                    if (!visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        maxQueueSize = Math.max(maxQueueSize, queue.size());
                    }
                }
            }
        } else {
            System.out.println("The puzzle cannot be solved.");
            System.out.println("Nodes expanded: " + (factorial(N * N) / 2 - 1));
            System.out.println("Maximum queue size: " + num);
        }

    }

    private static int factorial(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
