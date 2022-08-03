docker run \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -v /Users/tian/docker/mysql8.0/conf.d:/etc/mysql/conf.d \
  -v /Users/tian/docker/mysql8.0/data:/var/lib/mysql \
  -d \
  -p 3306:3306 \
  --name mysql8 \
  amd64/mysql:8.0.29
