#!/bin/sh

mkdir -p /data/logs/nginx
mkdir -p /data/logs/php-fpm
mkdir -p /data/logs/supervisor
mkdir -p /data/nginx/body
mkdir -p /data/nginx/fastcgi_temp

if [ "${PMA_SECRET}" = "" ]; then
  PMA_SECRET=$(< /dev/urandom tr -dc _A-Z-a-z-0-9 | head -c${1:-32};echo;)
fi

sed -i -E \
        -e "s/^(.+\['blowfish_secret'\]\s*=\s*).+/\1 '${PMA_SECRET}';/" \
        -e "s/^(.+\['host'\]\s*=\s*).+/\1 '${MYSQL_HOSTNAME}';/" \
        -e "s/^(.+\['controluser'\]\s*=\s*).+/\1 '${PMA_USERNAME}';/" \
        -e "s/^(.+\['controlpass'\]\s*=\s*).+/\1 '${PMA_PASSWORD}';/" \
        -e "s/^(.+\['pmadb'\]\s*=\s*).+/\1 '${PMA_DB}';/" \
      /www/phpmyadmin/config.inc.php

chown -R nginx: /data /www

/usr/bin/php-fpm --daemonize -c /etc/php/php.ini -c /etc/php/php-fpm.conf
/usr/sbin/nginx -c /etc/nginx/nginx.conf