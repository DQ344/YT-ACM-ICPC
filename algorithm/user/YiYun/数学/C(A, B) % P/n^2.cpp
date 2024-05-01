#include<bits/stdc++.h>
#define int long long
using namespace std;
const int N = 2010, mod = 1e9 + 7;
int C[N][N];

void init()
{
	C[0][0] = 1;
	for (int i = 0; i < N; ++ i)
		for (int j = 0; j <= i; ++ j)
			if (!j) C[i][j] = 1;
	else C[i][j] = (C[i - 1][j] + C[i - 1][j - 1]) % mod;
}

void solve()
{
	int a, b;
	cin >> a >> b;
	cout << C[a][b] << endl;
	
}

int32_t main(){
	ios::sync_with_stdio(false);
	cin.tie(nullptr);cout.tie(nullptr);
	int t = 1; cin >> t;
	init();
	while(t--) solve();
}
