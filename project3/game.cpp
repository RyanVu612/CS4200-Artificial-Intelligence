// Simple tic-tac-toe with minimax
#include <iostream>
using namespace std;

void makemove();
int min(int depth);
int max(int depth);
int evaluate();
int check4winner();
void checkGameOver();
void getamove();
void setup();
void printboard();

char b[9][9], maxdepth = 5;

int main ()
{ setup();
  printboard();
  for (;;)
  { getamove();
    printboard();
  //    checkGameOver();
  //    makemove();
  //    checkGameOver();
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

void getamove()
{ char i; int j;
  do {
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

    if (b[i - 'A' + 1][j] != '-') {
      cout << "This space is already taken." << endl;
      i = ' ';
      j = 0;
      continue;
    } else {
      b[i - 'A' + 1][j] = 'X';
    }
  } while (b[i - 'A' + 1][j] == '-');
}

int evaluate ()
{ return 0; }


void makemove()
{ int best=-20000,depth=maxdepth,score,mi,mj;
  for (int i=0; i<3; i++)
  { for (int j=0; j<3; j++)
    { if (b[i][j]==0)
      { b[i][j]=1; // make move on board
        score = min(depth-1);
        if (score > best) { mi=i; mj=j; best=score; }
        b[i][j]=0; // undo move
  } } }
  cout << "my move is " << mi << " " << mj << endl;
  b[mi][mj]=1;
}
int min(int depth)
{ int best=20000,score;
  if (check4winner() != 0) return (check4winner());
  if (depth == 0) return (evaluate());
  for (int i=0; i<3; i++)
  { for (int j=0; j<3; j++)
    { if (b[i][j]==0)
      { b[i][j]=2; // make move on board
        score = max(depth-1);
        if (score < best) best=score;
        b[i][j]=0; // undo move
  } } }
  return(best);
}

int max(int depth)
{ int best=-20000,score;
  if (check4winner() != 0) return (check4winner());
  if (depth == 0) return (evaluate());
  for (int i=0; i<3; i++)
  { for (int j=0; j<3; j++)
    { if (b[i][j]==0)
      { b[i][j]=1; // make move on board
        score = min(depth-1);
        if (score > best) best=score;
        b[i][j]=0; // undo move
  } } }
  return(best);
}

int check4winner()
{ if ((b[0][0]==1)&&(b[0][1]==1)&&(b[0][2]==1)
   || (b[1][0]==1)&&(b[1][1]==1)&&(b[1][2]==1)
   || (b[2][0]==1)&&(b[2][1]==1)&&(b[2][2]==1)
   || (b[0][0]==1)&&(b[1][0]==1)&&(b[2][0]==1)
   || (b[0][1]==1)&&(b[1][1]==1)&&(b[2][1]==1)
   || (b[0][2]==1)&&(b[1][2]==1)&&(b[2][2]==1)
   || (b[0][0]==1)&&(b[1][1]==1)&&(b[2][2]==1)
   || (b[0][2]==1)&&(b[1][1]==1)&&(b[2][0]==1)) return 5000;  // computer wins
  if ((b[0][0]==2)&&(b[0][1]==2)&&(b[0][2]==2)
   || (b[1][0]==2)&&(b[1][1]==2)&&(b[1][2]==2)
   || (b[2][0]==2)&&(b[2][1]==2)&&(b[2][2]==2)
   || (b[0][0]==2)&&(b[1][0]==2)&&(b[2][0]==2)
   || (b[0][1]==2)&&(b[1][1]==2)&&(b[2][1]==2)
   || (b[0][2]==2)&&(b[1][2]==2)&&(b[2][2]==2)
   || (b[0][0]==2)&&(b[1][1]==2)&&(b[2][2]==2)
   || (b[0][2]==2)&&(b[1][1]==2)&&(b[2][0]==2)) return -5000;
for (int i=0; i<3; i++)
  for (int j=0; j<3; j++)
    {if (b[i][j]==0) return 0;}
  return 1; // draw
}

void checkGameOver()
{ printboard();
  if (check4winner() == -5000) { cout << "you win" << endl; exit(0); }
  if (check4winner() == 5000)  { cout << "I win"   << endl; exit(0); }
  if (check4winner() == 1)     { cout << "draw"    << endl; exit(0); }
}
