Java performance: threads vs non-blocking I/O

<img src="graph.png">

# Servers

2 scaleway’s baremetal instances for client/server.


  **4** Dedicated x86 64bit Cores
  **8GB** Memory
  **50GB** SSD Disk
   
  **1** Flexible public IPv4
  **300Mbit/s** Internet bandwidth
  **2.5Gbit/s** Internal bandwidth
  
# Benchmarking code
## netty based vert.x server

See [vertx/](https://github.com/tokuhirom/java-threads-vs-nonblocking/tree/master/vertx) directory.

## spring-boot based tomcat threading server

See [spring-boot-tomcat/](https://github.com/tokuhirom/java-threads-vs-nonblocking/tree/master/spring-boot-tomcat/) directory.

# Server configuration
## `/etc/security/limits.conf` 

I added following lines to `/etc/security/limits.conf` 

    * soft nofile 100000
    * hard nofile 100000
    * soft nproc 100000
    * hard nproc 100000
## `/etc/sysctl.conf` 

Added following lines on `/etc/sysctl.conf` 

    net.ipv4.ip_local_port_range = 18000    65535
    net.ipv4.tcp_tw_reuse = 1

Note: default value is following:

    # sysctl net.ipv4.ip_local_port_range
    net.ipv4.ip_local_port_range = 32768        60999
    # sysctl net.ipv4.tcp_tw_reuse
    net.ipv4.tcp_tw_reuse = 0


## Other configuration values
    root@client1:~# cat /proc/sys/fs/file-max
    812123
    root@client1:~# cat /proc/sys/kernel/threads-max
    63451

# Benchmarking
## Warm up

I ran `wrk --latency -t 4 -c 10 -d 10s http://10.1.65.237:8080/` before  running benchmark.

## vert.x

```
$ wrk --latency -t 4 -c 10 -d 10s http://10.1.65.237:8080/
Running 10s test @ http://10.1.65.237:8080/
  4 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   276.27us  410.13us  14.86ms   99.63%
    Req/Sec     7.59k   538.55     8.25k    71.46%
  Latency Distribution
     50%  240.00us
     75%  259.00us
     90%  291.00us
     99%  511.00us
  304142 requests in 10.10s, 11.60MB read
Requests/sec:  30115.55
Transfer/sec:      1.15MB

$ wrk --latency -t 4 -c 100 -d 10s http://10.1.65.237:8080/
Running 10s test @ http://10.1.65.237:8080/
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.26ms    2.65ms  95.28ms   99.13%
    Req/Sec    22.96k     3.58k   56.96k    80.55%
  Latency Distribution
     50%    1.02ms
     75%    1.41ms
     90%    1.55ms
     99%    3.20ms
  915440 requests in 10.10s, 34.92MB read
Requests/sec:  90643.83
Transfer/sec:      3.46MB

$ wrk --latency -t 4 -c 1000 -d 10s http://10.1.65.237:8080/
Running 10s test @ http://10.1.65.237:8080/
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    12.96ms   21.34ms 444.63ms   98.60%
    Req/Sec    21.59k     2.47k   26.61k    88.75%
  Latency Distribution
     50%   10.06ms
     75%   10.58ms
     90%   16.53ms
     99%   57.02ms
  859191 requests in 10.06s, 32.78MB read
Requests/sec:  85401.88
Transfer/sec:      3.26MB

$ wrk --latency -t 4 -c 10000 -d 10s http://10.1.65.237:8080/
Running 10s test @ http://10.1.65.237:8080/
  4 threads and 10000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   116.25ms   80.43ms   1.92s    83.06%
    Req/Sec    18.17k     3.27k   28.82k    74.81%
  Latency Distribution
     50%   97.88ms
     75%  109.04ms
     90%  217.10ms
     99%  416.98ms
  710776 requests in 10.10s, 27.11MB read
  Socket errors: connect 0, read 0, write 0, timeout 10
Requests/sec:  70375.82
Transfer/sec:      2.68MB
```

## spring boot + Apache Tomcat/8.5.6

```
$ wrk -t 4 -c 10 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.06ms    6.73ms  81.36ms   89.43%
    Req/Sec     0.93k   205.29     1.52k    68.25%
  37071 requests in 10.01s, 4.07MB read
Requests/sec:   3704.44
Transfer/sec:    416.71KB

$ wrk -t 4 -c 100 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    41.87ms   76.00ms 766.17ms   92.03%
    Req/Sec     1.17k   234.21     1.68k    72.50%
  46562 requests in 10.01s, 5.11MB read
Requests/sec:   4653.67
Transfer/sec:    523.40KB

$ wrk -t 4 -c 1000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   160.96ms   91.36ms 890.29ms   81.16%
    Req/Sec     1.56k   343.23     2.17k    77.25%
  62010 requests in 10.08s, 6.80MB read
Requests/sec:   6153.31
Transfer/sec:    691.05KB

$ wrk -t 4 -c 10000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   408.26ms  187.75ms   1.99s    77.53%
    Req/Sec     2.72k   679.15     4.41k    72.61%
  104787 requests in 10.07s, 11.49MB read
  Socket errors: connect 0, read 0, write 0, timeout 920
Requests/sec:  10402.99
Transfer/sec:      1.14MB
```

## spring boot + jetty-9.3.14.v20161028

```
$ wrk -t 4 -c 10 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.99ms    7.06ms  95.74ms   89.37%
    Req/Sec     1.13k   250.38     1.89k    68.50%
  44807 requests in 10.01s, 5.00MB read
Requests/sec:   4477.52
Transfer/sec:    511.59KB

$ wrk -t 4 -c 100 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    22.41ms   31.45ms 388.82ms   88.36%
    Req/Sec     2.05k   582.89     3.25k    69.00%
  81427 requests in 10.00s, 9.09MB read
Requests/sec:   8139.89
Transfer/sec:      0.91MB

$ wrk -t 4 -c 1000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   119.23ms  118.64ms   1.89s    91.30%
    Req/Sec     2.14k   482.57     4.14k    83.29%
  82765 requests in 10.06s, 9.23MB read
  Socket errors: connect 0, read 0, write 0, timeout 46
Requests/sec:   8223.85
Transfer/sec:      0.92MB

$ wrk -t 4 -c 10000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   829.71ms  316.54ms   2.00s    71.95%
    Req/Sec     1.93k   556.15     3.97k    71.96%
  73008 requests in 10.02s, 8.15MB read
  Socket errors: connect 0, read 0, write 0, timeout 2330
Requests/sec:   7285.11
Transfer/sec:    832.38KB
```

## spring boot + undertow(XNIO version 3.3.6.Final, XNIO NIO Implementation Version 3.3.6.Final)

```
$ wrk -t 4 -c 10 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.23ms    5.91ms  88.99ms   90.62%
    Req/Sec     1.20k   506.87     2.79k    71.50%
  47651 requests in 10.00s, 6.41MB read
Requests/sec:   4763.39
Transfer/sec:    655.90KB

$ wrk -t 4 -c 100 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     8.13ms    6.58ms 107.34ms   89.65%
    Req/Sec     3.49k   481.19     4.72k    73.00%
  138913 requests in 10.00s, 18.68MB read
Requests/sec:  13889.73
Transfer/sec:      1.87MB

$ wrk -t 4 -c 1000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    57.35ms   21.15ms 490.43ms   86.76%
    Req/Sec     4.21k   627.11     6.27k    87.75%
  167418 requests in 10.06s, 22.51MB read
Requests/sec:  16645.62
Transfer/sec:      2.24MB

$ wrk -t 4 -c 10000 -d 10s http://10.1.65.237:8080
Running 10s test @ http://10.1.65.237:8080
  4 threads and 10000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   619.81ms  175.07ms   1.66s    75.36%
    Req/Sec     2.54k   761.22     4.25k    67.86%
  99102 requests in 10.06s, 13.33MB read
  Socket errors: connect 0, read 0, write 0, timeout 813
Requests/sec:   9848.78
Transfer/sec:      1.32MB
```

## undertow's nonblocking API

```
$ wrk --latency -t 4 -c 10 http://10.1.65.237:8084/
Running 10s test @ http://10.1.65.237:8084/
  4 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   319.08us  734.22us  37.87ms   99.29%
    Req/Sec     7.03k   783.58     8.22k    74.75%
  Latency Distribution
     50%  247.00us
     75%  273.00us
     90%  466.00us
     99%  649.00us
  282656 requests in 10.10s, 36.93MB read
Requests/sec:  27987.41
Transfer/sec:      3.66MB

$ wrk --latency -t 4 -c 100 http://10.1.65.237:8084/
Running 10s test @ http://10.1.65.237:8084/
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.71ms    2.64ms  99.68ms   95.20%
    Req/Sec    18.40k     5.54k   48.12k    72.07%
  Latency Distribution
     50%    1.14ms
     75%    1.93ms
     90%    2.81ms
     99%   11.45ms
  733901 requests in 10.10s, 95.89MB read
Requests/sec:  72666.07
Transfer/sec:      9.49MB

$ wrk --latency -t 4 -c 1000 http://10.1.65.237:8084/
Running 10s test @ http://10.1.65.237:8084/
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    12.06ms   11.20ms 278.07ms   63.00%
    Req/Sec    21.24k     5.60k   33.12k    67.42%
  Latency Distribution
     50%   13.32ms
     75%   15.58ms
     90%   21.76ms
     99%   33.35ms
  836630 requests in 10.06s, 109.31MB read
Requests/sec:  83186.92
Transfer/sec:     10.87MB

$ wrk --latency -t 4 -c 10000 http://10.1.65.237:8084/
Running 10s test @ http://10.1.65.237:8084/
  4 threads and 10000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   112.85ms  125.90ms   1.85s    83.34%
    Req/Sec    24.85k     5.46k   40.18k    72.94%
  Latency Distribution
     50%  112.54ms
     75%  172.99ms
     90%  297.21ms
     99%  419.14ms
  960146 requests in 10.10s, 125.45MB read
  Socket errors: connect 0, read 0, write 0, timeout 1
Requests/sec:  95101.59
Transfer/sec:     12.43MB
```
