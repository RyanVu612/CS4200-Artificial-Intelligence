// Simple tic-tac-toe with minimax
#include <iostream>
#include <chrono>
#include <vector>
#include <algorithm>

using namespace std;
using namespace std::chrono;

struct Move {
  int row, col;
  int priority;
};

int min(int depth, int alpha, int beta);
int max(int depth, int alpha, int beta);
int evaluate();
int check4winner(int i, int j, char move);
int scoreWindow(char c1, char c2, char c3, char c4);
int distToCenter(int row, int col);
int scoreNeighbor(int row, int col);
int movePriority(int row, int col, char move);
bool timeUp();
bool boardEmpty();
bool hasNeighbor(int row, int col);
bool betterMoveFirst(const Move &a, const Move &b);
vector<Move> generateMoves(char move);
void prioritizeBestMove(vector<Move> &moves, int bestRow, int bestCol);
void checkGameOver(int i, int j, char move);
void getamove(int &row, int &col);
void makemove(int &row, int &col);
void setup();
void printboard();

char b[9][9];
int maxdepth = 8;

steady_clock::time_point start;
int timeLimit = 5000; //ms

int firstPlay = 0;

int main ()
{ int r; int c;
  setup();
  printboard();
  for (;;)
  { 
    if (firstPlay == 1) {
      makemove(r, c);
      checkGameOver(r, c, 'O');

      getamove(r, c);
      checkGameOver(r, c, 'X');
    } else {
      getamove(r, c);
      checkGameOver(r, c, 'X');

      makemove(r, c);
      checkGameOver(r, c, 'O');
    }
    
} }

void printboard()
{ cout << endl;
  for (int i = 0; i < 9; i++) {
    for (int j = 0; j < 9; j++) {
      if (j == 9 - 1) {
        cout << b[i][j];
        continue;
      }
      cout << b[i][j] << " ";
    }
    cout << endl;
  }
  cout << endl;
}

void setup()
{ 
  for (int i = 0; i < 9; i++) {
    for (int j = 0; j < 9; j++) {
      b[i][j] = '-';
    }
  }
  
  b[0][0] = ' ';
  
  for (int i = 1; i < 9; i++) {
    b[0][i] = i + '0';
  }
  
  for (int i = 0; i < 9 - 1; i++) {
    b[i+1][0] = 'A' + i;
  }

  cout << "Enter computer time limit (5 - 30 seconds): ";
  timeLimit = -1;

  while (timeLimit < 5 || timeLimit > 30) {
    cin >> timeLimit;
    if (timeLimit < 5 || timeLimit > 30) {
      cout << "Please enter a time limit between 5 and 30 seconds: ";
    }
  }

  timeLimit *= 1000;

  cout << "Which player moves first? [0]Player [1]Computer: ";
  cin >> firstPlay;
}

void getamove(int &row, int &col)
{ char i; int j;
  while (true) {
    cout << "Enter row: ";
    cin >> i;
    while (i < 'A' || i > 'H'){
      cout << "Enter a valid row: ";
      cin >> i;
    }

    cout << "Enter column: ";
    cin >> j;
    while (j < 1 || j > 8){
      cout << "Enter a valid column: ";
      cin >> j;
    }

    row = i - 'A' + 1;
    col = j;

    if (b[row][col] != '-') {
      cout << "This space is already taken." << endl;
      i = ' ';
      j = 0;
      continue;
    } else {
      b[row][col] = 'X';
      break;
    }
  }
  printboard();
}


// Ways to gain points: 
// positive = computer (O)
// negative = player (X)
// 1: be one move away from winning
// 2: opponent one move away from winning
// 3: have 2 in a row with 2 open sides
// 4: opponent has 2 in a row with 2 open sides
// 5: make a move next to already placed with the ability to create 4 in a row (either 3 - to either side, 2 - on one side + 1 - on other side)
// 6: 
// 7: if move first, always pic k one of the middle tiles
// 8: block against outside priority. 

// Note: is only one way. Can prioritize a certain O/X
int evaluate ()
{ 
  int score=0;
  
  // Check horizontal window of 4
  for (int i = 1; i <= 8; i++) {
    for (int j = 1; j <= 5; j++) {
      score += scoreWindow(b[i][j], b[i][j+1], b[i][j+2], b[i][j+3]);
    }
  }

  // Check vertical window of 4
  for (int j = 1; j <= 8; j++) {
    for (int i = 1; i <= 5; i++) {
      score += scoreWindow(b[i][j], b[i+1][j], b[i+2][j], b[i+3][j]);
    }
  }

  // Reward being closer to center
  for (int i = 1; i <= 8; i++) {
    for (int j = 1; j <= 8; j++) {
      int bonus = 8 - distToCenter(i, j);

      if (b[i][j] == 'O') {
        score += 10 * bonus;
      } else if (b[i][j] == 'X') {
        score -= 10 * bonus;
      }
    }
  }


  return score;
}

void makemove(int &row, int &col)
{

  start = steady_clock::now();

  if (boardEmpty()) {
    b[4][4] = 'O';
    row = 4;
    col = 4;
    printboard();
    cout << "D4" << endl;
    return;
  }

  // If can win, do immediately
  for (int i=1; i<=8; i++) {
    for (int j=1; j<=8; j++) {
      if (b[i][j] == '-') {
        b[i][j] = 'O';
        if (check4winner(i, j, 'O') == 1000000) {
          row = i;
          col = j;
          printboard();
          cout << b[i][0] << j << endl;
          return;
        }
        b[i][j] = '-';
      }
    }
  }

  // If player can win, block it
  for (int i=1; i<=8; i++) {
    for (int j=1; j<=8; j++) {
      if (b[i][j] == '-') {
        b[i][j] = 'X';
        if (check4winner(i, j, 'X') == -1000000) {
          b[i][j] = 'O';
          row = i;
          col = j;
          printboard();
          cout << b[i][0] << j << endl;
          return;
        }
        b[i][j] = '-';
      }
    }
  }

  int bestRow = -1, bestCol = -1;
  int bestScore = -2000000;

  for (int depth = 1; depth <= maxdepth; depth++) {
    int currentBestRow = -1, currentBestCol = -1;
    int currentBestScore = -2000000;

    int alpha = -9999999;
    int beta = 9999999;
  
    vector<Move> moves = generateMoves('O');
    if (bestRow != -1) {
      prioritizeBestMove(moves, bestRow, bestCol);
    }
  
    for (int k = 0; k < moves.size(); k++) {
      Move m = moves[k];
      int i = m.row;
      int j = m.col;
  
      b[i][j] = 'O';
      int result = check4winner(i, j, 'O');
      int score;
      if (result != 0) {
        score = result;
      } else {
        score = min(depth - 1, alpha, beta);
      }

      b[i][j] = '-'; // undo move

      if (timeUp()) break;

      if (score > currentBestScore || (score == currentBestScore && distToCenter(i, j) < distToCenter(currentBestRow, currentBestCol))) {
        currentBestRow = i;
        currentBestCol = j;
        currentBestScore = score;
      }

      if (score > alpha) alpha = score;
    }

    if (!timeUp() && currentBestRow != -1) {
      bestRow = currentBestRow;
      bestCol = currentBestCol;
      bestScore = currentBestScore;
    } else {
      break;
    }
  }
  b[bestRow][bestCol]='O';
  row = bestRow;
  col = bestCol;
  printboard();
  cout << b[bestRow][0] << bestCol << endl;
}

int min(int depth, int alpha, int beta) // player turn
{ int best=20000,score;
  if (depth == 0 || timeUp()) return (evaluate());

  vector<Move> moves = generateMoves('X');
  
  for (int k = 0; k < moves.size(); k++) {
    Move m = moves[k];
    int i = m.row;
    int j = m.col;

    b[i][j] = 'X';
    int result = check4winner(i, j, 'X');
    if (result != 0) {
      b[i][j] = '-';
      return result;
    }

    score = max(depth - 1, alpha, beta);
    if (score < best) best = score;
    if (best < beta) beta = best;

    b[i][j] = '-';

    if (beta <= alpha) return best;
  }

  return best;
}

int max(int depth, int alpha, int beta) // computer turn
{ int best=-20000,score;
  if (depth == 0 || timeUp()) return (evaluate());

  vector<Move> moves = generateMoves('O');
  
  for (int k = 0; k < moves.size(); k++) {
    Move m = moves[k];
    int i = m.row;
    int j = m.col;

    b[i][j] = 'O';
    int result = check4winner(i, j, 'O');
    if (result != 0) {
      b[i][j] = '-';
      return result;
    }

    score = min(depth - 1, alpha, beta);
    if (score > best) best = score;
    if (best > alpha) alpha = best;

    b[i][j] = '-';

    if (alpha >= beta) return best;
  }

  return best;
}

int check4winner(int i, int j, char move) // i and j represent last move made; return 1000000 for computer win, -1000000 for player
{ int temp; int count;

  // check horizontal win condition
  count = 1;
  temp = j - 1;
  while (temp > 0 && temp < 9 && b[i][temp] == move) {
    count++;
    temp--;
  }

  temp = j + 1;
  while (temp > 0 && temp < 9 && b[i][temp] == move) {
    count++;
    temp++;
  }

  if (count >= 4) {
    if (move == 'X') return -1000000;
    if (move == 'O') return 1000000;
  }

  // check vertical win conditions
  count = 1;
  temp = i - 1;
  while (temp > 0 && temp < 9 && b[temp][j] == move) {
    count++;
    temp--;
  }

  temp = i + 1;
  while (temp > 0 && temp < 9 && b[temp][j] == move) {
    count++;
    temp++;
  }

  if (count >= 4) {
    if (move == 'X') return -1000000;
    if (move == 'O') return 1000000;
  }

  for (int i=1; i<=8; i++)
    for (int j=1; j<=8; j++)
      {if (b[i][j]=='-') return 0;}
  return 1; // draw
}

int distToCenter(int row, int col)
{
  int drow = min(abs(row - 4), abs(row - 5));
  int dcol = min(abs(col - 4), abs(col - 5));
  return drow + dcol;
}

int scoreWindow(char c1, char c2, char c3, char c4)
{
  int oCount = 0, xCount = 0, emptyCount = 0;

  char cells[5] = {c1, c2, c3, c4};

  for (int i = 0; i < 4; i++) {
    if (cells[i] == 'O') {
      oCount++;
    } else if (cells[i] == 'X') {
      xCount++;
    } else {
      emptyCount++;
    }
  }

  if (oCount == 4) return 1000000;
  if (xCount == 4) return -1000000;

  if (oCount == 3 && emptyCount == 1) return 10000;
  if (xCount == 3 && emptyCount == 1) return -12000;

  if (oCount == 2 && emptyCount == 2) return 400;
  if (xCount == 2 && emptyCount == 2) return -1000;

  return 0;
}

int scoreNeighbor(int row, int col) {
  int score = 0;

  for (int i = row - 1; i <= row + 1; i++) {
    for (int j = col - 1; j <= col + 1; j++) {
      if (i >= 1 && i <= 8 && j >= 1 && j <= 8) {
        if (row == i && col == j) continue;

        if (b[i][j] == 'O') score += 3;
        if (b[i][j] == 'X') score += 2;
      }
    }
  }
  return score;
}

int movePriority(int row, int col, char move) {
  int priority = 0;

  // 1. Immediate Win
  b[row][col] = move;
  int result = check4winner(row, col, move);
  b[row][col] = '-';

  if (move == 'O' && result == 1000000) return 1000000;
  if (move == 'X' && result == -1000000) return 1000000;

  // 2. Immediate Blocking Win
  char opponent;
  if (move == 'O') {
    opponent = 'X';
  } else if (move == 'X') {
    opponent = 'O';
  }

  b[row][col] = opponent;
  int opponentResult = check4winner(row, col, opponent);
  b[row][col] = '-';

  if (move == 'O' && opponentResult == -1000000) priority += 900000;
  if (move == 'X' && opponentResult == 1000000) priority += 900000;

  // 3. Connected Moves
  priority += scoreNeighbor(row, col) * 100;

  // 4. Center
  priority += (10 - distToCenter(row, col)) * 10;

  return priority;
}

bool timeUp() 
{
  return duration_cast<milliseconds>(steady_clock::now() - start).count() >= timeLimit;
}

bool boardEmpty() 
{
  for (int i = 1; i <= 8; i++) {
    for (int j = 1; j <= 8; j++) {
      if (b[i][j] != '-') return false;
    }
  }
  return true;
}

bool hasNeighbor(int row, int col) {
  for (int i = row - 3; i <= row + 3; i++) {
    for (int j = col - 3; j <= col + 3; j++) {
      if (i >= 1 && i <= 8 && j >= 1 && j <= 8) {
        if (!(i == row && j == col) && (b[i][j] == 'X' || b[i][j] == 'O')) {
          return true;
        }
      }
    }
  }
  return false;
}

bool betterMoveFirst(const Move &a, const Move &b) {
  return a.priority > b.priority;
}

vector<Move> generateMoves(char move) {
  vector<Move> moves;

  for (int i = 1; i <= 8; i++) {
    for (int j = 1; j <= 8; j++) {
      if (b[i][j] == '-' && hasNeighbor(i, j)) {
        Move m;
        m.row = i;
        m.col = j;
        m.priority = movePriority(i, j, move);
        moves.push_back(m);
      }
    }
  }

  sort(moves.begin(), moves.end(), betterMoveFirst);
  return moves;
}

void prioritizeBestMove(vector<Move> &moves, int bestRow, int bestCol) {
  for (int i = 0; i < moves.size(); i++) {
    if (moves[i].row == bestRow && moves[i].col == bestCol) {
      swap(moves[0], moves[i]);
      return;
    }
  }
}

void checkGameOver(int i, int j, char move)
{
  int result = check4winner(i, j, move);
  if (result == -1000000) { cout << "you win" << endl; exit(0); }
  if (result == 1000000)  { cout << "I win"   << endl; exit(0); }
  if (result == 1)     { cout << "draw"    << endl; exit(0); }
}
