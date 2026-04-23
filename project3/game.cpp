// Simple tic-tac-toe with minimax
#include <iostream>
using namespace std;

int min(int depth, int alpha, int beta);
int max(int depth, int alpha, int beta);
int evaluate();
int check4winner(int i, int j, char move);
void checkGameOver(int i, int j, char move);
void getamove(int &row, int &col);
void makemove(int &row, int &col);
void setup();
void printboard();

char b[9][9], maxdepth = 2;

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

int evaluate ()
{ int count = 0;
  return count;
}

void makemove(int &row, int &col)
{ int best=-20000,depth=maxdepth,score,mi,mj,alpha=0,beta=0;
  for (int i=0; i<9; i++)
  { for (int j=0; j<9; j++)
    { if (b[i][j]== '-')
      { b[i][j]='O'; // make move on board
        score = min(depth-1, alpha, beta);
        if (score > best) { mi=i; mj=j; best=score; }
        b[i][j]='-'; // undo move
  } } }
  cout << "my move is " << mi << " " << mj << endl;
  b[mi][mj]='O';
  row = mi;
  col = mj;
}

int min(int depth, int alpha, int beta) // player turn
{ int best=20000,score;
  if (depth == 0) return (evaluate());
  for (int i=1; i<9; i++)
  { for (int j=1; j<9; j++)
    { if (b[i][j]=='-')
      { b[i][j]='X'; // make move on board
        if (check4winner(i, j, 'X') != 0){
          b[i][j]='-'; //undo move
          return check4winner(i, j, 'X');
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
  if (depth == 0) return (evaluate());
  for (int i=1; i<9; i++)
  { for (int j=1; j<9; j++)
    { if (b[i][j]=='-')
      { b[i][j]='O'; // make move on board
        if (check4winner(i, j, 'O') != 0) {
          b[i][j]='-';  // undo move
          return check4winner(i, j, 'O');
        }
        score = min(depth-1, alpha, beta);
        if (score > best) best=score;
        if (best > alpha) alpha=best;

        b[i][j]='-'; // undo move

        if (alpha >= beta) return best;
  } } }
  return(best);
}

int check4winner(int i, int j, char move) // i and j represent last move made; return 5000 for computer win, -5000 for player
{ int temp; int count;

// check horizontal win condition
count = 1;
temp = j - 1;
while (b[i][temp] == move && temp > 0 && temp < 9) {
  count++;
  temp--;
}

temp = j + 1;
while (b[i][temp] == move && temp > 0 && temp < 9) {
  count++;
  temp++;
}

if (count == 4) {
  if (move == 'X') return -5000;
  if (move == 'O') return 5000;
}

// check vertical win conditions
count = 1;
temp = i - 1;
while (b[temp][j] == move && temp > 0 && temp < 9) {
  count++;
  temp--;
}

temp = i + 1;
while (b[temp][j] == move && temp > 0 && temp < 9) {
  count++;
  temp++;
}

if (count == 4) {
  if (move == 'X') return -5000;
  if (move == 'O') return 5000;
}

for (int i=1; i<=8; i++)
  for (int j=1; j<=8; j++)
    {if (b[i][j]=='-') return 0;}
  return 1; // draw
}

void checkGameOver(int i, int j, char move)
{ printboard();
  int result = check4winner(i, j, move);
  if (result == -5000) { cout << "you win" << endl; exit(0); }
  if (result == 5000)  { cout << "I win"   << endl; exit(0); }
  if (result == 1)     { cout << "draw"    << endl; exit(0); }
}
