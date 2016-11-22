# aggregate benchmark results from result/ directory.
import subprocess
import re

lines = subprocess.check_output('grep Request result/*.txt', shell=True)
for line in lines.split("\n"):
    if len(line) == 0:
        continue
    m = re.search("""result/(.*)-(.*).txt:Requests/sec:\s*(\S*)""", line)
    impl = m.group(1)
    conn = m.group(2)
    score = m.group(3)
    print "%s,%s,%s" % (impl, conn, score)
