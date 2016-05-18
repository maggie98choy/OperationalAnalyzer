# OperationalAnalyzer
This operational analyzer monitored the health of virtual machine running at a site. We developed an application  that interact with VMWare server for continuous collection of resource utilization and presented it to user for analysis using open source tools such as Logstash server, Chart4J, MongoDB. The system gather CPU, I/O and networking log for a virtual machine and decide threshold for System Management. To gather these information, we explored the capabilities offered by VMWare Hypervisor and its API.