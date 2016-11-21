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
# Monitoring

Installed collectd. See http://www.tecmint.com/install-collectd-and-collectd-web-to-monitor-server-resources-in-linux/ for more details.

    sudo apt-get install librrds-perl libjson-perl libhtml-parser-perl collectd libjson-perl
    sudo service collectd start
    cd /usr/local/ && git clone https://github.com/httpdss/collectd-web.git
    chmod +x /usr/local/collectd-web/cgi-bin/graphdefs.cgi

Then run
 `cd /usr/local/collectd-web/ && python runserver.py  0.0.0.0 8081` 


# Benchmarking
## Warm up

I ran `wrk --latency -t 4 -c 10 -d 10s http://10.1.65.237:8080/` before  running benchmark.

## vert.x
https://d2mxuefqeaa7sj.cloudfront.net/s_5B5F57F08A4E395102D430A9A6F7EAC8E79ECAA7363B9CF937372F24FB100B1C_1479700773563_file.png

    $ wrk --latency -t 4 -c 10 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 10 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   362.19us  506.86us  19.42ms   99.72%
        Req/Sec     5.80k   238.09     6.45k    76.73%
      Latency Distribution
         50%  274.00us
         75%  471.00us
         90%  504.00us
         99%  615.00us
      233328 requests in 10.10s, 8.90MB read
    Requests/sec:  23102.83
    Transfer/sec:      0.88MB
    
    $ wrk --latency -t 4 -c 100 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     3.98ms    1.00ms  18.48ms   92.73%
        Req/Sec     6.31k     1.05k   24.18k    95.51%
      Latency Distribution
         50%    3.77ms
         75%    3.86ms
         90%    3.98ms
         99%    7.68ms
      251688 requests in 10.10s, 9.60MB read
    Requests/sec:  24919.93
    Transfer/sec:      0.95MB
    
    $ wrk --latency -t 4 -c 1000 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 1000 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency    39.40ms   16.87ms 438.85ms   95.99%
        Req/Sec     6.30k   732.46     8.71k    84.25%
      Latency Distribution
         50%   38.01ms
         75%   42.89ms
         90%   43.38ms
         99%   48.37ms
      250749 requests in 10.08s, 9.57MB read
    Requests/sec:  24875.10
    Transfer/sec:      0.95MB
    
    $ wrk --latency -t 4 -c 10000 -d 10s http://10.1.65.237:8080/
    Running 10s test @ http://10.1.65.237:8080/
      4 threads and 10000 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency   370.45ms   70.35ms   1.14s    85.33%
        Req/Sec     6.46k     3.17k   19.24k    75.20%
      Latency Distribution
         50%  380.85ms
         75%  389.71ms
         90%  412.68ms
         99%  556.16ms
      248195 requests in 10.08s, 9.47MB read
    Requests/sec:  24612.71
    Transfer/sec:      0.94MB
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

