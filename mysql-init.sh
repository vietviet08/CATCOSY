#!/bin/bash   
set -e  
echo 'Setting MySQL parameters for large imports...'    
echo 'wait_timeout=300' >> /etc/mysql/conf.d/custom.cnf
echo 'interactive_timeout=300' >> /etc/mysql/conf.d/custom.cnf
echo 'net_read_timeout=600' >> /etc/mysql/conf.d/custom.cnf
echo 'net_write_timeout=600' >> /etc/mysql/conf.d/custom.cnf
echo 'max_allowed_packet=256M' >> /etc/mysql/conf.d/custom.cnf

exec \
\$@\
