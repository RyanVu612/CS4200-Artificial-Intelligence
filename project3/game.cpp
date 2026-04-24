// Simple tic-tac-toe with minimax
#include <iostream>
using namespace std;

int min(int depth, int alpha, int beta);
int max(int depth, int alpha, int beta);
int evaluate();
int check4winner(int i, int j, char move);
int distToCenter(int row, int col);
void checkGameOver(int i, int j, char move);
void getamove(int &row, int &col);
void makemove(int &row, int &col);
void setup();
void printboard();

char b[9][9], maxdepth = 4;

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
{ int score=0;
  
  // win already considered through check4win()

  for (int i = 1; i <= 8; i++) {
    int count1=0;
    char move1='-';
    int count2=0;
    char move2='-';
    for (int j = 1; j <= 8; j++) {
      // horizontal
      if (b[i][j] == '-') {
        if (count1 == 2) {  // 2 in a row
          if (move1 == 'X') score -= 400;
          if (move1 == 'O') score += 300;
        }
        count1 = 0;
        move1 = '-';
      } else if (b[i][j] == 'X') {
        if (move1 == 'X') {
          count1++;
        } else {
          if (count1 == 2) score += 300;
          move1 = 'X';
          count1 = 1;
        }
      } else {
        if (move1 == 'O') {
          count1++;
        } else {
          if (count1 == 2) score -= 400;
          move1 = 'O';
          count1 = 1;
        }
      }
      if (count1 == 3) {
        if (move1 == 'X') score -= 1200;
        if (move1 == 'O') score += 1000;
      }

      // vertical
      if (b[j][i] == '-') {
        if (count2 == 2) {  // 2 in a row
          if (move2 == 'X') score -= 400;
          if (move2 == 'O') score += 300;
        }
        count2 = 0;
        move2 = '-';
      } else if (b[j][i] == 'X') {
        if (move2 == 'X') {
          count2++;
        } else {
          if (count2 == 2) score += 300;
          move2 = 'X';
          count2 = 1;
        }
      } else {
        if (move2 == 'O') {
          count2++;
        } else {
          if (count2 == 2) score -= 400;
          move2 = 'O';
          count2 = 1;
        }
      }
      if (count2 == 3) {
        if (move2 == 'X') score -= 2500;
        if (move2 == 'O') score += 2000;
      }

      // reward for being around other already placed tiles
      if (b[i][j] == 'O') {
        for (int dRow = -1; dRow <= 1; dRow++) {
          for (int dCol = -1; dCol <= 1; dCol++) {
            if (dRow == 0 && dCol == 0) continue;
      
            int ni = i + dRow;
            int nj = j + dCol;
      
            if (ni < 1 || ni > 8 || nj < 1 || nj > 8) continue;
      
            if (b[ni][nj] == 'X') score += 20;
            if (b[ni][nj] == 'O') score += 10;
          }
        }
      } else if (b[i][j] == 'X') {
        for (int dRow = -1; dRow <= 1; dRow++) {
          for (int dCol = -1; dCol <= 1; dCol++) {
            if (dRow == 0 && dCol == 0) continue;
      
            int ni = i + dRow;
            int nj = j + dCol;
      
            if (ni < 1 || ni > 8 || nj < 1 || nj > 8) continue;
      
            if (b[ni][nj] == 'O') score -= 20;
            if (b[ni][nj] == 'X') score -= 10;
          }
        }
      }

      // reward being towards middle
      if (b[i][j] == 'O') {
        score += 8 - distToCenter(i, j);
      } else if (b[i][j] == 'X') {
        score -= 8 - distToCenter(i, j);
      }
    }
  }

  return score;
}

void makemove(int &row, int &col)
{ int best=-20000,depth=maxdepth,score,mi=1,mj=1,alpha=-999999,beta=999999;
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
  while (temp > 0 && temp < 9 && b[i][temp] == move) {
    count++;
    temp--;
  }

  temp = j + 1;
  while (temp > 0 && temp < 9 && b[i][temp] == move) {
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
  while (temp > 0 && temp < 9 && b[temp][j] == move) {
    count++;
    temp--;
  }

  temp = i + 1;
  while (temp > 0 && temp < 9 && b[temp][j] == move) {
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

int distToCenter(int row, int col)
{
  int drow = min(abs(row - 4), abs(row - 5));
  int dcol = min(abs(col - 4), abs(col - 5));
  return drow + dcol;
}

void checkGameOver(int i, int j, char move)
{ printboard();
  int result = check4winner(i, j, move);
  if (result == -5000) { cout << "you win" << endl; exit(0); }
  if (result == 5000)  { cout << "I win"   << endl; exit(0); }
  if (result == 1)     { cout << "draw"    << endl; exit(0); }
}
