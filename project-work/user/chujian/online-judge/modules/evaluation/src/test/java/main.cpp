#include<iostream>
#include <cmath>
#include<algorithm>

using namespace std;

const int N = 200010, M = 18;//log200010 不超过18
int n, m;
int f[N][M]; //f[i][j]表示以i为起点，区间长度为2^j，在此区间内的最大值
int w[N];
void init()
{
    for (int j = 0; j < M; ++ j)
      for (int i = 0; i + (1 << j) - 1 <= n; ++ i)//区间右端点要小于n
       if (j == 0) f[i][0] = w[i];
       else
        f[i][j] = max(f[i][j - 1], f[i + (1 << (j - 1))][j - 1]);
}
int query(int l, int r)
{
    int len = r - l + 1;
    int k = log(len)/log(2);
    return max(f[l][k], f[r - (1 << k) + 1][k]);
}
int main()
{
    scanf("%d",&n);
    for (int i = 1; i <= n; ++ i) scanf("%d",&w[i]);

    init();//预处理
    scanf("%d",&m);

    for (int i = 0; i < m; ++ i)
    {
        int a, b;
        scanf("%d%d", &a, &b);
        cout << query(a, b) << endl;
    }
    return 0;
}