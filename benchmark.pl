#!/usr/bin/env perl
use strict;
use warnings;
use utf8;
use 5.010000;
use autodie;

my $host = shift @ARGV;
my $port = shift @ARGV;

for my $connections (10, 100, 1000, 10000) {
    my @cmd = qw(wrk --latency -t 4 -c), $connections, "http://$host:$port/";
    print "\n\$ @cmd\n";
    system @cmd;
}
