Java performance: threads vs non-blocking I/O

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

See [spring-boot/](https://github.com/tokuhirom/java-threads-vs-nonblocking/tree/master/spring-boot) directory.

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


## spring boot/tomcat
https://d2mxuefqeaa7sj.cloudfront.net/s_5B5F57F08A4E395102D430A9A6F7EAC8E79ECAA7363B9CF937372F24FB100B1C_1479700720689_file.png

    $ wrk --latency -t 4 -c 10 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 10 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    15.63ms   33.29ms 303.30ms   90.45%
        Req/Sec   497.78    310.11     1.38k    67.53%
      Latency Distribution
         50%    1.91ms
         75%   12.58ms
         90%   46.44ms
         99%  170.47ms
      19494 requests in 10.01s, 2.14MB read
    Requests/sec:   1947.69
    Transfer/sec:    219.09KB
    $ wrk --latency -t 4 -c 100 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    49.67ms   92.14ms 629.78ms   90.14%
        Req/Sec     1.46k     0.89k    3.00k    54.08%
      Latency Distribution
         50%   12.87ms
         75%   40.61ms
         90%  138.72ms
         99%  463.72ms
      54152 requests in 10.01s, 5.95MB read
    Requests/sec:   5409.71
    Transfer/sec:    608.46KB
    $ wrk --latency -t 4 -c 1000 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 1000 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   115.72ms   48.65ms 700.31ms   82.61%
        Req/Sec     2.10k   481.01     3.01k    70.96%
      Latency Distribution
         50%  115.65ms
         75%  137.03ms
         90%  158.99ms
         99%  244.56ms
      82885 requests in 10.08s, 9.09MB read
    Requests/sec:   8224.93
    Transfer/sec:      0.90MB
    $ wrk --latency -t 4 -c 10000 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 10000 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   772.04ms  294.37ms   1.76s    79.70%
        Req/Sec     2.22k     0.95k    5.37k    69.97%
      Latency Distribution
         50%  798.78ms
         75%  884.98ms
         90%    1.03s
         99%    1.39s
      80974 requests in 10.08s, 8.88MB read
      Socket errors: connect 0, read 0, write 0, timeout 1237
    Requests/sec:   8034.93
    Transfer/sec:      0.88MB

