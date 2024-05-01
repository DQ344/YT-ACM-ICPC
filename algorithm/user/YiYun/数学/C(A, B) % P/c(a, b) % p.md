```cpp
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
```



```cpp
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
```



```cpp
#include<bits/stdc++.h>
#define int long long
#define gcd __gcd
using namespace std;

int ksm(int a, int b, int p)
{
	int f = 1 % p;
	while (b)
	{
		if (b & 1) f = f * a % p;
		a = a * a % p;
		b >>= 1;
	}
	return f;
}

int C(int a, int b, int p)
{
	if (a < b) return 0;
	
	int x = 1, y = 1;
	for (int i = a, j = b; j >= 1; -- i, -- j)
	{
		x = (x * i) % p;
		y = (y * j) % p;
	}
	
	return (x * ksm(y, p - 2, p)) % p;
}

int lucas(int a, int b, int p)
{
	if (a < p && b < p) return C(a, b, p);
	else return (C(a % p, b % p, p) * lucas(a / p, b / p, p)) % p;
}

void solve(){
	int a, b, p;
	cin >> a >> b >> p;
	cout << lucas(a, b, p) << endl;
}

int32_t main(){
	ios::sync_with_stdio(false);
	cin.tie(nullptr);cout.tie(nullptr);
	int t = 1; cin >> t;
	while(t--) solve();
}
```



```cpp
#include<bits/stdc++.h>
#define int long long
#define gcd __gcd
using namespace std;
const int N = 5010;
int sum[N];
int primes[N], cnt;
bool st[N];

void get_primes(int n)
{
	for (int i = 2; i <= n; ++ i)
	{
		if (!st[i]) primes[cnt ++] = i;
		
		for (int j = 0; primes[j] <= n / i; ++ j)
		{
			st[primes[j] * i] = true;
			if (i % primes[j] == 0) break;
		}
	}
}

int get(int a, int p)
{
	int res = 0;
	while (a)
	{
		res += a / p;
		a /= p;
	}
	
	return res;
}

vector<int> mul(vector<int> A, int x)
{
	vector<int> B;
	int t = 0;
	for (int i = 0; i < A.size(); ++ i)
	{
		t += A[i] * x;
		B.push_back(t % 10);
		t /= 10;
	}
	while (t) B.push_back(t % 10), t /= 10;
	return B;
}

void solve()
{
	int a, b;
	cin >> a >> b;
	get_primes(a);
	
	for (int i = 0; i < cnt; ++ i)
	{
		int p = primes[i];
		sum[i] = get(a, p) - get(a - b, p) - get(b, p);
	}
	
	vector<int> res;
	res.push_back(1);
	
	for (int i = 0; i < cnt; ++ i)
		for (int j = 0; j < sum[i]; ++ j)
			res = mul(res, primes[i]);

	for (int i = res.size() - 1; ~i; -- i) cout << res[i];        
}

int32_t main(){
	ios::sync_with_stdio(false);
	cin.tie(nullptr);cout.tie(nullptr);
	int t = 1; 
	while(t--) solve();
}
```