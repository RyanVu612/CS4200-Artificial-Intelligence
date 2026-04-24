// Simple tic-tac-toe with minimax
#include <iostream>
#include <chrono>

using namespace std;
using namespace std::chrono;

int min(int depth, int alpha, int beta);
int max(int depth, int alpha, int beta);
int evaluate();
int check4winner(int i, int j, char move);
int scoreWindow(char c1, char c2, char c3, char c4);
int distToCenter(int row, int col);
bool timeUp();
void checkGameOver(int i, int j, char move);
void getamove(int &row, int &col);
void makemove(int &row, int &col);
void setup();
void printboard();

char b[9][9], maxdepth = 4;

steady_clock::time_point start;
int timeLimit = 5000; //ms

int main ()
{ int r; int c;
  setup();
  printboard();
  for (;;)
  { getamove(r, c);
    checkGameOver(r, c, 'X');

    makemove(r, c);
    checkGameOver(r, c, 'O');
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
        score += 20 * bonus;
      } else if (b[i][j] == 'X') {
        score -= 20 * bonus;
      }
    }
  }


  return score;
}

void makemove(int &row, int &col)
{ int best=-2000000, depth=maxdepth, score, mi=1, mj=1, alpha=-9999999, beta=9999999;

  start = steady_clock::now();

  // If can win, do immediately
  for (int i=1; i<=8; i++) {
    for (int j=1; j<=8; j++) {
      if (b[i][j] == '-') {
        b[i][j] = 'O';
        if (check4winner(i, j, 'O') == 1000000) {
          cout << "my move is " << i << " " << j << endl;
          row = i;
          col = j;
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
          b[i][j] = '-';
          cout << "my move is " << i << " " << j << endl;
          b[i][j] = 'O';
          row = i;
          col = j;
          return;
        }
        b[i][j] = '-';
      }
    }
  }

  for (int i=1; i<=8; i++)
  { for (int j=1; j<=8; j++)
    { if (b[i][j]== '-')
      { b[i][j] = 'O';
        int result = check4winner(i, j, 'O');
        if (result != 0) {
          score = result;
        } else {
          score = min(depth - 1, alpha, beta);
        }
        if (score > best || (score == best && distToCenter(i, j) < distToCenter(mi, mj))) {
          mi = i;
          mj = j;
          best = score;
        }
        b[i][j]='-'; // undo move
  } } }
  cout << "my move is " << mi << " " << mj << endl;
  b[mi][mj]='O';
  row = mi;
  col = mj;
}

int min(int depth, int alpha, int beta) // player turn
{ int best=20000,score;
  if (depth == 0 || timeUp()) return (evaluate());
  for (int i=1; i<9; i++)
  { for (int j=1; j<9; j++)
    { if (b[i][j]=='-')
      { b[i][j]='X'; // make move on board
        int result = check4winner(i, j, 'X');
        if (result != 0){
          b[i][j]='-'; //undo move
          return result;
        } 
        score = max(depth-1, alpha, beta);
        if (score < best) best=score;
        if (score < beta) beta=score;
        b[i][j]='-'; // undo move

        if (beta <= alpha) return best;
  } } }
  return(best);
}

int max(int depth, int alpha, int beta) // computer turn
{ int best=-20000,score;
  if (depth == 0 || timeUp()) return (evaluate());
  for (int i=1; i<9; i++)
  { for (int j=1; j<9; j++)
    { if (b[i][j]=='-')
      { b[i][j]='O'; // make move on board
        int result = check4winner(i, j, 'O');
        if (result != 0) {
          b[i][j]='-';  // undo move
          return result;
        }
        score = min(depth-1, alpha, beta);
        if (score > best) best=score;
        if (best > alpha) alpha=best;

        b[i][j]='-'; // undo move

        if (alpha >= beta) return best;
  } } }
  return(best);
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

  char cells[4] = {c1, c2, c3, c4};

  for (int i = 0; i < 4; i++) {
    if (cells[i] == 'O') {
      oCount++;
    } else if (cells[i] == 'X') {
      xCount++;
    } else {
      emptyCount++;
    }
  }

  if (oCount > 0 && xCount > 0) return 0;

  if (oCount == 3 && emptyCount == 1) return 2000;
  if (oCount == 2 && emptyCount == 2) return 300;
  if (oCount == 1 && emptyCount == 3) return 100;

  if (xCount == 3 && emptyCount == 1) return -2500;
  if (xCount == 2 && emptyCount == 2) return -500;
  if (xCount == 1 && emptyCount == 3) return -100;

  return 0;
}

bool timeUp() 
{
  return duration_cast<milliseconds>(steady_clock::now() - start).count() >= timeLimit;
}

void checkGameOver(int i, int j, char move)
{ printboard();
  int result = check4winner(i, j, move);
  if (result == -1000000) { cout << "you win" << endl; exit(0); }
  if (result == 1000000)  { cout << "I win"   << endl; exit(0); }
  if (result == 1)     { cout << "draw"    << endl; exit(0); }
}
