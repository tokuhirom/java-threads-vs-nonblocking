import urllib2
import subprocess
import sys
import os

class Benchmarker:
	def __init__(self, host, port):
		self.host = host
		self.port = port

	def mk_cmdline(self, connections):
		return ['wrk', '--timeout', '10s', '--latency', '-t', '4', '-c', str(connections), self.url()]

	def url(self):
		return "http://%s:%s/" % (self.host, self.port)

	def run(self):
		print "======> WARMING UP <======="
		subprocess.check_call(self.mk_cmdline(10))

		print "======> RUN <======="
		for connections in [10, 100, 1000, 10000]:
			print "Connections: %d" % connections
			fname = "result/%s-%d.txt" % (port, connections)
			with open(fname, 'w') as fh:
				subprocess.check_call(self.mk_cmdline(100), stdout=fh, stderr=fh)
			with open(fname, 'r') as fh:
				print fh.read()

host = sys.argv[1]
port = sys.argv[2]

benchmarker = Benchmarker(host, port)
benchmarker.run()

