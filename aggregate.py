# aggregate benchmark results from result/ directory.
import subprocess
import re

for instance_type in ['baremetal', 'vps']:
    print "\n\n\n===> %s <===\n\n\n" % instance_type
    lines = subprocess.check_output('grep Request result/%s/*.txt' % instance_type, shell=True)
    for line in lines.split("\n"):
        if len(line) == 0:
            continue
        m = re.search("""result/[^/]+/(.*)-(.*).txt:Requests/sec:\s*(\S*)""", line)
        impl = m.group(1)
        conn = m.group(2)
        score = float(m.group(3))
        print "%s,%s connections,%.1f" % (impl, conn, score)
