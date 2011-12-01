#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void randCube(int*,int);
void printCube(int*,int);
void printBoard(int[9][9]);

void rotArrayRight(int*,int,int);
void rotArrayLeft(int*,int,int);

void rotCubeDown(int*,int,int);
void rotCubeLeft(int*,int,int);

int main( int argc, char** argv ) {
    srand(time(NULL));
    
    int sku[9][9];
    int i,j;
    for(i=0; i < 9; i++)
        for(j=0; j < 9; j++)
            sku[i][j]=j+1;
    printf("initial board:\n");
    printBoard(sku);
    
    for(i=0; i < 9; i++) {
        rotCubeDown(sku[i],9,i%3);
        rotCubeLeft(sku[i],9,i/3);
    }
    printf("\nrandom gen board:\n");
    printBoard(sku);

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

void randCube(int* cube, int n) {
    int i;
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

void printCube(int* cube, int size) {
    int i;
    for(i=0; i < size; i++) {
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

void rotCubeDown(int* cube, int len, int n) {
    rotArrayRight(cube,len,3*n);
}

void rotCubeLeft(int* cube, int len, int n) {
    int i;
    for(i=0; i < len; i+=3) {
        rotArrayLeft(cube+i,3,n);
    }
}

void printBoard(int board[9][9]) {
    /*int i;
    for(i=0; i < 9; i++) {
        printf("cube %d:\n",i);
        printCube(board[i],9);
        printf("\n");
    }*/
    int i,j;
    for(i=0; i<9; i++) {
        for(j=0; j<9; j++)
            printf("%d ",board[i][j]);
    printf("\n");
    }
}
