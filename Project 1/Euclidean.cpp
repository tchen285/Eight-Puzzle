#include <iostream>
#include <vector>
#include <map>
#include <cmath>
#include <string>
#include <queue>
#include <functional>
#include <set>

using namespace std;

// Helper function to print a state
void print_state(const vector<vector<string>>& state) {
    for (const auto& row : state) {
        for (const auto& tile : row) {
            cout << tile << " ";
        }
        cout << endl;
    }
}

// Helper function to check if two states are equal
bool states_equal(const vector<vector<string>>& state1, const vector<vector<string>>& state2) {
    for (size_t i = 0; i < state1.size(); ++i) {
        for (size_t j = 0; j < state1[i].size(); ++j) {
            if (state1[i][j] != state2[i][j]) {
                return false;
            }
        }
    }
    return true;
}

struct Node {
    vector<vector<string>> state;
    Node* parent;
    int g;
    int h;

    int f() const {
        return g + h;
    }

    bool operator<(const Node& other) const {
        return f() < other.f();
    }
};

struct CompareNodes {
    bool operator()(const Node* n1, const Node* n2) const {
        return n1->f() > n2->f();
    }
};


// Calculate the Euclidean distance for the given state
int euclidean_distance(const vector<vector<string>>& state, const vector<vector<string>>& goal_state) {
    int total_distance = 0;

    map<string, pair<int, int>> goal_mappings;
    for (size_t i = 0; i < goal_state.size(); ++i) {
        for (size_t j = 0; j < goal_state[i].size(); ++j) {
            goal_mappings[goal_state[i][j]] = {static_cast<int>(i), static_cast<int>(j)};
        }
    }

    for (size_t i = 0; i < state.size(); ++i) {
        for (size_t j = 0; j < state[i].size(); ++j) {
            if (state[i][j] != "0") {
                pair<int, int> goal_position = goal_mappings[state[i][j]];
                double distance = sqrt(pow(static_cast<int>(i) - goal_position.first, 2) + pow(static_cast<int>(j) - goal_position.second, 2));
                total_distance += static_cast<int>(distance);
            }
        }
    }

    return total_distance;
}



// Get possible moves from a given state
vector<vector<vector<string>>> get_moves(const vector<vector<string>>& state) {
    vector<vector<vector<string>>> moves;
    int blank_x = -1, blank_y = -1;

    // Find the blank position
    for (size_t i = 0; i < state.size(); ++i) {
        for (size_t j = 0; j < state[i].size(); ++j) {
            if (state[i][j] == "0") {
                blank_x = static_cast<int>(i);
                blank_y = static_cast<int>(j);
                break;
            }
        }
        if (blank_x != -1) {
            break;
        }
    }

        // Possible moves
    vector<pair<int, int>> directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // Generate new states
    for (const auto& direction : directions) {
        int new_x = blank_x + direction.first;
        int new_y = blank_y + direction.second;

        if (new_x >= 0 && new_x < static_cast<int>(state.size()) && new_y >= 0 && new_y < static_cast<int>(state[0].size())) {
            vector<vector<string>> new_state = state;
            swap(new_state[blank_x][blank_y], new_state[new_x][new_y]);
            moves.push_back(new_state);
        }
    }

    return moves;
}

void a_star_search(const vector<vector<string>>& start, const vector<vector<string>>& goal, int max_iterations) {
    priority_queue<Node*, vector<Node*>, CompareNodes> frontier;
    set<vector<vector<string>>> explored;

    Node* initial_node = new Node{start, nullptr, 0, euclidean_distance(start, goal)};
    frontier.push(initial_node);

    int max_frontier_size = 1;
    int nodes_expanded = 0;
    int iterations = 0;

    while (!frontier.empty() && iterations < max_iterations) {
        Node* current = frontier.top();
        frontier.pop();
        iterations++;

        if (states_equal(current->state, goal)) {
            // Print the solution
            vector<Node*> path;
            Node* temp = current;
            while (temp != nullptr) {
                path.push_back(temp);
                temp = temp->parent;
            }

            cout << "Solution found!" << endl;
            for (int i = static_cast<int>(path.size()) - 1; i >= 0; --i) {
                cout << "Step " << path.size() - i - 1 << ":" << endl;
                print_state(path[i]->state);
                cout << "g(n): " << path[i]->g << ", h(n): " << path[i]->h << endl << endl;
            }

            cout << "Total nodes expanded: " << nodes_expanded << endl;
            cout << "Maximum number of nodes in the queue at any one time: " << max_frontier_size << endl;
            cout << "The depth of the goal node: " << current->g << endl;

            // Clean up memory
            while (!frontier.empty()) {
                delete frontier.top();
                frontier.pop();
            }
            for (Node* node : path) {
                delete node;
            }

            return;
        }

        explored.insert(current->state);
        vector<vector<vector<string>>> moves = get_moves(current->state);

        for (const auto& move : moves) {
            if (explored.find(move) == explored.end()) {
                Node* child = new Node{move, current, current->g + 1, euclidean_distance(move, goal)};
                frontier.push(child);
                max_frontier_size = max(max_frontier_size, static_cast<int>(frontier.size()));
                nodes_expanded++;
            }
        }
    }

    if (iterations >= max_iterations) {
        cout << "No solution found within maximum iterations." << endl;
    } else {
        cout << "No solution found." << endl;
    }
}

int main() {
    vector<vector<string>> start = {{"8", "7", "1"},
                                    {"6", "0", "2"},
                                    {"5", "4", "3"}};

    vector<vector<string>> goal = {{"1", "2", "3"},
                                   {"4", "5", "6"},
                                   {"7", "8", "0"}};

    int max_iterations = 100000; // Set a limit for repeated states
    a_star_search(start, goal, max_iterations);

    return 0;
}


