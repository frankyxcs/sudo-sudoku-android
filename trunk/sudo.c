#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

void randCube(int[9]);
void printCube(int[9]);
void printBoard(int[9][9]);
void printFormattedBoard(int[9][9]);

void initBoard(int[9][9]);

void rotArrayRight(int*,int,int);
void rotArrayLeft(int*,int,int);

void rotCubeDown(int[9],int);
void rotCubeLeft(int[9],int);

void cubeToRC(int[9][9]);

void shuffleRC(int[9][9]);
void swapRows(int[9][9],int,int);
void swapCols(int[9][9],int,int);

void randClear(int[9][9],int);

int main( int argc, char** argv ) {
    srand(time(NULL));
   
    int numgivens;
    if(argc > 2) {
        perror("One Argument, Number of Givens\n");
        return 1;
    } 
    if(argc == 2)
        numgivens = atoi(argv[1]);
    else
        numgivens = 32;

    int sku[9][9];
    
    initBoard(sku);
    
    printf("\nrandom gen board:\n");
    cubeToRC(sku);
    printBoard(sku);

    printf("\nshuffled board:\n");
    shuffleRC(sku);
    printBoard(sku);

    printf("\nfinal board:\n");
    randClear(sku,numgivens);
    printBoard(sku);

    printf("\nformatted board:\n");
    printFormattedBoard(sku);

    /*
    int x[9];
    randCube(x,9);
    printf("random cube:\n");
    printCube(x,9);
    
    rotCubeLeft(x,9,1);
    printf("cube rotated left:\n");
    printCube(x,9);
    */
    return 0;
}

void randCube(int cube[9]) {
    int i,n=9;
    for(i=0; i < n; i++) {
        cube[i] = i+1;
    }

    for (i = 0; i < n - 1; i++) {
        size_t j = i + rand() / (RAND_MAX / (n - i) + 1);
        int t = cube[j];
        cube[j] = cube[i];
        cube[i] = t;
    }
}

void printCube(int cube[9]) {
    int i;
    for(i=0; i < 9; i++) {
        printf("%d ",cube[i]);
        if( (i+1)%3==0)
            printf("\n");
    }
}

void rotArrayRight(int* a, int len, int n) {
    int temp;
    int i;
    while(n>0) {
        temp = a[len-1];
        for(i=len-1; i>0; i--)
            a[i] = a[i-1];
        a[0] = temp;
        n--;
    }
}

void rotArrayLeft(int* a, int len, int n) {
    int temp;
    int i;
    while(n>0) {
        temp = a[0];
        for(i=0; i<len-1; i++)
            a[i] = a[i+1];
        a[len-1] = temp;
        n--;
    }
}

void rotCubeDown(int cube[9], int n) {
    rotArrayRight(cube,9,3*n);
}

void rotCubeLeft(int cube[9], int n) {
    int i;
    for(i=0; i < 9; i+=3) {
        rotArrayLeft(cube+i,3,n);
    }
}

void printBoard(int board[9][9]) {
    int i,j;
    for(i=0;i<9;i++) {
        for(j=0;j<9;j++) {
            printf("%d ",board[i][j]);
        }
        printf("\n");
    }
    /*for(i=0; i < 9; i++) {
        printf("cube %d:\n",i);
        printCube(board[i]);
        printf("\n");
    }*/
    /*
    int i,j,m,n;
    for(i=0; i<9; i++) {
        for(j=0; j<9; j++) {
            m = 3*(j%3)+i%3;
            n = (int)floor(i/3.0)+3*(int)floor(j/3.0);
            printf("%d ",board[m][n]);
            //printf("%d ",board[i][j]);
        }
    printf("\n");
    }*/
}

void cubeToRC(int oldb[9][9]) {
    int newb[9][9];
    int i,j,m,n;
    for(i=0; i<9; i++) {
        for(j=0; j<9; j++) {
            m = 3*(j%3)+i%3;
            n = (int)floor(i/3.0)+3*(int)floor(j/3.0);
            newb[m][n]= oldb[i][j];
        }
    }
    oldb=newb;
}

void shuffleRC(int board[9][9]) {
    int x,a,b,ct=0;
    int u = 12, l = 3;
    int n = l + rand()%(u-l+1); // random number from l to u inclusive
    while(n>0) { // swap rows randomly
        x=3*(rand()%3); // which of 3 cubes
        a=x+rand()%3; // random index in cube
        b=x+rand()%3;
        //printf("n= %d\tx= %d\ta= %d\tb= %d\n",n,x,a,b);
        if(a!=b) {
            swapRows(board,a,b);
            ct++;
        }
        n--;
    }
    n = l + rand()%(u-l+1); 
    while(n>0) { // swap columns randomly
        x=3*(rand()%3); // which of 3 cubes
        a=x+rand()%3; // random index in cube
        b=x+rand()%3;
        if(a!=b) {
            swapCols(board,a,b);
            ct++;
        }
        n--;
    }
    printf("successful swaps: %d\n",ct);
}

void swapRows(int board[9][9],int a,int b) {
    int i,temp;
    for(i=0;i<9;i++) {
        temp = board[a][i];
        board[a][i] = board[b][i];
        board[b][i] = temp;
    }
}

void swapCols(int board[9][9],int a,int b) {
    int i,temp;
    for(i=0;i<9;i++) {
        temp = board[i][a];
        board[i][a] = board[i][b];
        board[i][b] = temp;
    }
}

void randClear(int board[9][9],int givens) {
    if(givens < 17) {
        printf("WARNING: A unique solution requires 17 filled squares.\n");
        givens = 17;
    }
    if(givens > 81)
        givens = 81;
    int blank = 81 - givens;
    int r,c;
    while(blank > 0) {
        do {
        r=rand()%9;
        c=rand()%9;
        } while (board[r][c]==0);
        board[r][c]=0;
        blank--;
    }
}

void printFormattedBoard(int board[9][9]) {
    int r,c;
    for(r=0; r<9; r++) {
        for(c=0; c<9; c++) {
            if(board[r][c]!=0)
                printf("%d",board[r][c]);
            else
                printf(".");
        }
        printf("\n");
    }
}

void initBoard(int board[9][9]) {
    int i,j;
    randCube(board[0]); // first cube is random
    for(i=1; i < 9; i++) // each other cube is the same
        for(j=0; j < 9; j++)
            board[i][j]=board[0][j];
    //printf("initial board:\n");
    //printBoard(sku);
    
    for(i=0; i < 9; i++) {
        rotCubeDown(board[i],i%3); // cubes left to right rotated down
        rotCubeLeft(board[i],i/3); // cubes up to down rotated left
    }
}
