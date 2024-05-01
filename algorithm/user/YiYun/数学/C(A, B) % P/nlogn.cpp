#include<bits/stdc++.h>
#define int long long
using namespace std;
const int N = 100010, mod = 1e9 + 7;

int fact[N], infact[N];

int ksm(int a, int b, int p)
{
	int f = 1 % p;
	while(b){
		if (b & 1) f = f * a % p;
		a = a * a % p;
		b >>= 1;
	}
	return f;
}

void init(){
	fact[0] = 1;
	for (int i = 1; i < N; ++ i) fact[i] = fact[i - 1] * i % mod;
	infact[N - 1] = ksm(fact[N - 1], mod - 2, mod);
	for (int i = N - 1; i; -- i) infact[i - 1] = infact[i] * i % mod;
}

int c(int a, int b, int p){
	return fact[a] * infact[a - b] % mod * infact[b] % mod;
}

void solve()
{
	int a, b; cin >> a >> b;
	cout << c(a, b, mod) << endl;
}

int32_t main(){
	ios::sync_with_stdio(false);
	cin.tie(nullptr);cout.tie(nullptr);
	init();
	int t = 1; cin >> t;
	while(t--) solve();
}
