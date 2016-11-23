import urllib2
import subprocess
import sys
import os
import errno

def mkdir_p(path):
	try:
		os.makedirs(path)
	except OSError as exc:  # Python >2.5
		if exc.errno == errno.EEXIST and os.path.isdir(path):
			pass
		else:
			raise

class Benchmarker:
	def __init__(self, ip, port, port2impl, ip2instance_type):
		self.ip = ip
		self.instance_type = ip2instance_type[ip]
		self.port = port
		self.impl = port2impl[port]

	def mk_cmdline(self, connections):
		return ['wrk', '--timeout', '10s', '--latency', '-t', '4', '-c', str(connections), self.url()]

	def url(self):
		return "http://%s:%s/" % (self.ip, self.port)

	def run(self):
		print "======> WARMING UP <======="
		subprocess.check_call(self.mk_cmdline(10))

		basedir = 'result/' + self.instance_type
		mkdir_p(basedir)

		print "======> RUN <======="
		for connections in [10, 100, 1000, 10000]:
			print "Connections: %d" % connections
			fname = "%s/%s-%d.txt" % (basedir, self.impl, connections)
			with open(fname, 'w') as fh:
				subprocess.check_call(self.mk_cmdline(connections), stdout=fh, stderr=fh)
			with open(fname, 'r') as fh:
				print fh.read()

ip = sys.argv[1]
port = sys.argv[2]

benchmarker = Benchmarker(ip, port, {
	"8080": "vertx",
	"8081": "spring-boot-tomcat",
	"8082": "spring-boot-jetty",
	"8083": "spring-boot-undertow",
	"8084": "undertow",
	"8085": "jetty",
	"8086": "tomcat"
}, {
	'163.172.184.153': 'vps'
})
benchmarker.run()

